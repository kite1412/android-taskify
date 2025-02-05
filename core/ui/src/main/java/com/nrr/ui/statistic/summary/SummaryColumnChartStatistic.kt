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
import com.nrr.ui.statistic.summary.util.getColumnChartData
import com.nrr.ui.util.UIDictionary
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties

@Composable
fun SummaryColumnChartStatistic(
    summary: Summary,
    option: ColumnChartOption,
    onOptionClick: (ColumnChartOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val data = remember(summary) {
        summary.getColumnChartData(
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

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(UIDictionary.taskTrend),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            ColumnChart(
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
                    count = IndicatorCount.StepBased(stepBy = 4.0)
                ),
                labelProperties = LabelProperties(
                    enabled = true,
                    textStyle = labelStyle,
                    rotation = LabelProperties.Rotation(
                        degree = 0f
                    )
                ),
                barProperties = BarProperties(
                    cornerRadius = Bars.Data.Radius.Rectangle(
                        topLeft = 6.dp,
                        topRight = 6.dp
                    ),
                    spacing = 3.dp,
                    thickness = 8.dp
                )
            )
        }
    }
}