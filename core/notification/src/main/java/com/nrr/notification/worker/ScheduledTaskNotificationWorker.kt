package com.nrr.notification.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.nrr.model.ReminderType
import com.nrr.notification.R
import com.nrr.notification.model.TaskWithReminder
import com.nrr.notification.util.createNotification
import com.nrr.notification.util.getContent
import com.nrr.notification.util.getTitle
import com.nrr.notification.util.gson

private const val NOTIFICATION_ID = 1

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
            else -> return Result.failure()
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