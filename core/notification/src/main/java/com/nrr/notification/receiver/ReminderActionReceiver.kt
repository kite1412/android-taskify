package com.nrr.notification.receiver

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nrr.data.repository.TaskRepository
import com.nrr.model.ReminderType
import com.nrr.notification.model.ReminderAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

const val REMINDER_ACTION_ACTION = "com.nrr.notification.REMINDER_ACTION"

@AndroidEntryPoint
class ReminderActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val actionOrdinal = intent.getIntExtra(REMINDER_ACTION_ORDINAL_KEY, -1)
        val id = intent.getIntExtra(ACTIVE_STATUS_ID_KEY, 0)
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .cancel(id)

        if (actionOrdinal == -1 || id == 0) return

        when (ReminderAction.entries[actionOrdinal]) {
            ReminderAction.COMPLETE -> CoroutineScope(Dispatchers.Default).launch {
                taskRepository.getActiveTasksByIds(listOf(id.toLong()))
                    .firstOrNull()
                    ?.firstOrNull()
                    ?.let {
                        taskRepository.setActiveTaskAsCompleted(it)
                    }
            }
            ReminderAction.REMIND_LATER -> {
                val reminderTypeOrdinal =
                    intent.getIntExtra(REMINDER_TYPE_ORDINAL_KEY, -1)
                (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
                    .setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        (Clock.System.now() + 5.minutes).toEpochMilliseconds(),
                        simpleTaskNotifierReceiverPendingIntent(
                            context = context,
                            activeStatusId = id,
                            reminderType = ReminderType.entries[reminderTypeOrdinal]
                        )
                    )
            }
        }
    }

    internal companion object {
        const val REMINDER_ACTION_ORDINAL_KEY = "reminderActionOrdinal"
        const val ACTIVE_STATUS_ID_KEY = "activeStatusId"
        const val REMINDER_TYPE_ORDINAL_KEY = "reminderTypeOrdinal"
    }
}

internal fun Context.reminderActionPendingIntent(
    reminderActionOrdinal: Int,
    activeStatusId: Int,
    reminderTypeOrdinal: Int
) = PendingIntent.getBroadcast(
    this,
    activeStatusId * (reminderActionOrdinal + 1),
    Intent(this, ReminderActionReceiver::class.java).apply {
        action = REMINDER_ACTION_ACTION
        putExtra(ReminderActionReceiver.REMINDER_ACTION_ORDINAL_KEY, reminderActionOrdinal)
        putExtra(ReminderActionReceiver.ACTIVE_STATUS_ID_KEY, activeStatusId)
        putExtra(ReminderActionReceiver.REMINDER_TYPE_ORDINAL_KEY, reminderTypeOrdinal)
    },
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
)