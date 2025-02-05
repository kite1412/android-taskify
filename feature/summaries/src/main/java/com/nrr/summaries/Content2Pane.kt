package com.nrr.summaries

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.model.Summary
import com.nrr.model.TaskPeriod
import com.nrr.ui.DevicePreviews
import com.nrr.ui.statistic.summary.ColumnChartOption
import com.nrr.ui.statistic.summary.PieChartOption
import kotlinx.coroutines.launch

@Composable
internal fun Content2Pane(
    summaries: List<Summary>,
    onSummaryClick: (Summary) -> Unit,
    onBackClick: () -> Unit,
    period: TaskPeriod,
    onPeriodClick: (TaskPeriod) -> Unit,
    selectedSummary: Summary?,
    modifier: Modifier = Modifier
) {
    var pieChartOption by remember {
        mutableStateOf(PieChartOption.STATUS)
    }
    var columnChartOption by remember {
        mutableStateOf(ColumnChartOption.TASK_TREND)
    }

    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val state = rememberLazyListState()
        val scope = rememberCoroutineScope()
        val summaryClickWrapper = { s: Summary ->
            scope.launch {
                onSummaryClick(s)
                state.animateScrollToItem(0)
            }
            Unit
        }
        val periodClickWrapper = { p: TaskPeriod ->
            scope.launch {
                onPeriodClick(p)
                state.animateScrollToItem(0)
            }
            Unit
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.3f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Header(
                onBackClick = onBackClick
            )
            PeriodsTab(
                period = period,
                onPeriodClick = periodClickWrapper
            )
            Summaries(
                summaries = summaries,
                onClick = summaryClickWrapper,
                showIcon = false,
                selectedSummary = selectedSummary
            )
        }
        if (selectedSummary != null) SummaryDetail(
            summary = selectedSummary,
            pieChartOption = pieChartOption,
            lineChartOption = columnChartOption,
            onPieChartOptionClick = { pieChartOption = it },
            onColumnChartOptionClick = { columnChartOption = it },
            modifier = Modifier.weight(0.7f),
            state = state
        )
    }
}

@Preview
@DevicePreviews
@Composable
private fun Content2PanePreview() {
    TaskifyTheme {
        Content2Pane(
            summaries = listOf(),
            onSummaryClick = {},
            onBackClick = {},
            period = TaskPeriod.DAY,
            onPeriodClick = {},
            selectedSummary = null
        )
    }
}