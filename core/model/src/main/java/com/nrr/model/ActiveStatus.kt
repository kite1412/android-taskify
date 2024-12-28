package com.nrr.model

import kotlinx.datetime.Instant

data class ActiveStatus(
    val id: Long,
    val startDate: Instant,
    val dueDate: Instant?,
    val priority: TaskPriority,
    val period: TaskPeriod,
    val isDefault: Boolean,
    val isCompleted: Boolean
)