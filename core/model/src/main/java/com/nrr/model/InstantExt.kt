package com.nrr.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.time.YearMonth
import kotlin.math.abs

fun Instant.toLocalDateTime(): LocalDateTime =
    toLocalDateTime(TimeZone.currentSystemDefault())

fun Instant.toTimeString(withSecond: Boolean = false): String =
    formatTimeString(toLocalDateTime().time, withSecond)

fun LocalTime.toTimeString(withSecond: Boolean = false) = formatTimeString(this, withSecond)

fun Instant.getStartDate(period: TaskPeriod) = with(toLocalDateTime()) {
    val priorMonth = YearMonth.of(
        if (monthNumber != 1) year else year - 1,
        if (monthNumber != 1) monthNumber - 1 else 12
    )
    val domForWeek = dayOfMonth - dayOfWeek.value
    val dayOfMonth = when (period) {
        TaskPeriod.DAY -> dayOfMonth
        TaskPeriod.WEEK -> if (domForWeek >= 0) domForWeek + 1
        else priorMonth.lengthOfMonth() - abs(domForWeek) + 1
        TaskPeriod.MONTH -> 1
    }
    val monthNumber = when (period) {
        TaskPeriod.WEEK -> if (domForWeek < 0) priorMonth.monthValue
        else monthNumber
        else -> monthNumber
    }
    val year = when (period) {
        TaskPeriod.WEEK -> if (domForWeek < 0) priorMonth.year
        else year
        else -> year
    }

    LocalDateTime(
        year = year,
        monthNumber = monthNumber,
        dayOfMonth = dayOfMonth,
        hour = 0,
        minute = 0,
        second = 0
    ).toInstant(TimeZone.currentSystemDefault())
}

fun Instant.getEndDate(
    period: TaskPeriod
): Instant = with(toLocalDateTime()) {
    val thisMonth = YearMonth.of(year, monthNumber)
    val daysThisMonth = thisMonth.lengthOfMonth()
    val domForWeek = dayOfMonth + (7 - dayOfWeek.value)
    val dayOfMonth = when (period) {
        TaskPeriod.DAY -> dayOfMonth
        TaskPeriod.WEEK -> if (domForWeek > daysThisMonth) domForWeek - daysThisMonth
        else domForWeek
        TaskPeriod.MONTH -> daysThisMonth
    }
    val monthNumber = when (period) {
        TaskPeriod.WEEK -> if (domForWeek > daysThisMonth) monthNumber + 1
        else monthNumber
        else -> monthNumber
    }
    val year = when (period) {
        TaskPeriod.WEEK -> if (domForWeek > daysThisMonth
            && monthNumber == 12
        ) year + 1
        else year
        else -> year
    }

    LocalDateTime(
        year = year,
        monthNumber = monthNumber,
        dayOfMonth = dayOfMonth,
        hour = 23,
        minute = 59,
        second = 59
    ).toInstant(TimeZone.currentSystemDefault())
}

private fun formatTimeString(
    time: LocalTime,
    withSecond: Boolean = false
) = "${time.hour.toString().padStart(2, '0')}:" +
        time.minute.toString().padStart(2, '0') +
        if (withSecond) ":" + time.second.toString().padStart(2, '0') else ""