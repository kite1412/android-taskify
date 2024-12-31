package com.nrr.ui

import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.Gold
import com.nrr.designsystem.theme.Gray
import com.nrr.designsystem.theme.PastelBlue
import com.nrr.designsystem.theme.PastelGreen
import com.nrr.designsystem.theme.PastelOrange
import com.nrr.designsystem.theme.PastelPink
import com.nrr.model.Task
import com.nrr.model.TaskType

fun Task.color() = when (taskType) {
    TaskType.PERSONAL -> PastelBlue
    TaskType.WORK -> Gray
    TaskType.LEARNING -> PastelOrange
    TaskType.HEALTH -> PastelPink
    TaskType.REFLECTION -> PastelGreen
    TaskType.SPECIAL -> Gold
}

fun Task.iconId() = when (taskType) {
    TaskType.PERSONAL -> TaskifyIcon.profile
    TaskType.WORK -> TaskifyIcon.workCase
    TaskType.LEARNING -> TaskifyIcon.book
    TaskType.HEALTH -> TaskifyIcon.heartPulse
    TaskType.REFLECTION -> TaskifyIcon.pray
    TaskType.SPECIAL -> TaskifyIcon.star
}