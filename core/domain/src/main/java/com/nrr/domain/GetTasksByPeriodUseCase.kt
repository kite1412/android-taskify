package com.nrr.domain

import com.nrr.data.repository.TaskRepository
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksByPeriodUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(period: TaskPeriod): Flow<List<Task>> =
        taskRepository.getActiveTasksByPeriod(period)
}