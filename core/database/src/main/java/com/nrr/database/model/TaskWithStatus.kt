package com.nrr.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.nrr.database.entity.ActiveTaskEntity
import com.nrr.database.entity.TaskEntity
import com.nrr.database.entity.asExternalModel
import com.nrr.model.Task

data class TaskWithStatus(
    @Embedded
    val task: TaskEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "task_id"
    )
    val activeStatuses: List<ActiveTaskEntity>
)

fun TaskWithStatus.asExternalModel() =
    Task(
        id = task.id,
        title = task.title,
        description = task.description,
        createdAt = task.createdAt,
        updateAt = task.updateAt,
        taskType = task.taskType,
        activeStatuses = activeStatuses.map { it.asExternalModel() }
    )