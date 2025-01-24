package com.nrr.notification.model

import com.nrr.model.Task
import kotlinx.datetime.Instant

data class TaskFiltered(
    val id: Long,
    val title: String,
    val startDate: Instant,
    val dueDate: Instant?,
    val completed: Boolean = false,
    val set: Boolean = false
)

internal fun Task.toFiltered() = with(activeStatuses.first()) {
    TaskFiltered(
        id = id,
        title = title,
        startDate = startDate,
        dueDate = dueDate,
        completed = isCompleted,
        set = isSet
    )
}