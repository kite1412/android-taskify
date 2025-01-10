package com.nrr.ui

import androidx.compose.runtime.Composable
import com.nrr.model.toLocalDateTime
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import java.time.format.TextStyle

@Composable
fun LocalDateTime.toDayLocalized(): String =
        dayOfWeek.getDisplayName(
            TextStyle.FULL,
            getCurrentLocale()
        )

@Composable
fun Instant.toDayLocalized(): String =
    toLocalDateTime().toDayLocalized()

@Composable
fun LocalDateTime.toMonthLocalized(): String =
    month.getDisplayName(
        TextStyle.FULL,
        getCurrentLocale()
    )

@Composable
fun Instant.toDateStringLocalized(): String =
    with(toLocalDateTime()) {
        val locale = getCurrentLocale()
        "$dayOfMonth " +
                "${
                    month.getDisplayName(
                        TextStyle.FULL,
                        locale
                    )
                } " +
                "$year"
    }