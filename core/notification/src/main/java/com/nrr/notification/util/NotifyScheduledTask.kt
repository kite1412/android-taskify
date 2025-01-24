package com.nrr.notification.util

import android.Manifest
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.nrr.model.ReminderType
import com.nrr.model.Task
import com.nrr.notification.R
import com.nrr.notification.model.toFiltered
import com.nrr.notification.receiver.DEEP_LINK_SCHEME_AND_HOST
import com.nrr.notification.receiver.REMIND_LATER_SCHEDULER_ACTION
import com.nrr.notification.receiver.RemindLaterSchedulerReceiver
import com.nrr.notification.receiver.SequentialTaskNotifierReceiver.Companion.DATA_KEY
import com.nrr.notification.receiver.SequentialTaskNotifierReceiver.Companion.REMINDER_TYPE_ORDINAL_KEY
import kotlinx.datetime.Clock

private const val MAIN_ACTIVITY_NAME = "com.nrr.taskify.MainActivity"

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
internal fun notifyScheduledTask(
    context: Context,
    task: Task,
    reminderType: ReminderType
) {
    val taskFiltered = task.toFiltered()
    val notification = context.createNotification {
        val now = Clock.System.now()
        val overdue = now > when (reminderType) {
            ReminderType.START -> taskFiltered.startDate
            ReminderType.END -> taskFiltered.dueDate ?: now
        }
        val title = context.getTitle(
            type = reminderType,
            overdue = overdue
        )
        val content = context.getContent(
            task = taskFiltered,
            type = reminderType,
            overdue = overdue
        )

        setContentTitle(title)
        setContentText(content)
        setSmallIcon(R.drawable.app_icon_small)
        setContentIntent(notificationContentIntentNotifier(context, task))
        addAction(
            0,
            context.getString(NotificationDictionary.remindLater),
            context.remindLaterSchedulerPendingIntent(
                activeStatusId = taskFiltered.id.toInt(),
                reminderTypeOrdinal = ReminderType.valueOf(reminderType.name).ordinal
            )
        )
        setAutoCancel(true)
    }
    NotificationManagerCompat.from(context)
        .notify(
            taskFiltered.id.toInt(),
            notification
        )
}

private fun notificationContentIntentNotifier(
    context: Context,
    task: Task
) = PendingIntent.getActivity(
    context,
    0,
    Intent().apply {
        action = Intent.ACTION_VIEW
        data = task.toDeepLinkUri()
        component = ComponentName(
            context.packageName,
            MAIN_ACTIVITY_NAME
        )
    },
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
)

private fun Context.remindLaterSchedulerPendingIntent(
    activeStatusId: Int,
    reminderTypeOrdinal: Int
) = PendingIntent.getBroadcast(
    this,
    0,
    Intent(this, RemindLaterSchedulerReceiver::class.java).apply {
        action = REMIND_LATER_SCHEDULER_ACTION
        putExtra(DATA_KEY, activeStatusId)
        putExtra(REMINDER_TYPE_ORDINAL_KEY, reminderTypeOrdinal)
    },
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
)

private fun Task.toDeepLinkUri() = with(activeStatuses.first()) {
    "$DEEP_LINK_SCHEME_AND_HOST/${period.ordinal}/$id".toUri()
}