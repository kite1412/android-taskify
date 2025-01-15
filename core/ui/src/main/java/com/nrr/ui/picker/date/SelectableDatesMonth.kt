package com.nrr.ui.picker.date

import com.nrr.model.toLocalDateTime
import com.nrr.ui.picker.CustomSelectableDates
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

object SelectableDatesMonth : CustomSelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val curDate = Clock.System.now().toLocalDateTime()
        val date = Instant.fromEpochMilliseconds(utcTimeMillis)
            .toLocalDateTime()

        return curDate.month == date.month
                && date.dayOfMonth >= curDate.dayOfMonth
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year == Clock.System.now().toLocalDateTime().year
    }
}