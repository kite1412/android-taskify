package com.nrr.taskify.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrr.designsystem.component.Destination
import com.nrr.designsystem.component.TaskifyTopAppBarDefaults
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TaskifyViewModel : ViewModel() {
    var titleIndex by mutableIntStateOf(0)
        private set

    var currentDestination by mutableStateOf(Destination.HOME)
        private set

    val topBarTitles = TaskifyTopAppBarDefaults.defaultTitles

    init {
        startSlidingText()
    }

    private fun startSlidingText() = viewModelScope.launch {
        while (true) {
            delay(3500)
            if (titleIndex < topBarTitles.size - 1) titleIndex++
            else titleIndex = 0
        }
    }

    fun onDestinationChange(destination: Destination) {
        currentDestination = destination
    }
}