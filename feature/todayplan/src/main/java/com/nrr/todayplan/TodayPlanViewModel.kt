package com.nrr.todayplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrr.data.repository.UserDataRepository
import com.nrr.domain.GetTasksByPeriodUseCase
import com.nrr.model.TaskPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TodayPlanViewModel @Inject constructor(
    getTasksByPeriodUseCase: GetTasksByPeriodUseCase,
    userDataRepository: UserDataRepository
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
}