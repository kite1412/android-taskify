package com.nrr.notification.model

import com.nrr.model.Task
import kotlinx.datetime.Instant

data class TaskFiltered(
    val id: Long,
    val title: String,
    val startDate: Instant,
    val dueDate: Instant?
)

internal fun Task.toFiltered() = with(activeStatuses.first()) {
    TaskFiltered(
        id = id,
        title = title,
        startDate = startDate,
        dueDate = dueDate
    )
}