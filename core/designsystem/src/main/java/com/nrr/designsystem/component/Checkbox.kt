package com.nrr.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nrr.designsystem.icon.TaskifyIcon

@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    colors: CheckboxColors = TaskifyCheckboxDefaults.colors()
) {
    val animatedColor by animateColorAsState(
        targetValue = if (checked) colors.checkedBoxColor else colors.uncheckedBoxColor,
        label = "checkbox box"
    )
    val borderShape = RoundedCornerShape(8.dp)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(22.dp)
            .border(
                width = 1.dp,
                color = colors.uncheckedBorderColor,
                shape = borderShape
            )
            .background(
                color = if (checked) animatedColor else Color.Transparent,
                shape = borderShape
            )
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) {
                onCheckedChange(!checked)
            }
    ) {
        AnimatedVisibility(
            visible = checked,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn(),
            exit = fadeOut(spring(stiffness = Spring.StiffnessHigh))
        ) {
            Icon(
                painter = painterResource(TaskifyIcon.check),
                contentDescription = "checked",
                tint = colors.checkedCheckmarkColor,
                modifier = Modifier
                    .size(16.dp)

            )
        }
    }
}

object TaskifyCheckboxDefaults {
    @Composable
    fun colors(
        checkmarkColor: Color = Color.Black,
        uncheckedColor: Color = Color.White,
        checkedColor: Color = Color.White
    ) = CheckboxDefaults.colors(
        checkedColor = checkedColor,
        uncheckedColor = uncheckedColor,
        checkmarkColor = checkmarkColor
    )
}