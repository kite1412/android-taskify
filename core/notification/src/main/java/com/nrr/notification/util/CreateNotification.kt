package com.nrr.notification.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

private const val CHANNEL_ID = "1"

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

internal fun Context.createNotification(
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