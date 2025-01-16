package com.nrr.planarrangement

import com.nrr.model.ActiveStatus
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.model.TaskPriority
import kotlinx.datetime.Clock

internal data class TaskEdit private constructor(
    val task: Task,
    val selectedStartDate: Date?,
    val selectedDueDate: Date? = null,
    val activeStatus: ActiveStatus
) {
    fun toTask() = selectedStartDate?.let {
        Task(
            id = task.id,
            title = task.title,
            description = task.description,
            taskType = task.taskType,
            createdAt = task.createdAt,
            updateAt = task.updateAt,
            activeStatuses = listOf(
                activeStatus.copy(
                    startDate = it.toInstant(),
                    dueDate = if (activeStatus.period != TaskPeriod.DAY) selectedDueDate?.toInstant(
                        dayOfMonth = it.dayOfMonth
                    ) else selectedDueDate?.copy(dayOfMonth = it.dayOfMonth)?.toInstant()
                )
            )
        )
    }

    companion object {
        fun create(task: Task, status: ActiveStatus?) = TaskEdit(
            task = task,
            selectedStartDate = status?.startDate?.toDate(),
            selectedDueDate = status?.dueDate?.toDate(),
            activeStatus = status ?: ActiveStatus(
                id = 0,
                startDate = Clock.System.now(),
                dueDate = null,
                priority = TaskPriority.NORMAL,
                period = TaskPeriod.DAY,
                isSet = true,
                isDefault = false,
                isCompleted = false
            )
        )
    }
}

internal fun TaskEdit(
    task: Task,
    activeStatus: ActiveStatus? = null
) = TaskEdit.create(
    task = task,
    status = activeStatus
)