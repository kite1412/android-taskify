package com.nrr.ui.chart.pie

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color

internal data class CircleSectorData(
    val color: Color,
    @FloatRange(0.0, 1.0)
    val percentage: Float
)

internal fun PieChartData.toSectorData(totalData: Int) = CircleSectorData(
    color = color,
    percentage = dataCount.toFloat() / totalData
)
