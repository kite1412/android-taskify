package com.nrr.planarrangement

import androidx.compose.runtime.Composable
import com.nrr.model.toLocalDateTime
import com.nrr.ui.toDateStringLocalized
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetIn
import kotlinx.datetime.toInstant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

internal data class Date(
    val time: Time = Time(),
    val dayOfMonth: Int? = null,
    val month: Int? = null
) : Comparable<Date> {
    fun toInstant(
        ignoreTime: Boolean = false,
        useCurrentTimeZone: Boolean = false
    ): Instant {
        val curDate = Clock.System.now()
        val localDateTime = with(curDate.toLocalDateTime()) {
            LocalDateTime(
                year = year,
                month = this@Date.month?.let { Month(it) } ?: month,
                dayOfMonth = this@Date.dayOfMonth ?: dayOfMonth,
                hour = if (!ignoreTime) this@Date.time.hour else hour,
                minute = if (!ignoreTime) this@Date.time.minute else minute
            )
        }

        return localDateTime.toInstant(TimeZone.currentSystemDefault()) +
            (if (useCurrentTimeZone)
                curDate.offsetIn(TimeZone.currentSystemDefault()).totalSeconds / 3600
            else 0).hours
    }

    override infix fun compareTo(other: Date): Int {
        if (dayOfMonth != null && other.dayOfMonth != null) {
            val checkDayOfMonth = dayOfMonth compareTo other.dayOfMonth
            if (checkDayOfMonth != 0) {
                return checkDayOfMonth
            }
        }
        return time compareTo other.time
    }

    @Composable
    fun toStringLocalized() = with(toInstant()) {
        toDateStringLocalized() + " ($time)"
    }

    operator fun minus(duration: Duration): Date =
        (toInstant() - duration).toDate()
}

internal fun Instant.toDate() = with(toLocalDateTime()) {
    Date(
        time = Time(hour = hour, minute = minute),
        dayOfMonth = dayOfMonth,
        month = monthNumber
    )
}