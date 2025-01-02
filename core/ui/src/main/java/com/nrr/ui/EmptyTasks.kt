package com.nrr.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrr.designsystem.icon.TaskifyIcon

@Composable
fun EmptyTasks(
    message: String,
    modifier: Modifier = Modifier,
    iconId: Int = TaskifyIcon.emptyNote
) {
    val color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(iconId),
            contentDescription = message,
            modifier = Modifier.size(60.dp),
            tint = color
        )
        Text(
            text = message,
            maxLines = 2,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = color
        )
    }
}