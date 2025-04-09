package com.nrr.settings

import com.nrr.model.ReminderType
import com.nrr.model.Task
import com.nrr.model.TaskReminder
import com.nrr.model.TaskType
import com.nrr.ui.iconId
import kotlinx.datetime.Instant

internal data class ReminderInfo private constructor(
    val taskTitle: String,
    val taskIconId: Int,
    val remindedAt: Instant,
    val reminderType: ReminderType,
    val taskType: TaskType
) {
    companion object {
        fun from(task: Task, reminder: TaskReminder) = ReminderInfo(
            taskTitle = task.title,
            taskIconId = task.taskType.iconId(),
            remindedAt = reminder.date,
            reminderType = reminder.reminderType,
            taskType = task.taskType
        )
    }
}