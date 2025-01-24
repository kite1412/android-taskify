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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
                var index = 0
                if (startDate > now) {
                    add(index, reminders.first.copy(date = startDate))
                    index = 1
                }
                reminders.second?.let {
                    val endDate = reminders.second!!.date - notificationOffset
                    if (endDate > now) add(index, reminders.second!!.copy(date = endDate))
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

        override fun cancelReminder(activeTask: Task) {
            val activeStatusId = activeTask.activeStatuses.firstOrNull()?.id?.toInt()
                ?: return

            alarmManager.cancel(sequentialTaskNotifierPendingIntent(context, activeStatusId))
        }
    }
}