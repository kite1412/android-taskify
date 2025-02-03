package com.nrr.ui.statistic.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.nrr.designsystem.theme.Gray
import com.nrr.designsystem.util.drawRoundRectShadow


@Composable
internal fun StatisticFrame(
    modifier: Modifier = Modifier,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    val density = LocalDensity.current
    val shadowOffset = 6.dp
    val cornerSize = 16.dp

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = shadowOffset
            )
            .drawRoundRectShadow(
                color = Gray,
                offset = with(density) {
                    Offset(
                        x = -(shadowOffset.toPx()),
                        y = (shadowOffset.toPx() / 1.5f)
                    )
                },
                cornerRadius = with(density) {
                    CornerRadius(cornerSize.toPx())
                }
            )
            .background(
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(cornerSize)
            )
            .padding(
                vertical = 16.dp,
                horizontal = 24.dp
            )
    ) {
        content()
    }
}