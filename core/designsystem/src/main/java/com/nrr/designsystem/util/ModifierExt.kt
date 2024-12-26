package com.nrr.designsystem.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

fun Modifier.drawRoundedShadow(
    color: Color = Color.Black,
    cornerRadius: CornerRadius = CornerRadius(10f),
    alpha: Float = 0.25f
) = drawBehind {
    drawRoundRect(
        color = color,
        cornerRadius = cornerRadius,
        topLeft = Offset(x = -5f, y = 6f),
        size = this.size,
        alpha = alpha
    )
}