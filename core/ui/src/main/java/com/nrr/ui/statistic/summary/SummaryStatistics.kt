package com.nrr.ui.statistic.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nrr.model.Summary
import com.nrr.model.TaskPeriod

@Composable
fun SummaryStatistics(
    summary: Summary,
    pieChartOption: PieChartOption,
    lineChartOption: LineChartOption,
    onPieChartOptionClick: (PieChartOption) -> Unit,
    onLineChartOptionClick: (LineChartOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        if (summary.period != TaskPeriod.DAY) SummaryLineChartStatistic(
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