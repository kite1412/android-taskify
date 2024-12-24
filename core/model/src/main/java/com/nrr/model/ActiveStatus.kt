package com.nrr.model

import kotlinx.datetime.Instant

data class ActiveStatus(
    val startDate: Instant?,
    val dueDate: Instant?,
    val priority: TaskPriority,
    val period: TaskPeriod,
    val isDefault: Boolean
)