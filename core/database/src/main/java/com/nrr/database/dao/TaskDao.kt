package com.nrr.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nrr.database.model.TaskEntity
import com.nrr.database.model.TaskWithStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Transaction
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskWithStatus>>

    @Query("""
        SELECT * FROM tasks 
        WHERE title LIKE '%' || :title || '%'
        OR description LIKE '%' || :title || '%'
    """)
    fun getByTitle(title: String): Flow<List<TaskWithStatus>>
}