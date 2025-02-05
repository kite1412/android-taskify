package com.nrr.ui.statistic.summary.util

import android.content.Context
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.SolidColor
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import com.nrr.model.Summary
import com.nrr.model.TaskPeriod
import com.nrr.model.TaskSummary
import com.nrr.model.toLocalDateTime
import com.nrr.ui.color
import com.nrr.ui.statistic.summary.ColumnChartOption
import com.nrr.ui.statistic.summary.PieChartOption
import com.nrr.ui.statusLogic
import com.nrr.ui.toStringLocalized
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.datetime.DayOfWeek
import java.time.format.TextStyle

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

internal fun Summary.getColumnChartData(
    context: Context,
    option: ColumnChartOption
): List<Bars> {
    require(period != TaskPeriod.DAY) {
        "Column chart is not supported for day summaries"
    }

    return when (option) {
        ColumnChartOption.TASK_TREND -> {
            val animationSpec = tween<Float>(durationMillis = 500)
            val toBarData: List<TaskSummary>.() -> List<Bars.Data> = {
                groupBy(TaskSummary::statusLogic)
                    .map { (s, t) ->
                        Bars.Data(
                            label = s.toStringLocalized(context),
                            value = t.size.toDouble(),
                            animationSpec = animationSpec,
                            color = SolidColor(s.color())
                        )
                    }
            }

            when (period) {
                TaskPeriod.WEEK -> DayOfWeek.entries.map {
                    Bars(
                        label = it.getDisplayName(
                            TextStyle.SHORT,
                            ConfigurationCompat.getLocales(
                                context.resources.configuration
                            ).get(0) ?: LocaleListCompat.getDefault()[0]!!
                        ),
                        values = tasks
                            .filter {
                                t -> t.startDate.toLocalDateTime().dayOfWeek == it
                            }
                            .toBarData()
                    )
                }
                TaskPeriod.MONTH -> {
                    tasks
                        .groupBy {
                            it.startDate.toLocalDateTime().dayOfMonth
                        }
                        .map { (k, v) ->
                            Bars(
                                label = k.toString(),
                                values = v.toBarData()
                            )
                        }
                }
                else -> throw IllegalArgumentException("Unsupported period: $period")
            }
        }
    }
}