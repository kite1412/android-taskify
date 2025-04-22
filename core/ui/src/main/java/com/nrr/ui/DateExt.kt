package com.nrr.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import com.nrr.model.toLocalDateTime
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

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
fun Instant.toDateStringLocalized(): String = getDateString(getCurrentLocale())

fun Instant.toDateStringLocalized(context: Context): String = getDateString(
    with(context.resources.configuration) {
        ConfigurationCompat.getLocales(this).get(0)
            ?: LocaleListCompat.getDefault()[0]!!
    }
)

private fun Instant.getDateString(locale: Locale) = with(toLocalDateTime()) {
    "$dayOfMonth " +
            "${
                month.getDisplayName(
                    TextStyle.FULL,
                    locale
                )
            } " +
            "$year"
}