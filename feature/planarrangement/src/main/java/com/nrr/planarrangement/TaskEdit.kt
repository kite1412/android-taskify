package com.nrr.planarrangement

import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import kotlinx.datetime.Instant

internal data class TaskEdit(
    val taskId: Long,
    val title: String,
    val description: String?,
    val period: TaskPeriod,
    val selectedStartDate: Instant? = null,
    val selectedDueDate: Instant? = null
)

internal fun Task.toTaskEdit(period: TaskPeriod = TaskPeriod.DAY) = TaskEdit(
    taskId = id,
    title = title,
    description = description,
    period = activeStatuses.firstOrNull()?.period ?: period,
    selectedStartDate = activeStatuses.firstOrNull()?.startDate,
    selectedDueDate = activeStatuses.firstOrNull()?.dueDate
)