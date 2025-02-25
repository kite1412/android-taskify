package com.nrr.ui.picker.date

import com.nrr.model.TaskPeriod
import com.nrr.model.getEndDate
import com.nrr.model.getStartDate
import com.nrr.model.toLocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

object SelectableDatesWeek : CustomSelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val curDate = Clock.System.now()
        val date = Instant.fromEpochMilliseconds(utcTimeMillis)
        val start = curDate.getStartDate(TaskPeriod.WEEK)
        val end = curDate.getEndDate(TaskPeriod.WEEK)
        val curDateNormalized = curDate.getStartDate(TaskPeriod.DAY)

        return date in start..end
                && curDateNormalized <= date
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year == Clock.System.now().toLocalDateTime().year
    }
}