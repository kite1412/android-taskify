package com.nrr.designsystem.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

fun Modifier.drawRoundRectShadow(
    color: Color = Color.Black,
    cornerRadius: CornerRadius = CornerRadius(10f),
    alpha: Float = 0.25f,
    offset: Offset = Offset(x = -7f, y = 8f)
) = drawBehind {
    drawRoundRect(
        color = color,
        cornerRadius = cornerRadius,
        topLeft = offset,
        size = this.size,
        alpha = alpha
    )
}