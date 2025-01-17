package com.nrr.notification

import com.nrr.model.Task
import javax.inject.Singleton

@Singleton
class ScheduledTaskNotifierImpl : ScheduledTaskNotifier {
    override fun postNotification(task: Task) {
        TODO("Not yet implemented")
    }
}