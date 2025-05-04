package com.nrr.domain

import com.nrr.data.repository.TaskRepository
import com.nrr.model.Task
import com.nrr.notification.ScheduledTaskNotifier
import javax.inject.Inject

class DeleteTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val scheduledTaskNotifier: ScheduledTaskNotifier
) {
    suspend operator fun invoke(tasks: List<Task>) {
        taskRepository.deleteTasks(tasks)

        tasks.forEach {
            it.activeStatuses.forEach { s ->
                if (s.reminderSet) scheduledTaskNotifier.cancelReminder(it)
            }
        }
    }
}