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

const val SEQUENTIAL_TASK_SCHEDULER_ACTION = "com.nrr.taskify.SEQUENTIAL_TASK_SCHEDULER"

@AndroidEntryPoint
class SequentialTaskSchedulerReceiver : BroadcastReceiver() {
    @Inject
    lateinit var userDataRepository: UserDataRepository

    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        if (intent.action != Intent.ACTION_BOOT_COMPLETED
            && intent.action != SEQUENTIAL_TASK_SCHEDULER_ACTION
        ) return

        val alarmManager = context
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            && !alarmManager.canScheduleExactAlarms()
        ) return

        CoroutineScope(Dispatchers.Main).launch {
            val queue = userDataRepository.userData.first().reminderQueue

            if (queue.isEmpty()) return@launch

            val now = Clock.System.now()

            val (invalidReminder, validReminders) = queue
                .mapIndexed { i, r -> i to r }
                .partition { it.second.date <= now }

            if (invalidReminder.isNotEmpty()) userDataRepository.removeTaskReminders(
                indexes = invalidReminder.map { it.first }
            )

            if (validReminders.isNotEmpty()) {
                val first = validReminders.first().second
                val pendingIntent = sequentialTaskNotifierPendingIntent(
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

//            val firstIndex = queue.indexOfFirst {
//                it.date > now
//            }.takeIf { it >= 0 } ?: run {
//                userDataRepository.removeAllTaskReminders()
//                return@launch
//            }
//
//            if (queue.size != 1) userDataRepository.removeTaskReminders(
//                indexes = (0 until firstIndex).toList()
//            )
//
//            val first = queue[firstIndex]
//
//            val pendingIntent = sequentialTaskNotifierPendingIntent(
//                context = context,
//                activeStatusId = first.activeTaskId.toInt(),
//                reminderType = first.reminderType
//            )
//
//            alarmManager.cancel(pendingIntent)
//            alarmManager.setExactAndAllowWhileIdle(
//                AlarmManager.RTC_WAKEUP,
//                first.date.toEpochMilliseconds(),
//                pendingIntent
//            )
        }
    }
}

internal fun sequentialTaskSchedulerIntent(context: Context) =
    Intent(context, SequentialTaskSchedulerReceiver::class.java).apply {
        action = SEQUENTIAL_TASK_SCHEDULER_ACTION
    }