package com.nrr.notification.util

import com.nrr.model.Task
import com.nrr.model.TaskReminder
import com.nrr.model.ReminderType

internal fun Task.toTaskReminders() = with(activeStatuses.first()) {
    TaskReminder(
        activeTaskId = id,
        reminderType = ReminderType.START,
        date = startDate
    ) to dueDate?.let {
        TaskReminder(
            activeTaskId = id,
            reminderType = ReminderType.END,
            date = it
        )
    }
}