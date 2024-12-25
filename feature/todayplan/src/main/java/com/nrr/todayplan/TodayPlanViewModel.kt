package com.nrr.todayplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrr.domain.GetTasksByPeriodUseCase
import com.nrr.model.TaskPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TodayPlanViewModel @Inject constructor(
    getTasksByPeriodUseCase: GetTasksByPeriodUseCase
) : ViewModel() {
    val todayPlan = getTasksByPeriodUseCase(TaskPeriod.DAY)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}