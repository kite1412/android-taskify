package com.nrr.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrr.designsystem.component.Action
import com.nrr.designsystem.component.Swipeable
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.model.Task
import com.nrr.model.TaskPriority
import com.nrr.model.TaskType
import com.nrr.model.Time
import kotlinx.datetime.Clock

@Composable
fun TaskCard(
    task: Task,
    actions: List<Action>,
    modifier: Modifier = Modifier,
    showStartTime: Boolean = false
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            if (showStartTime) 8.dp else 0.dp
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showStartTime) Text(
            text = task.startTime.toString(),
            fontWeight = FontWeight.Bold
        )
        Swipeable(actions) { m ->
            Row(
                modifier = m
                    .fillMaxWidth()
                    .background(task.color())
                    .padding(8.dp)
            ) {

                Column {
                    Text(task.title)
                    task.description?.let {
                        Text(it)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun TaskCardPreview() {
    TaskifyTheme {
        TaskCard(
            task = Task(
                id = "1",
                title = "Learn Android",
                description = "Learn Android Development",
                createdAt = Clock.System.now(),
                updateAt = Clock.System.now(),
                startTime = Time(12, 0),
                endTime = null,
                taskType = TaskType.PERSONAL,
                priority = TaskPriority.NORMAL,
                isSet = false,
                isDefault = false
            ),
            actions = listOf(
                Action(
                    action = "Delete",
                    iconId = TaskifyIcon.home,
                    onClick = {},
                    color = Color.Red
                )
            )
        )
    }
}