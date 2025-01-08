package com.nrr.designsystem.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object TaskifyDefault {
    const val HEADER_FONT_SIZE = 24
    const val EMPTY_LABEL_FONT_SIZE = 20
    const val EMPTY_ICON_SIZE = 60
    const val EMPTY_WARNING_CONTENT_SPACE = 16
    val emptyWarningContentColor: Color
        @Composable get() = MaterialTheme.colorScheme.primary.copy(0.6f)
}