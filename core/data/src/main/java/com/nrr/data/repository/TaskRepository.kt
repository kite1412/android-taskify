package com.nrr.data.repository

import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(): Flow<List<Task>>

    fun getActiveTasksByPeriod(period: TaskPeriod): Flow<List<Task>>

    fun getByTitle(title: String): Flow<List<Task>>

    fun getByIds(ids: List<Long>): Flow<List<Task>>

    fun getActiveTasksByIds(activeTaskIds: List<Long>): Flow<List<Task>>

    suspend fun saveTasks(tasks: List<Task>): List<Long>

    suspend fun saveActiveTasks(tasks: List<Task>): List<Long>

    suspend fun deleteActiveTasks(task: List<Task>): Int

    suspend fun setActiveTaskAsCompleted(task: Task): Long

    suspend fun deleteTasks(tasks: List<Task>): Int

    suspend fun deleteActiveTasksByPeriod(period: TaskPeriod): Int
}