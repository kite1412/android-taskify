package com.nrr.domain

import com.nrr.data.repository.TaskRepository
import com.nrr.model.Task
import com.nrr.notification.ScheduledTaskNotifier
import javax.inject.Inject

class RemoveActiveTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val scheduledTaskNotifier: ScheduledTaskNotifier
) {
    suspend operator fun invoke(tasks: List<Task>): Int {
        tasks.forEach {
            scheduledTaskNotifier.cancelReminder(it)
        }

        return (if (tasks.isNotEmpty())
            taskRepository.deleteActiveTasks(tasks)
        else 0)
    }
}