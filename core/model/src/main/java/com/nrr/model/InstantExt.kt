package com.nrr.model

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Instant.toTimeString(): String =
    toLocalDateTime(TimeZone.currentSystemDefault()).run {
        "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
    }

fun Instant.toDateString(): String =
    toLocalDateTime(TimeZone.currentSystemDefault()).run {
        "$dayOfMonth " +
                "${
                    month.toString()
                        .lowercase()
                        .replaceFirstChar { it.uppercase() }
                } " +
                "$year"
    }