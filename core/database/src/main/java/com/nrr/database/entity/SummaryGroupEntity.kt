package com.nrr.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nrr.model.TaskPeriod
import kotlinx.datetime.Instant

@Entity(tableName = "summary_groups")
data class SummaryGroupEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val period: TaskPeriod,
    @ColumnInfo(name = "start_date")
    val startDate: Instant,
    @ColumnInfo(name = "end_date")
    val endDate: Instant
)
