package com.nrr.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nrr.model.TaskPriority
import com.nrr.model.TaskType
import com.nrr.model.Time
import kotlinx.datetime.Instant

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val description: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant,
    @ColumnInfo(name = "updated_at")
    val updateAt: Instant,
    @ColumnInfo(name = "start_time")
    val startTime: Time?,
    @ColumnInfo(name = "end_time")
    val endTime: Time?,
    @ColumnInfo(name = "task_type")
    val taskType: TaskType,
    val priority: TaskPriority,
    @ColumnInfo(name = "is_set")
    val isSet: Boolean,
    @ColumnInfo(name = "is_default")
    val isDefault: Boolean
)