package com.nrr.todayplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrr.data.repository.UserDataRepository
import com.nrr.domain.GetTasksByPeriodUseCase
import com.nrr.domain.MarkTaskCompletedUseCase
import com.nrr.domain.RemoveActiveTaskUseCase
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodayPlanViewModel @Inject constructor(
    getTasksByPeriodUseCase: GetTasksByPeriodUseCase,
    userDataRepository: UserDataRepository,
    private val removeActiveTaskUseCase: RemoveActiveTaskUseCase,
    private val markTaskCompletedUseCase: MarkTaskCompletedUseCase
) : ViewModel() {
    val todayTasks = getTasksByPeriodUseCase(TaskPeriod.DAY)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val weeklyTasks = getTasksByPeriodUseCase(TaskPeriod.WEEK)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val monthlyTasks = getTasksByPeriodUseCase(TaskPeriod.MONTH)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val username = userDataRepository.userData.map { it.username }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ""
        )

    // TODO add confirmation
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            removeActiveTaskUseCase(task)
        }
    }

    // TODO add confirmation
    fun completeTask(task: Task) {
        viewModelScope.launch {
            markTaskCompletedUseCase(task)
        }
    }
}