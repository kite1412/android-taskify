package com.nrr.settings.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nrr.model.TimeUnit

@Composable
fun TimeUnit.toStringLocalized() = stringResource(
    when (this) {
        TimeUnit.MINUTES -> SettingsDictionary.minute
        TimeUnit.HOURS -> SettingsDictionary.hour
        TimeUnit.DAYS -> SettingsDictionary.day
    }
)