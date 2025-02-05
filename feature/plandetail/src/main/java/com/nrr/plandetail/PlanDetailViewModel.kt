package com.nrr.plandetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nrr.domain.GetTasksByPeriodUseCase
import com.nrr.domain.MarkTaskCompletedUseCase
import com.nrr.domain.RemoveActiveTasksUseCase
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.notification.receiver.DEEP_LINK_ACTIVE_TASK_ID_KEY
import com.nrr.plandetail.navigation.PlanDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
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

    val deepLinkTaskId = savedStateHandle.getStateFlow<String?>(
        key = DEEP_LINK_ACTIVE_TASK_ID_KEY,
        initialValue = null
    )
        .map { it?.toLong() }

    val tasks = getTasksByPeriodUseCase(period)
        .map {
            it.sortedBy { t ->
                t.activeStatuses.firstOrNull()?.startDate
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    var safeToAnimate by mutableStateOf(false)
        private set

    var oneTimeAnimate by mutableStateOf(false)
        private set

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

    fun updateSafeToAnimate(value: Boolean) {
        safeToAnimate = value
    }

    fun updateOneTimeAnimate(value: Boolean) {
        oneTimeAnimate = value
    }
}