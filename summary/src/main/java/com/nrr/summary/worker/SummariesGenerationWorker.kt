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
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.nrr.data.repository.SummaryRepository
import com.nrr.data.repository.TaskRepository
import com.nrr.model.ActiveStatus
import com.nrr.model.Summary
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.model.toLocalDateTime
import com.nrr.notification.ScheduledTaskNotifier
import com.nrr.summary.util.showNotification
import com.nrr.summary.worker.SummariesGenerationWorker.Companion.enqueuePeriodSummariesGeneration
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.time.YearMonth
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.time.Duration.Companion.days

/**
 * This worker generates summaries for all available [TaskPeriod]s
 * and schedule [ActiveStatus]s with isDefault set to true for the next period.
 *
 * The work execution happens every day with [TaskPeriod.DAY] summary
 * always generated (if any), and [TaskPeriod.WEEK] & [TaskPeriod.MONTH]
 * summaries only generated when their respective period has ended.
 *
 * The [WorkRequest] of this worker expected to be a [PeriodicWorkRequest]
 * with interval of 1 day and [PeriodicWorkRequest.Builder.setInitialDelay]
 * set to the start of the next day (00:00) based on the system's current time
 * as the scheduling logic is to generate summaries based on each previous [TaskPeriod].
 *
 * The [WorkRequest] of this worker can be enqueued as a [PeriodicWorkRequest]
 * using [SummariesGenerationWorker.enqueuePeriodSummariesGeneration] with the initial delay
 * configured as explained above to align with the scheduling logic.
 */
@HiltWorker
internal class SummariesGenerationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val summaryRepository: SummaryRepository,
    private val taskRepository: TaskRepository,
    private val scheduledTaskNotifier: ScheduledTaskNotifier
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

    private suspend fun generateTodaySummary() = tryGenerateSummary(
        period = TaskPeriod.DAY,
        timeConstraint = { true }
    )

    private suspend fun tryGenerateWeekSummary() = tryGenerateSummary(
        period = TaskPeriod.WEEK,
        timeConstraint = {
            toLocalDateTime().dayOfWeek.value == 1
        }
    )

    private suspend fun tryGenerateMonthSummary() = tryGenerateSummary(
        period = TaskPeriod.MONTH,
        timeConstraint = {
            toLocalDateTime().dayOfMonth == 1
        }
    )

    private suspend fun tryGenerateSummary(
        period: TaskPeriod,
        timeConstraint: Instant.() -> Boolean
    ): Summary? {
        val now = Clock.System.now()
        val constraintMet = now.timeConstraint()

        if (!constraintMet) return null

        return summaryRepository.createSummary(
            period = period,
            startDate = now - 1.days
        )?.also {
            taskRepository.getActiveTasksByPeriod(period).firstOrNull()?.let { l ->
                val (defaults, nonDefaults) = l.partition {
                    it.activeStatuses.first().isDefault
                }

                taskRepository.deleteActiveTasks(nonDefaults)

                if (defaults.isEmpty()) return@also

                val nextPeriodTasks = defaults.toNextPeriod(period)
                taskRepository.saveActiveTasks(nextPeriodTasks)
                scheduledTaskNotifier.scheduleReminders(nextPeriodTasks, period)
            }
        }
    }

    private fun List<Task>.toNextPeriod(
        period: TaskPeriod
    ) = map {
        it.copy(
            activeStatuses = it.activeStatuses.map { s ->
                s.copy(
                    startDate = s.startDate.toNextDate(period),
                    dueDate = s.dueDate?.toNextDate(period),
                    completedAt = null,
                    isSet = true
                )
            }
        )
    }

    private fun Instant.toNextDate(period: TaskPeriod): Instant =
        when (period) {
            TaskPeriod.DAY -> this + 1.days
            TaskPeriod.WEEK -> this + 7.days
            TaskPeriod.MONTH -> {
                val yearMonth = YearMonth.now()
                val localDateTime = toLocalDateTime()
                val thisMonthTotalDays = yearMonth.lengthOfMonth()

                LocalDateTime(
                    year = yearMonth.year,
                    monthNumber = yearMonth.monthValue,
                    dayOfMonth = min(localDateTime.dayOfMonth, thisMonthTotalDays),
                    hour = localDateTime.hour,
                    minute = localDateTime.minute,
                    second = localDateTime.second
                ).toInstant(TimeZone.currentSystemDefault())
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