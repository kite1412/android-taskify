package com.nrr.weeklyschedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nrr.weeklyschedule.util.Day
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeeklyScheduleViewModel @Inject constructor() : ViewModel() {
    internal var selectedDay by mutableStateOf(Day.MONDAY)
}