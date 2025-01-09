package com.nrr.plandetail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nrr.designsystem.component.Action
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.Blue
import com.nrr.designsystem.theme.Gray
import com.nrr.designsystem.theme.Green
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.designsystem.util.TaskifyDefault
import com.nrr.model.ActiveStatus
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.model.toDateString
import com.nrr.model.toTimeString
import com.nrr.plandetail.util.PlanDetailDictionary
import com.nrr.plandetail.util.dashHeight
import com.nrr.plandetail.util.dashSpace
import com.nrr.plandetail.util.dashWidth
import com.nrr.ui.TaskCards
import com.nrr.ui.TaskPreviewParameter
import com.nrr.ui.color
import com.nrr.ui.getCurrentLocale
import com.nrr.ui.toStringLocalized
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle

@Composable
internal fun PlanDetailScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlanDetailViewModel = hiltViewModel()
) {
    val period = viewModel.period
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    Content(
        period = period,
        tasks = tasks,
        onBackClick = onBackClick,
        currentDate = viewModel.currentDate,
        onRemove = {},
        onComplete = {},
        modifier = modifier
    )
}

@Composable
private fun Content(
    period: TaskPeriod,
    tasks: List<Task>?,
    onBackClick: () -> Unit,
    currentDate: Instant,
    onRemove: (Task) -> Unit,
    onComplete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Header(
                period = period,
                onBackClick = onBackClick
            )
            if (tasks?.isNotEmpty() == true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    RealTimeClock(
                        instant = currentDate,
                        modifier = Modifier.weight(0.8f)
                    )
                    StartIndicator(
                        period = period,
                        modifier = Modifier.align(Alignment.Bottom)
                    )
                }
                Tasks(
                    period = period,
                    tasks = tasks,
                    onRemove = onRemove,
                    onComplete = onComplete,
                    modifier = Modifier.verticalScroll(rememberScrollState())
                )
            }
        }
        if (tasks?.isEmpty() == true) NoPlan()
    }
}

@Composable
private fun Header(
    period: TaskPeriod,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                painter = painterResource(TaskifyIcon.back),
                contentDescription = "back"
            )
        }
        Text(
            text = stringResource(
                id = when (period) {
                    TaskPeriod.DAY -> PlanDetailDictionary.todayPlan
                    TaskPeriod.WEEK -> PlanDetailDictionary.weekPlan
                    TaskPeriod.MONTH -> PlanDetailDictionary.monthPlan
                }
            ),
            fontWeight = FontWeight.Bold,
            fontSize = TaskifyDefault.HEADER_FONT_SIZE.sp
        )
    }
}

@Composable
private fun weekIndicator(currentDate: Instant): String {
    val localDateTime = currentDate.toLocalDateTime(TimeZone.currentSystemDefault())
    val today = localDateTime.dayOfWeek.value
    val start = localDateTime.dayOfMonth - today
    val end = localDateTime.dayOfMonth + today - 1
    return "($start - " +
            "$end " +
            "${localDateTime.month.getDisplayName(TextStyle.FULL, getCurrentLocale())})"
}

@Composable
private fun monthIndicator(currentDate: Instant) =
    with(currentDate.toLocalDateTime(TimeZone.currentSystemDefault())) {
        this.month.getDisplayName(TextStyle.FULL, getCurrentLocale()) +
                " ${this.year}"
    }

@Composable
fun StartIndicator(
    period: TaskPeriod,
    modifier: Modifier = Modifier
) {
    val currentDate = Clock.System.now()
    val density = LocalDensity.current
    val textStyle = LocalTextStyle.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val style = remember {
        with(density) {
            val offset = 1.dp.toPx()
            textStyle.copy(
                shadow = Shadow(
                    color = primaryColor,
                    offset = Offset(-offset, offset)
                ),
                fontWeight = FontWeight.Bold
            )
        }
    }
    val smallFontSize = MaterialTheme.typography.bodySmall.fontSize
    val largeFontSize = MaterialTheme.typography.labelLarge.fontSize

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        when (period) {
            TaskPeriod.DAY -> {
                Text(
                    text = stringResource(PlanDetailDictionary.startTime),
                    style = style,
                    fontSize = largeFontSize
                )
            }
            TaskPeriod.WEEK -> {
                Text(
                    text = weekIndicator(currentDate),
                    style = style,
                    fontSize = smallFontSize,
                    lineHeight = smallFontSize
                )
                Text(
                    text = stringResource(PlanDetailDictionary.startDay),
                    style = style,
                    fontSize = largeFontSize
                )
            }
            TaskPeriod.MONTH -> {
                Text(
                    text = monthIndicator(currentDate),
                    style = style,
                    fontSize = smallFontSize,
                    lineHeight = smallFontSize
                )
                Text(
                    text = stringResource(PlanDetailDictionary.startDate),
                    style = style,
                    fontSize = largeFontSize
                )
            }
        }
    }
}

@Composable
private fun RealTimeClock(
    instant: Instant,
    modifier: Modifier = Modifier
) {
    val locale = getCurrentLocale()
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = localDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, locale),
            fontWeight = FontWeight.Bold,
            fontSize = TaskifyDefault.HEADER_FONT_SIZE.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "${instant.toDateString()}\n" +
                    instant.toTimeString(withSecond = true),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = TaskifyDefault.HEADER_FONT_SIZE.sp
        )
    }
}

@Composable
private fun NoPlan(modifier: Modifier = Modifier) {
    val color = TaskifyDefault.emptyWarningContentColor

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = TaskifyDefault.EMPTY_WARNING_CONTENT_SPACE.dp,
            alignment = Alignment.CenterVertically
        )
    ) {
        Icon(
            painter = painterResource(TaskifyIcon.calendarCross),
            contentDescription = "no plan",
            modifier = Modifier.size(TaskifyDefault.EMPTY_ICON_SIZE.dp),
            tint = color
        )
        Text(
            text = stringResource(PlanDetailDictionary.noPlan),
            fontWeight = FontWeight.Bold,
            fontSize = TaskifyDefault.EMPTY_LABEL_FONT_SIZE.sp,
            color = color
        )
    }
}

private fun actions(
    task: Task,
    removeMessage: String,
    completeMessage: String,
    onRemove: (Task) -> Unit,
    onComplete: (Task) -> Unit
) = mutableListOf(
    Action(
        action = removeMessage,
        iconId = TaskifyIcon.cancel,
        color = Color.Red,
        onClick = { onRemove(task) }
    )
).apply {
    if (!task.activeStatus!!.isCompleted) add(
        index = 0,
        element = Action(
            action = completeMessage,
            iconId = TaskifyIcon.check,
            color = Blue,
            onClick = { onComplete(task) }
        )
    )
}.toList()

@Composable
private fun Tasks(
    period: TaskPeriod,
    tasks: List<Task>?,
    onRemove: (Task) -> Unit,
    onComplete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    tasks?.let {
        val removeMessage = stringResource(PlanDetailDictionary.remove)
        val completeMessage = stringResource(PlanDetailDictionary.complete)

        TaskCards(
            tasks = tasks,
            actions = {
                actions(
                    task = it,
                    removeMessage = removeMessage,
                    completeMessage = completeMessage,
                    onRemove = onRemove,
                    onComplete = onComplete
                )
            },
            modifier = modifier,
            header = if (period == TaskPeriod.DAY) {
                {
                    Text(
                        text = tasks[it].activeStatus?.startDate?.toTimeString() ?: "",
                        modifier = Modifier.align(Alignment.End),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else null,
            spacer = {
                TaskSpacer(
                    task = tasks[it],
                    dashedLine = it != tasks.lastIndex,
                    modifier = Modifier
                        .padding(
                            start = 32.dp,
                            end = 16.dp,
                            top = dashSpace
                        )
                )
            },
            verticalArrangement = Arrangement.spacedBy(dashSpace),
            showStartTime = period != TaskPeriod.DAY
        )
    }
}

private fun <T> status(
    currentDate: Instant,
    activeStatus: ActiveStatus,
    completed: T,
    inProgress: T,
    waiting: T,
    late: T
): T {
    val startDate = activeStatus.startDate
    val due = activeStatus.dueDate
    return when {
        activeStatus.isCompleted -> completed
        currentDate > startDate && due != null && due < currentDate -> late
        currentDate > startDate -> inProgress
        else -> waiting
    }
}

@Composable
private fun TaskSpacer(
    task: Task,
    dashedLine: Boolean,
    modifier: Modifier = Modifier
) {
    val activeStatus = task.activeStatus

    activeStatus?.let {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                val fontSize = 14
                val lineHeight = fontSize + 8
                val currentDate = Clock.System.now()

                Text(
                    text = buildAnnotatedString {
                        append("Status: ")
                        withStyle(
                            style = SpanStyle(
                                color = status(
                                    currentDate = currentDate,
                                    activeStatus = activeStatus,
                                    completed = Green,
                                    inProgress = Gray,
                                    waiting = Gray,
                                    late = Color.Red
                                )
                            )
                        ) {
                            append(
                                stringResource(
                                    id = status(
                                        currentDate = currentDate,
                                        activeStatus = activeStatus,
                                        completed = PlanDetailDictionary.completed,
                                        inProgress = PlanDetailDictionary.inProgress,
                                        waiting = PlanDetailDictionary.waiting,
                                        late = PlanDetailDictionary.late
                                    )
                                )
                            )
                        }
                    },
                    fontSize = fontSize.sp,
                    lineHeight = lineHeight.sp
                )
                activeStatus.dueDate?.let {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(PlanDetailDictionary.due) + ": ")
                            withStyle(
                                style = SpanStyle(
                                    color = if (currentDate > it) Color.Red else Green
                                )
                            ) {
                                append(it.toTimeString())
                            }
                        },
                        fontSize = fontSize.sp,
                        lineHeight = lineHeight.sp
                    )
                }
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(PlanDetailDictionary.priority) + ": ")
                        withStyle(
                            style = SpanStyle(color = activeStatus.priority.color())
                        ) {
                            append(activeStatus.priority.toStringLocalized())
                        }
                    },
                    fontSize = fontSize.sp,
                    lineHeight = lineHeight.sp
                )
            }
            if (dashedLine) VerticalDashedLine(4)
        }
    }
}

@Composable
private fun VerticalDashedLine(
    dashCount: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Canvas(
        modifier = modifier
            .height(
                dashHeight * dashCount +
                        dashSpace * (dashCount - 1)
            )
            .width(dashWidth)
    ) {
        val dashHeight = dashHeight.toPx()
        val dashWidth = dashWidth.toPx()
        val space = dashSpace.toPx()
        var currentY = 0f

        repeat(dashCount) {
            drawRoundRect(
                color = color,
                topLeft = Offset(x = 0f, y = currentY),
                size = Size(width = dashWidth, height = dashHeight),
                cornerRadius = CornerRadius(100f)
            )
            currentY += dashHeight + space
        }
    }
}

@Preview
@Composable
private fun ContentPreview(
    @PreviewParameter(TaskPreviewParameter::class)
    tasks: List<Task>
) {
    var curDate by remember { mutableStateOf(Clock.System.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            curDate = Clock.System.now()
            delay(1000)
        }
    }
    TaskifyTheme {
        Content(
            period = TaskPeriod.WEEK,
            tasks = tasks,
            onBackClick = {},
            currentDate = curDate,
            onRemove = {},
            onComplete = {},
        )
    }
}