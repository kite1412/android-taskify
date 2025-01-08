package com.nrr.plandetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.nrr.model.TaskPeriod
import com.nrr.plandetail.navigation.PlanDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlanDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val period = TaskPeriod
        .entries[savedStateHandle.toRoute<PlanDetailRoute>().periodOrdinal]

}