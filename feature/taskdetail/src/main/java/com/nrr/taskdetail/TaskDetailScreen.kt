package com.nrr.taskdetail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import com.nrr.designsystem.component.RoundRectButton
import com.nrr.designsystem.component.TaskifyButtonDefaults
import com.nrr.designsystem.component.TextField
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.model.Task
import com.nrr.model.TaskType
import com.nrr.model.toTimeString
import com.nrr.taskdetail.util.TaskDetailDictionary
import com.nrr.taskdetail.util.examplesId
import com.nrr.ui.ConfirmationDialog
import com.nrr.ui.LocalSnackbarHostState
import com.nrr.ui.TaskDescription
import com.nrr.ui.TaskPreviewParameter
import com.nrr.ui.TaskStatuses
import com.nrr.ui.TaskTitle
import com.nrr.ui.TaskTypeBar
import com.nrr.ui.TaskifyDialogDefaults
import com.nrr.ui.color
import com.nrr.ui.toDateStringLocalized
import com.nrr.ui.toStringLocalized
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

@Composable
internal fun TaskDetailScreen(
    onBackClick: () -> Unit,
    onPlanTaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val task = viewModel.task
    val createMode = viewModel.taskId == null
    val editMode = viewModel.editMode
    val scope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current
    val taskDeleted = stringResource(
        id = TaskDetailDictionary.taskDeleted, task?.title ?: ""
    )
    val taskCreated = stringResource(
        id = TaskDetailDictionary.taskCreated, viewModel.editedTask.title
    )

    Content(
        task = task,
        createMode = createMode,
        editedTask = viewModel.editedTask,
        editMode = editMode,
        onBackClick = {
            if (!editMode) onBackClick()
            else viewModel.cancelEditMode()
        },
        onEditClick = { viewModel.updateEditMode(true) },
        onTitleChange = viewModel::updateTitle,
        onDescriptionChange = viewModel::updateDescription,
        onTypeChange = viewModel::updateType,
        onEditComplete = {
            scope.launch {
                viewModel.saveEdit()
                if (createMode) {
                    snackbarHostState.showSnackbar(taskCreated)
                    onBackClick()
                }
            }
        },
        confirmation = viewModel.confirmation,
        onConfirm = {
            scope.launch {
                viewModel.handleConfirmation(it)
                if (it == ConfirmationType.DELETE_TASK) {
                    snackbarHostState.showSnackbar(taskDeleted)
                    onBackClick()
                }
            }
        },
        onDelete = viewModel::deleteConfirmation,
        onDismissConfirmation = viewModel::dismissConfirmation,
        onPlanTaskClick = onPlanTaskClick,
        modifier = modifier
    )
}

@Composable
private fun Content(
    task: Task?,
    createMode: Boolean,
    editedTask: TaskEdit,
    editMode: Boolean,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTypeChange: (TaskType) -> Unit,
    onEditComplete: () -> Unit,
    onDelete: () -> Unit,
    confirmation: ConfirmationType?,
    onConfirm: (ConfirmationType) -> Unit,
    onDismissConfirmation: () -> Unit,
    onPlanTaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Header(
                createMode = createMode,
                editMode = editMode,
                onBackClick = onBackClick,
                onEditClick = onEditClick
            )
            AnimatedContent(
                targetState = editMode,
                label = "main content",
                modifier = Modifier.fillMaxSize(),
                transitionSpec = {
                    slideInVertically { -it } + fadeIn() togetherWith
                            slideOutVertically { -it } + fadeOut()
                }
            ) {
                if (it || createMode) EditPage(
                    task = task,
                    taskEdit = editedTask,
                    createMode = createMode,
                    onTitleChange = onTitleChange,
                    onDescriptionChange = onDescriptionChange,
                    onTypeChange = onTypeChange,
                    onComplete = onEditComplete
                ) else DetailPage(
                    task = task,
                    onPlanClick = onPlanTaskClick,
                    modifier = Modifier.verticalScroll(rememberScrollState())
                )
            }
        }
        AnimatedVisibility(
            visible = !editMode && !createMode,
            modifier = Modifier.align(Alignment.BottomEnd),
            enter = slideInVertically { it * 2 } + fadeIn(),
            exit = slideOutVertically { it * 2 } + fadeOut()
        ) {
            RoundRectButton(
                onClick = onDelete,
                action = stringResource(TaskDetailDictionary.delete),
                iconId = TaskifyIcon.trashBin,
                colors = TaskifyButtonDefaults.colors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            )
        }
        if (confirmation != null) ConfirmationDialog(
            onDismiss = onDismissConfirmation,
            title = stringResource(confirmation.title),
            confirmText = stringResource(confirmation.confirmText),
            cancelText = stringResource(confirmation.cancelText),
            confirmationDesc = stringResource(confirmation.confirmationDesc),
            onConfirm = { onConfirm(confirmation) },
            colors = TaskifyDialogDefaults.colors(
                confirmButtonColor = confirmation.confirmColor,
                titleContentColor = if (confirmation == ConfirmationType.DELETE_TASK)
                    Color.Red else if (isSystemInDarkTheme()) Color.White else Color.Black
            )
        )
    }
}

@Composable
private fun Header(
    createMode: Boolean,
    editMode: Boolean,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AnimatedContent(
            targetState = editMode,
            label = "header",
            transitionSpec = {
                slideInVertically { -it } + fadeIn() togetherWith
                        slideOutVertically { -it } + fadeOut()
            }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = if (it) Color.Red else LocalContentColor.current
                    )
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (it) TaskifyIcon.cancel else TaskifyIcon.back
                        ),
                        contentDescription = "cancel",
                        modifier = Modifier.size(40.dp)
                    )
                }
                Text(
                    text = stringResource(
                        id = if (createMode) TaskDetailDictionary.createNew
                        else if (it) TaskDetailDictionary.editTask
                        else TaskDetailDictionary.detail
                    ),
                    modifier = Modifier.padding(top = 2.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
        }
        AnimatedVisibility(
            visible = !createMode && !editMode,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it }
        ) {
            IconButton(
                onClick = onEditClick,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    painter = painterResource(id = TaskifyIcon.pencil),
                    contentDescription = "edit",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Composable
private fun DetailPage(
    task: Task?,
    onPlanClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    task?.let {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            Title(
                title = it.title,
                taskType = it.taskType,
                createdAt = it.createdAt,
                updatedAt = it.updateAt
            )
            TaskDescription(
                description = it.description ?: ""
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TaskStatuses(
                    statuses = it.activeStatuses,
                    modifier = Modifier.weight(0.8f)
                )
                RoundRectButton(
                    onClick = { onPlanClick(task) },
                    action = stringResource(TaskDetailDictionary.planTask),
                    colors = TaskifyButtonDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                )
            }
        }
    }
}

@Composable
private fun EditPage(
    task: Task?,
    taskEdit: TaskEdit,
    createMode: Boolean,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTypeChange: (TaskType) -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            TitleEdit(
                value = taskEdit.title,
                onValueChange = onTitleChange
            )
            DescriptionEdit(
                value = taskEdit.description,
                onValueChange = onDescriptionChange
            )
            TaskTypeEdit(
                type = taskEdit.taskType,
                onTypeChange = onTypeChange
            )
        }
        CompleteEditButton(
            task = task,
            taskEdit = taskEdit,
            createMode = createMode,
            onComplete = onComplete,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Composable
private fun Title(
    title: String,
    taskType: TaskType,
    createdAt: Instant,
    updatedAt: Instant,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        TaskTitle(title)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val smallFontSize = MaterialTheme.typography.bodySmall.fontSize

                Column {
                    Text(
                        text = stringResource(TaskDetailDictionary.createdOn),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = smallFontSize,
                        lineHeight = smallFontSize
                    )
                    Text(
                        text = "${createdAt.toDateStringLocalized()}\n${createdAt.toTimeString()}",
                        fontSize = smallFontSize,
                        lineHeight = smallFontSize,
                    )
                }
                Column {
                    Text(
                        text = stringResource(TaskDetailDictionary.updatedOn),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = smallFontSize,
                        lineHeight = smallFontSize
                    )
                    Text(
                        text = "${updatedAt.toDateStringLocalized()}\n${updatedAt.toTimeString()}",
                        fontSize = smallFontSize,
                        lineHeight = smallFontSize,
                    )
                }
            }
            TaskTypeBar(
                taskType = taskType,
                fillBackground = true
            )
        }
    }
}

@Composable
private fun TitleEdit(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        Text(
            text = stringResource(TaskDetailDictionary.title),
            fontWeight = FontWeight.Bold
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(stringResource(TaskDetailDictionary.editTitle))
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            )
        )
    }
}

@Composable
private fun DescriptionEdit(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val descTextStyle = MaterialTheme.typography.bodyLarge
    val descMaxLines = 8
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(TaskDetailDictionary.description),
            fontWeight = FontWeight.Bold
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
                .defaultMinSize(
                    minHeight = (descTextStyle.lineHeight.value.toInt() * descMaxLines).dp
                ),
            textStyle = descTextStyle.copy(
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            ),
            maxLines = descMaxLines,
            decorationBox = {
                if (value.isEmpty()) Text(
                    text = stringResource(TaskDetailDictionary.editDescription),
                    color = Color.Gray,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
                it()
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            cursorBrush = SolidColor(LocalContentColor.current)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TaskTypeEdit(
    type: TaskType?,
    onTypeChange: (TaskType) -> Unit,
    modifier: Modifier = Modifier
) {
    var showInfo by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(TaskDetailDictionary.taskType),
                fontWeight = FontWeight.Bold
            )
            Column {
                val dropdownShape = RoundedCornerShape(8.dp)
                val iconSize = 16
                val config = LocalConfiguration.current

                Icon(
                    painter = painterResource(TaskifyIcon.info),
                    contentDescription = "info",
                    modifier = Modifier
                        .size(iconSize.dp)
                        .clickable(
                            indication = null,
                            interactionSource = null
                        ) {
                            showInfo = !showInfo
                        },
                    tint = MaterialTheme.colorScheme.primary
                )
                if (showInfo) Popup(
                    onDismissRequest = { showInfo = false },
                    offset = with(LocalDensity.current) {
                        IntOffset(0, (iconSize + 2).dp.roundToPx())
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .clip(dropdownShape)
                            .widthIn(
                                max = (config.screenWidthDp / 2).dp
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = dropdownShape
                            )
                            .background(
                                color = MaterialTheme.colorScheme.background,
                                shape = dropdownShape
                            )
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TaskType.entries.forEach {
                            TaskTypeExamples(it)
                        }
                    }
                }
            }
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TaskType.entries.forEach {
                TaskTypeBar(
                    taskType = it,
                    fillBackground = it == type,
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = null
                    ) { onTypeChange(it) }
                )
            }
        }
    }
}

@Composable
private fun TaskTypeExamples(
    taskType: TaskType,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = taskType.toStringLocalized(),
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            lineHeight = MaterialTheme.typography.bodyMedium.fontSize,
            color = taskType.color()
        )
        Text(
            text = stringResource(taskType.examplesId()),
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            lineHeight = MaterialTheme.typography.bodySmall.fontSize
        )
    }
}

@Composable
private fun CompleteEditButton(
    task: Task?,
    taskEdit: TaskEdit,
    createMode: Boolean,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onComplete,
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.tertiary,
            disabledContentColor = Color.Gray
        ),
        enabled = !taskEdit.equals(task)
                && (taskEdit.title.isNotEmpty()
                && taskEdit.taskType != null)
    ) {
        Text(
            text = stringResource(
                id = if (createMode) TaskDetailDictionary.createTask
                    else TaskDetailDictionary.save
            )
        )
    }
}

@Preview
@Composable
private fun ContentPreview(
    @PreviewParameter(TaskPreviewParameter::class)
    tasks: List<Task>
) {
    val task = tasks[0]
    var editMode by remember { mutableStateOf(false) }
    var editedTask by remember { mutableStateOf(TaskEdit()) }

    TaskifyTheme {
        Content(
            task = task,
            createMode = false,
            editedTask = editedTask,
            editMode = editMode,
            onBackClick = { if (editMode) editMode = false },
            onEditClick = { editMode = true },
            onTitleChange = { editedTask = editedTask.copy(title = it) },
            onDescriptionChange = { editedTask = editedTask.copy(description = it) },
            onTypeChange = { editedTask = editedTask.copy(taskType = it) },
            onEditComplete = { editMode = false },
            onDelete = {},
            confirmation = null,
            onConfirm = {},
            onDismissConfirmation = {},
            onPlanTaskClick = {},
            modifier = Modifier
        )
    }
}