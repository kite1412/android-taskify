package com.nrr.taskdetail

import com.nrr.model.ActiveStatus
import com.nrr.model.Task
import com.nrr.model.TaskType
import kotlinx.datetime.Clock

internal data class TaskEdit(
    val id: Long? = null,
    val title: String = "",
    val description: String = "",
    val taskType: TaskType? = null,
    val activeStatus: ActiveStatus? = null
) {
    override fun equals(other: Any?): Boolean {
        if (other is Task) {
            return title == other.title &&
                    (description == other.description ||
                            (description.isEmpty() && other.description == null)) &&
                    taskType == other.taskType &&
                    activeStatus == other.activeStatuses.firstOrNull()
        }
        return this === other
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

internal fun Task.toTaskEdit() = TaskEdit(
    id = id,
    title = title,
    description = description ?: "",
    taskType = taskType,
    activeStatus = activeStatuses.firstOrNull()
)

internal fun TaskEdit.toTask() = Task(
    id = id ?: 0,
    title = title,
    description = description,
    createdAt = Clock.System.now(),
    updateAt = Clock.System.now(),
    taskType = taskType!!,
    activeStatuses = activeStatus?.let { listOf(it) } ?: emptyList()
)