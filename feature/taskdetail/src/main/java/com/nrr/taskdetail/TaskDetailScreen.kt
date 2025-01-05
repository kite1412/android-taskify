package com.nrr.taskdetail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.nrr.designsystem.component.TextField
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.model.Task
import com.nrr.model.TaskType
import com.nrr.taskdetail.util.TaskDetailDictionary
import com.nrr.ui.TaskPreviewParameter
import com.nrr.ui.color
import com.nrr.ui.iconId
import com.nrr.ui.toStringLocalized

@Composable
internal fun TaskDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val task = viewModel.task

    Content(
        task = task,
        createMode = viewModel.taskId == null,
        editedTask = viewModel.editedTask,
        editMode = viewModel.editMode,
        onBackClick = {},
        onEditClick = {},
        onTitleChange = {},
        onDescriptionChange = {},
        onTypeChange = {},
        modifier = modifier
    )
}

@Composable
private fun Content(
    task: Task?,
    createMode: Boolean,
    editedTask: TaskEdit,
    editMode: Boolean,
    onBackClick: (editMode: Boolean) -> Unit,
    onEditClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTypeChange: (TaskType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
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
                task = editedTask,
                onTitleChange = onTitleChange,
                onDescriptionChange = onDescriptionChange,
                onTypeChange = onTypeChange
            )
        }
    }
}

@Composable
private fun Header(
    createMode: Boolean,
    editMode: Boolean,
    onBackClick: (editMode: Boolean) -> Unit,
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
                    onClick = { onBackClick(editMode) },
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
private fun EditPage(
    task: TaskEdit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTypeChange: (TaskType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        TitleEdit(
            value = task.title,
            onValueChange = onTitleChange
        )
        DescriptionEdit(
            value = task.description,
            onValueChange = onDescriptionChange
        )
        TaskTypeEdit(
            type = task.taskType,
            onTypeChange = onTypeChange
        )
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
            }
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
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = dropdownShape
                            )
                            .background(
                                color = MaterialTheme.colorScheme.background,
                                shape = dropdownShape
                            )
                    ) {

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
private fun TaskTypeBar(
    taskType: TaskType,
    fillBackground: Boolean,
    modifier: Modifier = Modifier
) {
    val name = taskType.toStringLocalized()
    val color = taskType.color()
    val animatedBackground by animateColorAsState(
        targetValue = if (fillBackground) color else Color.Transparent,
        label = "background color"
    )
    val animatedContentColor by animateColorAsState(
        targetValue = if (fillBackground) Color.White else color,
        label = "content color"
    )
    val shape = RoundedCornerShape(8.dp)

    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = color,
                shape = shape
            )
            .background(
                color = animatedBackground,
                shape = shape
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(taskType.iconId()),
            contentDescription = name,
            modifier = Modifier.size(24.dp),
            tint = animatedContentColor
        )
        Text(
            text = name,
            color = animatedContentColor,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize
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
            createMode = true,
            editedTask = editedTask,
            editMode = editMode,
            onBackClick = { if (it) editMode = false },
            onEditClick = { editMode = true },
            onTitleChange = { editedTask = editedTask.copy(title = it) },
            onDescriptionChange = { editedTask = editedTask.copy(description = it) },
            onTypeChange = { editedTask = editedTask.copy(taskType = it) }
        )
    }
}