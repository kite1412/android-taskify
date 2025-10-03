package com.nrr.todayplan

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrr.data.repository.UserDataRepository
import com.nrr.domain.GetTasksByPeriodUseCase
import com.nrr.domain.MarkTaskCompletedUseCase
import com.nrr.domain.RemoveActiveTasksUseCase
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.model.toLocalDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class TodayPlanViewModel @Inject constructor(
    getTasksByPeriodUseCase: GetTasksByPeriodUseCase,
    private val userDataRepository: UserDataRepository,
    private val removeActiveTaskUseCase: RemoveActiveTasksUseCase,
    private val markTaskCompletedUseCase: MarkTaskCompletedUseCase
) : ViewModel() {
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

    val todayTasks = combine(
        flow = getTasksByPeriodUseCase(TaskPeriod.DAY),
        flow2 = weeklyTasks,
        flow3 = monthlyTasks
    ) { tt, wt, mt ->
        listOf(
            *tt.toTypedArray(),
            *wt.filter(::isToday).toTypedArray(),
            *mt.filter(::isToday).toTypedArray()
        )
            .sortedBy { it.activeStatuses.firstOrNull()?.startDate }
    }
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

    var showProfile by mutableStateOf(false)
        private set

    private fun isToday(task: Task) =
        task.activeStatuses.firstOrNull()
            ?.startDate
            ?.toLocalDateTime()
            ?.date == Clock.System.now().toLocalDateTime().date

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            removeActiveTaskUseCase(listOf(task))
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            markTaskCompletedUseCase(task)
        }
    }

    fun updateShowProfile(value: Boolean) {
        showProfile = value
    }

    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            userDataRepository.setUsername(newUsername)
        }
    }
}