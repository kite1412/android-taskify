package com.nrr.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nrr.database.model.ActiveTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveTaskDao {
    @Query("SELECT * FROM active_tasks")
    fun getAllActiveTasks(): Flow<List<ActiveTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActiveTask(activeTask: ActiveTaskEntity)
}