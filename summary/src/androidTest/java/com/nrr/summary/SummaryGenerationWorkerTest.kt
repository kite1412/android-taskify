package com.nrr.summary

import android.content.Context
import androidx.concurrent.futures.await
import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.WorkManagerTestInitHelper
import androidx.work.workDataOf
import com.nrr.data.repository.SummaryRepository
import com.nrr.data.util.getEndDate
import com.nrr.data.util.getStartDate
import com.nrr.model.Summary
import com.nrr.model.TaskPeriod
import com.nrr.summary.worker.SummaryGenerationWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.days

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
                .build()
        )
    }

    @Test
    fun doWork_isSuccess() = runTest {
        val workManager = WorkManager.getInstance(context)
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

        while (true) {
            val workInfo = workManager.getWorkInfosForUniqueWork("test")
                .await()
                .first()
            if (workInfo.state == WorkInfo.State.SUCCEEDED) break
            delay(1000)
        }

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
            expectedStartDate == startDate &&
                    expectedEndDate == endDate
        )
    }
}