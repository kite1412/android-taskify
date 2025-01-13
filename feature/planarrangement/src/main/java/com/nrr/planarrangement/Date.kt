package com.nrr.planarrangement

import com.nrr.model.toLocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

internal data class Date(
    val time: Time,
    val dayOfWeek: Int? = null,
    val dayOfMonth: Int? = null
) {
    fun toInstant(): Instant {
        val curDate = Clock.System.now().toLocalDateTime()
        val localDateTime = LocalDateTime(
            year = curDate.year,
            month = curDate.month,
            dayOfMonth = dayOfMonth ?: curDate.dayOfMonth,
            hour = time.hour,
            minute = time.minute
        )

        return localDateTime.toInstant(TimeZone.UTC)
    }
}