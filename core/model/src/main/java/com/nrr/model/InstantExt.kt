package com.nrr.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Instant.toLocalDateTime(): LocalDateTime =
    toLocalDateTime(TimeZone.currentSystemDefault())

fun Instant.toTimeString(withSecond: Boolean = false): String =
    toLocalDateTime().run {
        "${hour.toString().padStart(2, '0')}:" +
                minute.toString().padStart(2, '0') +
                if (withSecond) ":" + second.toString().padStart(2, '0') else ""
    }

fun Instant.toDateString(): String =
    toLocalDateTime().run {
        "$dayOfMonth " +
                "${
                    month.toString()
                        .lowercase()
                        .replaceFirstChar { it.uppercase() }
                } " +
                "$year"
    }

fun Instant.toDayOfWeekValue(): Int =
    toLocalDateTime().dayOfWeek.value