package com.nrr.domain

import com.nrr.data.repository.TaskRepository
import javax.inject.Inject

class GetTasksByTitleUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(title: String) =
        taskRepository.getByTitle(title)
}