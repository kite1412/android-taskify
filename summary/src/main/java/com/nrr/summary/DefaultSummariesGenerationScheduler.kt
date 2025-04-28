package com.nrr.summary

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.WorkManager
import com.nrr.model.TaskPeriod
import com.nrr.model.getEndDate
import com.nrr.summary.receiver.SUMMARIES_GENERATION_ACTION
import com.nrr.summary.receiver.SUMMARY_GENERATION_ACTION
import com.nrr.summary.receiver.SummariesGenerationReceiver
import com.nrr.summary.receiver.SummaryGenerationReceiver
import com.nrr.summary.worker.SummariesGenerationWorker.Companion.enqueuePeriodSummariesGeneration
import com.nrr.summary.worker.SummaryGenerationWorker.Companion.enqueuePeriodicSummaryGeneration
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

/**
 * Default implementation of [SummariesGenerationScheduler] using [WorkManager].
 */
internal class DefaultSummariesGenerationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : SummariesGenerationScheduler {
    private val workManager = WorkManager.getInstance(context)
    private val scheduler: Scheduler = DailySchedule()

    override fun scheduleSummariesGeneration() =
        scheduler.schedule()

    private interface Scheduler {
        fun schedule()
    }

    @Deprecated(
        message = """
            Enqueue 3 different periodic WorkManager works for the same task
            and may potentially cause overlaps between summary generations, use a safer option
            such as DailySchedule instead.
        """,
        replaceWith = ReplaceWith("DailySchedule")
    )
    private inner class ScheduleByPeriod : Scheduler {
        override fun schedule() {
            CoroutineScope(Dispatchers.Default).launch {
                for (period in TaskPeriod.entries) {
                    workManager
                        .getWorkInfosForUniqueWork(getUniqueWorkName(period))
                        .get()
                        .takeIf { it.isEmpty() } ?: continue

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) schedulePeriodicSummaryGeneration(
                        period = period,
                        workName = getUniqueWorkName(period)
                    ) else scheduleWithAlarmManager(period)
                }
            }
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

        private fun scheduleWithAlarmManager(period: TaskPeriod) {
            val pendingIntent = summaryGenerationReceiverPendingIntent(period)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManager.cancel(pendingIntent)
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                (Clock.System.now().getEndDate(period) + 1.seconds).toEpochMilliseconds(),
                pendingIntent
            )
        }

        private fun summaryGenerationReceiverPendingIntent(period: TaskPeriod) = PendingIntent.getBroadcast(
            context,
            getPeriodId(period),
            Intent(context, SummaryGenerationReceiver::class.java).apply {
                action = SUMMARY_GENERATION_ACTION
                putExtra(SummaryGenerationReceiver.TASK_PERIOD_ORDINAL_KEY, period.ordinal)
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private inner class DailySchedule : Scheduler {
        override fun schedule() {
            CoroutineScope(Dispatchers.Default).launch {
                // ensure scheduling only performed by SummariesGenerationWorker
                for (period in TaskPeriod.entries)
                    workManager.cancelUniqueWork(getUniqueWorkName(period))

                workManager
                    .getWorkInfosForUniqueWork(SUMMARIES_GENERATION_WORK_NAME)
                    .get()
                    .takeIf { it.isEmpty() } ?: return@launch

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    scheduleDailySummariesGeneration()
                else scheduleWithAlarmManager()
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun scheduleDailySummariesGeneration() {
            workManager.enqueuePeriodSummariesGeneration(
                uniqueWorkName = SUMMARIES_GENERATION_WORK_NAME,
                builder = {
                    setInitialDelay(
                        duration = initialExecutionDelay(TaskPeriod.DAY)
                            .toJavaDuration()
                    )
                }
            )
        }

        private fun scheduleWithAlarmManager() {
            with(context.getSystemService(Context.ALARM_SERVICE) as AlarmManager) {
                val pendingIntent = summariesGenerationReceiverPendingIntent()
                cancel(pendingIntent)
                setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    (Clock.System.now().getEndDate(TaskPeriod.DAY) + 1.seconds)
                        .toEpochMilliseconds(),
                    pendingIntent
                )
            }
        }

        private fun summariesGenerationReceiverPendingIntent() = PendingIntent.getBroadcast(
            context,
            -4,
            Intent(context, SummariesGenerationReceiver::class.java).apply {
                action = SUMMARIES_GENERATION_ACTION
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    companion object {
        private const val DAILY_SUMMARY_WORK_NAME = "daily_summary"
        private const val WEEKLY_SUMMARY_WORK_NAME = "weekly_summary"
        private const val MONTHLY_SUMMARY_WORK_NAME = "monthly_summary"
        const val SUMMARIES_GENERATION_WORK_NAME = "summaries_generation"

        fun getUniqueWorkName(period: TaskPeriod) = when (period) {
            TaskPeriod.DAY -> DAILY_SUMMARY_WORK_NAME
            TaskPeriod.WEEK -> WEEKLY_SUMMARY_WORK_NAME
            TaskPeriod.MONTH -> MONTHLY_SUMMARY_WORK_NAME
        }

        // expose for testing
        fun initialExecutionDelay(period: TaskPeriod) = with(Clock.System.now()) {
            getEndDate(period) - this + 1.seconds
        }

        fun getPeriodId(period: TaskPeriod) = when (period) {
            TaskPeriod.DAY -> -1
            TaskPeriod.WEEK -> -2
            TaskPeriod.MONTH -> -3
        }
    }
}