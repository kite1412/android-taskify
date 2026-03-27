package com.nrr.ui.picker.date

import com.nrr.model.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

object SelectableDatesMonth : CustomSelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val curDate = Clock.System.now().toLocalDateTime()
        val date = Instant.fromEpochMilliseconds(utcTimeMillis)
            .toLocalDateTime()

        return curDate.month == date.month
                && date.day >= curDate.day
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year == Clock.System.now().toLocalDateTime().year
    }
}