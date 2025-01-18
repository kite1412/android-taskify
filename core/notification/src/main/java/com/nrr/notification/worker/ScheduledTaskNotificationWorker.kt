package com.nrr.notification.worker

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.nrr.model.toTimeString
import com.nrr.notification.R
import com.nrr.notification.model.ReminderType
import com.nrr.notification.model.TaskFiltered
import com.nrr.notification.model.TaskWithReminder
import com.nrr.notification.util.NotificationDictionary
import com.nrr.notification.util.gson

private const val CHANNEL_ID = "1"
private const val NOTIFICATION_ID = 1
private const val NOTIFICATION_REQUEST_CODE = 0

// REQUIRE DATA OF TaskWithReminder TYPE
internal class ScheduledTaskNotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = with(context) {
        if (
            ActivityCompat
                .checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
        ) return Result.failure()

        val data = gson {
            inputData.getString(DATA_KEY)?.let {
                fromJson(
                    it,
                    TaskWithReminder::class.java
                )
            }
        }
        if (data == null) return Result.failure()

        val task = data.task
        val reminderType = data.reminderType
        val date = when (reminderType) {
            ReminderType.START -> task.startDate
            ReminderType.END -> task.dueDate
        }
        if (date == null) return Result.failure()

        val notification = createNotification {
            val title = getTitle(reminderType)
            val content = getContent(task, reminderType)

            setSmallIcon(R.drawable.app_icon_small)
            setContentTitle(title)
            setContentText(content)
        }

        NotificationManagerCompat.from(this)
            .notify(
                NOTIFICATION_ID,
                notification
            )
        Result.success()
    }

    private fun Context.getTitle(type: ReminderType) = when (type) {
        ReminderType.START -> getString(NotificationDictionary.taskStartTitle)
        ReminderType.END -> getString(NotificationDictionary.taskEndTitle)
    }

    private fun Context.getContent(
        task: TaskFiltered,
        type: ReminderType
    ) = when (type) {
        ReminderType.START -> getString(
            NotificationDictionary.taskStartContent,
            task.title,
            task.startDate.toTimeString()
        )
        ReminderType.END -> getString(
            NotificationDictionary.taskEndContent,
            task.title,
            task.dueDate?.toTimeString()
        )
    }

    companion object {
        const val DATA_KEY = "taskWithReminder"

        fun workRequest(
            inputData: Data,
            builder: (OneTimeWorkRequest.Builder.() -> Unit)? = null
        ) = OneTimeWorkRequestBuilder<ScheduledTaskNotificationWorker>()
                .setInputData(inputData)
                .apply {
                    builder?.invoke(this)
                }
                .build()
    }
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

private fun Context.createNotification(
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