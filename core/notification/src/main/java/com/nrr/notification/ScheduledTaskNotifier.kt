package com.nrr.notification

import com.nrr.model.Task
import com.nrr.notification.model.ReminderType
import com.nrr.notification.model.Result

interface ScheduledTaskNotifier {
    fun scheduleReminder(
        task: Task,
        reminderType: ReminderType
    ) : Result
}