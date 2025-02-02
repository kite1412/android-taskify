package com.nrr.summaries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nrr.data.repository.SummaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SummariesViewModel @Inject constructor(
    private val summaryRepository: SummaryRepository
) : ViewModel() {
    val summaries = summaryRepository.getSummaries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}