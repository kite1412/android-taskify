package com.nrr.domain

import com.nrr.data.repository.TaskRepository
import com.nrr.model.Task
import javax.inject.Inject

class SaveTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(tasks: List<Task>) =
        taskRepository.saveTasks(tasks)
}