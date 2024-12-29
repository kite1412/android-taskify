package com.nrr.data.repository

import com.nrr.model.ActiveStatus
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun saveTask(
        task: Task,
        activeStatus: ActiveStatus? = null
    )

    fun getAllTasks(): Flow<List<Task>>

    fun getAllActiveTasksByPeriod(period: TaskPeriod): Flow<List<Task>>

    suspend fun deleteActiveTask(task: Task): Int

    suspend fun setActiveTaskAsCompleted(task: Task): Long
}