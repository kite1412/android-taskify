package com.nrr.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.util.TaskifyDefault

@Composable
fun EmptyTasks(
    message: String,
    modifier: Modifier = Modifier,
    iconId: Int = TaskifyIcon.emptyNote
) {
    val color = TaskifyDefault.emptyWarningContentColor

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            space = TaskifyDefault.EMPTY_WARNING_CONTENT_SPACE.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(iconId),
            contentDescription = message,
            modifier = Modifier.size(TaskifyDefault.EMPTY_ICON_SIZE.dp),
            tint = color
        )
        Text(
            text = message,
            maxLines = 2,
            fontWeight = FontWeight.Bold,
            fontSize = TaskifyDefault.EMPTY_LABEL_FONT_SIZE.sp,
            color = color
        )
    }
}