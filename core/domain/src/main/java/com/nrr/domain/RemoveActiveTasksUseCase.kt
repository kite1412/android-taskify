package com.nrr.domain

import com.nrr.data.repository.TaskRepository
import com.nrr.model.Task
import javax.inject.Inject

class RemoveActiveTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(tasks: List<Task>): Int {
        val (defaultTasks, activeTasks) = tasks.partition { t ->
            t.activeStatuses.any { it.isDefault }
        }
        val ids = if (defaultTasks.isNotEmpty()) taskRepository.saveTasks(
            defaultTasks.map {
                it.copy(activeStatuses = it.activeStatuses.map { s -> s.copy(isDefault = false) })
            }
        ).size else 0

        return (if (activeTasks.isNotEmpty())
            taskRepository.deleteActiveTasks(activeTasks)
        else 0) + ids
    }
}