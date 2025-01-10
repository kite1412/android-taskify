package com.nrr.plandetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nrr.domain.GetTasksByPeriodUseCase
import com.nrr.domain.MarkTaskCompletedUseCase
import com.nrr.domain.RemoveActiveTasksUseCase
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.plandetail.navigation.PlanDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getTasksByPeriodUseCase: GetTasksByPeriodUseCase,
    private val markTaskCompletedUseCase: MarkTaskCompletedUseCase,
    private val removeActiveTasksUseCase: RemoveActiveTasksUseCase
) : ViewModel() {
    val period = TaskPeriod
        .entries[savedStateHandle.toRoute<PlanDetailRoute>().periodOrdinal]

    val tasks = getTasksByPeriodUseCase(period)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun markCompleted(task: Task) {
        viewModelScope.launch {
            markTaskCompletedUseCase(task)
        }
    }

    fun removeTask(task: Task) {
        viewModelScope.launch {
            removeActiveTasksUseCase(listOf(task))
        }
    }
}