package com.nrr.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nrr.model.TaskSummary
import com.nrr.ui.statistic.summary.TaskSummaryStatus
import com.nrr.ui.util.UIDictionary

@Composable
fun TaskSummary.stringStatus() = stringResource(
    with(statusLogic()) {
        when (this) {
            TaskSummaryStatus.NOT_COMPLETED -> UIDictionary.notCompleted
            TaskSummaryStatus.LATE -> UIDictionary.late
            else -> UIDictionary.completed
        }
    }
)

@Composable
fun TaskSummary.statusColor() = statusLogic().color()

internal fun TaskSummary.statusLogic() = when {
    completedAt == null -> TaskSummaryStatus.NOT_COMPLETED
    dueDate != null && dueDate!! < completedAt!! -> TaskSummaryStatus.LATE
    else -> TaskSummaryStatus.COMPLETED
}
