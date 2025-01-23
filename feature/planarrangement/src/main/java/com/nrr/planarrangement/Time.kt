package com.nrr.planarrangement

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState

internal data class Time(
    val minute: Int = 0,
    val hour: Int = 0
) : Comparable<Time> {
    override infix fun compareTo(other: Time): Int {
        val checkHour = hour compareTo other.hour
        if (checkHour != 0) {
            return checkHour
        }
        val checkMinute = minute compareTo other.minute
        return checkMinute
    }

    override fun toString(): String {
        return "${"%02d".format(hour)}:${"%02d".format(minute)}"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
internal fun TimePickerState.toTime() = Time(
    minute = minute,
    hour = hour
)