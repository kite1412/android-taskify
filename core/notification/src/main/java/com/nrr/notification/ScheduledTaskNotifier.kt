package com.nrr.notification

import com.nrr.model.Task
import com.nrr.notification.model.Result

interface ScheduledTaskNotifier {
    suspend fun scheduleReminder(task: Task) : Result

    fun cancelReminder(activeTask: Task)
}