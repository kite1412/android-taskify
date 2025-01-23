package com.nrr.planarrangement

import androidx.compose.runtime.Composable
import com.nrr.model.toLocalDateTime
import com.nrr.ui.toDateStringLocalized
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Duration

internal data class Date(
    val time: Time = Time(),
    val dayOfMonth: Int? = null
) : Comparable<Date> {
    fun toInstant(
        ignoreTime: Boolean = false,
        dayOfMonth: Int? = null
    ): Instant {
        val curDate = Clock.System.now().toLocalDateTime()
        val localDateTime = LocalDateTime(
            year = curDate.year,
            month = curDate.month,
            dayOfMonth = this.dayOfMonth ?: dayOfMonth ?: curDate.dayOfMonth,
            hour = if (!ignoreTime) time.hour else curDate.hour,
            minute = if (!ignoreTime) time.minute else curDate.minute
        )

        return localDateTime.toInstant(TimeZone.currentSystemDefault())
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
        dayOfMonth = dayOfMonth
    )
}