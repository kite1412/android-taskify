package com.nrr.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nrr.database.model.ActiveTask
import com.nrr.database.model.ActiveTaskEntity
import com.nrr.model.TaskPeriod
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveTaskDao {
    @Query("SELECT * FROM active_tasks")
    fun getAllActiveTasks(): Flow<List<ActiveTaskEntity>>

    @Query("""
        SELECT * FROM active_tasks AS at
        WHERE at.task_period = :period
    """)
    fun getAllByPeriod(period: TaskPeriod): Flow<List<ActiveTask>>

    @Query("""
        SELECT * FROM active_tasks AS at
        WHERE at.id IN (:ids)
    """)
    fun getAllByIds(ids: List<Long>): Flow<List<ActiveTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActiveTasks(activeTasks: List<ActiveTaskEntity>): List<Long>

    @Query(
        """
        DELETE FROM active_tasks
        WHERE id IN (:ids)
    """
    )
    suspend fun deleteActiveTasks(ids: List<Long>): Int
}