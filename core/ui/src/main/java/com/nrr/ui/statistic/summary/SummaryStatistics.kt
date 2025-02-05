package com.nrr.ui.statistic.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.model.Summary
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.model.TaskSummary
import com.nrr.ui.TaskPreviewParameter
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days

@Composable
fun SummaryStatistics(
    summary: Summary,
    pieChartOption: PieChartOption,
    lineChartOption: ColumnChartOption,
    onPieChartOptionClick: (PieChartOption) -> Unit,
    onLineChartOptionClick: (ColumnChartOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        if (summary.period != TaskPeriod.DAY) SummaryColumnChartStatistic(
            summary = summary,
            option = lineChartOption,
            onOptionClick = onLineChartOptionClick
        )
        SummaryPieChartStatistic(
            summary = summary,
            option = pieChartOption,
            onOptionClick = onPieChartOptionClick
        )
    }
}

@Preview
@Composable
private fun SummaryStatisticsPreview(
    @PreviewParameter(TaskPreviewParameter::class)
    tasks: List<Task>
) {
    val date = Clock.System.now() - 1.days
    var pieChartOption by remember {
        mutableStateOf(PieChartOption.STATUS)
    }
    var lineChartOption by remember {
        mutableStateOf(ColumnChartOption.TASK_TREND)
    }

    TaskifyTheme {
        SummaryStatistics(
            summary = Summary(
                id = 1,
                period = TaskPeriod.WEEK,
                startDate = date,
                endDate = date + 1.days,
                tasks = tasks.map {
                    with(it.activeStatuses.first()) {
                        TaskSummary(
                            id = id,
                            title = it.title,
                            description = it.description,
                            startDate = startDate,
                            completedAt = completedAt,
                            taskType = it.taskType,
                            dueDate = dueDate
                        )
                    }
                }
            ),
            pieChartOption = pieChartOption,
            lineChartOption = lineChartOption,
            onPieChartOptionClick = { pieChartOption = it },
            onLineChartOptionClick = { lineChartOption = it }
        )
    }
}