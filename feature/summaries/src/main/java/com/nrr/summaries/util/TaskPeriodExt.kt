package com.nrr.summaries.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nrr.model.TaskPeriod

@Composable
fun TaskPeriod.toStringLocalized() = stringResource(
    when (this) {
        TaskPeriod.DAY -> SummariesDictionary.day
        TaskPeriod.WEEK -> SummariesDictionary.week
        TaskPeriod.MONTH -> SummariesDictionary.month
    }
)