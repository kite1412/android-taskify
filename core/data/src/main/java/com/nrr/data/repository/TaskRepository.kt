package com.nrr.data.repository

import com.nrr.model.ActiveStatus
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun saveTasks(
        tasks: List<Task>,
        activeStatus: List<ActiveStatus?> = emptyList()
    ): List<Long>

    fun getAllTasks(): Flow<List<Task>>

    fun getAllActiveTasksByPeriod(period: TaskPeriod): Flow<List<Task>>

    suspend fun deleteActiveTasks(task: List<Task>): Int

    suspend fun setActiveTaskAsCompleted(task: Task): Long

    fun getByTitle(title: String): Flow<List<Task>>
}