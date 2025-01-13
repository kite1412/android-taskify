package com.nrr.data.repository

import com.nrr.database.dao.ActiveTaskDao
import com.nrr.database.dao.TaskDao
import com.nrr.database.model.ActiveTask
import com.nrr.database.model.TaskWithStatus
import com.nrr.database.model.asEntity
import com.nrr.database.model.asExternalModel
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class RoomTaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val activeTaskDao: ActiveTaskDao
) : TaskRepository {
    override fun getTasks(): Flow<List<Task>> =
        taskDao.getAllTasks().map {
            it.map(TaskWithStatus::asExternalModel)
        }

    override fun getActiveTasksByPeriod(period: TaskPeriod): Flow<List<Task>> =
        activeTaskDao.getAllByPeriod(period).map {
            it.map(ActiveTask::asExternalModel)
        }

    override fun getByTitle(title: String): Flow<List<Task>> =
        taskDao.getByTitle(title).map {
            it.map(TaskWithStatus::asExternalModel)
        }

    override fun getByIds(ids: List<Long>): Flow<List<Task>> =
        taskDao.getAllByIds(ids).map {
            it.map(TaskWithStatus::asExternalModel)
        }

    override fun getActiveTasksByIds(activeTaskIds: List<Long>): Flow<List<Task>> =
        activeTaskDao.getAllByIds(activeTaskIds).map {
            it.map(ActiveTask::asExternalModel)
        }

    override suspend fun saveTasks(tasks: List<Task>): List<Long> {
        val ids = taskDao.insertTasks(tasks.map(Task::asEntity))
        tasks
            .filter { it.activeStatuses.isNotEmpty() }
            .flatMap { it.activeStatuses.map { s -> s.asEntity(it.id) } }
            .run {
                if (isNotEmpty()) activeTaskDao.insertActiveTasks(this)
            }
        return ids
    }

    override suspend fun deleteActiveTasks(task: List<Task>): Int =
        task
            .filter { it.activeStatuses.isNotEmpty() }
            .flatMap { it.activeStatuses.map { s -> s.asEntity(it.id).id } }
            .run {
                if (isNotEmpty()) activeTaskDao.deleteActiveTasks(this) else 0
            }

    override suspend fun setActiveTaskAsCompleted(task: Task): Long =
        task.activeStatuses.firstOrNull()?.let {
            activeTaskDao.insertActiveTasks(
                listOf(it.copy(isCompleted = true).asEntity(task.id))
            ).firstOrNull() ?: 0
        } ?: 0

    override suspend fun deleteTasks(tasks: List<Task>): Int =
        taskDao.deleteTasks(tasks.map { it.id })
}