package com.nrr.notification.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.nrr.data.repository.TaskRepository
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

const val REMIND_LATER_NOTIFIER_ACTION = "com.nrr.notification.SIMPLE_TASK_NOTIFIER"

@AndroidEntryPoint
class SimpleTaskNotifierReceiver : BroadcastReceiver() {
    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val activeStatusId = intent.getIntExtra(ACTIVE_STATUS_ID, -1)
        val reminderTypeOrdinal = intent.getIntExtra(REMINDER_TYPE_ORDINAL, -1)

        if (activeStatusId == -1 || reminderTypeOrdinal == -1) return

        CoroutineScope(Dispatchers.Main).launch {
            val reminderType = ReminderType.entries[reminderTypeOrdinal]
            val task = taskRepository.getActiveTasksByIds(listOf(activeStatusId.toLong()))
                .firstOrNull()?.firstOrNull() ?: return@launch

            val taskWithReminder = TaskWithReminder(task.toFiltered(), reminderType)

            with(taskWithReminder.task) {
                if (completed || !set) {
                    return@launch
                }
            }

            notifyScheduledTask(
                context = context,
                task = task,
                reminderType = taskWithReminder.reminderType
            )
        }
    }

    companion object {
        const val ACTIVE_STATUS_ID = "activeStatusId"
        const val REMINDER_TYPE_ORDINAL = "reminderTypeOrdinal"
    }
}

internal fun simpleTaskNotifierReceiverPendingIntent(
    context: Context,
    activeStatusId: Int,
    reminderType: ReminderType
) = PendingIntent.getBroadcast(
    context,
    activeStatusId,
    Intent(context, SimpleTaskNotifierReceiver::class.java).apply {
        action = REMIND_LATER_NOTIFIER_ACTION
        putExtra(SimpleTaskNotifierReceiver.ACTIVE_STATUS_ID, activeStatusId)
        putExtra(SimpleTaskNotifierReceiver.REMINDER_TYPE_ORDINAL, reminderType.ordinal)
    },
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
)