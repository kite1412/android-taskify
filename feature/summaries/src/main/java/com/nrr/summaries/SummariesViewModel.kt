package com.nrr.summaries

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrr.data.repository.SummaryRepository
import com.nrr.model.Summary
import com.nrr.model.TaskPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SummariesViewModel @Inject constructor(
    private val summaryRepository: SummaryRepository
) : ViewModel() {
    var period by mutableStateOf(TaskPeriod.DAY)
        private set

    var summary by mutableStateOf<Summary?>(null)
        private set

    @OptIn(ExperimentalCoroutinesApi::class)
    val summaries = snapshotFlow { period }
        .flatMapLatest {
            summaryRepository.getSummaries()
        }
        .map {
            it.filter { s -> s.period == period }
        }
        .onEach {
            summary = it.firstOrNull()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    var showingDetail by mutableStateOf(false)
        private set

    fun updatePeriod(taskPeriod: TaskPeriod) {
        period = taskPeriod
    }

    fun updateSummary(summary: Summary?) {
        this.summary = summary
        updateShowingDetail(true)
    }

    fun updateShowingDetail(value: Boolean) {
        showingDetail = value
    }
}