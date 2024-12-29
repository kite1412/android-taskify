package com.nrr.domain

import com.nrr.data.repository.TaskRepository
import com.nrr.model.Task
import javax.inject.Inject

class MarkTaskCompletedUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Long =
        taskRepository.setActiveTaskAsCompleted(task)
}