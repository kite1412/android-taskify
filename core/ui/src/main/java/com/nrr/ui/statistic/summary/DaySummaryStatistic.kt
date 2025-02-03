package com.nrr.ui.statistic.summary

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nrr.model.Summary

@Composable
fun DaySummaryStatistic(
    summary: Summary,
    options: PieChartOptions = PieChartOptions.STATUS,
    modifier: Modifier = Modifier
) {
    StatisticFrame(modifier = modifier) {
        PieChartStatistic(
            summary = summary,
            options = options,
            chartSize = minOf(
                maxWidth / 2.5f,
                200.dp
            )
        )
    }
}