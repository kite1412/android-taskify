package com.nrr.analytics.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nrr.model.TaskPeriod

@Composable
fun TaskPeriod.toStringLocalized() = stringResource(
    id = when (this) {
        TaskPeriod.DAY -> AnalyticsDictionary.day
        TaskPeriod.WEEK -> AnalyticsDictionary.week
        TaskPeriod.MONTH -> AnalyticsDictionary.month
    }
)