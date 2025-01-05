package com.nrr.taskdetail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nrr.designsystem.component.TextField
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.model.Task
import com.nrr.taskdetail.util.TaskDetailDictionary
import com.nrr.ui.TaskPreviewParameter

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
                onDescriptionChange = onDescriptionChange
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
            onDescriptionChange = { editedTask = editedTask.copy(description = it) }
        )
    }
}