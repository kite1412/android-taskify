package com.nrr.summaries

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.model.Summary
import com.nrr.model.TaskPeriod
import com.nrr.ui.statistic.summary.ColumnChartOption
import com.nrr.ui.statistic.summary.PieChartOption

@Composable
internal fun Content(
    summaries: List<Summary>,
    period: TaskPeriod,
    selectedSummary: Summary?,
    showingDetail: Boolean,
    onBackClick: () -> Unit,
    onSummaryClick: (Summary) -> Unit,
    onPeriodClick: (TaskPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    var pieChartOption by remember {
        mutableStateOf(PieChartOption.STATUS)
    }
    var columnChartOption by remember {
        mutableStateOf(ColumnChartOption.TASK_TREND)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Header(
            onBackClick = {
                pieChartOption = PieChartOption.STATUS
                onBackClick()
            }
        )
        AnimatedContent(
            targetState = !showingDetail,
            modifier = modifier.fillMaxSize(),
            transitionSpec = {
                fadeIn() + slideInHorizontally {
                    if (targetState) -it else it
                } togetherWith
                        fadeOut() + slideOutHorizontally {
                    if (targetState) it else -it
                }
            }
        ) {
            if (it) Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!showingDetail) PeriodsTab(
                    period = period,
                    onPeriodClick = onPeriodClick
                )
                Summaries(
                    summaries = summaries,
                    onClick = onSummaryClick,
                    showIcon = true,
                    selectedSummary = selectedSummary
                )
            } else if (selectedSummary != null) SummaryDetail(
                summary = selectedSummary,
                pieChartOption = pieChartOption,
                lineChartOption = columnChartOption,
                onPieChartOptionClick = { o -> pieChartOption = o },
                onColumnChartOptionClick = { o -> columnChartOption = o }
            )
        }
    }
}

@Preview
@Composable
private fun ContentPreview() {
    TaskifyTheme {
        Content(
            onBackClick = {},
            summaries = listOf(),
            period = TaskPeriod.DAY,
            onSummaryClick = {},
            selectedSummary = null,
            showingDetail = false,
            onPeriodClick = {}
        )
    }
}