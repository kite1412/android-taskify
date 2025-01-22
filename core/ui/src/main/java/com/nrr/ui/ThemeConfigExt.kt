package com.nrr.ui

import androidx.compose.runtime.Composable
import com.nrr.model.ThemeConfig

@Composable
fun ThemeConfig.toStringLocalized() = with(getCurrentLocale()) {
    if (language == "in") toStringIn()
    else this@toStringLocalized.toString()
}