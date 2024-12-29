package com.nrr.domain

import com.nrr.data.repository.TaskRepository
import com.nrr.model.Task
import javax.inject.Inject

class RemoveActiveTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Int =
        task.activeStatus?.let {
            if (it.isDefault) {
                taskRepository.saveTask(
                    task = task,
                    activeStatus = it.copy(isSet = false)
                )
                1
            } else taskRepository.deleteActiveTask(task)
        } ?: 0
}