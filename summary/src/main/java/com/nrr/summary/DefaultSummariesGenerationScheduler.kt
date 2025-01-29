package com.nrr.summary

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.WorkManager
import com.nrr.data.util.getEndDate
import com.nrr.model.TaskPeriod
import com.nrr.summary.worker.SummaryGenerationWorker.Companion.enqueuePeriodicSummaryGeneration
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.datetime.Clock
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

/**
 * Default implementation of [SummariesGenerationScheduler] using [WorkManager].
 */
internal class DefaultSummariesGenerationScheduler @Inject constructor(
    @ApplicationContext context: Context
) : SummariesGenerationScheduler {
    private val workManager = WorkManager.getInstance(context)

    override fun scheduleSummariesGeneration() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            schedulePeriodicSummaryGeneration(
                period = TaskPeriod.DAY,
                workName = DAILY_SUMMARY_WORK_NAME
            )
            schedulePeriodicSummaryGeneration(
                period = TaskPeriod.WEEK,
                workName = WEEKLY_SUMMARY_WORK_NAME
            )
            schedulePeriodicSummaryGeneration(
                period = TaskPeriod.MONTH,
                workName = MONTHLY_SUMMARY_WORK_NAME
            )
        } // TODO handle api level 24-25
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun schedulePeriodicSummaryGeneration(
        period: TaskPeriod,
        workName: String
    ) {
        workManager.enqueuePeriodicSummaryGeneration(
            uniqueWorkName = workName,
            taskPeriod = period,
            builder = {
                setInitialDelay(initialExecutionDelay(period).toJavaDuration())
            }
        )
    }

    companion object {
        private const val DAILY_SUMMARY_WORK_NAME = "daily_summary"
        private const val WEEKLY_SUMMARY_WORK_NAME = "weekly_summary"
        private const val MONTHLY_SUMMARY_WORK_NAME = "monthly_summary"

        // expose for testing
        fun initialExecutionDelay(period: TaskPeriod) = with(Clock.System.now()) {
            getEndDate(period) - this + 1.seconds
        }
    }
}