package com.nrr.data.repository

import com.nrr.model.Summary
import com.nrr.model.TaskPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface SummaryRepository {
    fun getSummary(period: TaskPeriod, startDate: Instant): Flow<Summary>

    fun getSummaries(): Flow<List<Summary>>

    suspend fun createSummary(
        period: TaskPeriod,
        startDate: Instant
    ): Summary?
}