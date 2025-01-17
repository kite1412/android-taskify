package com.nrr.notification

import com.nrr.model.Task

interface ScheduledTaskNotifier {
    fun postNotification(task: Task)
}