package com.nrr.ui.picker.date

import com.nrr.model.toLocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

object SelectableDatesWeek : CustomSelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val curDate = Clock.System.now().toLocalDateTime()
        val date = Instant.fromEpochMilliseconds(utcTimeMillis)
            .toLocalDateTime()
        val firstDayOfWeek = curDate.dayOfMonth - (curDate.dayOfWeek.value - 1)
        val lastDayOfWeek = firstDayOfWeek + 6

        return date.month == curDate.month
                && date.dayOfMonth in firstDayOfWeek..lastDayOfWeek
                && date.dayOfWeek >= curDate.dayOfWeek
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year == Clock.System.now().toLocalDateTime().year
    }
}