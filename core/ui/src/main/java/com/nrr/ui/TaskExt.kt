package com.nrr.ui

import androidx.compose.ui.graphics.Color
import com.nrr.designsystem.theme.PastelBlue
import com.nrr.designsystem.theme.PastelGreen
import com.nrr.designsystem.theme.PastelOrange
import com.nrr.designsystem.theme.PastelPink
import com.nrr.model.Task
import com.nrr.model.TaskType

// TODO change magenta
fun Task.color() = when (taskType) {
    TaskType.PERSONAL -> PastelBlue
    TaskType.WORK -> Color.Magenta
    TaskType.LEARNING -> PastelOrange
    TaskType.HEALTH -> PastelPink
    TaskType.REFLECTION -> PastelGreen
    TaskType.SPECIAL -> Color.Magenta
}