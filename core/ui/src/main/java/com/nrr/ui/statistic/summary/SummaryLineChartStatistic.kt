package com.nrr.ui.statistic.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrr.model.Summary
import com.nrr.model.TaskPeriod
import com.nrr.model.toLocalDateTime
import com.nrr.ui.statistic.summary.util.getLineChartData
import com.nrr.ui.toDayLocalized
import com.nrr.ui.util.UIDictionary
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties

@Composable
fun SummaryLineChartStatistic(
    summary: Summary,
    option: LineChartOption,
    onOptionClick: (LineChartOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val data = remember(summary) {
        summary.getLineChartData(
            context = context,
            option = option
        )
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        val maxWidth = maxWidth
        val contentColor = LocalContentColor.current
        val labelStyle = MaterialTheme.typography.bodySmall.copy(
            color = contentColor
        )
        val labels = summary.tasks.map {
            if (summary.period == TaskPeriod.WEEK) it.startDate.toDayLocalized()
            else it.startDate.toLocalDateTime().dayOfMonth.toString()
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(UIDictionary.taskTrend),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            LineChart(
                data = data,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(maxWidth / 2f),
                labelHelperProperties = LabelHelperProperties(enabled = false),
                indicatorProperties = HorizontalIndicatorProperties(
                    textStyle = labelStyle,
                    contentBuilder = {
                        it.toInt().toString()
                    },
                    count = IndicatorCount.CountBased(data.firstOrNull()?.values?.size ?: 0)
                ),
                labelProperties = LabelProperties(
                    enabled = true,
                    labels = labels,
                    textStyle = labelStyle
                )
            )
        }
    }
}