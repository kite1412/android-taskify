package com.nrr.notification

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import com.nrr.data.repository.UserDataRepository
import com.nrr.model.ReminderType
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.model.TaskReminder
import com.nrr.notification.model.Result
import com.nrr.notification.model.Result.Fail.Reason
import com.nrr.notification.model.TaskWithReminder
import com.nrr.notification.model.toFiltered
import com.nrr.notification.receiver.sequentialScheduledTaskIntent
import com.nrr.notification.receiver.taskReminderReceiverPendingIntent
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
            val pendingIntent = taskReminderReceiverPendingIntent(context, activeStatusId)

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

            alarmManager.cancel(taskReminderReceiverPendingIntent(context, activeStatusId))
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
            val startIndex = if (startDate > now) if (queue.isNotEmpty()) queue.indexOfFirst {
                startDate <= it.date
            }
                .takeIf { it >= 0 } ?: queue.size else 0 else -1

            var endIndex: Int = -1
            reminders.second?.let { end ->
                val endDate = end.date - notificationOffset
                endIndex = if (endDate > now) if (queue.isNotEmpty()) queue.indexOfFirst {
                    endDate <= it.date
                }
                    .takeIf { it >= 0 } ?: queue.size else 1 else -1
            }
            if (startIndex != -1 || endIndex != -1) userDataRepository.addTaskReminders(
                reminders = mutableMapOf<Int, TaskReminder>().apply {
                    if (startIndex != -1) put(
                        key = startIndex,
                        value = reminders.first.copy(date = startDate)
                    )
                    if (endIndex != -1 && startIndex != endIndex) put(
                        key = endIndex,
                        value = reminders.second!!.copy(
                            date = reminders.second!!.date - notificationOffset
                        )
                    )
                }
            ) else return Result.Fail(Reason.BOTH_DATE_IN_PAST)

            if (startIndex == 0 || endIndex == 0) {
                context.sendBroadcast(sequentialScheduledTaskIntent(context))
            }
            return Result.Success(
                warning = if (startIndex == -1) Result.Success.Warning.START_REMINDER_IN_PAST
                    else if (endIndex == -1) Result.Success.Warning.END_REMINDER_IN_PAST
                    else null
            )
        }

        override fun cancelReminder(activeTask: Task) {
            val activeStatusId = activeTask.activeStatuses.firstOrNull()?.id?.toInt()
                ?: return

            alarmManager.cancel(taskReminderReceiverPendingIntent(context, activeStatusId))
        }
    }
}