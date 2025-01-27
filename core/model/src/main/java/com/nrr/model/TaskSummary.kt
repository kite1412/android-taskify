package com.nrr.model

import kotlinx.datetime.Instant

data class TaskSummary(
    val id: Long,
    val title: String,
    val description: String?,
    val startDate: Instant,
    val dueDate: Instant?,
    val completedAt: Instant?
)
