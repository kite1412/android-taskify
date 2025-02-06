package com.nrr.notification

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import com.nrr.data.repository.UserDataRepository
import com.nrr.model.ReminderType
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.notification.model.Result
import com.nrr.notification.model.Result.Fail.Reason
import com.nrr.notification.model.TaskWithReminder
import com.nrr.notification.model.toFiltered
import com.nrr.notification.receiver.sequentialTaskNotifierPendingIntent
import com.nrr.notification.receiver.sequentialTaskSchedulerIntent
import com.nrr.notification.util.toTaskReminders
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AlarmManagerScheduledTaskNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDataRepository: UserDataRepository
) : ScheduledTaskNotifier {
    private val alarmManager = context
        .getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val scheduler = SequentialScheduler()

    override suspend fun scheduleReminder(task: Task): Result =
        scheduler.scheduleReminder(task)

    override suspend fun scheduleReminders(tasks: List<Task>, period: TaskPeriod): Result =
        scheduler.scheduleReminders(tasks, period)

    override fun cancelReminder(activeTask: Task) =
        scheduler.cancelReminder(activeTask)

    private inner class ImmediateScheduler : ScheduledTaskNotifier {
        override suspend fun scheduleReminder(task: Task): Result {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && !alarmManager.canScheduleExactAlarms()
            ) return Result.Fail(Reason.EXACT_ALARM_NOT_PERMITTED)

            val data = TaskWithReminder(task.toFiltered(), ReminderType.START)
            val notificationOffset = userDataRepository.userData
                .map {
                    when (task.activeStatuses.first().period) {
                        TaskPeriod.DAY -> it.dayNotificationOffset
                        TaskPeriod.WEEK -> it.weekNotificationOffset
                        TaskPeriod.MONTH -> it.monthNotificationOffset
                    }
                }.first()
            val notificationDate = data.task.startDate - notificationOffset.toDuration()

            if (notificationDate <= Clock.System.now())
                return Result.Fail(Reason.START_DATE_IN_PAST)

            val activeStatusId = data.task.id.toInt()
            val pendingIntent = sequentialTaskNotifierPendingIntent(context, activeStatusId)

            alarmManager.cancel(pendingIntent)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                notificationDate.toEpochMilliseconds(),
                pendingIntent
            )
            return Result.Success(null)
        }

        override fun cancelReminder(activeTask: Task) {
            val activeStatusId = activeTask.activeStatuses.firstOrNull()?.id?.toInt()
                ?: return

            alarmManager.cancel(sequentialTaskNotifierPendingIntent(context, activeStatusId))
        }
    }

    private inner class SequentialScheduler : ScheduledTaskNotifier {
        override suspend fun scheduleReminder(task: Task): Result {
            val reminders = task.toTaskReminders()
            val userData = userDataRepository.userData.first()
            var queue = userData.reminderQueue
            val notificationOffset = when (task.activeStatuses.first().period) {
                TaskPeriod.DAY -> userData.dayNotificationOffset
                TaskPeriod.WEEK -> userData.weekNotificationOffset
                TaskPeriod.MONTH -> userData.monthNotificationOffset
            }.toDuration()
            val deleteIndexes = queue.mapIndexed { index, r ->
                index to r.activeTaskId
            }
                .filter {
                    it.second == reminders.first.activeTaskId
                }
                .map { it.first }

            userDataRepository.removeTaskReminders(deleteIndexes)

            queue = queue.filter { it.activeTaskId != reminders.first.activeTaskId }
            val startDate = reminders.first.date - notificationOffset

            val now = Clock.System.now()
            val remindersInQueue = queue.toMutableList().apply {
                if (startDate > now) {
                    add(reminders.first.copy(date = startDate))
                }
                reminders.second?.let {
                    val endDate = reminders.second!!.date - notificationOffset
                    if (endDate > now) add(reminders.second!!.copy(date = endDate))
                }
            }
                .sortedBy { it.date }
                .mapIndexed { i, r -> i to r }
                .filter { it.second.activeTaskId == reminders.first.activeTaskId }

            if (remindersInQueue.isNotEmpty()) userDataRepository.addTaskReminders(
                remindersInQueue.associate { it }
            ) else return Result.Fail(Reason.BOTH_DATE_IN_PAST)

            remindersInQueue.forEach {
                if (it.first == 0) {
                    context.sendBroadcast(sequentialTaskSchedulerIntent(context))
                    return@forEach
                }
            }

            // TODO set warning
            return Result.Success(null)
        }

        override suspend fun scheduleReminders(
            tasks: List<Task>,
            period: TaskPeriod
        ): Result {
            val reminders = tasks.map(Task::toTaskReminders)
            val userData = userDataRepository.userData.first()
            val queue = userData.reminderQueue
            val notificationOffset = when (period) {
                TaskPeriod.DAY -> userData.dayNotificationOffset
                TaskPeriod.WEEK -> userData.weekNotificationOffset
                TaskPeriod.MONTH -> userData.monthNotificationOffset
            }.toDuration()
            val reminderActiveTaskIds = reminders.map { it.first.activeTaskId }

            queue
                .mapIndexed { i, r -> i to r }
                .filter {
                    it.second.activeTaskId in reminderActiveTaskIds
                }
                .map { it.first }
                .let {
                    userDataRepository.removeTaskReminders(it)
                }

            val flattenedWithFixedDate = reminders
                .map { p ->
                    listOf(
                        p.first.copy(date = p.first.date - notificationOffset),
                        p.second?.date?.minus(notificationOffset)?.let {
                            p.second?.copy(date = it)
                        }
                    )
                }
                .flatten()
            val now = Clock.System.now()

            queue
                .filter {
                    it.activeTaskId !in reminderActiveTaskIds
                }
                .toMutableList().apply {
                    addAll(
                        flattenedWithFixedDate
                            .filterNotNull()
                            .filter {
                                it.date > now
                            }
                    )
                }
                .sortedBy { it.date }
                .mapIndexed { i, r -> i to r }
                .filter { it.second.activeTaskId in reminderActiveTaskIds }
                .takeIf { it.isNotEmpty() }
                ?.let {
                    userDataRepository.addTaskReminders(
                        it.associate { p -> p }
                    )
                    if (it.first().first == 0) {
                        context.sendBroadcast(sequentialTaskSchedulerIntent(context))
                        return Result.Success(null)
                    }
                }

            return Result.Fail(Reason.BOTH_DATE_IN_PAST)
        }

        override fun cancelReminder(activeTask: Task) {
            val activeStatusId = activeTask.activeStatuses.firstOrNull()?.id?.toInt()
                ?: return

            CoroutineScope(Dispatchers.Main).launch {
                val queue = userDataRepository.userData.first().reminderQueue
                queue
                    .mapIndexed { i, r -> i to r }
                    .filter { it.second.activeTaskId == activeStatusId.toLong() }
                    .let {
                        if (it.isNotEmpty()) userDataRepository.removeTaskReminders(
                            indexes = it.map { p -> p.first }
                        )
                    }

                alarmManager.cancel(sequentialTaskNotifierPendingIntent(context, activeStatusId))
            }
        }
    }
}