package com.nrr.taskify.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrr.data.repository.UserDataRepository
import com.nrr.designsystem.component.Destination
import com.nrr.designsystem.component.TaskifyTopAppBarDefaults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskifyViewModel @Inject constructor(
    userDataRepository: UserDataRepository
) : ViewModel() {
    var titleIndex by mutableIntStateOf(0)
        private set

    var currentDestination by mutableStateOf(Destination.HOME)
        private set

    private var slidingTextJob: Job? = null

    val registered = userDataRepository.userData
        .map {
            it.username.isNotEmpty()
        }
        .onEach {
            if (it) {
                titleIndex = 0
                slidingTextJob?.cancel()
                startSlidingText()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val topBarTitles = TaskifyTopAppBarDefaults.defaultTitles

    private fun startSlidingText() {
        slidingTextJob = viewModelScope.launch {
            while (true) {
                delay(3500)
                if (titleIndex < topBarTitles.size - 1) titleIndex++
                else titleIndex = 0
            }
        }
    }

    fun onDestinationChange(destination: Destination) {
        currentDestination = destination
    }
}