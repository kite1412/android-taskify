package com.nrr.notification

import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.notification.model.Result

interface ScheduledTaskNotifier {
    suspend fun scheduleReminder(task: Task) : Result

    suspend fun scheduleReminders(
        tasks: List<Task>,
        period: TaskPeriod
    ): Result = Result.Fail(Result.Fail.Reason.NOT_IMPLEMENTED)

    fun cancelReminder(activeTask: Task)
}