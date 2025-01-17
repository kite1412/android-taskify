package com.nrr.notification

import com.nrr.model.Task

interface ScheduledTaskNotifier {
    fun scheduleReminder(
        task: Task,
        reminderType: ReminderType
    )
}