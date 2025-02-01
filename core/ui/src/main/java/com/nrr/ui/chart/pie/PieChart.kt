package com.nrr.ui.chart.pie

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.model.Task
import com.nrr.model.TaskPriority
import com.nrr.ui.TaskPreviewParameter
import com.nrr.ui.color

const val FULL_CIRCLE_ANGLE = 360f
const val START_ANGLE = 90f

@Composable
fun PieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier
) {
    data.ifEmpty { return }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val minSize = 80.dp
        val maxSize = min(maxWidth, maxHeight)
        val size = maxOf(minSize, maxSize)
        val dataCount by rememberUpdatedState(
            newValue = data.sumOf { it.dataCount }
        )
        val sectorData by rememberUpdatedState(
            newValue = data.map { it.toSectorData(dataCount) }
        )

        Box(
            modifier = Modifier.size(size)
        ) {
            sectorData.forEachIndexed { i, d ->
                val sweepAngle = FULL_CIRCLE_ANGLE * d.percentage
                val startAngle = START_ANGLE + sectorData
                    .filterIndexed { index, _ -> index < i }
                    .map { it.percentage }
                    .sum() * FULL_CIRCLE_ANGLE

                CircleSector(
                    color = d.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle
                )
            }
        }
    }
}

@Composable
private fun CircleSector(
    color: Color,
    startAngle: Float,
    sweepAngle: Float,
    alpha: Float = 1f,
    modifier: Modifier = Modifier
) = Canvas(modifier = modifier.fillMaxSize()) {
    drawSector(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        alpha = alpha
    )
}

private fun DrawScope.drawSector(
    color: Color,
    startAngle: Float,
    sweepAngle: Float,
    alpha: Float = 1f
) {
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = true,
        alpha = alpha,
        size = size
    )
}

@Preview
@Composable
private fun PieChartPreview(
    @PreviewParameter(TaskPreviewParameter::class)
    tasks: List<Task>
) {
    val (normal, rest) = tasks.partition {
        it.activeStatuses.first().priority == TaskPriority.NORMAL
    }
    val (high, critical) = rest.partition {
        it.activeStatuses.first().priority == TaskPriority.HIGH
    }

    TaskifyTheme {
        PieChart(
            data = listOf(
                PieChartData(
                    "Normal",
                    color = TaskPriority.NORMAL.color(),
                    dataCount = normal.size
                ),
                PieChartData(
                    "High",
                    color = TaskPriority.HIGH.color(),
                    dataCount = high.size
                ),
                PieChartData(
                    "Critical",
                    color = TaskPriority.CRITICAL.color(),
                    dataCount = critical.size
                )
            )
        )
    }
}