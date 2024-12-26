package com.nrr.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp

@Composable
fun CircularTaskProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
    strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
    content: @Composable (() -> Unit)? = null
) {
    Box(modifier = modifier) {
        CircularProgressIndicator(
            modifier = modifier,
            progress = progress,
            color = color,
            trackColor = trackColor,
            strokeCap = StrokeCap.Round,
            strokeWidth = strokeWidth
        )
        Box(modifier = Modifier.align(Alignment.Center)) {
            content?.invoke()
        }
    }
}