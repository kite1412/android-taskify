package com.nrr.ui.util

import com.nrr.designsystem.theme.PastelGreen
import com.nrr.designsystem.theme.PastelOrange
import com.nrr.designsystem.theme.Red
import kotlinx.datetime.Instant

// Convenient method to get progress status based on provided Instants.
//
// outputs:
// -1: not completed, 0: late, 1: completed
fun resolveProgressStatus(
    target: Instant?,
    limit: Instant?
) = when {
    target == null -> -1
    limit != null && limit < target -> 0
    else -> 1
}

fun statusColor(status: Int) = when (status) {
    -1 -> Red
    0 -> PastelOrange
    else -> PastelGreen
}

fun statusNameId(status: Int) = when (status) {
    -1 -> UIDictionary.notCompleted
    0 -> UIDictionary.late
    else -> UIDictionary.completed
}