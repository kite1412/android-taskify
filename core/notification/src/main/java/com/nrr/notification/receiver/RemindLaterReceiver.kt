package com.nrr.notification.receiver

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nrr.notification.model.ReminderType
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.minutes

const val REMIND_LATER_ACTION = "com.nrr.notification.REMIND_LATER"

class RemindLaterReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val id = intent.getIntExtra(ScheduledTaskReceiver.DATA_KEY, 0)
        val reminderType = intent.getIntExtra(ScheduledTaskReceiver.REMINDER_TYPE_ORDINAL_KEY, -1)
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .cancel(id)

        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
            .setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                (Clock.System.now() + 5.minutes).toEpochMilliseconds(),
                scheduledTaskReceiverPendingIntent(
                    context = context,
                    activeStatusId = id,
                    reminderType = reminderType.takeIf { it != -1 }
                        ?.let { ReminderType.entries[it] }
                )
            )
    }
}