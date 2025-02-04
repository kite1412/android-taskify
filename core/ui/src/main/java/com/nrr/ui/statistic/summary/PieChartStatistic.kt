package com.nrr.ui.statistic.summary

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nrr.designsystem.component.AdaptiveText
import com.nrr.model.Summary
import com.nrr.ui.statistic.summary.util.getPieChartData
import com.nrr.ui.util.UIDictionary
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun PieChartStatistic(
    summary: Summary,
    option: PieChartOption,
    chartSize: Dp,
    onOptionClick: (PieChartOption) -> Unit,
    modifier: Modifier = Modifier,
    chartStyle: Pie.Style = Pie.Style.Fill
) {
    val data by rememberUpdatedState(summary.getPieChartData(option))

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                space = 4.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            PieChartOption.entries.forEach {
                Option(
                    option = it,
                    selected = it == option,
                    onClick = onOptionClick
                )
            }
        }
        Row(
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
                                .size(12.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
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
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            data.forEach {
                Text(
                    text = it.label!! + ": ${it.data.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = it.color
                )
            }
        }
    }
}

@Composable
private fun Option(
    option: PieChartOption,
    selected: Boolean,
    onClick: (PieChartOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary
            else Color.Transparent
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) Color.White
            else MaterialTheme.colorScheme.primary
    )
    val shape = RoundedCornerShape(100)

    Text(
        text = stringResource(
            when (option) {
                PieChartOption.STATUS -> UIDictionary.status
                PieChartOption.TASK_TYPE -> UIDictionary.taskType
            }
        ),
        modifier = modifier
            .clip(shape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = shape
            )
            .background(backgroundColor)
            .padding(8.dp)
            .clickable(
                indication = null,
                interactionSource = null
            ) { onClick(option) },
        color = contentColor,
        style = MaterialTheme.typography.bodySmall
    )
}