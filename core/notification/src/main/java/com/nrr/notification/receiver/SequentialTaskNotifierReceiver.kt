package com.nrr.notification.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.nrr.data.repository.TaskRepository
import com.nrr.data.repository.UserDataRepository
import com.nrr.model.ReminderType
import com.nrr.notification.model.TaskWithReminder
import com.nrr.notification.model.toFiltered
import com.nrr.notification.util.notifyScheduledTask
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

const val DEEP_LINK_ACTIVE_TASK_ID_KEY = "taskId"
const val DEEP_LINK_PERIOD_ORDINAL_KEY = "periodOrdinal"
const val DEEP_LINK_SCHEME_AND_HOST = "com.nrr.taskify://plan"
const val DEEP_LINK_URI_PATTERN = "$DEEP_LINK_SCHEME_AND_HOST/{$DEEP_LINK_PERIOD_ORDINAL_KEY}/{$DEEP_LINK_ACTIVE_TASK_ID_KEY}"
const val SEQUENTIAL_TASK_NOTIFIER_ACTION = "com.nrr.notification.SEQUENTIAL_TASK_NOTIFIER"

@AndroidEntryPoint
class SequentialTaskNotifierReceiver : BroadcastReceiver() {
    @Inject
    lateinit var taskRepository: TaskRepository

    @Inject
    lateinit var userDataRepository: UserDataRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val id = intent.getIntExtra(DATA_KEY, 0)
        val reminderType = intent.getIntExtra(REMINDER_TYPE_ORDINAL_KEY, -1)
            .takeIf { it != -1 }
            ?.let {
                ReminderType.entries[it]
            } ?: ReminderType.START
        
        CoroutineScope(Dispatchers.Main).launch {
            val task = id.takeIf { it > 0 }?.let {
                taskRepository.getActiveTasksByIds(listOf(it.toLong()))
                    .firstOrNull()?.firstOrNull() ?: return@launch
            } ?: return@launch
            val taskWithReminder = TaskWithReminder(task.toFiltered(), reminderType)
            val removeFirstAndSendBroadcast = suspend {
                userDataRepository.removeTaskReminders(listOf(0))
                context.sendBroadcast(sequentialTaskSchedulerIntent(context))
            }

            with(taskWithReminder.task) {
                if (completed || !set) {
                    removeFirstAndSendBroadcast()
                    return@launch
                }
            }

            notifyScheduledTask(
                context = context,
                task = task,
                reminderType = taskWithReminder.reminderType
            )

            removeFirstAndSendBroadcast()
        }
    }

    companion object {
        const val DATA_KEY = "activeStatusId"
        const val REMINDER_TYPE_ORDINAL_KEY = "reminderTypeOrdinal"
    }
}

internal fun sequentialTaskNotifierPendingIntent(
    context: Context,
    activeStatusId: Int,
    reminderType: ReminderType? = null
) = PendingIntent.getBroadcast(
    context,
    activeStatusId,
    Intent(context, SequentialTaskNotifierReceiver::class.java).apply {
        action = SEQUENTIAL_TASK_NOTIFIER_ACTION
        putExtra(SequentialTaskNotifierReceiver.DATA_KEY, activeStatusId)
        reminderType?.let {
            putExtra(SequentialTaskNotifierReceiver.REMINDER_TYPE_ORDINAL_KEY, it.ordinal)
        }
    },
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
)