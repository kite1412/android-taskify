package com.nrr.settings.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nrr.model.ReminderType

@Composable
internal fun ReminderType.toStringLocalized() = stringResource(
    id = when (this) {
        ReminderType.START -> SettingsDictionary.reminderStart
        ReminderType.END -> SettingsDictionary.reminderEnd
    }
)