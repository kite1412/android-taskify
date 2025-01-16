package com.nrr.planarrangement

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nrr.model.TaskPeriod
import com.nrr.planarrangement.util.PlanArrangementDictionary

@Composable
internal fun TaskPeriod.string() = stringResource(
    when (this) {
        TaskPeriod.DAY -> PlanArrangementDictionary.today
        TaskPeriod.WEEK -> PlanArrangementDictionary.thisWeek
        TaskPeriod.MONTH -> PlanArrangementDictionary.thisMonth
    }
)