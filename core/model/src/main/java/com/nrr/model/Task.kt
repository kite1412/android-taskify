package com.nrr.model

import kotlinx.datetime.Instant

data class Task(
    val id: String,
    val title: String,
    val description: String?,
    val createdAt: Instant,
    val updateAt: Instant,
    val startTime: Time?,
    val endTime: Time?,
    val taskType: TaskType,
    val priority: TaskPriority,
    val period: TaskPeriod,
    val isSet: Boolean,
    val isDefault: Boolean
)