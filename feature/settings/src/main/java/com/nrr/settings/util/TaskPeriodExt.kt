package com.nrr.settings.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nrr.model.TaskPeriod
import com.nrr.settings.NotificationOffsetConstraint

@Composable
fun TaskPeriod.toStringLocalized() = stringResource(
    when (this) {
        TaskPeriod.DAY -> SettingsDictionary.dayPeriod
        TaskPeriod.WEEK -> SettingsDictionary.weekPeriod
        TaskPeriod.MONTH -> SettingsDictionary.monthPeriod
    }
)

internal fun TaskPeriod.notificationOffsetConstraint() = when (this) {
    TaskPeriod.DAY -> NotificationOffsetConstraint.Day
    TaskPeriod.WEEK -> NotificationOffsetConstraint.Week
    TaskPeriod.MONTH -> NotificationOffsetConstraint.Month
}