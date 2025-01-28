package com.nrr.data

import com.nrr.data.repository.RoomSummaryRepository
import com.nrr.data.util.normalize
import com.nrr.database.RoomTransactionScope
import com.nrr.database.dao.ActiveTaskDao
import com.nrr.database.dao.ActiveTaskSummaryDao
import com.nrr.database.dao.SummaryGroupDao
import com.nrr.database.entity.ActiveTaskEntity
import com.nrr.database.entity.ActiveTaskSummaryEntity
import com.nrr.database.entity.TaskEntity
import com.nrr.database.entity.toSummary
import com.nrr.database.model.ActiveTask
import com.nrr.model.Summary
import com.nrr.model.TaskPeriod
import com.nrr.model.TaskPriority
import com.nrr.model.TaskType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.Before
import org.junit.Test

class RoomSummaryRepositoryTest {
    private val activeTaskSummaryDao = mockk<ActiveTaskSummaryDao>()
    private val summaryGroupDao = mockk<SummaryGroupDao>()
    private val transactionScope = mockk<RoomTransactionScope>()
    private val activeTaskDao = mockk<ActiveTaskDao>()

    private lateinit var repo: RoomSummaryRepository

    @Before
    fun setUp() {
        repo = RoomSummaryRepository(
            activeTaskSummaryDao = activeTaskSummaryDao,
            summaryGroupDao = summaryGroupDao,
            activeTaskDao = activeTaskDao,
            transactionScope = transactionScope
        )
    }

    private fun answerAll(period: TaskPeriod, tasks: List<ActiveTask>) {
        activeTaskDao_answer_getAllByPeriod(period, tasks)
        val id = summaryGroupDao_answer_insertSummaryGroups()
        activeTaskSummaryDao_answer_insertActiveTaskSummaries(
            tasks.map { it.toSummary(id) }
        )
        roomTransactionScope_answer_invoke()
    }

    private fun activeTaskDao_answer_getAllByPeriod(period: TaskPeriod, tasks: List<ActiveTask>) {
        coEvery { activeTaskDao.getAllByPeriod(period) } returns
                flowOf(tasks)
    }

    private fun summaryGroupDao_answer_insertSummaryGroups(): Long {
        coEvery { summaryGroupDao.insertSummaryGroups(any()) } returns
                listOf(1L)
        return 1L
    }

    private fun activeTaskSummaryDao_answer_insertActiveTaskSummaries(summaries: List<ActiveTaskSummaryEntity>) {
        coEvery { activeTaskSummaryDao.insertActiveTaskSummaries(any()) } returns
                summaries.map { it.id }
    }

    private fun roomTransactionScope_answer_invoke() {
        coEvery { transactionScope.invoke(any<suspend () -> Summary?>()) } coAnswers {
            (it.invocation.args[0] as suspend () -> Summary?)()
        }
    }

    @Test
    fun createSummary_isSuccess() = runTest {
        val period = TaskPeriod.DAY
        val now = Clock.System.now()
        val mockActiveTasks = List(5) {
            val long = it.toLong()
            ActiveTask(
                entity = ActiveTaskEntity(
                    id = long,
                    taskId = long,
                    taskPeriod = period,
                    startDate = now,
                    dueDate = null,
                    completedAt = null,
                    isCompleted = false,
                    taskPriority = TaskPriority.HIGH,
                    reminderSet = true,
                    isSet = true,
                    isDefault = false
                ),
                task = TaskEntity(
                    id = long,
                    title = "title",
                    description = null,
                    createdAt = now,
                    updateAt = now,
                    taskType = TaskType.PERSONAL
                )
            )
        }

        answerAll(period, mockActiveTasks)

        val summary = repo.createSummary(period, now)
        val sd = now.normalize()
        val ed = now.normalize()

        print(sd)
        assert(
            summary != null
                    && summary.tasks.size == mockActiveTasks.size
                    && summary.startDate == sd
                    && summary.endDate == ed
                    && summary.id == 1L
        )
    }

    @Test
    fun createSummary_isFailure() = runTest {
        val period = TaskPeriod.DAY
        val now = Clock.System.now()

        answerAll(period, listOf())

        val summary = repo.createSummary(period, now)

        assert(summary == null)
    }
}