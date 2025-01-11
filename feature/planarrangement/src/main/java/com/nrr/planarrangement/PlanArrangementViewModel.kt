package com.nrr.planarrangement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.nrr.data.repository.TaskRepository
import com.nrr.model.TaskPeriod
import com.nrr.planarrangement.navigation.PlanArrangementRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlanArrangementViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository
) : ViewModel() {
    var period by mutableStateOf(
        TaskPeriod.entries[savedStateHandle.toRoute<PlanArrangementRoute>().periodOrdinal]
    )
        private set
}