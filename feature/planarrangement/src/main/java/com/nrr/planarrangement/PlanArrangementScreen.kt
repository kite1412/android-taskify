package com.nrr.planarrangement

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nrr.designsystem.component.AdaptiveText
import com.nrr.designsystem.component.OutlinedRoundRectButton
import com.nrr.designsystem.component.RoundRectButton
import com.nrr.designsystem.component.TaskifyButtonDefaults
import com.nrr.designsystem.component.Toggle
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.Blue
import com.nrr.designsystem.theme.CharcoalClay30
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.designsystem.util.TaskifyDefault
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.model.TaskPriority
import com.nrr.model.TaskType
import com.nrr.model.toLocalDateTime
import com.nrr.planarrangement.util.PlanArrangementDictionary
import com.nrr.planarrangement.util.dashHeight
import com.nrr.planarrangement.util.dashSpace
import com.nrr.planarrangement.util.dashWidth
import com.nrr.ui.EmptyTasks
import com.nrr.ui.LocalSnackbarHostState
import com.nrr.ui.TaskCards
import com.nrr.ui.TaskDescription
import com.nrr.ui.TaskPreviewParameter
import com.nrr.ui.TaskStatuses
import com.nrr.ui.TaskTitle
import com.nrr.ui.TaskTypeBar
import com.nrr.ui.TaskifyDialogDefaults
import com.nrr.ui.color
import com.nrr.ui.picker.date.DatePicker
import com.nrr.ui.picker.date.SelectableDatesMonth
import com.nrr.ui.picker.date.SelectableDatesWeek
import com.nrr.ui.picker.date.rememberDefaultDatePickerState
import com.nrr.ui.picker.time.TimePicker
import com.nrr.ui.toStringLocalized
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Composable
internal fun PlanArrangementScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlanArrangementViewModel = hiltViewModel()
) {
    val period = viewModel.period
    val tasks by viewModel.tasks.collectAsStateWithLifecycle(null)
    val taskEdit = viewModel.taskEdit
    val assigningTask = viewModel.assigningTask
    val backClick = {
        if (viewModel.immediatePopBackStack) onBackClick()
        else if (assigningTask) viewModel.updateAssigningTask(false)
        else onBackClick()
    }
    val saveEnabled by viewModel.saveEnabled.collectAsStateWithLifecycle(false)
    val scope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current
    val scheduledMessage = stringResource(PlanArrangementDictionary.scheduled)

    BackHandler(onBack = backClick)
    Content(
        tasks = tasks,
        assigningTask = assigningTask,
        onTaskClick = viewModel::updateEditTask,
        taskEdit = taskEdit,
        onBackClick = backClick,
        period = period,
        onPeriodChange = viewModel::updatePeriod,
        onPeriodEditChange = viewModel::updateStatusPeriod,
        onStartTimeChange = viewModel::updateStatusStartTime,
        onEndTimeChange = viewModel::updateStatusEndTime,
        onStartDateChange = viewModel::updateStatusStartDate,
        onEndDateChange = viewModel::updateStatusEndDate,
        onReminderChange = viewModel::updateStatusReminder,
        onDefaultChange = viewModel::updateStatusDefault,
        onPriorityChange = viewModel::updateStatusPriority,
        saveEnabled = saveEnabled,
        onSave = {
            scope.launch {
                viewModel.save()
                backClick()
                snackbarHostState.showSnackbar(
                    message = scheduledMessage,
                    withDismissAction = true
                )
            }
        },
        modifier = modifier
    )
}

@Composable
private fun Content(
    tasks: List<Task>?,
    assigningTask: Boolean,
    onTaskClick: (Task) -> Unit,
    taskEdit: TaskEdit?,
    onBackClick: () -> Unit,
    period: TaskPeriod?,
    onPeriodChange: (TaskPeriod) -> Unit,
    // used while in edit mode
    onPeriodEditChange: (TaskPeriod) -> Unit,
    onStartTimeChange: (Time) -> Unit,
    onEndTimeChange: (Time) -> Unit,
    onStartDateChange: (Int) -> Unit,
    onEndDateChange: (Int) -> Unit,
    onReminderChange: (Boolean) -> Unit,
    onDefaultChange: (Boolean) -> Unit,
    onPriorityChange: (TaskPriority) -> Unit,
    saveEnabled: Boolean,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Header(
            onBackClick = onBackClick
        )
        AnimatedContent(
            targetState = !assigningTask,
            transitionSpec = {
                fadeIn() + slideInHorizontally {
                    if (targetState) -it else it
                } togetherWith
                        fadeOut() + slideOutHorizontally {
                            if (targetState) it else -it
                        }
            }
        ) {
            if (it) Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (period != null) Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SelectTask()
                    PeriodSelect(
                        period = period,
                        onPeriodChange = onPeriodChange
                    )
                }
                when (tasks) {
                    null -> Unit
                    else -> if (tasks.isEmpty())
                        EmptyTasks(
                            message = stringResource(PlanArrangementDictionary.noTasks)
                        )
                    else Tasks(
                        tasks = tasks,
                        onClick = onTaskClick
                    )
                }
            } else AssignTask(
                taskEdit = taskEdit,
                onPeriodChange = onPeriodEditChange,
                onStartTimeChange = onStartTimeChange,
                onEndTimeChange = onEndTimeChange,
                onStartDateChange = onStartDateChange,
                onEndDateChange = onEndDateChange,
                onReminderChange = onReminderChange,
                onDefaultChange = onDefaultChange,
                onPriorityChange = onPriorityChange,
                saveEnabled = saveEnabled,
                onSave = onSave
            )
        }
    }
}

@Composable
private fun Header(
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
        AdaptiveText(
            text = stringResource(PlanArrangementDictionary.arrangePlan),
            initialFontSize = TaskifyDefault.HEADER_FONT_SIZE.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SelectTask(modifier: Modifier = Modifier) = Text(
    text = stringResource(PlanArrangementDictionary.selectTask),
    modifier = modifier,
    fontSize = 20.sp,
    color = Blue,
    fontWeight = FontWeight.Bold
)

@Composable
private fun PeriodSelect(
    period: TaskPeriod,
    onPeriodChange: (TaskPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPeriods by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = null
                ) {
                    showPeriods = !showPeriods
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            AdaptiveText(
                text = period.string(),
                initialFontSize = 20.sp,
                maxLines = 1,
                fontWeight = FontWeight.Bold
            )
            Icon(
                painter = painterResource(TaskifyIcon.chevronDown),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        DropdownMenu(
            expanded = showPeriods,
            onDismissRequest = { showPeriods = false },
            modifier = Modifier.background(CharcoalClay30)
        ) {
            TaskPeriod.entries.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = it.string(),
                            color = if (it == period) Blue else Color.White
                        )
                    },
                    onClick = {
                        showPeriods = false
                        onPeriodChange(it)
                    }
                )
            }
        }
    }
}

@Composable
private fun Tasks(
    tasks: List<Task>,
    onClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    TaskCards(
        tasks = tasks,
        actions = { emptyList() },
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        onClick = onClick
    )
}

@Composable
private fun AssignTask(
    taskEdit: TaskEdit?,
    onPeriodChange: (TaskPeriod) -> Unit,
    onStartTimeChange: (Time) -> Unit,
    onEndTimeChange: (Time) -> Unit,
    onStartDateChange: (Int) -> Unit,
    onEndDateChange: (Int) -> Unit,
    onReminderChange: (Boolean) -> Unit,
    onDefaultChange: (Boolean) -> Unit,
    onPriorityChange: (TaskPriority) -> Unit,
    saveEnabled: Boolean,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (taskEdit != null) {
        val task = taskEdit.task
        val state = rememberLazyListState()
        val canScrollForward by remember {
            derivedStateOf {
                state.canScrollForward
            }
        }
        val scope = rememberCoroutineScope()

        Box(modifier = modifier) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                state = state,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Title(
                        title = task.title,
                        taskType = task.taskType
                    )
                }
                item {
                    TaskDescription(
                        description = task.description ?: ""
                    )
                }
                item {
                    TaskStatuses(
                        statuses = task.activeStatuses
                    )
                }
                item {
                    AssignmentConfiguration(
                        taskEdit = taskEdit,
                        onPeriodChange = onPeriodChange,
                        onStartTimeChange = onStartTimeChange,
                        onEndTimeChange = onEndTimeChange,
                        onStartDateChange = onStartDateChange,
                        onEndDateChange = onEndDateChange,
                        onReminderChange = onReminderChange,
                        onDefaultChange = onDefaultChange,
                        onPriorityChange = onPriorityChange
                    )
                }
            }
            AnimatedVisibility(
                visible = canScrollForward,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                val infiniteTransition = rememberInfiniteTransition()
                val density = LocalDensity.current
                val offset = with(density) {
                    infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 8.dp.toPx(),
                        animationSpec = infiniteRepeatable(
                            animation = tween(300),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                }

                Icon(
                    painter = painterResource(TaskifyIcon.chevronDown),
                    contentDescription = "scroll down",
                    modifier = Modifier
                        .size(24.dp)
                        .offset {
                            IntOffset(0, offset.value.toInt())
                        }
                        .clickable(
                            indication = null,
                            interactionSource = null
                        ) {
                            scope.launch {
                                state.animateScrollBy(
                                    state.layoutInfo.viewportEndOffset.toFloat()
                                )
                            }
                        }
                )
            }
            TextButton(
                onClick = onSave,
                modifier = Modifier.align(Alignment.BottomEnd),
                colors = TaskifyButtonDefaults.textButtonColors(
                    disabledContentColor = Color.Gray,
                    contentColor = MaterialTheme.colorScheme.tertiary
                ),
                enabled = saveEnabled
            ) {
                Text(stringResource(PlanArrangementDictionary.save))
            }
        }
    }
}

@Composable
private fun Title(
    title: String,
    taskType: TaskType,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TaskTitle(
            title = title,
            modifier = Modifier.weight(1f)
        )
        TaskTypeBar(
            taskType = taskType,
            fillBackground = true,
            iconSize = 18.dp,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        )
    }
}

@Composable
private fun AssignmentConfiguration(
    taskEdit: TaskEdit,
    onPeriodChange: (TaskPeriod) -> Unit,
    onStartTimeChange: (Time) -> Unit,
    onEndTimeChange: (Time) -> Unit,
    onStartDateChange: (Int) -> Unit,
    onEndDateChange: (Int) -> Unit,
    onReminderChange: (Boolean) -> Unit,
    onDefaultChange: (Boolean) -> Unit,
    onPriorityChange: (TaskPriority) -> Unit,
    modifier: Modifier = Modifier
) {
    val status = taskEdit.activeStatus

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        PeriodField(
            period = status.period,
            onPeriodChange = onPeriodChange
        )
        TimeField(
            startDate = taskEdit.selectedStartDate,
            endDate = taskEdit.selectedDueDate,
            onStartTimeChange = onStartTimeChange,
            onEndTimeChange = onEndTimeChange
        )
        AnimatedVisibility(
            visible = status.period != TaskPeriod.DAY
        ) {
            DateField(
                period = status.period,
                startDate = taskEdit.selectedStartDate,
                endDate = taskEdit.selectedDueDate,
                onStartDateChange = onStartDateChange,
                onEndDateChange = onEndDateChange
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ReminderToggle(
                checked = status.reminderSet,
                onCheckedChange = onReminderChange
            )
            DefaultToggle(
                checked = status.isDefault,
                onCheckedChange = onDefaultChange
            )
        }
        PriorityField(
            priority = status.priority,
            onPriorityChange = onPriorityChange
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PeriodField(
    period: TaskPeriod,
    onPeriodChange: (TaskPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Field(
        label = stringResource(PlanArrangementDictionary.schedule),
        labelFontStyle = MaterialTheme.typography.bodyLarge
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TaskPeriod.entries.forEach {
                val backgroundColor by animateColorAsState(
                    targetValue = if (period == it) Blue else Color.LightGray
                )
                val contentColor by animateColorAsState(
                    targetValue = if (period == it) Color.White else Color.Black
                )

                RoundRectButton(
                    onClick = { onPeriodChange(it) },
                    action = it.string(),
                    colors = TaskifyButtonDefaults.colors(
                        containerColor = backgroundColor,
                        contentColor = contentColor
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    contentPadding = PaddingValues(12.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeField(
    startDate: Date?,
    endDate: Date?,
    onStartTimeChange: (Time) -> Unit,
    onEndTimeChange: (Time) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingStartTime by remember {
        mutableStateOf<Boolean?>(null)
    }
    var warning by remember {
        mutableStateOf<String?>(null)
    }
    val invalidTimeWarning = stringResource(PlanArrangementDictionary.invalidTimeWarning)
    val invalid = if (startDate == null) -1
        else {
            endDate?.let {
                if (startDate > it) 1
                else 0
            } ?: 0
        }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        when (invalid) {
            0 -> Unit
            else -> {
                InvalidWarning(
                    warning = stringResource(
                        id = if (invalid == -1) PlanArrangementDictionary.enterStartTimeWarning
                        else PlanArrangementDictionary.invalidTimeWarning
                    )
                )
            }
        }
        IntervalField(
            leftLabel = stringResource(PlanArrangementDictionary.startTime),
            rightLabel = stringResource(PlanArrangementDictionary.endTime),
            left = {
                RoundRectButton(
                    onClick = {
                        editingStartTime = true
                    },
                    action = startDate?.time?.toString() ?: stringResource(PlanArrangementDictionary.none),
                    iconId = TaskifyIcon.clock
                )
            },
            right = {
                OutlinedRoundRectButton(
                    onClick = {
                        editingStartTime = false
                    },
                    action = endDate?.time?.toString() ?: stringResource(PlanArrangementDictionary.none),
                    iconId = TaskifyIcon.clock,
                    enabled = startDate != null,
                    colors = TaskifyButtonDefaults.colors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        disabledContentColor = Color.Gray
                    )
                )
            }
        )
    }
    if (editingStartTime != null) TimePicker(
        onDismissRequest = {
            editingStartTime = null
            warning = null
        },
        onConfirm = {
            if (editingStartTime!!) {
                onStartTimeChange(it.toTime())
                editingStartTime = null
                warning = null
            } else {
                startDate?.let { st ->
                    val time = it.toTime()
                    val ed = endDate?.copy(time = time) ?: Date(time = time)
                    if (st <= ed) {
                        onEndTimeChange(time)
                        editingStartTime = null
                        warning = null
                    } else {
                        warning = invalidTimeWarning
                    }
                }
            }
        },
        confirmText = stringResource(PlanArrangementDictionary.set),
        cancelText = stringResource(PlanArrangementDictionary.cancel),
        title = stringResource(
            id = if (editingStartTime!!) PlanArrangementDictionary.startTime
            else PlanArrangementDictionary.endTime
        ),
        state = rememberTimePickerState(
            initialHour = if (editingStartTime!!) startDate?.time?.hour ?: 0
                else endDate?.time?.hour ?: 0,
            initialMinute = if (editingStartTime!!) startDate?.time?.minute ?: 0
                else endDate?.time?.minute ?: 0,
            is24Hour = true
        ),
        dialogColors = TaskifyDialogDefaults.colors(
            confirmButtonColor = MaterialTheme.colorScheme.tertiary
        ),
        desc = warning?.let {
            {
                BoxWithConstraints {
                    Text(
                        text = it,
                        modifier = Modifier.width(maxWidth / 1.5f),
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(
    period: TaskPeriod,
    startDate: Date?,
    endDate: Date?,
    onStartDateChange: (Int) -> Unit,
    onEndDateChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingStartDate by remember {
        mutableStateOf<Boolean?>(null)
    }
    val curDate = remember {
        Clock.System.now().toLocalDateTime()
    }
    val invalid = if (startDate?.dayOfMonth == null) -1
        else {
            endDate?.dayOfMonth?.let {
                if (startDate.dayOfMonth > it) 1
                else 0
            } ?: 0
        }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        when (invalid) {
            0 -> Unit
            else -> InvalidWarning(
                warning = stringResource(
                    id = if (invalid == -1) PlanArrangementDictionary.enterStartDateWarning
                        else PlanArrangementDictionary.invalidDateWarning
                )
            )
        }
        IntervalField(
            leftLabel = stringResource(PlanArrangementDictionary.startDate),
            rightLabel = stringResource(PlanArrangementDictionary.endDate),
            left = {
                RoundRectButton(
                    onClick = {
                        editingStartDate = true
                    },
                    action = if (startDate?.dayOfMonth == null) stringResource(PlanArrangementDictionary.none)
                    else "${curDate.month.value}/${startDate.dayOfMonth}",
                    iconId = TaskifyIcon.calendar
                )
            },
            right = {
                OutlinedRoundRectButton(
                    onClick = {
                        editingStartDate = false
                    },
                    action = if (endDate?.dayOfMonth == null) stringResource(PlanArrangementDictionary.none)
                    else "${curDate.month.value}/${endDate.dayOfMonth}",
                    iconId = TaskifyIcon.calendar
                )
            }
        )
    }
    if (editingStartDate != null) DatePicker(
        onDismiss = { editingStartDate = null },
        onConfirm = {
            val dayOfMonth = Instant.fromEpochMilliseconds(it).toLocalDateTime()
                .dayOfMonth
            if (editingStartDate!!) onStartDateChange(dayOfMonth)
            else onEndDateChange(dayOfMonth)
            editingStartDate = null
        },
        confirmText = stringResource(PlanArrangementDictionary.set),
        cancelText = stringResource(PlanArrangementDictionary.cancel),
        title = stringResource(
            id = if (editingStartDate!!) PlanArrangementDictionary.startDate
                else PlanArrangementDictionary.endDate
        ),
        state = rememberDefaultDatePickerState(
            selectableDates = if (period == TaskPeriod.WEEK) SelectableDatesWeek
                else SelectableDatesMonth,
            initialSelectedDateMillis = if (editingStartDate!!) if (startDate?.dayOfMonth == null)
                null else startDate.toInstant(true).toEpochMilliseconds()
                else endDate?.toInstant(true)?.toEpochMilliseconds()
        ),
        confirmColors = TaskifyButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.tertiary
        ),
        cancelColors = TaskifyButtonDefaults.textButtonColors(
            contentColor = Color.White
        )
    )
}

@Composable
private fun InvalidWarning(
    warning: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val color = Color.Red
        val style = MaterialTheme.typography.bodySmall

        Icon(
            painter = painterResource(TaskifyIcon.info),
            contentDescription = "invalid",
            tint = color,
            modifier = Modifier.size((style.fontSize.value * 1.5).dp)
        )
        Text(
            text = warning,
            color = Color.Red,
            style = style.copy(
                fontStyle = FontStyle.Italic
            )
        )
    }
}

@Composable
private fun IntervalField(
    leftLabel: String,
    rightLabel: String,
    left: @Composable () -> Unit,
    right: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    rangeDesc: String = stringResource(PlanArrangementDictionary.to),
    dashCount: Int = 6
) {
    val dashCountOnEach = dashCount / 2

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dashSpace),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Field(
            label = leftLabel
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(dashSpace),
                verticalAlignment = Alignment.CenterVertically
            ) {
                left()
                HorizontalDashedLine(dashCountOnEach)
                Text(
                    text = rangeDesc,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Field(
            label = rightLabel,
            alignment = Alignment.End
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(dashSpace),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDashedLine(dashCountOnEach)
                right()
            }
        }
    }
}

@Composable
private fun PriorityField(
    priority: TaskPriority,
    onPriorityChange: (TaskPriority) -> Unit,
    modifier: Modifier = Modifier
) {
    Field(
        label = stringResource(PlanArrangementDictionary.priority),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TaskPriority.entries.forEach {
                PriorityButton(
                    priority = it,
                    onClick = onPriorityChange,
                    fillBackground = priority == it
                )
            }
        }
    }
}

@Composable
private fun PriorityButton(
    priority: TaskPriority,
    onClick: (TaskPriority) -> Unit,
    fillBackground: Boolean,
    modifier: Modifier = Modifier
) {
    val background by animateColorAsState(
        targetValue = if (fillBackground) priority.color()
            else Color.Transparent,
        label = "priority background"
    )
    val contentColor by animateColorAsState(
        targetValue = if (!fillBackground) priority.color()
            else Color.White,
        label = "priority content"
    )
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(background)
            .border(
                width = 1.dp,
                color = priority.color(),
                shape = shape
            )
            .clickable { onClick(priority) }
            .padding(12.dp),
    ) {
        Text(
            text = priority.toStringLocalized(),
            color = contentColor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ReminderToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    ToggleField(
        checked = checked,
        onCheckedChange = onCheckedChange,
        label = stringResource(PlanArrangementDictionary.reminder),
        information = stringResource(PlanArrangementDictionary.reminderInfo),
        modifier = modifier,
        showState = true
    )
}

@Composable
private fun DefaultToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    ToggleField(
        checked = checked,
        onCheckedChange = onCheckedChange,
        label = stringResource(PlanArrangementDictionary.default),
        information = stringResource(PlanArrangementDictionary.defaultInfo),
        modifier = modifier,
        showState = false
    )
}

@Composable
private inline fun Field(
    label: String,
    alignment: Alignment.Horizontal = Alignment.Start,
    modifier: Modifier = Modifier,
    information: String? = null,
    labelFontStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    content: @Composable () -> Unit
) {
    var showInformation by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = alignment
    ) {
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.primary,
                    style = labelFontStyle,
                    fontWeight = FontWeight.Bold
                )
                if (information != null) Icon(
                    painter = painterResource(TaskifyIcon.info),
                    contentDescription = "information",
                    modifier = Modifier
                        .size(labelFontStyle.fontSize.value.dp)
                        .clickable(
                            indication = null,
                            interactionSource = null
                        ) {
                            showInformation = !showInformation
                        },
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            if (showInformation && information != null) {
                val density = LocalDensity.current

                Popup(
                    onDismissRequest = { showInformation = false },
                    offset = IntOffset(
                        x = 0,
                        y = with(density) {
                            labelFontStyle.fontSize.roundToPx()
                        }
                    )
                ) {
                    val shape = RoundedCornerShape(16.dp)
                    val width = (LocalConfiguration.current.screenWidthDp / 2).dp

                    Box(
                        modifier = Modifier
                            .width(width)
                            .clip(shape)
                            .background(MaterialTheme.colorScheme.background)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = shape
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = information,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
        content()
    }
}

@Composable
private fun ToggleField(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    information: String,
    modifier: Modifier = Modifier,
    showState: Boolean
) {
    Field(
        label = label,
        modifier = modifier,
        information = information
    ) {
        Toggle(
            checked = checked,
            onCheckedChange = onCheckedChange,
            showState = showState
        )
    }
}

@Composable
private fun HorizontalDashedLine(
    dashCount: Int,
    modifier: Modifier = Modifier
        .size(
            height = dashHeight,
            width = dashWidth * dashCount + ((dashCount - 1) * dashSpace)
        ),
    dashColor: Color = LocalContentColor.current
) = Canvas(modifier = modifier) {
    val dashWidth = dashWidth.toPx()
    val dashHeight = dashHeight.toPx()
    val dashSpace = dashSpace.toPx()
    var currentX = 0f

    repeat(dashCount) {
        drawRoundRect(
            color = dashColor,
            topLeft = Offset(x = currentX, y = 0f),
            size = Size(width = dashWidth, height = dashHeight),
            cornerRadius = CornerRadius(x = 8f, y = 8f)
        )
        currentX += dashWidth + dashSpace
    }
}

@Preview
@Composable
private fun ContentPreview(
    @PreviewParameter(TaskPreviewParameter::class)
    tasks: List<Task>
) {
    var period by remember { mutableStateOf(TaskPeriod.DAY) }
    var taskEdit by remember { mutableStateOf<TaskEdit?>(null) }
    var assigningTask by remember { mutableStateOf(false) }

    TaskifyTheme {
        Content(
            tasks = tasks,
            assigningTask = assigningTask,
            onTaskClick = {
                taskEdit = TaskEdit(it)
                assigningTask = true
            },
            taskEdit = taskEdit,
            onBackClick = {
                assigningTask = false
            },
            period = period,
            onPeriodChange = { period = it },
            onPeriodEditChange = {
                taskEdit = taskEdit?.copy(
                    activeStatus = taskEdit!!.activeStatus.copy(period = it)
                )
            },
            onStartTimeChange = {
                taskEdit = taskEdit!!.copy(
                    selectedStartDate = taskEdit!!.selectedStartDate?.copy(time = it)
                )
            },
            onEndTimeChange = {
                taskEdit = taskEdit?.copy(
                    selectedDueDate = taskEdit!!.selectedDueDate?.copy(time = it)
                        ?: Date(it)
                )
            },
            onStartDateChange = {
                taskEdit = taskEdit?.copy(
                    selectedStartDate = taskEdit!!.selectedStartDate?.copy(dayOfMonth = it)
                )
            },
            onEndDateChange = {
                taskEdit = taskEdit?.copy(
                    selectedDueDate = taskEdit!!.selectedDueDate?.copy(dayOfMonth = it)
                        ?: Date(dayOfMonth = it)
                )
            },
            onReminderChange = {
                taskEdit = taskEdit?.copy(
                    activeStatus = taskEdit!!.activeStatus.copy(reminderSet = it)
                )
            },
            onDefaultChange = {
                taskEdit = taskEdit?.copy(
                    activeStatus = taskEdit!!.activeStatus.copy(isDefault = it)
                )
            },
            onPriorityChange = {
                taskEdit = taskEdit?.copy(
                    activeStatus = taskEdit!!.activeStatus.copy(priority = it)
                )
            },
            saveEnabled = true,
            onSave = {}
        )
    }
}