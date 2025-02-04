package com.nrr.ui.statistic.summary.util

import android.content.Context
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.SolidColor
import com.nrr.designsystem.theme.PastelPink
import com.nrr.model.Summary
import com.nrr.model.TaskPeriod
import com.nrr.model.TaskSummary
import com.nrr.model.toLocalDateTime
import com.nrr.ui.color
import com.nrr.ui.statistic.summary.LineChartOption
import com.nrr.ui.statistic.summary.PieChartOption
import com.nrr.ui.statusLogic
import com.nrr.ui.toStringLocalized
import com.nrr.ui.util.UIDictionary
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie

@Composable
internal fun Summary.getPieChartData(
    option: PieChartOption,
    selectedLabel: String
): List<Pie> {
    return when (option) {
        PieChartOption.STATUS -> tasks.groupBy(TaskSummary::statusLogic)
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

internal fun Summary.getLineChartData(
    context: Context,
    option: LineChartOption
): List<Line> {
    require(period != TaskPeriod.DAY) {
        "Line chart is not supported for day summaries"
    }

    return when (option) {
        LineChartOption.TASK_TREND -> {
            val animationSpec = tween<Float>(durationMillis = 500)

            listOf(
                Line(
                    label = context.getString(UIDictionary.taskTrend),
                    values = tasks
                        .groupBy {
                            it.startDate.toLocalDateTime().dayOfMonth
                        }
                        .map { (_, v) ->
                            v.size.toDouble()
                        },
                    color = SolidColor(PastelPink),
                    firstGradientFillColor = PastelPink.copy(alpha = 0.5f),
                    secondGradientFillColor = PastelPink.copy(alpha = 0.1f),
                    strokeAnimationSpec = animationSpec,
                    gradientAnimationSpec = animationSpec,
                    gradientAnimationDelay = 0
                )
            )
        }
    }
}