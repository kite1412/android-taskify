package com.nrr.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nrr.database.entity.TaskEntity
import com.nrr.database.model.TaskWithStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Transaction
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskWithStatus>>

    @Query("""
        SELECT * FROM tasks 
        WHERE title LIKE '%' || :title || '%'
        OR description LIKE '%' || :title || '%'
    """)
    fun getByTitle(title: String): Flow<List<TaskWithStatus>>

    @Query("""
        SELECT * FROM tasks
        WHERE id IN (:ids)
    """)
    fun getAllByIds(ids: List<Long>): Flow<List<TaskWithStatus>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(task: List<TaskEntity>): List<Long>

    @Query("""
        DELETE FROM tasks
        WHERE id IN (:ids)
    """)
    suspend fun deleteTasks(ids: List<Long>): Int
}