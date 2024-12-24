package com.nrr.domain

import com.nrr.database.dao.ActiveTaskDao
import com.nrr.database.model.TaskWithStatus
import com.nrr.database.model.asExternalModel
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTasksByPeriodUseCase @Inject constructor(
    private val activeTaskDao: ActiveTaskDao
) {
    operator fun invoke(period: TaskPeriod): Flow<List<Task>> =
        activeTaskDao.getAllByPeriod(period).map {
            it.map(TaskWithStatus::asExternalModel)
        }
}