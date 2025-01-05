package com.nrr.ui

import androidx.compose.runtime.Composable
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.Gold
import com.nrr.designsystem.theme.Gray
import com.nrr.designsystem.theme.PastelBlue
import com.nrr.designsystem.theme.PastelGreen
import com.nrr.designsystem.theme.PastelOrange
import com.nrr.designsystem.theme.PastelPink
import com.nrr.model.TaskType

fun TaskType.color() = when (this) {
    TaskType.PERSONAL -> PastelBlue
    TaskType.WORK -> Gray
    TaskType.LEARNING -> PastelOrange
    TaskType.HEALTH -> PastelPink
    TaskType.REFLECTION -> PastelGreen
    TaskType.SPECIAL -> Gold
}

fun TaskType.iconId() = when (this) {
    TaskType.PERSONAL -> TaskifyIcon.profile
    TaskType.WORK -> TaskifyIcon.workCase
    TaskType.LEARNING -> TaskifyIcon.book
    TaskType.HEALTH -> TaskifyIcon.heartPulse
    TaskType.REFLECTION -> TaskifyIcon.pray
    TaskType.SPECIAL -> TaskifyIcon.star
}

@Composable
fun TaskType.toStringLocalized() =
    with(getCurrentLocale()) {
        if (this.language == "in") toStringIn()
        else this@toStringLocalized.toString()
    }