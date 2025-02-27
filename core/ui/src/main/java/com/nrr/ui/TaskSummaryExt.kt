package com.nrr.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nrr.model.TaskSummary
import com.nrr.ui.statistic.summary.TaskSummaryStatus
import com.nrr.ui.util.UIDictionary
import com.nrr.ui.util.resolveProgressStatus

@Composable
fun TaskSummary.stringStatus() = stringResource(stringStatusId())

fun TaskSummary.stringStatusId() = with(statusLogic()) {
    when (this) {
        TaskSummaryStatus.NOT_COMPLETED -> UIDictionary.notCompleted
        TaskSummaryStatus.LATE -> UIDictionary.late
        else -> UIDictionary.completed
    }
}

@Composable
fun TaskSummary.statusColor() = statusLogic().color()

internal fun TaskSummary.statusLogic() = when (
    resolveProgressStatus(
        target = completedAt,
        limit = dueDate
    )
) {
    -1 -> TaskSummaryStatus.NOT_COMPLETED
    0 -> TaskSummaryStatus.LATE
    else -> TaskSummaryStatus.COMPLETED
}