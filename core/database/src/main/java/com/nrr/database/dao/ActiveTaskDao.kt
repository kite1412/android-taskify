package com.nrr.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nrr.database.model.ActiveTaskEntity
import com.nrr.database.model.TaskWithStatus
import com.nrr.model.TaskPeriod
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveTaskDao {
    @Query("SELECT * FROM active_tasks")
    fun getAllActiveTasks(): Flow<List<ActiveTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActiveTask(activeTask: ActiveTaskEntity)

    @Query("""
        SELECT * FROM tasks
        LEFT JOIN active_tasks AS at ON tasks.id = at.task_id
        WHERE at.task_period = :period
    """)
    fun getAllByPeriod(period: TaskPeriod): Flow<List<TaskWithStatus>>
}