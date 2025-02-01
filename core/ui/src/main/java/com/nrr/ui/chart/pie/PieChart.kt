package com.nrr.ui.chart.pie

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.delay

private const val FULL_CIRCLE_ANGLE = 360f
private const val START_ANGLE = 90f

@Composable
fun PieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier,
    sliceAnimationDuration: Int = TaskifyPieChartDefaults.SLICE_ANIMATION_DURATION,
    sliceAnimationEasing: Easing = LinearEasing
) {
    data.ifEmpty { return }
    var animate by remember(data) {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        delay(200)
        animate = true
    }
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
                val sweepAngle by animateFloatAsState(
                    targetValue = if (!animate) 0f
                        else FULL_CIRCLE_ANGLE * d.percentage,
                    animationSpec = tween(
                        durationMillis = sliceAnimationDuration,
                        delayMillis = i * sliceAnimationDuration,
                        easing = sliceAnimationEasing
                    )
                )
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