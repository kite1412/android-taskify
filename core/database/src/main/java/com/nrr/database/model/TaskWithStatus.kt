package com.nrr.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class TaskWithStatus(
    @Embedded
    val task: TaskEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "task_id"
    )
    val activeStatus: ActiveTaskEntity?
)