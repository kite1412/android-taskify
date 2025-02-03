package com.nrr.ui.statistic.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nrr.designsystem.component.AdaptiveText
import com.nrr.model.Summary
import com.nrr.ui.statistic.summary.util.getPieChartData
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie

@Composable
internal fun PieChartStatistic(
    summary: Summary,
    options: PieChartOptions,
    chartSize: Dp,
    modifier: Modifier = Modifier,
    chartStyle: Pie.Style = Pie.Style.Fill
) {
    val data by rememberUpdatedState(summary.getPieChartData(options))

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PieChart(
            data = data,
            modifier = Modifier.size(chartSize),
            style = chartStyle
        )
        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            data.forEach {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val style = MaterialTheme.typography.bodyMedium

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .background(it.color)
                    )
                    AdaptiveText(
                        text = it.label!!,
                        initialFontSize = style.fontSize,
                        style = style,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )
                }
            }
        }
    }
}