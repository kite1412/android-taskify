package com.nrr.domain

import com.nrr.data.repository.TaskRepository
import com.nrr.model.Task
import javax.inject.Inject

class RemoveActiveTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(tasks: List<Task>): Int {
        val (defaultTasks, activeTasks) = tasks.partition { it.activeStatus?.isDefault == true }
        val ids = taskRepository.saveTasks(
            defaultTasks.map {
                it.copy(activeStatus = it.activeStatus?.copy(isDefault = false))
            }
        ).size
        return taskRepository.deleteActiveTasks(activeTasks) + ids
    }
}