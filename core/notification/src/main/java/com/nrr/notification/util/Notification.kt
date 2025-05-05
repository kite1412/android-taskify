package com.nrr.notification.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nrr.model.ReminderType
import com.nrr.model.toTimeString
import com.nrr.notification.model.TaskFiltered

private const val CHANNEL_ID = "1"

fun Context.createNotification(
    block: NotificationCompat.Builder.() -> Unit
): Notification {
    ensureNotificationChannelExists()

    return NotificationCompat.Builder(
        this,
        CHANNEL_ID
    )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .apply(block)
        .build()
}

fun Context.cancelNotification(id: Int) {
    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        .cancel(id)
}

private fun Context.ensureNotificationChannelExists() {
    if (VERSION.SDK_INT < VERSION_CODES.O) return

    val channel = NotificationChannel(
        CHANNEL_ID,
        getString(NotificationDictionary.channelName),
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = getString(NotificationDictionary.channelDesc)
    }

    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

internal fun Context.getTitle(
    type: ReminderType,
    overdue: Boolean = false
) = when (type) {
    ReminderType.START -> getString(
        if (!overdue) NotificationDictionary.taskStartTitle
        else NotificationDictionary.taskStartTitleOverdue
    )
    ReminderType.END -> getString(
        if (!overdue) NotificationDictionary.taskEndTitle
        else NotificationDictionary.taskEndTitleOverdue
    )
}

internal fun Context.getContent(
    task: TaskFiltered,
    type: ReminderType,
    overdue: Boolean = false
) = when (type) {
    ReminderType.START -> getString(
        if (!overdue) NotificationDictionary.taskStartContent
        else NotificationDictionary.taskStartContentOverdue,
        task.title,
        task.startDate.toTimeString()
    )
    ReminderType.END -> getString(
        if (!overdue) NotificationDictionary.taskEndContent
        else NotificationDictionary.taskEndContentOverdue,
        task.title,
        task.dueDate?.toTimeString()
    )
}