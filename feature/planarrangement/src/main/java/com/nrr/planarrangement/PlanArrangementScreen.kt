package com.nrr.planarrangement

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.font.FontWeight
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
import com.nrr.planarrangement.util.PlanArrangementDictionary
import com.nrr.planarrangement.util.dashHeight
import com.nrr.planarrangement.util.dashSpace
import com.nrr.planarrangement.util.dashWidth
import com.nrr.ui.EmptyTasks
import com.nrr.ui.TaskCards
import com.nrr.ui.TaskDescription
import com.nrr.ui.TaskPreviewParameter
import com.nrr.ui.TaskStatuses
import com.nrr.ui.TaskTitle
import com.nrr.ui.TaskTypeBar
import com.nrr.ui.TimePicker
import com.nrr.ui.color
import com.nrr.ui.toStringLocalized

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

    Content(
        tasks = tasks,
        assigningTask = assigningTask,
        onTaskClick = viewModel::updateEditTask,
        taskEdit = taskEdit,
        onBackClick = {
            if (viewModel.immediatePopBackStack) onBackClick()
            else if (assigningTask) viewModel.updateAssigningTask(false)
            else onBackClick()
        },
        period = period,
        onPeriodChange = viewModel::updatePeriod,
        onPeriodEditChange = viewModel::updateStatusPeriod,
        onStartTimeChange = viewModel::updateStatusStartTime,
        onEndTimeChange = viewModel::updateStatusEndTime,
        onReminderChange = viewModel::updateStatusReminder,
        onDefaultChange = viewModel::updateStatusDefault,
        onPriorityChange = viewModel::updateStatusPriority,
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
    onReminderChange: (Boolean) -> Unit,
    onDefaultChange: (Boolean) -> Unit,
    onPriorityChange: (TaskPriority) -> Unit,
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
                onReminderChange = onReminderChange,
                onDefaultChange = onDefaultChange,
                onPriorityChange = onPriorityChange
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
    onReminderChange: (Boolean) -> Unit,
    onDefaultChange: (Boolean) -> Unit,
    onPriorityChange: (TaskPriority) -> Unit,
    modifier: Modifier = Modifier
) {
    if (taskEdit != null) {
        val task = taskEdit.task

        Box(modifier = modifier) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
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
                        onReminderChange = onReminderChange,
                        onDefaultChange = onDefaultChange,
                        onPriorityChange = onPriorityChange
                    )
                }
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TaskTitle(
            title = title,
            modifier = Modifier.weight(1f)
        )
        TaskTypeBar(
            taskType = taskType,
            fillBackground = true,
            iconSize = 18.dp
        )
    }
}

@Composable
private fun AssignmentConfiguration(
    taskEdit: TaskEdit,
    onPeriodChange: (TaskPeriod) -> Unit,
    onStartTimeChange: (Time) -> Unit,
    onEndTimeChange: (Time) -> Unit,
    onReminderChange: (Boolean) -> Unit,
    onDefaultChange: (Boolean) -> Unit,
    onPriorityChange: (TaskPriority) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        PeriodField(
            period = taskEdit.activeStatus.period,
            onPeriodChange = onPeriodChange
        )
        TimeField(
            startTime = taskEdit.selectedStartDate.time,
            endTime = taskEdit.selectedDueDate?.time,
            onStartTimeChange = onStartTimeChange,
            onEndTimeChange = onEndTimeChange
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ReminderToggle(
                checked = taskEdit.activeStatus.reminderSet,
                onCheckedChange = onReminderChange
            )
            DefaultToggle(
                checked = taskEdit.activeStatus.isDefault,
                onCheckedChange = onDefaultChange
            )
        }
        PriorityField(
            priority = taskEdit.activeStatus.priority,
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
                    contentPadding = PaddingValues(12.dp),
                    enabled = it == TaskPeriod.DAY
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeField(
    startTime: Time,
    endTime: Time?,
    onStartTimeChange: (Time) -> Unit,
    onEndTimeChange: (Time) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingStartTime by remember {
        mutableStateOf<Boolean?>(null)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dashSpace),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Field(
            label = stringResource(PlanArrangementDictionary.startTime)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(dashSpace),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoundRectButton(
                    onClick = {
                        editingStartTime = true
                    },
                    action = startTime.toString(),
                    iconId = TaskifyIcon.clock
                )
                HorizontalDashedLine(3)
                Text(
                    text = stringResource(PlanArrangementDictionary.to),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Field(
            label = stringResource(PlanArrangementDictionary.endTime),
            alignment = Alignment.End
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(dashSpace),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDashedLine(3)
                OutlinedRoundRectButton(
                    onClick = {
                        editingStartTime = false
                    },
                    action = endTime?.toString() ?: stringResource(PlanArrangementDictionary.none),
                    iconId = TaskifyIcon.clock
                )
            }
        }
    }
    if (editingStartTime != null) TimePicker(
        onDismissRequest = { editingStartTime = null },
        onConfirm = {
            if (editingStartTime!!) onStartTimeChange(it.toTime())
            else onEndTimeChange(it.toTime())
            editingStartTime = null
        },
        confirmText = stringResource(PlanArrangementDictionary.set),
        cancelText = stringResource(PlanArrangementDictionary.cancel),
        title = stringResource(
            id = if (editingStartTime!!) PlanArrangementDictionary.startTime
            else PlanArrangementDictionary.endTime
        ),
        state = rememberTimePickerState(
            initialHour = if (editingStartTime!!) startTime.hour else endTime?.hour ?: 0,
            initialMinute = if (editingStartTime!!) startTime.minute else endTime?.minute ?: 0,
            is24Hour = true
        )
    )
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
            horizontalArrangement = Arrangement.spacedBy(12.dp)
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
            .padding(
                vertical = 6.dp,
                horizontal = 12.dp
            ),
    ) {
        Text(
            text = priority.toStringLocalized(),
            color = contentColor
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
                    selectedStartDate = taskEdit!!.selectedStartDate.copy(time = it)
                )
            },
            onEndTimeChange = {
                taskEdit = taskEdit?.copy(
                    selectedDueDate = taskEdit!!.selectedDueDate?.copy(time = it)
                        ?: Date(it)
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
            }
        )
    }
}