package com.nrr.summary.util

import com.nrr.model.SummariesGenerationReport
import com.nrr.model.TaskPeriod
import com.nrr.model.getEndDate
import com.nrr.model.getStartDate
import kotlinx.datetime.Instant

internal fun SummariesGenerationReport.generatedToday(todayInstant: Instant) = with(todayInstant) {
    lastGenerationDate in getStartDate(TaskPeriod.DAY)..getEndDate(TaskPeriod.DAY)
}