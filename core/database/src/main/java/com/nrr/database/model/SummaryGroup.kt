package com.nrr.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.nrr.database.entity.ActiveTaskSummaryEntity
import com.nrr.database.entity.SummaryGroupEntity

data class SummaryGroup(
    @Embedded
    val group: SummaryGroupEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "summary_group_id"
    )
    val tasks: List<ActiveTaskSummaryEntity>
)
