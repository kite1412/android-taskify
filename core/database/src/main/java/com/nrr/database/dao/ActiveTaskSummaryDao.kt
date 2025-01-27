package com.nrr.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.nrr.database.entity.ActiveTaskSummaryEntity

@Dao
interface ActiveTaskSummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActiveTaskSummaries(summaries: List<ActiveTaskSummaryEntity>): List<Long>
}