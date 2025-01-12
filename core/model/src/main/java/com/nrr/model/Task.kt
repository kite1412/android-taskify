package com.nrr.model

import kotlinx.datetime.Instant

// TODO change to list of active statuses completely
data class Task(
    val id: Long,
    val title: String,
    val description: String?,
    val createdAt: Instant,
    val updateAt: Instant,
    val taskType: TaskType,
    val activeStatus: ActiveStatus? = null,
    val activeStatuses: List<ActiveStatus> = emptyList()
)