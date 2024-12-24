package com.nrr.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.nrr.model.TaskPeriod
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
    val id: Int,
    @ColumnInfo(name = "task_id")
    val taskId: Int,
    @ColumnInfo(name = "task_period")
    val taskPeriod: TaskPeriod,
    @ColumnInfo(name = "reminder_set")
    val reminderSet: Boolean,
    @ColumnInfo(name = "start_date")
    val startDate: Instant,
    @ColumnInfo(name = "due_date")
    val dueDate: Instant,
    @ColumnInfo(name = "is_default")
    val isDefault: Boolean
)