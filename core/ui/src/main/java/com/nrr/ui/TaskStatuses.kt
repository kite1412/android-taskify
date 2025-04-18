package com.nrr.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrr.designsystem.theme.Gray
import com.nrr.designsystem.theme.PastelGreen
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.model.ActiveStatus
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.model.toTimeString
import com.nrr.ui.util.UIDictionary

private const val CIRCLE_DIAMETER = 14
private val space = 4.dp

@Composable
fun TaskStatuses(
    statuses: List<ActiveStatus>,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(6.dp),
    onStatusClick: ((ActiveStatus) -> Unit)? = null
) {
    val (today, rest) = statuses.partition { it.period == TaskPeriod.DAY }
    val (week, month) = rest.partition { it.period == TaskPeriod.WEEK }

    if (statuses.isNotEmpty()) Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement
    ) {
        if (today.isNotEmpty()) ActiveIndicator(
            statuses = today.sortedBy { it.startDate },
            period = TaskPeriod.DAY,
            onStatusClick = onStatusClick
        )
        if (week.isNotEmpty()) ActiveIndicator(
            statuses = week.sortedBy { it.startDate },
            period = TaskPeriod.WEEK,
            onStatusClick = onStatusClick
        )
        if (month.isNotEmpty()) ActiveIndicator(
            statuses = month.sortedBy { it.startDate },
            period = TaskPeriod.MONTH,
            onStatusClick = onStatusClick
        )
    } else Indicator(
        color = Gray,
        text = stringResource(UIDictionary.notSet),
        modifier = modifier
    )
}

@Composable
private fun Indicator(
    color: Color,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space)
    ) {
        Box(
            modifier = Modifier
                .size(CIRCLE_DIAMETER.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = text,
            color = color,
            fontSize = CIRCLE_DIAMETER.sp,
            lineHeight = CIRCLE_DIAMETER.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ActiveIndicator(
    statuses: List<ActiveStatus>,
    period: TaskPeriod,
    modifier: Modifier = Modifier,
    onStatusClick: ((ActiveStatus) -> Unit)? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space)
    ) {
        Indicator(
            color = PastelGreen,
            text = stringResource(
                id = when (period) {
                    TaskPeriod.DAY -> UIDictionary.todaySet
                    TaskPeriod.WEEK -> UIDictionary.weekSet
                    TaskPeriod.MONTH -> UIDictionary.monthSet
                }
            )
        )
        statuses.forEach {
            Text(
                text = it.startDate.toDateStringLocalized() +
                        " (${it.startDate.toTimeString()}" +
                        "${if (it.dueDate != null) " - " + it.dueDate?.toTimeString() else ""})",
                modifier = Modifier
                    .padding(start = CIRCLE_DIAMETER.dp + space)
                    .then(
                        if (onStatusClick != null) Modifier.clickable(
                            indication = null,
                            interactionSource = null
                        ) { onStatusClick(it) }
                        else Modifier
                    ),
                fontWeight = FontWeight.Bold,
                fontSize = (CIRCLE_DIAMETER - 2).sp,
                lineHeight = (CIRCLE_DIAMETER - 2).sp,
                color = if (onStatusClick != null) MaterialTheme.colorScheme.tertiary
                    else LocalContentColor.current
            )
        }
    }
}

@Preview
@Composable
private fun TaskStatusesPreview(
    @PreviewParameter(TaskPreviewParameter::class)
    tasks: List<Task>
) {
    TaskifyTheme {
        TaskStatuses(
            statuses = tasks.flatMap { it.activeStatuses }
        )
    }
}