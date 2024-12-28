package com.nrr.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Task(
    val id: Long,
    val title: String,
    val description: String?,
    val createdAt: Instant,
    val updateAt: Instant,
    val taskType: TaskType,
    val activeStatus: ActiveStatus? = null
) {
    companion object {
        val mock = Task(
            id = 1,
            title = "Learn Android",
            description = "Learn Android Development",
            createdAt = Clock.System.now(),
            updateAt = Clock.System.now(),
            taskType = TaskType.LEARNING,
            activeStatus = ActiveStatus(
                id = 1,
                startDate = Clock.System.now(),
                dueDate = Clock.System.now(),
                priority = TaskPriority.HIGH,
                period = TaskPeriod.DAY,
                isDefault = true,
                isCompleted = true
            )
        )
    }
}