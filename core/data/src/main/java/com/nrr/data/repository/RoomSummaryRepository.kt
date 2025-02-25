package com.nrr.data.repository

import com.nrr.data.util.normalize
import com.nrr.database.RoomTransactionScope
import com.nrr.database.dao.ActiveTaskDao
import com.nrr.database.dao.ActiveTaskSummaryDao
import com.nrr.database.dao.SummaryGroupDao
import com.nrr.database.entity.ActiveTaskSummaryEntity
import com.nrr.database.entity.SummaryGroupEntity
import com.nrr.database.entity.asExternalModel
import com.nrr.database.entity.toSummary
import com.nrr.database.model.SummaryGroup
import com.nrr.database.model.asExternalModel
import com.nrr.model.Summary
import com.nrr.model.TaskPeriod
import com.nrr.model.getStartDate
import com.nrr.model.getEndDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import javax.inject.Inject

internal class RoomSummaryRepository @Inject constructor(
    private val activeTaskSummaryDao: ActiveTaskSummaryDao,
    private val summaryGroupDao: SummaryGroupDao,
    private val activeTaskDao: ActiveTaskDao,
    private val transactionScope: RoomTransactionScope
) : SummaryRepository {
    override fun getSummary(
        period: TaskPeriod,
        startDate: Instant
    ): Flow<Summary> = summaryGroupDao.getSummaryByPeriod(
        startDate = startDate.normalize(),
        period = period
    )
        .map(SummaryGroup::asExternalModel)

    override fun getSummaries(): Flow<List<Summary>> = summaryGroupDao.getAllSummaries()
        .map { it.map(SummaryGroup::asExternalModel) }

    override suspend fun createSummary(
        period: TaskPeriod,
        startDate: Instant
    ): Summary? = transactionScope {
        val tasks = activeTaskDao.getAllByPeriod(period).first()

        tasks.takeIf { it.isNotEmpty() }?.let {
            val startDateNormalized = startDate.getStartDate(period)
            val endDate = startDate.getEndDate(period)
            val summaryGroupEntity = SummaryGroupEntity(
                period = period,
                startDate = startDateNormalized,
                endDate = endDate
            )

            val summaryGroupId = summaryGroupDao.insertSummaryGroups(
                listOf(summaryGroupEntity)
            ).firstOrNull()

            summaryGroupId?.let { groupId ->
                val taskSummaries = tasks.map { it.toSummary(groupId) }
                activeTaskSummaryDao.insertActiveTaskSummaries(taskSummaries)
                Summary(
                    id = groupId,
                    period = period,
                    startDate = startDateNormalized,
                    endDate = endDate,
                    tasks = taskSummaries.map(ActiveTaskSummaryEntity::asExternalModel)
                )
            }
        }
    }
}