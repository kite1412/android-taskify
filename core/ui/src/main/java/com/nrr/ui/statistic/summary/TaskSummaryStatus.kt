package com.nrr.ui.statistic.summary

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nrr.designsystem.theme.PastelGreen
import com.nrr.designsystem.theme.PastelOrange
import com.nrr.designsystem.theme.Red
import com.nrr.ui.util.UIDictionary

enum class TaskSummaryStatus {
    COMPLETED,
    NOT_COMPLETED,
    LATE;

    internal fun color() = when (this) {
        COMPLETED -> PastelGreen
        NOT_COMPLETED -> Red
        LATE -> PastelOrange
    }

    private fun toStringId() = when (this) {
        COMPLETED -> UIDictionary.completed
        NOT_COMPLETED -> UIDictionary.notCompleted
        LATE -> UIDictionary.late
    }

    @Composable
    internal fun toStringLocalized() = stringResource(toStringId())

    internal fun toStringLocalized(context: Context) =
        context.getString(toStringId())
}