package com.nrr.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nrr.database.model.ActiveTask
import com.nrr.model.TaskSummary
import com.nrr.model.TaskType
import kotlinx.datetime.Instant

@Entity(
    tableName = "active_task_summaries",
    foreignKeys = [
        ForeignKey(
            entity = SummaryGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["summary_group_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["summary_group_id"])
    ]
)
data class ActiveTaskSummaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "summary_group_id")
    val summaryGroupId: Long,
    val title: String,
    val description: String?,
    @ColumnInfo(name = "start_date")
    val startDate: Instant,
    @ColumnInfo(name = "due_date")
    val dueDate: Instant?,
    @ColumnInfo(name = "completed_at")
    val completedAt: Instant?,
    @ColumnInfo(name = "task_type", defaultValue = "0")
    val taskType: TaskType
)

fun ActiveTask.toSummary(summaryGroupId: Long) = ActiveTaskSummaryEntity(
    id = 0,
    summaryGroupId = summaryGroupId,
    title = task.title,
    description = task.description,
    startDate = entity.startDate,
    dueDate = entity.dueDate,
    completedAt = entity.completedAt,
    taskType = task.taskType
)

fun ActiveTaskSummaryEntity.asExternalModel() = TaskSummary(
    id = id,
    title = title,
    description = description,
    startDate = startDate,
    dueDate = dueDate,
    completedAt = completedAt,
    taskType = taskType
)