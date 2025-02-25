package com.nrr.data

import com.nrr.data.util.normalize
import com.nrr.model.TaskPeriod
import com.nrr.model.toLocalDateTime
import com.nrr.model.getStartDate
import com.nrr.model.getEndDate
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.Test

class InstantTest {
    @Test
    fun normalize_isCorrect() {
        val now = Clock.System.now()
        val normalized = now.normalize()
        println(now)
        println(normalized)
        with(normalized.toLocalDateTime()) {
            assert(hour == 0)
            assert(minute == 0)
            assert(second == 0)
        }
    }

    @Test
    fun getWeekStartDate_isCorrect() {
        // NOTE: 12 Jan is SUN
        val startDate = LocalDateTime(
            year = 2025,
            monthNumber = 1,
            dayOfMonth = 12,
            hour = 1,
            minute = 1,
            second = 1
        )
            .toInstant(TimeZone.currentSystemDefault())
            .getStartDate(TaskPeriod.WEEK)
            .toLocalDateTime()
        assert(startDate.dayOfMonth == 6)
    }

    @Test
    fun getWeekStartDate_priorMonth_isCorrect() {
        // NOTE: 2 Feb is SUN & Jan has 31 days
        val startDate = LocalDateTime(
            year = 2025,
            monthNumber = 2,
            dayOfMonth = 2,
            hour = 1,
            minute = 1,
        )
            .toInstant(TimeZone.currentSystemDefault())
            .getStartDate(TaskPeriod.WEEK)
            .toLocalDateTime()
        assert(startDate.dayOfMonth == 27)
    }

    @Test
    fun getWeekStartDate_priorYear_isCorrect() {
        // NOTE: 1 Jan is WED & Dec has 31 days
        val startDate = LocalDateTime(
            year = 2025,
            monthNumber = 1,
            dayOfMonth = 1,
            hour = 1,
            minute = 1,
            second = 1
        )
            .toInstant(TimeZone.currentSystemDefault())
            .getStartDate(TaskPeriod.WEEK)
            .toLocalDateTime()

        assert(
            startDate.month.value == 12
                    && startDate.year == 2024
                    && startDate.dayOfMonth == 30
        )
    }

    @Test
    fun getWeekEndDate_isCorrect() {
        // NOTE: 1 Jan is WED
        val date = LocalDateTime(
            year = 2025,
            monthNumber = 1,
            dayOfMonth = 1,
            hour = 1,
            minute = 1,
            second = 1
        )
            .toInstant(TimeZone.currentSystemDefault())
            .getEndDate(TaskPeriod.WEEK)
            .toLocalDateTime()

        assert(date.dayOfMonth == 5)
    }

    @Test
    fun getWeekEndDate_subsequentMonth_isCorrect() {
        // NOTE: 31 Jan is FRI
        val date = LocalDateTime(
            year = 2025,
            monthNumber = 1,
            dayOfMonth = 31,
            hour = 1,
            minute = 1,
            second = 1
        )
            .toInstant(TimeZone.currentSystemDefault())
            .getEndDate(TaskPeriod.WEEK)
            .toLocalDateTime()

        assert(
            date.dayOfMonth == 2
                    && date.month.value == 2
        )
    }
}