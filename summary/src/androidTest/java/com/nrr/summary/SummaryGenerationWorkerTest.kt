package com.nrr.summary

import android.content.Context
import androidx.concurrent.futures.await
import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.WorkManagerTestInitHelper
import androidx.work.workDataOf
import com.nrr.data.repository.SummaryRepository
import com.nrr.model.Summary
import com.nrr.model.TaskPeriod
import com.nrr.model.getEndDate
import com.nrr.model.getStartDate
import com.nrr.summary.worker.SummaryGenerationWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

class SummaryGenerationWorkerTest {
    private lateinit var context: Context
    private val mockSummaryRepository = object : SummaryRepository {
        override fun getSummary(period: TaskPeriod, startDate: Instant): Flow<Summary> {
            TODO("Not yet implemented")
        }

        override fun getSummaries(): Flow<List<Summary>> {
            TODO("Not yet implemented")
        }

        override suspend fun createSummary(period: TaskPeriod, startDate: Instant): Summary? =
            Summary(
                id = 1,
                period = period,
                startDate = startDate.getStartDate(period),
                endDate = startDate.getEndDate(period),
                tasks = emptyList()
            )
    }
    private lateinit var workManager: WorkManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        val factory = object : WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters
            ): ListenableWorker {
                return SummaryGenerationWorker(
                    appContext,
                    workerParameters,
                    mockSummaryRepository
                )
            }
        }

        WorkManagerTestInitHelper.initializeTestWorkManager(
            context,
            Configuration.Builder()
                .setWorkerFactory(factory)
                .setExecutor(Runnable::run)
                .build()
        )
        workManager = WorkManager.getInstance(context)
    }

    @Test
    fun doWork_oneTime_isSuccess() = runTest {
        val now = Clock.System.now()
        val period = TaskPeriod.DAY

        workManager
            .enqueueUniqueWork(
                "test",
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequestBuilder<SummaryGenerationWorker>()
                    .setInputData(
                        workDataOf(
                            SummaryGenerationWorker.TASK_PERIOD_ORDINAL_INPUT_KEY to period.ordinal
                        )
                    )
                    .build()
            )

        val outputData = workManager.getWorkInfosForUniqueWork("test")
            .await()
            .first()
            .outputData

        val startDate = outputData
            .getLong(SummaryGenerationWorker.CREATED_START_DATE_OUTPUT_KEY, -1)

        val endDate = outputData
            .getLong(SummaryGenerationWorker.CREATED_END_DATE_OUTPUT_KEY, -1)

        val expectedStartDate = (now.getStartDate(period) - 1.days).toEpochMilliseconds()
        val expectedEndDate = (now.getEndDate(period) - 1.days).toEpochMilliseconds()

        assert(
            expectedStartDate == startDate
                    && expectedEndDate == endDate
        )
    }

    @Test
    fun doWork_periodic_isSuccess() = runTest {
        val period = TaskPeriod.DAY
        val initialDelay = DefaultSummariesGenerationScheduler
            .initialExecutionDelay(period)
        val req = SummaryGenerationWorker
            .periodicSummaryGenerationWorkRequest(
                taskPeriod = period
            ) {
                setInitialDelay(initialDelay.toJavaDuration())
            }

        workManager.enqueueUniquePeriodicWork(
            "test",
            ExistingPeriodicWorkPolicy.KEEP,
            req
        )

        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)!!

        testDriver.setPeriodDelayMet(req.id)
        testDriver.setAllConstraintsMet(req.id)
        testDriver.setInitialDelayMet(req.id)

        delay(2000)

        val now = Clock.System.now()

        // use direct result of createSummary as PeriodicWorkRequest is almost always in ENQUEUED state,
        // so it's difficult to receive the work info in SUCCEEDED state.
        // ref: https://stackoverflow.com/questions/51476480/workstatus-observer-always-in-enqueued-state
        val summary = mockSummaryRepository.createSummary(period, now)

        // subtraction comes from SummaryGenerationWorker logic
        val startDate = (summary!!.startDate - 1.days).toEpochMilliseconds()
        val endDate = (summary.endDate - 1.days).toEpochMilliseconds()

        val expectedStartDate = (now - 1.days).getStartDate(period).toEpochMilliseconds()
        val expectedEndDate = (now - 1.days).getEndDate(period).toEpochMilliseconds()

        println(initialDelay)
        assert(
            expectedStartDate == startDate
                    && expectedEndDate == endDate
        )
    }
}