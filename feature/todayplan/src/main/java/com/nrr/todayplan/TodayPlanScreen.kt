package com.nrr.todayplan

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nrr.designsystem.component.Action
import com.nrr.designsystem.component.AdaptiveText
import com.nrr.designsystem.component.CircularTaskProgressIndicator
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.CharcoalClay
import com.nrr.designsystem.theme.Green
import com.nrr.designsystem.theme.PastelGreen
import com.nrr.designsystem.theme.Red
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.designsystem.theme.lightBlueGradient
import com.nrr.designsystem.theme.lightOrangeGradient
import com.nrr.designsystem.theme.lightRedGradient
import com.nrr.designsystem.util.TaskifyDefault
import com.nrr.designsystem.util.drawRoundRectShadow
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.model.toTimeString
import com.nrr.todayplan.util.TodayPlanDictionary
import com.nrr.ui.LocalSafeAnimateContent
import com.nrr.ui.TaskCardTimeIndicator
import com.nrr.ui.TaskPreviewParameter
import com.nrr.ui.rememberTaskCardsState
import com.nrr.ui.taskCards

@Composable
internal fun TodayPlanScreen(
    onSettingClick: () -> Unit,
    onPlanForTodayClick: (TaskPeriod) -> Unit,
    onWeeklyClick: (TaskPeriod) -> Unit,
    onMonthlyClick: (TaskPeriod) -> Unit,
    onSetTodayTasksClick: () -> Unit,
    onScheduledTaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TodayPlanViewModel = hiltViewModel()
) {
    val todayTasks by viewModel.todayTasks.collectAsStateWithLifecycle()
    val weeklyTasks by viewModel.weeklyTasks.collectAsStateWithLifecycle()
    val monthlyTasks by viewModel.monthlyTasks.collectAsStateWithLifecycle()
    val username by viewModel.username.collectAsStateWithLifecycle()

    Content(
        username = username,
        todayTasks = todayTasks,
        weeklyTasks = weeklyTasks,
        monthlyTasks = monthlyTasks,
        onPlanForTodayClick = onPlanForTodayClick,
        onSettingClick = onSettingClick,
        onRemoveTask = viewModel::deleteTask,
        onCompleteTask = viewModel::completeTask,
        onTaskClick = onScheduledTaskClick,
        onWeeklyClick = onWeeklyClick,
        onMonthlyClick = onMonthlyClick,
        onSetTodayTasksClick = onSetTodayTasksClick,
        modifier = modifier
    )
}

private fun scheduleActions(
    task: Task,
    removeMessage: String,
    completeMessage: String,
    onRemove: (Task) -> Unit,
    onComplete: (Task) -> Unit
) = mutableListOf(
    Action(
        action = removeMessage,
        iconId = TaskifyIcon.trashBin,
        onClick = { onRemove(task) },
        color = Red
    )
).apply {
    if (task.activeStatuses.firstOrNull()?.isCompleted == false)
        add(
            Action(
                action = completeMessage,
                iconId = TaskifyIcon.check,
                onClick = { onComplete(task) },
                color = Green
            )
        )
}.toList()

@Composable
private fun Content(
    username: String,
    todayTasks: List<Task>,
    weeklyTasks: List<Task>,
    monthlyTasks: List<Task>,
    onPlanForTodayClick: (TaskPeriod) -> Unit,
    onSettingClick: () -> Unit,
    onRemoveTask: (Task) -> Unit,
    onCompleteTask: (Task) -> Unit,
    onTaskClick: (Task) -> Unit,
    onWeeklyClick: (TaskPeriod) -> Unit,
    onMonthlyClick: (TaskPeriod) -> Unit,
    onSetTodayTasksClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentWithRoundRectShadowPadding = with(LocalDensity.current) {
        7f.toDp()
    }
    val removeMessage = stringResource(TodayPlanDictionary.removeFromSchedule)
    val completeMessage = stringResource(TodayPlanDictionary.markAsCompleted)
    val taskCardsState = rememberTaskCardsState(todayTasks, todayTasks)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                GreetingHeader(username)
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Max)
                        .padding(start = contentWithRoundRectShadowPadding),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlanForToday(
                        modifier = Modifier.weight(0.9f),
                        onClick = onPlanForTodayClick
                    )
                    IconButton(
                        onClick = onSettingClick,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(TaskifyIcon.setting),
                            contentDescription = "setting",
                            modifier = Modifier.fillMaxHeight()
                        )

                    }
                }
                TodayProgress(
                    todayTasks = todayTasks,
                    onSetTodayTasksClick = onSetTodayTasksClick,
                    modifier = Modifier.padding(start = contentWithRoundRectShadowPadding)
                )
                Periods(
                    weeklyTasks = weeklyTasks,
                    monthlyTasks = monthlyTasks,
                    onWeeklyClick = onWeeklyClick,
                    onMonthlyClick = onMonthlyClick,
                    modifier = Modifier.padding(start = contentWithRoundRectShadowPadding)
                )
                if (todayTasks.isNotEmpty()) Text(
                    text = stringResource(TodayPlanDictionary.schedule),
                    modifier = Modifier.padding(top = 16.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
        taskCards(
            tasks = todayTasks,
            actions = {
                scheduleActions(
                    task = it,
                    removeMessage = removeMessage,
                    completeMessage = completeMessage,
                    onRemove = onRemoveTask,
                    onComplete = onCompleteTask
                )
            },
            state = taskCardsState,
            onClick = onTaskClick,
            showCard = { it.activeStatuses.any { s -> s.isSet } },
            spacer = {
                val darkMode = isSystemInDarkTheme()
                if (it != todayTasks.lastIndex) BoxWithConstraints(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth()
                ) {
                    Box(
                        Modifier
                            .align(Alignment.Center)
                            .fillMaxHeight()
                            .padding(end = maxWidth / 2f)
                            .drawBehind {
                                val lineHeight = 13.dp.toPx()
                                val space = 4.dp.toPx()
                                repeat(2) { i ->
                                    drawLine(
                                        color = if (darkMode) Color.White else Color.Black,
                                        start = Offset(x = 0f, y = lineHeight * i + (space * i)),
                                        end = Offset(
                                            x = 0f,
                                            y = lineHeight * (i + 1) + (space * i)
                                        ),
                                        strokeWidth = 2.dp.toPx()
                                    )
                                }
                            }
                    )
                }
            },
            leadingIcon = { i ->
                TaskCardTimeIndicator(
                    time = todayTasks[i].activeStatuses.first().startDate.toTimeString()
                )
            }
        )
    }
}

@Composable
private fun GreetingHeader(
    username: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val initialNameSize = 32
        var greetingFontSize by remember {
            mutableStateOf(TaskifyDefault.HEADER_FONT_SIZE.sp)
        }

        Column(
            modifier = Modifier.weight(0.6f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdaptiveText(
                text = "${stringResource(TodayPlanDictionary.greeting)}, $username",
                initialFontSize = 24.sp,
                maxLines = 1
            )
            AdaptiveText(
                text = stringResource(TodayPlanDictionary.question),
                initialFontSize = greetingFontSize,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                lineHeight = (greetingFontSize.value + 4).sp,
                onSizeChange = { greetingFontSize = it }
            )
        }
        if (username.isNotEmpty()) Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(PastelGreen)
                .size((initialNameSize * 2).dp)
        ) {
            Text(
                text = username[0].uppercase(),
                modifier = Modifier.align(Alignment.Center),
                fontSize = initialNameSize.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun PlanForToday(
    modifier: Modifier = Modifier,
    onClick: (TaskPeriod) -> Unit
) {
    val cornerRadius = 10.dp
    val density = LocalDensity.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawRoundRectShadow(
                cornerRadius = with(density) {
                    CornerRadius(x = cornerRadius.toPx(), y = cornerRadius.toPx())
                },
                color = boxShadowColor(),
                alpha = boxShadowOpacity()
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.linearGradient(
                    colors = lightOrangeGradient
                )
            )
            .clickable { onClick(TaskPeriod.DAY) }
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(TaskifyIcon.calendar3d),
            contentDescription = "calendar illustration",
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = stringResource(TodayPlanDictionary.planForToday),
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
private fun TodayProgress(
    todayTasks: List<Task>,
    onSetTodayTasksClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val cornerRadius = 20.dp
    val completed = todayTasks.filter {
        it.activeStatuses.any { s -> s.isCompleted }
    }
    val textColor = Color.White
    val progress = completed.size / todayTasks.size.toFloat()
    val safeToAnimate = LocalSafeAnimateContent.current
    val progressAnimated by animateFloatAsState(
        targetValue = if (safeToAnimate && todayTasks.isNotEmpty()) progress
        else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawRoundRectShadow(
                cornerRadius = with(density) {
                    CornerRadius(x = cornerRadius.toPx(), y = cornerRadius.toPx())
                },
                color = boxShadowColor(),
                alpha = if (isSystemInDarkTheme()) 0.4f else 0.25f
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground
                else CharcoalClay
            )
            .padding(
                horizontal = 24.dp,
                vertical = 48.dp
            )
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (todayTasks.isEmpty()) Column {
            Text(
                text = stringResource(TodayPlanDictionary.noTasksToday),
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = textColor
            )
            Text(
                text = stringResource(TodayPlanDictionary.setTodayTasks),
                modifier = Modifier.clickable(onClick = onSetTodayTasksClick),
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )
        }
        if (todayTasks.isNotEmpty()) Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(TodayPlanDictionary.todayProgress),
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 16.sp
                )
                Text(
                    text = "${(progress * 100).toInt()}% ${stringResource(TodayPlanDictionary.completed)}",
                    fontWeight = FontWeight.SemiBold,
                    color = textColor,
                    fontSize = 12.sp
                )
            }
            Text(
                text = "${todayTasks.size - completed.size} ${stringResource(TodayPlanDictionary.tasksLeft)}",
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(horizontal = 8.dp),
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                fontSize = 12.sp
            )
        }
        if (todayTasks.isNotEmpty()) CircularTaskProgressIndicator(
            progress = { progressAnimated },
            modifier = Modifier.size(100.dp),
            strokeWidth = 6.dp
        ) {
            Text(
                text = "${completed.size}/${todayTasks.size}",
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun Periods(
    weeklyTasks: List<Task>,
    monthlyTasks: List<Task>,
    onWeeklyClick: (TaskPeriod) -> Unit,
    onMonthlyClick: (TaskPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PeriodCard(
            period = stringResource(TodayPlanDictionary.weekly),
            tasks = weeklyTasks,
            modifier = Modifier
                .weight(0.5f)
                .clickable(
                    indication = null,
                    interactionSource = null
                ) { onWeeklyClick(TaskPeriod.WEEK) },
            imageColorFilter = ColorFilter.lighting(lightBlueGradient[0], Color.DarkGray)
        )
        PeriodCard(
            period = stringResource(TodayPlanDictionary.monthly),
            tasks = monthlyTasks,
            modifier = Modifier
                .weight(0.5f)
                .clickable(
                    indication = null,
                    interactionSource = null
                ) { onMonthlyClick(TaskPeriod.MONTH) },
            gradientBackgroundColors = lightRedGradient,
            imageColorFilter = ColorFilter.lighting(lightRedGradient[0], Color(200, 35, 0))
        )
    }
}

@Composable
private fun PeriodCard(
    period: String,
    tasks: List<Task>,
    modifier: Modifier = Modifier,
    gradientBackgroundColors: List<Color> = lightBlueGradient,
    imageColorFilter: ColorFilter? = null
) {
    val cornerRadius = 10.dp
    val density = LocalDensity.current
    val completed = tasks.filter {
        it.activeStatuses.any { s -> s.isCompleted }
    }

    Box(
        modifier = modifier
            .drawRoundRectShadow(
                cornerRadius = with(density) {
                    CornerRadius(x = cornerRadius.toPx(), y = cornerRadius.toPx())
                },
                color = boxShadowColor(),
                alpha = boxShadowOpacity()
            )
            .background(
                brush = Brush.linearGradient(gradientBackgroundColors),
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        Image(
            painter = painterResource(TaskifyIcon.calendar3d),
            contentDescription = "calendar illustration",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(y = (-16).dp)
                .size(56.dp),
            colorFilter = imageColorFilter
        )
        if (tasks.isNotEmpty()) Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 8.dp)
                .offset(y = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${completed.size}/${tasks.size}",
                color = Color.Black,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = if (tasks.isEmpty()) FontStyle.Italic else FontStyle.Normal
            )
            CircularProgressIndicator(
                progress = { completed.size / tasks.size.toFloat() },
                modifier = Modifier.size(10.dp),
                color = Color.Black,
                trackColor = Color.Black.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round,
                strokeWidth = 2.dp,
                gapSize = 0.dp
            )
        }
        Text(
            text = period,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp),
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun boxShadowOpacity() = if (isSystemInDarkTheme()) 0.5f else 0.25f

@Composable
private fun boxShadowColor() = if (isSystemInDarkTheme()) Color.White else Color.Black

@Preview
@Composable
private fun ContentPreview(
    @PreviewParameter(TaskPreviewParameter::class)
    values: List<Task>
) {
    TaskifyTheme {
        Surface {
            Content(
                username = "Kite1412",
                todayTasks = values,
                weeklyTasks = values,
                monthlyTasks = values,
                onPlanForTodayClick = {},
                onSettingClick = {},
                onRemoveTask = {},
                onCompleteTask = {},
                onWeeklyClick = {},
                onMonthlyClick = {},
                onSetTodayTasksClick = {},
                onTaskClick = {},
                modifier = Modifier.padding(32.dp)
            )
        }
    }
}