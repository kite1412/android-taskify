package com.nrr.domain

import com.nrr.data.repository.TaskRepository
import com.nrr.model.Task
import com.nrr.notification.ScheduledTaskNotifier
import javax.inject.Inject

class SaveActiveTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val scheduledTaskNotifier: ScheduledTaskNotifier
) {
    suspend operator fun invoke(activeTasks: List<Task>): List<Long> {
        require(
            activeTasks
                .flatMap { it.activeStatuses }
                .size == activeTasks.size
        ) {
            "Each task must have exactly one active status"
        }

        return taskRepository.saveActiveTasks(activeTasks).also {
            if (it.size != activeTasks.size) return@also

            it.forEachIndexed { i, id ->
                val task = activeTasks[i].copy(
                    activeStatuses = listOf(
                        activeTasks[i].activeStatuses.first().copy(id = id)
                    )
                )
                if (task.activeStatuses.first().reminderSet)
                    scheduledTaskNotifier.scheduleReminder(task)
                else scheduledTaskNotifier.cancelReminder(task)
            }
        }
    }
}