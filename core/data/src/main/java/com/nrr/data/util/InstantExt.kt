package com.nrr.data.util

import com.nrr.model.TaskPeriod
import com.nrr.model.toLocalDateTime
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.time.YearMonth
import kotlin.math.abs

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

internal fun Instant.getStartDate(
    period: TaskPeriod
): Instant = with(toLocalDateTime()) {
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

internal fun Instant.getEndDate(
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
        hour = 0,
        minute = 0,
        second = 0
    ).toInstant(TimeZone.currentSystemDefault())
}