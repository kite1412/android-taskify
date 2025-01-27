package com.nrr.data.repository

import com.nrr.database.dao.ActiveTaskSummaryDao
import com.nrr.database.dao.SummaryGroupDao
import com.nrr.model.Summary
import com.nrr.model.TaskPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import javax.inject.Inject

internal class RoomSummaryRepository @Inject constructor(
    private val activeTaskSummaryDao: ActiveTaskSummaryDao,
    private val summaryGroupDao: SummaryGroupDao,
    private val taskRepository: TaskRepository
) : SummaryRepository {
    override fun getSummary(
        period: TaskPeriod,
        startDate: Instant
    ): Flow<Summary> {
        TODO("Not yet implemented")
    }

    override fun getSummaries(): Flow<List<Summary>> {
        TODO("Not yet implemented")
    }

    override suspend fun createSummary(
        period: TaskPeriod,
        startDate: Instant
    ): Summary {
        TODO("Not yet implemented")
    }
}