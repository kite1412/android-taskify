package com.nrr.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nrr.model.Task
import com.nrr.model.TaskType
import kotlinx.datetime.Instant

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant,
    @ColumnInfo(name = "updated_at")
    val updateAt: Instant,
    @ColumnInfo(name = "task_type")
    val taskType: TaskType
)

fun Task.asEntity() =
    TaskEntity(
        id = id,
        title = title,
        description = description,
        createdAt = createdAt,
        updateAt = updateAt,
        taskType = taskType
    )