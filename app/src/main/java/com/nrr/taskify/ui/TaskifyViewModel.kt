package com.nrr.taskify.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrr.data.repository.UserDataRepository
import com.nrr.designsystem.component.TaskifyTopAppBarDefaults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskifyViewModel @Inject constructor(
    userDataRepository: UserDataRepository
) : ViewModel() {
    private var slidingTextJob: Job? = null

    var titleIndex by mutableIntStateOf(0)
        private set

    var showSplash by mutableStateOf(true)
        private set

    var showContent by mutableStateOf(false)
        private set

    var safeToAnimate by mutableStateOf(false)
        private set

    val userData = userDataRepository.userData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val registered = combine(
        userDataRepository.userData,
        snapshotFlow { showContent }
    ) { userData, showContent ->
        if (userData.username.isNotEmpty() && showContent) {
            titleIndex = 0
            slidingTextJob?.cancel()
            startSlidingText()
            true
        } else false
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val contentEnterDelay = 300

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

    fun dismissSplash(delay: Long) = viewModelScope.launch {
        showSplash = false
        // create an overlapping effect instead of
        // waiting for the splash screen to fully disappear
        delay(delay / 5)
        showContent = true
        delay(contentEnterDelay + 200L)
        safeToAnimate = true
    }
}