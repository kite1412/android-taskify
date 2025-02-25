package com.nrr.data.util

import com.nrr.model.toLocalDateTime
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

internal fun Instant.normalize() = toLocalDateTime().run {
    LocalDateTime(
        year = year,
        monthNumber = monthNumber,
        dayOfMonth = dayOfMonth,
        hour = 0,
        minute = 0,
        second = 0
    ).toInstant(TimeZone.currentSystemDefault())
}