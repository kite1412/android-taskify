package com.nrr.ui.statistic.summary.util

import androidx.compose.runtime.Composable
import com.nrr.model.Summary
import com.nrr.ui.color
import com.nrr.ui.statistic.summary.PieChartOption
import com.nrr.ui.statistic.summary.TaskSummaryStatus
import com.nrr.ui.toStringLocalized
import ir.ehsannarmani.compose_charts.models.Pie

@Composable
internal fun Summary.getPieChartData(
    option: PieChartOption,
    selectedLabel: String
): List<Pie> {
    return when (option) {
        PieChartOption.STATUS -> tasks.groupBy {
            when {
                it.completedAt == null -> TaskSummaryStatus.NOT_COMPLETED
                it.dueDate != null && it.dueDate!! < it.completedAt!! -> TaskSummaryStatus.LATE
                else -> TaskSummaryStatus.COMPLETED
            }
        }
            .map { (t, l) ->
                val label = t.toStringLocalized()
                Pie(
                    label = label,
                    data = l.size.toDouble(),
                    color = t.color(),
                    selected = selectedLabel == label
                )
            }
        PieChartOption.TASK_TYPE -> tasks
            .groupBy { it.taskType }
            .map { (t, l) ->
                val label = t.toStringLocalized()
                Pie(
                    label = label,
                    data = l.size.toDouble(),
                    color = t.color(),
                    selected = selectedLabel == label
                )
            }
    }
}