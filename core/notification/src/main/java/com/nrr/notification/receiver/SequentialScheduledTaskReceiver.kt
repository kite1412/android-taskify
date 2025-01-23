package com.nrr.notification.receiver

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.nrr.data.repository.TaskRepository
import com.nrr.data.repository.UserDataRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

const val SEQUENTIAL_TASK_REMINDER_ACTION = "com.nrr.taskify.SEQUENTIAL_TASK_REMINDER"

@AndroidEntryPoint
class SequentialScheduledTaskReceiver : BroadcastReceiver() {
    @Inject
    lateinit var userDataRepository: UserDataRepository

    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val alarmManager = context
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            && !alarmManager.canScheduleExactAlarms()
        ) return

        CoroutineScope(Dispatchers.Main).launch {
            val queue = userDataRepository.userData.first().reminderQueue

            if (queue.isEmpty()) return@launch

            val now = Clock.System.now()
            val firstIndex = queue.indexOfFirst {
                it.date > now
            }.takeIf { it >= 0 } ?: run {
                userDataRepository.removeAllTaskReminders()
                return@launch
            }

            if (queue.size != 1) userDataRepository.removeTaskReminders(
                indexes = (0 until firstIndex).toList()
            )

            val first = queue[firstIndex]

            val pendingIntent = scheduledTaskReceiverPendingIntent(
                context = context,
                activeStatusId = first.activeTaskId.toInt(),
                reminderType = first.reminderType
            )

            alarmManager.cancel(pendingIntent)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                first.date.toEpochMilliseconds(),
                pendingIntent
            )
        }
    }
}

internal fun sequentialScheduledTaskIntent(context: Context) =
    Intent(context, SequentialScheduledTaskReceiver::class.java).apply {
        action = SEQUENTIAL_TASK_REMINDER_ACTION
    }