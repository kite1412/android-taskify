package com.nrr.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrr.designsystem.component.Action
import com.nrr.designsystem.component.Swipeable
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.model.ActiveStatus
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.model.TaskPriority
import com.nrr.model.TaskType
import com.nrr.model.toTimeString
import kotlinx.datetime.Clock

@Composable
fun TaskCard(
    task: Task,
    actions: List<Action>,
    modifier: Modifier = Modifier,
    showStartTime: Boolean = false
) {
    val swipeableClip = 10.dp
    val density = LocalDensity.current
    var textWidth by remember { mutableIntStateOf(0) }
    val showTime = showStartTime && task.activeStatus != null

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        if (showTime) Text(
            text = task.activeStatus!!.startDate.toTimeString(),
            modifier = Modifier.align(Alignment.CenterStart),
            fontWeight = FontWeight.Bold,
            onTextLayout = { textWidth = it.size.width }
        )
        Swipeable(
            actions = actions,
            modifier = Modifier
                .padding(
                    start = with(density) {
                        if (showTime) textWidth.toDp() + 8.dp else 0.dp
                    }
                ),
            actionButtonsBorderShape = RoundedCornerShape(swipeableClip)
        ) { m ->
            Row(
                modifier = m
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(swipeableClip))
                    .background(task.color())
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.White)
                        .padding(12.dp)
                ) {
                    Icon(
                        painter = painterResource(task.iconId()),
                        contentDescription = task.taskType.name,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black
                    )
                }
                Column {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold
                    )
                    task.description?.let {
                        Text(
                            text = it,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun TaskCardPreview() {
    val task = @Composable { s: Boolean ->
        TaskCard(
            task = Task(
                id = 1,
                title = "Learn Android",
                description = "Learn Android Development",
                createdAt = Clock.System.now(),
                updateAt = Clock.System.now(),
                taskType = TaskType.LEARNING,
                activeStatus = ActiveStatus(
                    startDate = Clock.System.now(),
                    dueDate = Clock.System.now(),
                    priority = TaskPriority.HIGH,
                    period = TaskPeriod.DAY,
                    isDefault = true
                )
            ),
            actions = listOf(
                Action(
                    action = "Delete",
                    iconId = TaskifyIcon.home,
                    onClick = {},
                    color = Color.Red
                )
            ),
            showStartTime = s
        )
    }
    TaskifyTheme {
        Column {
            repeat(2) {
                task(it == 1)
            }
        }
    }
}