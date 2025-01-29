package com.nrr.summary.worker

import android.Manifest
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.nrr.data.repository.SummaryRepository
import com.nrr.model.TaskPeriod
import com.nrr.notification.util.MAIN_ACTIVITY_NAME
import com.nrr.notification.util.createNotification
import com.nrr.summary.util.SummaryDictionary
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.datetime.Clock
import java.time.YearMonth
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

private const val SUMMARY_NOTIFICATION_ID = 0

@HiltWorker
internal class SummaryGenerationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val summaryRepository: SummaryRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val taskPeriod = inputData.getInt(TASK_PERIOD_ORDINAL_INPUT_KEY, -1)
            .takeIf { it >= 0 }
            ?.let { TaskPeriod.entries[it] } ?: return Result.failure()

        val priorDay = Clock.System.now() - 1.days
        val summary = summaryRepository.createSummary(taskPeriod, priorDay)
        // TODO auto delete active tasks on given period

        if (
            context
                .checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) showNotification(taskPeriod)

        return if (summary != null) Result.success(
            workDataOf(
                CREATED_START_DATE_OUTPUT_KEY to summary.startDate.toEpochMilliseconds(),
                CREATED_END_DATE_OUTPUT_KEY to summary.endDate.toEpochMilliseconds()
            )
        ) else Result.failure()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(period: TaskPeriod) {
        val notification = context.createNotification {
            setSmallIcon(com.nrr.notification.R.drawable.app_icon_small)
            setContentTitle(notificationTitle(period))
            setContentText(notificationContent(period))
            setContentIntent(notificationIntent())
            setAutoCancel(true)
        }

        NotificationManagerCompat.from(context)
            .notify(
                SUMMARY_NOTIFICATION_ID,
                notification
            )
    }

    private fun notificationTitle(period: TaskPeriod) = context.getString(
        when (period) {
            TaskPeriod.DAY -> SummaryDictionary.dailySummaryTitle
            TaskPeriod.WEEK -> SummaryDictionary.weeklySummaryTitle
            TaskPeriod.MONTH -> SummaryDictionary.monthlySummaryTitle
        }
    )

    private fun notificationContent(period: TaskPeriod) = context.getString(
        when (period) {
            TaskPeriod.DAY -> SummaryDictionary.dailySummaryContent
            TaskPeriod.WEEK -> SummaryDictionary.weeklySummaryContent
            TaskPeriod.MONTH -> SummaryDictionary.monthlySummaryContent
        }
    )

    private fun notificationIntent() = PendingIntent.getActivity(
        context,
        SUMMARY_NOTIFICATION_ID,
        Intent().apply {
            action = Intent.ACTION_VIEW
            component = ComponentName(
                context.packageName,
                MAIN_ACTIVITY_NAME
            )
        },
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    companion object {
        const val TASK_PERIOD_ORDINAL_INPUT_KEY = "task_period_ordinal"
        const val CREATED_START_DATE_OUTPUT_KEY = "created_start_date"
        const val CREATED_END_DATE_OUTPUT_KEY = "created_end_date"

        fun periodicSummaryGenerationWorkRequest(
            taskPeriod: TaskPeriod,
            builder: (PeriodicWorkRequest.Builder.() -> Unit)? = null
        ) = PeriodicWorkRequestBuilder<SummaryGenerationWorker>(
            repeatInterval = when (taskPeriod) {
                TaskPeriod.DAY -> 1.days
                TaskPeriod.WEEK -> 7.days
                TaskPeriod.MONTH -> YearMonth.now().lengthOfMonth().days
            }.toJavaDuration()
        )
            .setInputData(
                workDataOf(TASK_PERIOD_ORDINAL_INPUT_KEY to taskPeriod.ordinal)
            )
            .apply {
                builder?.invoke(this)
            }
            .build()

        fun WorkManager.enqueuePeriodicSummaryGeneration(
            uniqueWorkName: String,
            taskPeriod: TaskPeriod,
            builder: (PeriodicWorkRequest.Builder.() -> Unit)? = null
        ) = enqueueUniquePeriodicWork(
            uniqueWorkName = uniqueWorkName,
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
            request = periodicSummaryGenerationWorkRequest(
                taskPeriod = taskPeriod,
                builder = builder
            )
        )
    }
}