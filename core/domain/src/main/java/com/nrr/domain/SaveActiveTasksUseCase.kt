package com.nrr.domain

import com.nrr.data.repository.TaskRepository
import com.nrr.model.Task
import javax.inject.Inject

// TODO handle notification scheduling
class SaveActiveTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(activeTasks: List<Task>) =
        taskRepository.saveActiveTasks(activeTasks)
}