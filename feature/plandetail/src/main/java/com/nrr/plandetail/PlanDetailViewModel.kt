package com.nrr.plandetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nrr.model.TaskPeriod
import com.nrr.plandetail.navigation.PlanDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class PlanDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val period = TaskPeriod
        .entries[savedStateHandle.toRoute<PlanDetailRoute>().periodOrdinal]

    var currentDate by mutableStateOf(Clock.System.now())
        private set

    init {
        viewModelScope.launch {
            currentDate = Clock.System.now()
            delay(1000)
        }
    }
}