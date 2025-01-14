package com.nrr.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun RoundRectButton(
    onClick: () -> Unit,
    action: String,
    modifier: Modifier = Modifier,
    iconId: Int? = null,
    shape: Shape = RoundedCornerShape(8.dp),
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    enabled: Boolean = true,
    colors: ButtonColors = TaskifyButtonDefaults.colors(),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(4.dp),
    contentPadding: PaddingValues = PaddingValues(
        vertical = 8.dp,
        horizontal = 12.dp
    )
) = Row(
    modifier = modifier
        .clip(shape)
        .background(
            if (enabled) colors.containerColor else colors.disabledContainerColor
        )
        .clickable(
            enabled = enabled,
            indication = null,
            interactionSource = null,
            onClick = onClick
        )
        .padding(contentPadding),
    horizontalArrangement = horizontalArrangement,
    verticalAlignment = Alignment.CenterVertically
) {
    Text(
        text = action,
        style = textStyle,
        color = if (enabled) colors.contentColor else colors.disabledContentColor
    )
    iconId?.let {
        Icon(
            painter = painterResource(it),
            contentDescription = "add",
            modifier = Modifier
                .size((textStyle.fontSize.value * 1.5).dp),
            tint = if (enabled) colors.contentColor else colors.disabledContentColor
        )
    }
}

@Composable
fun OutlinedRoundRectButton(
    onClick: () -> Unit,
    action: String,
    modifier: Modifier = Modifier,
    iconId: Int? = null,
    shape: Shape = RoundedCornerShape(8.dp),
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    enabled: Boolean = true,
    colors: ButtonColors = TaskifyButtonDefaults.colors(
        contentColor = MaterialTheme.colorScheme.primary
    ),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(4.dp),
    contentPadding: PaddingValues = PaddingValues(
        vertical = 8.dp,
        horizontal = 12.dp
    )
) {
    RoundRectButton(
        onClick = onClick,
        action = action,
        modifier = modifier
            .border(
                width = 1.dp,
                color = if (enabled) colors.contentColor
                    else colors.disabledContentColor,
                shape = shape
            ),
        iconId = iconId,
        shape = shape,
        textStyle = textStyle,
        enabled = enabled,
        colors = colors.copy(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        horizontalArrangement = horizontalArrangement,
        contentPadding = contentPadding
    )
}

object TaskifyButtonDefaults {
    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.primary,
        contentColor: Color = Color.White,
        disabledContainerColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
        disabledContentColor: Color = Color.White.copy(alpha = 0.7f)
    ) = ButtonDefaults.textButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContentColor = disabledContentColor,
        disabledContainerColor = disabledContainerColor
    )
}