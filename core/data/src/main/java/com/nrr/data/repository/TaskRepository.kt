package com.nrr.data.repository

import com.nrr.model.ActiveStatus
import com.nrr.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun saveTask(
        task: Task,
        activeStatus: ActiveStatus? = null
    )

    fun getAllTasks(): Flow<List<Task>>

    suspend fun deleteActiveTask(task: Task): Int
}