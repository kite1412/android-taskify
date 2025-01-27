package com.nrr.model

import kotlinx.datetime.Instant

data class Summary(
    val id: Long,
    val period: TaskPeriod,
    val startDate: Instant,
    val endDate: Instant,
    val tasks: List<TaskSummary>
)
