package com.nrr.summary.worker

import android.content.Context
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
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.datetime.Clock
import java.time.YearMonth
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

@HiltWorker
internal class SummaryGenerationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val summaryRepository: SummaryRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val taskPeriod = inputData.getInt(TASK_PERIOD_ORDINAL_INPUT_KEY, -1)
            .takeIf { it >= 0 }
            ?.let { TaskPeriod.entries[it] } ?: return Result.failure()

        val priorDay = Clock.System.now() - 1.days
        val summary = summaryRepository.createSummary(taskPeriod, priorDay)

        return if (summary != null) Result.success(
            workDataOf(
                CREATED_START_DATE_OUTPUT_KEY to summary.startDate.toEpochMilliseconds(),
                CREATED_END_DATE_OUTPUT_KEY to summary.endDate.toEpochMilliseconds()
            )
        ) else Result.failure()
    }

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