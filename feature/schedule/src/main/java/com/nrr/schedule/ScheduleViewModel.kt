package com.nrr.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.nrr.model.TaskPeriod
import com.nrr.model.TimeOffset
import com.nrr.model.TimeUnit
import com.nrr.model.toLocalDateTime
import com.nrr.schedule.navigation.ScheduleRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    internal val period = TaskPeriod.entries[savedStateHandle.toRoute<ScheduleRoute>().periodOrdinal]
    internal var timeOffset by mutableStateOf(TimeOffset(0, TimeUnit.MINUTES))
        private set
    internal var dailySchedule by mutableStateOf(true)
        private set
    internal var date by mutableStateOf(Clock.System.now().toLocalDateTime().date)
        private set

    internal fun onTimeOffsetValueChange(new: Int) {
        timeOffset = timeOffset.copy(value = new)
    }

    internal fun onTimeOffsetTimeUnitChange(new: TimeUnit) {
        timeOffset = timeOffset.copy(timeUnit = new)
    }

    internal fun onDailyScheduleChange(new: Boolean) {
        dailySchedule = new
    }

    internal fun onDateChange(new: LocalDate) {
        date = new
    }
}