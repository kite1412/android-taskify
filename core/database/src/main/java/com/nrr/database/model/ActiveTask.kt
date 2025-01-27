package com.nrr.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.nrr.database.entity.ActiveTaskEntity
import com.nrr.database.entity.TaskEntity
import com.nrr.database.entity.asExternalModel
import com.nrr.model.Task

data class ActiveTask(
    @Embedded
    val entity: ActiveTaskEntity,
    @Relation(
        parentColumn = "task_id",
        entityColumn = "id"
    )
    val task: TaskEntity
)

fun ActiveTask.asExternalModel() = Task(
    id = task.id,
    title = task.title,
    description = task.description,
    createdAt = task.createdAt,
    updateAt = task.updateAt,
    taskType = task.taskType,
    activeStatuses = listOf(entity.asExternalModel())
)
