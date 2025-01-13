package com.nrr.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nrr.model.TaskType


@Composable
fun TaskTypeBar(
    taskType: TaskType,
    fillBackground: Boolean,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp,
    nameTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    contentPadding: Dp = 8.dp
) {
    val name = taskType.toStringLocalized()
    val color = taskType.color()
    val animatedBackground by animateColorAsState(
        targetValue = if (fillBackground) color else Color.Transparent,
        label = "background color"
    )
    val animatedContentColor by animateColorAsState(
        targetValue = if (fillBackground) Color.White else color,
        label = "content color"
    )
    val shape = RoundedCornerShape(8.dp)

    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = color,
                shape = shape
            )
            .background(
                color = animatedBackground,
                shape = shape
            )
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(taskType.iconId()),
            contentDescription = name,
            modifier = Modifier.size(iconSize),
            tint = animatedContentColor
        )
        Text(
            text = name,
            color = animatedContentColor,
            style = nameTextStyle
        )
    }
}