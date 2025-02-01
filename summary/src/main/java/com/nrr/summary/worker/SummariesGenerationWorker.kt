package com.nrr.summary.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.nrr.data.repository.SummaryRepository
import com.nrr.data.repository.TaskRepository
import com.nrr.model.TaskPeriod
import com.nrr.model.toLocalDateTime
import com.nrr.summary.util.showNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.datetime.Clock
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.days

@HiltWorker
class SummariesGenerationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val summaryRepository: SummaryRepository,
    private val taskRepository: TaskRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val res = generateSummaries().onEach { p ->
            if (
                context.checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) context.showNotification(p)
        }

        return Result.success(
            workDataOf(
                GENERATED_PERIODS_ORDINAL_KEY to res.map {
                    it.ordinal
                }
            )
        )
    }

    private suspend fun generateSummaries() = listOfNotNull(
        generateTodaySummary(),
        tryGenerateWeekSummary(),
        tryGenerateMonthSummary()
    ).map { it.period }

    private suspend fun generateTodaySummary() =
        with(TaskPeriod.DAY) {
            summaryRepository.createSummary(
                period = this,
                startDate = Clock.System.now() - 1.days
            )?.also {
                taskRepository.deleteActiveTasksByPeriod(this)
            }
        }

    private suspend fun tryGenerateWeekSummary() =
        with(Clock.System.now()) {
            val week = TaskPeriod.WEEK

            toLocalDateTime().takeIf { it.dayOfWeek.value == 1 }
                ?.let {
                    summaryRepository.createSummary(
                        period = week,
                        startDate = this - 1.days
                    )
                }
                ?.also {
                    taskRepository.deleteActiveTasksByPeriod(week)
                }
        }

    private suspend fun tryGenerateMonthSummary() =
        with(Clock.System.now()) {
            val month = TaskPeriod.MONTH

            toLocalDateTime().takeIf { it.dayOfMonth == 1 }
                ?.let {
                    summaryRepository.createSummary(
                        period = month,
                        startDate = this - 1.days
                    )
                }
                ?.also {
                    taskRepository.deleteActiveTasksByPeriod(month)
                }
        }

    companion object {
        const val GENERATED_PERIODS_ORDINAL_KEY = "generated_period_ordinal"

        private fun periodicSummariesGenerationWorkRequest(
            builder: (PeriodicWorkRequest.Builder.() -> Unit)? = null
        ) = PeriodicWorkRequestBuilder<SummariesGenerationWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .apply {
                builder?.invoke(this)
            }
            .build()

        internal fun WorkManager.enqueuePeriodSummariesGeneration(
            uniqueWorkName: String,
            builder: (PeriodicWorkRequest.Builder.() -> Unit)? = null
        ) = enqueueUniquePeriodicWork(
            uniqueWorkName = uniqueWorkName,
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
            request = periodicSummariesGenerationWorkRequest(
                builder = builder
            )
        )
    }
}