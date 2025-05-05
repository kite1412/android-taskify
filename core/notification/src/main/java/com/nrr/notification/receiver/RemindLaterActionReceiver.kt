package com.nrr.notification.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nrr.model.ReminderType
import com.nrr.notification.util.cancelNotification
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.minutes

const val REMIND_LATER_ACTION_ACTION = "com.nrr.notification.REMIND_LATER_ACTION_ACTION"

class RemindLaterActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val id = intent.getIntExtra(ACTIVE_STATUS_ID_KEY, 0)
        context.cancelNotification(id)
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

    internal companion object {
        const val ACTIVE_STATUS_ID_KEY = "activeStatusId"
        const val REMINDER_TYPE_ORDINAL_KEY = "reminderTypeOrdinal"
    }
}

internal fun Context.reminderActionPendingIntent(
    activeStatusId: Int,
    reminderTypeOrdinal: Int
) = PendingIntent.getBroadcast(
    this,
    activeStatusId,
    Intent(this, RemindLaterActionReceiver::class.java).apply {
        action = REMIND_LATER_ACTION_ACTION
        putExtra(RemindLaterActionReceiver.ACTIVE_STATUS_ID_KEY, activeStatusId)
        putExtra(RemindLaterActionReceiver.REMINDER_TYPE_ORDINAL_KEY, reminderTypeOrdinal)
    },
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
)