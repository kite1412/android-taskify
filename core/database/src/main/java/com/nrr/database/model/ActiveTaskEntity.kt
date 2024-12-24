package com.nrr.database.model

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
    @ColumnInfo(name = "is_default")
    val isDefault: Boolean
)

fun ActiveTaskEntity.asExternalModel() = ActiveStatus(
    startDate = startDate,
    dueDate = dueDate,
    priority = taskPriority,
    period = taskPeriod,
    isDefault = isDefault
)

fun ActiveStatus.asEntity(taskId: Long) = ActiveTaskEntity(
    taskId = taskId,
    taskPriority = priority,
    taskPeriod = period,
    reminderSet = false,
    startDate = startDate,
    dueDate = dueDate,
    isDefault = isDefault
)