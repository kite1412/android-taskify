package com.nrr.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nrr.designsystem.component.Action
import com.nrr.designsystem.component.RoundRectButton
import com.nrr.designsystem.component.TaskifyButtonDefaults
import com.nrr.designsystem.component.Toggle
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.Red
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.designsystem.util.TaskifyDefault
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.model.TimeOffset
import com.nrr.model.TimeUnit
import com.nrr.model.toLocalDateTime
import com.nrr.model.toTimeString
import com.nrr.schedule.util.ScheduleDictionary
import com.nrr.schedule.util.TaskDuration
import com.nrr.ui.Header
import com.nrr.ui.color
import com.nrr.ui.iconId
import com.nrr.ui.layout.MainLayout
import com.nrr.ui.picker.date.DatePicker
import com.nrr.ui.picker.time.TimePicker
import com.nrr.ui.rememberTaskCardsState
import com.nrr.ui.taskCards
import com.nrr.ui.toStringLocalized
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ScheduleScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val availableTasks by viewModel.availableTasks.collectAsStateWithLifecycle()

    Content(
        period = viewModel.period,
        taskDurations = viewModel.taskDurations,
        availableTasks = availableTasks,
        timeOffset = viewModel.timeOffset,
        date = viewModel.date,
        scheduleStartAt = viewModel.scheduleStartAt,
        onValueChange = viewModel::onTimeOffsetValueChange,
        onTimeUnitChange = viewModel::onTimeOffsetTimeUnitChange,
        dailySchedule = viewModel.dailySchedule,
        onDailyScheduleChange = viewModel::onDailyScheduleChange,
        onDateChange = viewModel::onDateChange,
        onTaskSelect = viewModel::onTaskSelect,
        onTaskRemove = viewModel::onTaskRemove,
        onPickDuration = viewModel::onPickDuration,
        onBackClick = onBackClick,
        onScheduleStartClick = viewModel::onPickScheduleStart,
        modifier = modifier
    )
    viewModel.pickDurationTask?.let {
        TimePicker(
            onDismissRequest = viewModel::dismissDurationPicker,
            onConfirm = { s ->
                viewModel.onPickDurationConfirm(
                    hour = s.hour,
                    minute = s.minute
                )
            },
            confirmText = stringResource(ScheduleDictionary.set),
            cancelText = stringResource(ScheduleDictionary.cancel),
            title = stringResource(ScheduleDictionary.taskDuration),
            state = rememberTimePickerState(
                initialHour = it.duration.inWholeHours.toInt(),
                initialMinute = (it.duration.inWholeMinutes % 60).toInt()
            )
        )
    }
    if (viewModel.pickScheduleStart) TimePicker(
        onDismissRequest = viewModel::dismissScheduleStartPicker,
        onConfirm = {
            viewModel.onPickScheduleStartConfirm(
                hour = it.hour,
                minute = it.minute
            )
        },
        confirmText = stringResource(ScheduleDictionary.set),
        cancelText = stringResource(ScheduleDictionary.cancel),
        title = stringResource(ScheduleDictionary.scheduleStartTime),
        state = rememberTimePickerState(
            initialHour = viewModel.scheduleStartAt.hour,
            initialMinute = viewModel.scheduleStartAt.minute
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    period: TaskPeriod,
    taskDurations: List<TaskDuration>,
    availableTasks: List<Task>,
    timeOffset: TimeOffset,
    dailySchedule: Boolean,
    date: LocalDate,
    scheduleStartAt: LocalTime,
    onValueChange: (Int) -> Unit,
    onTimeUnitChange: (TimeUnit) -> Unit,
    onDailyScheduleChange: (Boolean) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onTaskSelect: (Task) -> Unit,
    onTaskRemove: (TaskDuration) -> Unit,
    onPickDuration: (TaskDuration) -> Unit,
    onScheduleStartClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pickDate by rememberSaveable { mutableStateOf(false) }
    var showAvailableTasks by rememberSaveable { mutableStateOf(false) }

    MainLayout(modifier = modifier) {
        Header(
            title = stringResource(
                when (period) {
                    TaskPeriod.DAY -> ScheduleDictionary.dailySchedule
                    TaskPeriod.WEEK -> ScheduleDictionary.weeklySchedule
                    TaskPeriod.MONTH -> ScheduleDictionary.monthlySchedule
                }
            ),
            onBackClick = onBackClick
        )
        Row(
            modifier = Modifier
                .align(Alignment.End)
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Date(
                selectedDate = date,
                onClick = { pickDate = true },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(2f)
            )
            ScheduleStart(
                time = scheduleStartAt,
                onClick = onScheduleStartClick,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1.5f)
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OffsetBetweenTasks(
                timeOffset = timeOffset,
                onValueChange = onValueChange,
                onTimeUnitChange = onTimeUnitChange
            )
            ToggleSetting(
                name = stringResource(ScheduleDictionary.dailyScheduleSetting),
                checked = dailySchedule,
                onCheckedChange = onDailyScheduleChange
            )
        }
        TaskOrganizer(
            taskDurations = taskDurations,
            onAddClick = { showAvailableTasks = true },
            onTaskRemove = onTaskRemove,
            onPickDuration = onPickDuration
        )
    }

    if (pickDate) DatePicker(
        onDismiss = { pickDate = false },
        onConfirm = {
            onDateChange(
                Instant.fromEpochMilliseconds(it).toLocalDateTime().date
            )
            pickDate = false
        },
        confirmText = stringResource(ScheduleDictionary.choose),
        cancelText = stringResource(ScheduleDictionary.cancel),
        title = stringResource(ScheduleDictionary.chooseDate)
    )

    if (showAvailableTasks) TaskPicker(
        availableTasks = availableTasks,
        onDismissRequest = { showAvailableTasks = false },
        onTaskSelect = onTaskSelect
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OffsetBetweenTasks(
    timeOffset: TimeOffset,
    onValueChange: (Int) -> Unit,
    onTimeUnitChange: (TimeUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(ScheduleDictionary.gapBetweenTasks))
            Box {
                var showOptions by remember { mutableStateOf(false) }

                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.primary
                ) {
                    Row(
                        modifier = Modifier.clickable(
                            interactionSource = null,
                            indication = null
                        ) {
                            showOptions = !showOptions
                        },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = timeOffset.value.toString() + " ${
                                stringResource(
                                    id = when (timeOffset.timeUnit) {
                                        TimeUnit.MINUTES -> ScheduleDictionary.minutes
                                        else -> ScheduleDictionary.hours
                                    }
                                )
                            }",
                            style = textStyle
                        )
                        Icon(
                            painter = painterResource(TaskifyIcon.chevronDown),
                            contentDescription = null,
                            modifier = Modifier.size(textStyle.fontSize.value.dp)
                        )
                    }
                }
                DropdownMenu(
                    expanded = showOptions,
                    onDismissRequest = { showOptions = false }
                ) {
                    val onTimeUnitChange = { unit: TimeUnit ->
                        onTimeUnitChange(unit)
                        onValueChange(0)
                        showOptions = false
                    }

                    DropdownMenuItem(
                        text = {
                            Text(stringResource(ScheduleDictionary.minutes))
                        },
                        onClick = { onTimeUnitChange(TimeUnit.MINUTES) }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(ScheduleDictionary.hours))
                        },
                        onClick = { onTimeUnitChange(TimeUnit.HOURS) }
                    )
                }
            }
        }
        val interactionSource = remember { MutableInteractionSource() }

        Slider(
            value = timeOffset.value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            steps = (if (timeOffset.timeUnit == TimeUnit.MINUTES) 60 else 12) - 1,
            valueRange = 0f..(if (timeOffset.timeUnit == TimeUnit.MINUTES) 60f else 12f),
            colors = SliderDefaults.colors(
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            ),
            thumb = {
                SliderDefaults.Thumb(
                    interactionSource = interactionSource,
                    thumbSize = with (24.dp) {
                        DpSize(
                            width = this / 5,
                            height = this
                        )
                    }
                )
            }
        )
    }
}

@Composable
private fun ToggleSetting(
    name: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name)
        Toggle(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun Date(
    selectedDate: LocalDate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RoundRectButton(
        onClick = onClick,
        action = selectedDate.toStringLocalized(),
        modifier = modifier,
        iconId = TaskifyIcon.calendar,
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterHorizontally
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
private fun TaskOrganizer(
    taskDurations: List<TaskDuration>,
    onAddClick: () -> Unit,
    onTaskRemove: (TaskDuration) -> Unit,
    onPickDuration: (TaskDuration) -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks = remember(taskDurations.size) {
        taskDurations.map { it.task }
    }
    val state = rememberTaskCardsState(tasks)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val remove = stringResource(ScheduleDictionary.remove)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(ScheduleDictionary.tasks),
                style = style
            )
            Text(
                text = stringResource(ScheduleDictionary.duration),
                style = style.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            taskCards(
                tasks = tasks,
                actions = { i, _ ->
                    listOf(
                        Action(
                            action = remove,
                            iconId = TaskifyIcon.cancel,
                            onClick = {
                                onTaskRemove(taskDurations[i])
                            },
                            color = Red
                        )
                    )
                },
                state = state,
                key = { i, t ->
                    taskDurations.getOrNull(i)?.uuid ?: t.hashCode()
                },
                content = { i, _, card ->
                    taskDurations.getOrNull(i)?.let {
                        TaskDuration(
                            taskDuration = it,
                            onPickDurationClick = { onPickDuration(it) }
                        )
                    }
                }
            )
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = onAddClick)
                        .background(MaterialTheme.colorScheme.onBackground)
                        .padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(TaskifyIcon.add),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(8.dp)
                            .size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskDuration(
    taskDuration: TaskDuration,
    onPickDurationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(taskDuration.task.taskType.color())
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(taskDuration.task.taskType.iconId()),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = taskDuration.task.title,
                style = LocalTextStyle.current.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.tertiary)
                .clickable(onClick = onPickDurationClick)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = with(taskDuration.duration) {
                    buildAnnotatedString {
                        val minutes = inWholeMinutes % 60
                        val hours = inWholeHours

                        if (hours != 0L) append("$hours ${stringResource(ScheduleDictionary.hours)}")
                        if (hours != 0L && minutes != 0L) append("\n")
                        if (minutes != 0L) append("$minutes ${stringResource(ScheduleDictionary.minutes)}")
                    }
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskPicker(
    availableTasks: List<Task>,
    onDismissRequest: () -> Unit,
    onTaskSelect: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = { WindowInsets.safeContent }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val taskCardsState = rememberTaskCardsState(availableTasks)

            Text(
                text = stringResource(ScheduleDictionary.addTask),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = TaskifyDefault.HEADER_FONT_SIZE.sp
                )
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                taskCards(
                    tasks = availableTasks,
                    actions = { _, _ -> emptyList() },
                    state = taskCardsState,
                    content = { _, _, card ->
                        card()
                    },
                    onClick = {
                        onTaskSelect(it)
                        onDismissRequest()
                    }
                )
            }
        }
    }
}

@Composable
private fun ScheduleStart(
    time: LocalTime,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RoundRectButton(
        onClick = onClick,
        action = "${stringResource(ScheduleDictionary.startAt)}: ${time.toTimeString()}",
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterHorizontally
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold
        ),
        colors = TaskifyButtonDefaults.colors(
            containerColor = MaterialTheme.colorScheme.tertiary
        )
    )
}

@Preview
@Composable
private fun TaskDurationPreview() {
    TaskifyTheme {
        TaskDuration(
            taskDuration = TODO(),
            onPickDurationClick = {}
        )
    }
}