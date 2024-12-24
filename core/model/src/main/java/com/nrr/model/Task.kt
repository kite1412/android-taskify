package com.nrr.model

import kotlinx.datetime.Instant

data class Task(
    val id: Int,
    val title: String,
    val description: String?,
    val createdAt: Instant,
    val updateAt: Instant,
    val taskType: TaskType,
    val activeStatus: ActiveStatus? = null
)