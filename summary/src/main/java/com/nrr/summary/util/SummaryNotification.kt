package com.nrr.summary.util

import android.Manifest
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationManagerCompat
import com.nrr.model.TaskPeriod
import com.nrr.notification.util.MAIN_ACTIVITY_NAME
import com.nrr.notification.util.createNotification
import com.nrr.summary.DefaultSummariesGenerationScheduler


@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
internal fun Context.showNotification(period: TaskPeriod) {
    val notification = createNotification {
        setSmallIcon(com.nrr.notification.R.drawable.app_icon_small)
        setContentTitle(notificationTitle(period))
        setContentText(notificationContent(period))
        setContentIntent(notificationIntent(period))
        setAutoCancel(true)
    }

    NotificationManagerCompat.from(this)
        .notify(
            DefaultSummariesGenerationScheduler.getPeriodId(period),
            notification
        )
}

private fun Context.notificationTitle(period: TaskPeriod) = getString(
    when (period) {
        TaskPeriod.DAY -> SummaryDictionary.dailySummaryTitle
        TaskPeriod.WEEK -> SummaryDictionary.weeklySummaryTitle
        TaskPeriod.MONTH -> SummaryDictionary.monthlySummaryTitle
    }
)

private fun Context.notificationContent(period: TaskPeriod) = getString(
    when (period) {
        TaskPeriod.DAY -> SummaryDictionary.dailySummaryContent
        TaskPeriod.WEEK -> SummaryDictionary.weeklySummaryContent
        TaskPeriod.MONTH -> SummaryDictionary.monthlySummaryContent
    }
)

private fun Context.notificationIntent(period: TaskPeriod) = PendingIntent.getActivity(
    this,
    DefaultSummariesGenerationScheduler.getPeriodId(period),
    Intent().apply {
        action = Intent.ACTION_VIEW
        component = ComponentName(
            packageName,
            MAIN_ACTIVITY_NAME
        )
    },
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
)