package com.nrr.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.nrr.designsystem.theme.PastelOrange
import com.nrr.model.TaskPriority

fun TaskPriority.color() = when (this) {
    TaskPriority.NORMAL -> Color.Green
    TaskPriority.HIGH -> PastelOrange
    TaskPriority.CRITICAL -> Color.Red
}

@Composable
fun TaskPriority.toStringLocalized() = with(getCurrentLocale()) {
    if (this.language == "in") toStringIn()
    else this@toStringLocalized.toString()
}