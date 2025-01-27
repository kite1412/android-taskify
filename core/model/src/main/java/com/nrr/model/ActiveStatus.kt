package com.nrr.model

import kotlinx.datetime.Instant

data class ActiveStatus(
    val id: Long,
    val startDate: Instant,
    val dueDate: Instant?,
    val priority: TaskPriority,
    val period: TaskPeriod,
    val isSet: Boolean,
    val isDefault: Boolean,
    @Deprecated(
        message = "Use the nullability of completedAt as complete status",
        replaceWith = ReplaceWith("completedAt")
    )
    val isCompleted: Boolean,
    val reminderSet: Boolean = true,
    val completedAt: Instant? = null
)