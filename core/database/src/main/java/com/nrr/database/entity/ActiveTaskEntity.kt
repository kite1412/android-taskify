package com.nrr.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nrr.model.ActiveStatus
import com.nrr.model.TaskPeriod
import com.nrr.model.TaskPriority
import kotlinx.datetime.Instant

@Entity(
    tableName = "active_tasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ActiveTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "task_id")
    val taskId: Long,
    @ColumnInfo(name = "task_priority")
    val taskPriority: TaskPriority,
    @ColumnInfo(name = "task_period")
    val taskPeriod: TaskPeriod,
    @ColumnInfo(name = "reminder_set")
    val reminderSet: Boolean,
    @ColumnInfo(name = "start_date")
    val startDate: Instant,
    @ColumnInfo(name = "due_date")
    val dueDate: Instant?,
    @ColumnInfo(name = "is_set")
    val isSet: Boolean,
    @ColumnInfo(name = "is_default")
    val isDefault: Boolean,
    @ColumnInfo(name = "completed_at")
    val completedAt: Instant?
)

fun ActiveTaskEntity.asExternalModel() = ActiveStatus(
    id = id,
    startDate = startDate,
    dueDate = dueDate,
    priority = taskPriority,
    period = taskPeriod,
    isSet = isSet,
    isDefault = isDefault,
    isCompleted = completedAt != null,
    reminderSet = reminderSet,
    completedAt = completedAt
)

fun ActiveStatus.asEntity(taskId: Long) = ActiveTaskEntity(
    id = id,
    taskId = taskId,
    taskPriority = priority,
    taskPeriod = period,
    reminderSet = reminderSet,
    startDate = startDate,
    dueDate = dueDate,
    isSet = isSet,
    isDefault = isDefault,
    completedAt = completedAt
)