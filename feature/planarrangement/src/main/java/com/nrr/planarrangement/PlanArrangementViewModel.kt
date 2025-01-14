package com.nrr.planarrangement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nrr.data.repository.TaskRepository
import com.nrr.model.TaskPeriod
import com.nrr.planarrangement.navigation.PlanArrangementRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanArrangementViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository
) : ViewModel() {
    // navigation from non-null ActiveStatus to be used in TaskEdit typically for
    // editing existing ActiveStatus
    // if not null navigate back to calling route instead of making TaskEdit null
    private val activeStatusId = savedStateHandle.toRoute<PlanArrangementRoute>().activeStatusId

    // navigation from nullable ActiveStatus typically for creating a new ActiveStatus
    // if not null navigate back to calling route instead of making TaskEdit null
    private val taskId = savedStateHandle.toRoute<PlanArrangementRoute>().taskId

    val immediatePopBackStack = activeStatusId != null || taskId != null

    val tasks = if (activeStatusId == null && taskId == null) taskRepository.getTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        ) else flowOf(null)

    var period by mutableStateOf<TaskPeriod?>(
        savedStateHandle.toRoute<PlanArrangementRoute>().periodOrdinal?.let { TaskPeriod.entries[it] }
    )
        private set

    var assigningTask by mutableStateOf(false)
        private set

    internal var taskEdit by mutableStateOf<TaskEdit?>(null)
        private set

    init {
        viewModelScope.launch {
            if (activeStatusId != null) taskRepository.getActiveTasksByIds(listOf(activeStatusId))
                .collect {
                    if (it.isNotEmpty()) with(it.first()) {
                        taskRepository.getByIds(listOf(id)).collect { l ->
                            if (l.isNotEmpty()) {
                                taskEdit = TaskEdit(
                                    task = l.first(),
                                    activeStatus = activeStatuses.first()
                                )
                                assigningTask = true
                            }
                        }
                    }
                }
            else if (taskId != null) taskRepository.getByIds(listOf(taskId)).collect {
                if (it.isNotEmpty()) with(it.first()) {
                    taskEdit = TaskEdit(this)
                    assigningTask = true
                }
            }
        }
    }

    fun updatePeriod(period: TaskPeriod) {
        this.period = period
    }
}