package com.nrr.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.nrr.database.entity.ActiveTaskSummaryEntity
import com.nrr.database.entity.SummaryGroupEntity
import com.nrr.database.entity.asExternalModel
import com.nrr.model.Summary

data class SummaryGroup(
    @Embedded
    val group: SummaryGroupEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "summary_group_id"
    )
    val tasks: List<ActiveTaskSummaryEntity>
)

fun SummaryGroup.asExternalModel() = Summary(
    id = group.id,
    period = group.period,
    startDate = group.startDate,
    endDate = group.endDate,
    tasks = tasks.map(ActiveTaskSummaryEntity::asExternalModel)
)
