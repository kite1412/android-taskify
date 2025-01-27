package com.nrr.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nrr.database.entity.SummaryGroupEntity
import com.nrr.database.model.SummaryGroup
import com.nrr.model.TaskPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
interface SummaryGroupDao {
    @Query("""
        SELECT * FROM summary_groups
        WHERE start_date = :startDate
        AND period = :period
    """)
    fun getSummaryByPeriod(startDate: Instant, period: TaskPeriod): Flow<SummaryGroup>

    @Query("""
        SELECT * FROM summary_groups
    """)
    fun getAllSummaries(): Flow<List<SummaryGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummaryGroups(groups: List<SummaryGroupEntity>): List<Long>
}