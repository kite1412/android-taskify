package com.nrr.notification.model

import com.nrr.notification.ReminderType

data class TaskWithReminder(
    val task: TaskFiltered,
    val reminderType: ReminderType
) {
    override fun equals(other: Any?): Boolean {
        if (other !is TaskWithReminder) return false
        return task.id == other.task.id
                && task.title == other.task.title
                && reminderType == other.reminderType
    }

    override fun hashCode(): Int {
        var result = task.hashCode()
        result = 31 * result + reminderType.hashCode()
        return result
    }
}
