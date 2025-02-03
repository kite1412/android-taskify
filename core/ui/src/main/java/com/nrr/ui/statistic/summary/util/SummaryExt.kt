package com.nrr.ui.statistic.summary.util

import androidx.compose.runtime.Composable
import com.nrr.model.Summary
import com.nrr.ui.statistic.summary.PieChartOption
import com.nrr.ui.statistic.summary.TaskSummaryStatus
import ir.ehsannarmani.compose_charts.models.Pie

@Composable
internal fun Summary.getPieChartData(option: PieChartOption): List<Pie> {
    return when (option) {
        PieChartOption.STATUS -> {
            val groups = tasks.groupBy {
                when {
                    it.completedAt == null -> TaskSummaryStatus.NOT_COMPLETED
                    it.dueDate != null && it.dueDate!! > it.completedAt!! -> TaskSummaryStatus.COMPLETED
                    else -> TaskSummaryStatus.LATE
                }
            }
            val pies = mutableListOf<Pie>()

            groups.forEach { (k, v) ->
                pies.add(
                    Pie(
                        label = k.toStringLocalized(),
                        data = v.size.toDouble(),
                        color = k.color()
                    )
                )
            }
            pies
        }
        // TODO modify TaskSummary model
        PieChartOption.TASK_TYPE -> listOf()
    }
}