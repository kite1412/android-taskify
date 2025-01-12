package com.nrr.designsystem.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrr.designsystem.theme.PastelGreen
import com.nrr.designsystem.theme.Red
import com.nrr.designsystem.theme.TaskifyTheme

@Composable
fun Toggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showState: Boolean = false,
    colors: ToggleColors = ToggleDefaults.colors()

) {
    val containerHeight = ToggleDefaults.CONTAINER_HEIGHT
    val containerWidth = ToggleDefaults.CONTAINER_WIDTH
    val thumbDiameter = ToggleDefaults.THUMB_DIAMETER
    val shape = RoundedCornerShape(100)
    val containerColor by animateColorAsState(
        targetValue = if (checked) colors.checkedContainerColor
            else colors.uncheckedContainerColor,
        label = "toggle container"
    )
    val thumbColor by animateColorAsState(
        targetValue = if (checked) colors.checkedThumbColor
            else colors.uncheckedThumbColor,
        label = "toggle content"
    )
    val thumbPosition by animateDpAsState(
        targetValue = if (!checked) 0.dp else (containerWidth / 2).dp,
        label = "toggle position"
    )

    Box(
        modifier = modifier
            .height(containerHeight.dp)
            .width(containerWidth.dp)
            .clip(shape)
            .background(containerColor)
            .clickable(
                indication = null,
                interactionSource = null,
                enabled = enabled
            ) { onCheckedChange(!checked) }
            .padding(ToggleDefaults.THUMB_PADDING_FROM_CONTAINER.dp)
    ) {
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = thumbPosition.roundToPx(),
                        y = 0
                    )
                }
                .clip(shape)
                .size(thumbDiameter.dp)
                .background(thumbColor)
        ) {
            if (showState) AnimatedContent(
                targetState = checked,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    text = if (it) "On" else "Off",
                    fontSize = 10.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
private fun TogglePreview() {
    TaskifyTheme {
        var checked by remember { mutableStateOf(false) }

        Toggle(
            checked = checked,
            onCheckedChange = {
                checked = it
            },
            showState = true
        )
    }
}

data class ToggleColors(
    val checkedContainerColor: Color,
    val checkedThumbColor: Color,
    val uncheckedContainerColor: Color,
    val uncheckedThumbColor: Color
)

object ToggleDefaults {
    internal const val CONTAINER_HEIGHT = 40
    internal const val CONTAINER_WIDTH = CONTAINER_HEIGHT * 2
    internal const val THUMB_DIAMETER = CONTAINER_HEIGHT - 4
    internal const val THUMB_PADDING_FROM_CONTAINER = ((CONTAINER_HEIGHT - THUMB_DIAMETER) / 2)

    @Composable
    fun colors(
        checkedContainerColor: Color = PastelGreen,
        checkedContentColor: Color = Color.White,
        uncheckedContainerColor: Color = Red,
        uncheckedContentColor: Color = Color.White
    ) = ToggleColors(
        checkedContainerColor = checkedContainerColor,
        checkedThumbColor = checkedContentColor,
        uncheckedContainerColor = uncheckedContainerColor,
        uncheckedThumbColor = uncheckedContentColor
    )
}