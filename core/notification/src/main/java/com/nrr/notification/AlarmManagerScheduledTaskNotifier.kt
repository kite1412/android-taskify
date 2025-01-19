package com.nrr.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.nrr.model.Task
import com.nrr.notification.model.ReminderType
import com.nrr.notification.model.Result
import com.nrr.notification.model.Result.Fail.Reason
import com.nrr.notification.model.TaskWithReminder
import com.nrr.notification.model.toFiltered
import com.nrr.notification.receiver.ScheduledTaskReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmManagerScheduledTaskNotifier @Inject constructor(
    @ApplicationContext private val context: Context
) : ScheduledTaskNotifier {
    private val alarmManager = context
        .getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleReminder(task: Task): Result {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            && !alarmManager.canScheduleExactAlarms()
        ) return Result.Fail(Reason.EXACT_ALARM_NOT_PERMITTED)

        val data = TaskWithReminder(task.toFiltered(), ReminderType.START)
        val activeStatusId = data.task.id.toInt()
        val intent = Intent(context, ScheduledTaskReceiver::class.java).apply {
            putExtra(
                DATA_KEY,
                activeStatusId
            )
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            activeStatusId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            data.task.startDate.toEpochMilliseconds(),
            pendingIntent
        )
        return Result.Success(null)
    }

    override fun cancelReminder(activeTask: Task) {
        val activeStatusId = activeTask.activeStatuses.firstOrNull()?.id?.toInt()
            ?: return

        val intent = Intent(context, ScheduledTaskReceiver::class.java).apply {
            putExtra(DATA_KEY, activeStatusId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            activeStatusId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }

    companion object {
        const val DATA_KEY = "taskWithReminder"
    }
}