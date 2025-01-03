package com.nrr.data.repository

import com.nrr.database.dao.ActiveTaskDao
import com.nrr.database.dao.TaskDao
import com.nrr.database.model.TaskWithStatus
import com.nrr.database.model.asEntity
import com.nrr.database.model.asExternalModel
import com.nrr.model.ActiveStatus
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class RoomTaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val activeTaskDao: ActiveTaskDao
) : TaskRepository {
    override suspend fun saveTask(
        task: Task,
        activeStatus: ActiveStatus?
    ) {
        taskDao.insertTask(task.asEntity()).also {
            if (activeStatus != null)
                activeTaskDao.insertActiveTask(activeStatus.asEntity(it))
        }
    }

    override fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAllTasks().map {
            it.map(TaskWithStatus::asExternalModel)
        }

    override fun getAllActiveTasksByPeriod(period: TaskPeriod): Flow<List<Task>> =
        activeTaskDao.getAllByPeriod(period).map {
            it.map(TaskWithStatus::asExternalModel)
        }

    override suspend fun deleteActiveTask(task: Task): Int =
        task.activeStatus?.let {
            activeTaskDao.deleteActiveTask(it.asEntity(task.id))
        } ?: 0

    override suspend fun setActiveTaskAsCompleted(task: Task): Long =
        task.activeStatus?.let {
            activeTaskDao.insertActiveTask(it.copy(isCompleted = true).asEntity(task.id))
        } ?: 0
}