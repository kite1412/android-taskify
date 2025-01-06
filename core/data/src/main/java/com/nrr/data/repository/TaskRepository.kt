package com.nrr.data.repository

import com.nrr.model.ActiveStatus
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>

    fun getAllActiveTasksByPeriod(period: TaskPeriod): Flow<List<Task>>

    fun getByTitle(title: String): Flow<List<Task>>

    fun getByIds(ids: List<Long>): Flow<List<Task>>

    suspend fun saveTasks(
        tasks: List<Task>,
        activeStatus: List<ActiveStatus?> = emptyList()
    ): List<Long>

    suspend fun deleteActiveTasks(task: List<Task>): Int

    suspend fun setActiveTaskAsCompleted(task: Task): Long

    suspend fun deleteTasks(tasks: List<Task>): Int
}