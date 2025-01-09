package com.nrr.ui

import androidx.compose.runtime.Composable
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle

@Composable
fun Instant.toDayLocalized(): String =
    toLocalDateTime(TimeZone.currentSystemDefault())
        .dayOfWeek.getDisplayName(
            TextStyle.FULL,
            getCurrentLocale()
        )

@Composable
fun LocalDateTime.toMonthLocalized(): String =
    month.getDisplayName(
        TextStyle.FULL,
        getCurrentLocale()
    )