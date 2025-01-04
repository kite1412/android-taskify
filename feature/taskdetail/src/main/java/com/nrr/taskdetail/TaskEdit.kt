package com.nrr.taskdetail

import com.nrr.model.ActiveStatus
import com.nrr.model.Task
import com.nrr.model.TaskType
import kotlinx.datetime.Clock

internal data class TaskEdit(
    val id: Long? = null,
    val title: String = "",
    val description: String? = null,
    val taskType: TaskType? = null,
    val activeStatus: ActiveStatus? = null
)

internal fun Task.toTaskEdit() = TaskEdit(
    id = id,
    title = title,
    description = description,
    taskType = taskType,
    activeStatus = activeStatus
)

internal fun TaskEdit.toTask() = Task(
    id = id ?: 0,
    title = title,
    description = description,
    createdAt = Clock.System.now(),
    updateAt = Clock.System.now(),
    taskType = taskType!!,
    activeStatus = activeStatus
)