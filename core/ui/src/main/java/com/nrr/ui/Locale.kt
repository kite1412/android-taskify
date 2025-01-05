package com.nrr.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat

@Composable
fun getCurrentLocale(modifier: Modifier = Modifier) =
    with(LocalConfiguration.current) {
        ConfigurationCompat.getLocales(this).get(0)
            ?: LocaleListCompat.getDefault()[0]!!
    }