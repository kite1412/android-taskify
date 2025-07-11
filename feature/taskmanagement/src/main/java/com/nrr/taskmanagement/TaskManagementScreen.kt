package com.nrr.taskmanagement

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nrr.designsystem.component.Action
import com.nrr.designsystem.component.Checkbox
import com.nrr.designsystem.component.RoundRectButton
import com.nrr.designsystem.component.TaskifyButtonDefaults
import com.nrr.designsystem.component.TaskifyCheckboxDefaults
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.Blue
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.designsystem.util.TaskifyDefault
import com.nrr.model.Task
import com.nrr.taskmanagement.util.TaskManagementDictionary
import com.nrr.ui.ConfirmationDialog
import com.nrr.ui.EmptyTasks
import com.nrr.ui.LocalSnackbarHostState
import com.nrr.ui.TaskCardsDefaults
import com.nrr.ui.TaskPreviewParameter
import com.nrr.ui.TaskifyDialogDefaults
import com.nrr.ui.rememberTaskCardsState
import com.nrr.ui.taskCards
import com.nrr.ui.util.rootContentBottomPadding

@Composable
internal fun TaskManagementScreen(
    onAddClick: () -> Unit,
    onTaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskManagementViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val searchTasks by viewModel.searchTasks.collectAsStateWithLifecycle()
    val editedTasks = viewModel.editedTasks
    val snackbarState = LocalSnackbarHostState.current
    val snackbarMessage = viewModel.snackbarEvent
    val removeMessage = stringResource(TaskManagementDictionary.removeMessage)
    val deleteMessage = stringResource(TaskManagementDictionary.deleteMessage)
    val removeTasksMessage = stringResource(TaskManagementDictionary.removeTasksMessage)
    val deleteTasksMessage = stringResource(TaskManagementDictionary.deleteTasksMessage)
    val tasksState = rememberLazyGridState()
    val sortState = viewModel.sortState
    val filterState = viewModel.filterState

    LaunchedEffect(snackbarMessage) {
        if (snackbarMessage.isNotEmpty()) snackbarState.showSnackbar(
            message = snackbarMessage,
            withDismissAction = true
        ) {
            if (it == SnackbarResult.Dismissed) viewModel.updateSnackbarEvent("")
        }
    }
    LaunchedEffect(sortState.selected, filterState.selected) {
        tasksState.scrollToItem(0)
    }
    Content(
        tasks = searchTasks ?: tasks,
        onTaskClick = onTaskClick,
        onTaskLongClick = { viewModel.updateEditedTasks(it, true) },
        checked = editedTasks::contains,
        onCheckedChange = viewModel::updateEditedTasks,
        searchValue = viewModel.searchValue,
        onSearchValueChange = viewModel::updateSearchValue,
        editMode = viewModel.editMode,
        searchMode = searchTasks != null,
        onClear = viewModel::clearSearch,
        onSearch = viewModel::searchTask,
        onAddClick = onAddClick,
        sortState = sortState,
        filterState = filterState,
        onSortSelect = viewModel::onSort,
        onFilterSelect = viewModel::onFilter,
        onRemoveTaskFromPlan = viewModel::removeActiveTask,
        onDeleteTask = viewModel::deleteTask,
        selectAll = viewModel.selectAll,
        removeAllEnabled = editedTasks.any { it.activeStatuses.isNotEmpty() },
        deleteAllEnable = editedTasks.isNotEmpty(),
        onCancelEditMode = viewModel::cancelEditMode,
        onSelectAll = viewModel::updateSelectAll,
        onRemoveAllFromPlan = viewModel::removeAllConfirmation,
        onDeleteAllTasks = viewModel::deleteAllConfirmation,
        showSnackbar = viewModel::updateSnackbarEvent,
        confirmation = viewModel.confirmation,
        onConfirm = { type ->
            viewModel.handleConfirmation(
                type = type,
                message = {
                    when (type) {
                        ConfirmationType.REMOVE_ALL -> {
                            (if (it > 1) "$it " else "") +
                                    if (it == 1) removeMessage else removeTasksMessage
                        }
                        ConfirmationType.DELETE_ALL -> {
                            (if (it > 1) "$it " else "") +
                                    if (it == 1) deleteMessage else deleteTasksMessage
                        }
                    }
                }
            )
        },
        onDismissConfirmation = viewModel::dismissConfirmation,
        tasksState = tasksState,
        modifier = modifier
    )
}

// TODO remove tasks removal from plan
@Composable
private fun Content(
    tasks: List<Task>?,
    onTaskClick: (Task) -> Unit,
    onTaskLongClick: (Task) -> Unit,
    checked: (Task) -> Boolean,
    onCheckedChange: (Task, Boolean) -> Unit,
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    editMode: Boolean,
    searchMode: Boolean,
    onClear: () -> Unit,
    onSearch: () -> Unit,
    onAddClick: () -> Unit,
    sortState: CustomizeState<Customize.Sort>,
    filterState: CustomizeState<Customize.Filter>,
    onSortSelect: (Customize.Sort) -> Unit,
    onFilterSelect: (Customize.Filter) -> Unit,
    onRemoveTaskFromPlan: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    selectAll: Boolean,
    removeAllEnabled: Boolean,
    deleteAllEnable: Boolean,
    onSelectAll: (Boolean) -> Unit,
    onCancelEditMode: () -> Unit,
    onRemoveAllFromPlan: () -> Unit,
    onDeleteAllTasks: () -> Unit,
    showSnackbar: (String) -> Unit,
    confirmation: ConfirmationType?,
    onConfirm: (ConfirmationType) -> Unit,
    onDismissConfirmation: () -> Unit,
    tasksState: LazyGridState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Header()
            AnimatedContent(
                targetState = !editMode,
                label = "search bar"
            ) {
                if (it) Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SearchBar(
                            value = searchValue,
                            onValueChange = onSearchValueChange,
                            editMode = editMode,
                            searchMode = searchMode,
                            onClear = onClear,
                            onSearch = onSearch,
                            modifier = Modifier
                                .weight(0.9f)
                                .fillMaxHeight()
                        )
                        AddTask(
                            editMode = editMode,
                            onClick = onAddClick,
                            modifier = Modifier.fillMaxHeight()
                        )
                    }
                    Customizes(
                        sortState = sortState,
                        filterState = filterState,
                        onSortSelect = onSortSelect,
                        onFilterSelect = onFilterSelect
                    )
                } else EditToolbar(
                    selectAll = selectAll,
                    onSelectAll = onSelectAll,
                    onRemove = onRemoveAllFromPlan,
                    onDelete = onDeleteAllTasks,
                    onCancel = onCancelEditMode,
                    removeEnabled = removeAllEnabled,
                    deleteEnabled = deleteAllEnable
                )
            }
            if (tasks != null) Tasks(
                tasks = tasks,
                editMode = editMode,
                onClick = onTaskClick,
                onLongClick = onTaskLongClick,
                checked = checked,
                onCheckedChange = onCheckedChange,
                onRemoveFromPlan = onRemoveTaskFromPlan,
                onDelete = onDeleteTask,
                showSnackbar = showSnackbar,
                tasksState = tasksState
            )
        }
        when {
            // TODO change
            tasks == null -> Unit
            tasks.isEmpty() -> EmptyTasks(
                message = stringResource(TaskManagementDictionary.emptyTasks),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        if (confirmation != null) ConfirmationDialog(
            onDismiss = onDismissConfirmation,
            title = stringResource(confirmation.title),
            confirmText = stringResource(confirmation.confirmText),
            cancelText = stringResource(confirmation.cancelText),
            confirmationDesc = stringResource(
                id = confirmation.confirmationDesc,
                formatArgs = listOf(confirmation.totalAffected).toTypedArray()
            ),
            onConfirm = { onConfirm(confirmation) },
            colors = TaskifyDialogDefaults.colors(
                titleContentColor = Color.Red,
                confirmButtonColor = Color.Red
            )
        )
    }
}

@Composable
private fun Header(modifier: Modifier = Modifier) = Text(
    text = stringResource(TaskManagementDictionary.yourTasks),
    modifier = modifier,
    fontWeight = FontWeight.Bold,
    fontSize = TaskifyDefault.HEADER_FONT_SIZE.sp
)

@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    editMode: Boolean,
    searchMode: Boolean,
    onClear: () -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val fontSize = MaterialTheme.typography.bodyMedium.fontSize
    val clearColor by animateColorAsState(
        targetValue = if (searchMode) Color.Red else Color.Black,
        label = "search bar color"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(Color.White.copy(alpha = 0.7f))
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(100.dp)
            )
            .padding(
                start = 32.dp,
                end = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                color = Color.Black,
                fontSize = fontSize
            ),
            decorationBox = {
                if (value.isEmpty()) Text(
                    text = stringResource(TaskManagementDictionary.searchTask),
                    color = Color.Black,
                    fontSize = fontSize
                )
                it()
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch()
                    focusManager.clearFocus()
                }
            ),
            enabled = !editMode
        )
        AnimatedContent(
            targetState = value.isEmpty(),
            label = "search bar icon"
        ) {
            val interactionSource = remember { MutableInteractionSource() }

            Icon(
                painter = painterResource(if (it) TaskifyIcon.search else TaskifyIcon.cancel),
                contentDescription = if (it) "search" else "clear",
                tint = clearColor,
                modifier = Modifier
                    .height(30.dp)
                    .then(
                        if (!it) Modifier.clickable(
                            indication = null,
                            interactionSource = interactionSource,
                            onClick = {
                                focusManager.clearFocus()
                                onClear()
                            }
                        )
                        else Modifier
                    )
            )
        }
    }
}

@Composable
private fun AddTask(
    editMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = FilledIconButton(
    onClick = {
        if (!editMode) onClick()
    },
    modifier = modifier.size(48.dp)
) {
    Icon(
        painter = painterResource(TaskifyIcon.add),
        contentDescription = "add",
        tint = Color.White,
        modifier = Modifier
            .size(30.dp)
    )
}

@Composable
private fun Customizes(
    sortState: CustomizeState<Customize.Sort>,
    filterState: CustomizeState<Customize.Filter>,
    onSortSelect: (Customize.Sort) -> Unit,
    onFilterSelect: (Customize.Filter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Customize(
            customizeState = sortState,
            onSelect = onSortSelect
        )
        Customize(
            customizeState = filterState,
            onSelect = onFilterSelect
        )
    }
}

@Composable
private fun <T : Customize> Customize(
    customizeState: CustomizeState<T>,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    with(customizeState) {
        Box(modifier = modifier) {
            RoundRectButton(
                onClick = {
                    if (expanded) dismissDropdown() else expandDropdown()
                },
                action = selected.name,
                iconId = TaskifyIcon.chevronDown,
                shape = RoundedCornerShape(100)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { dismissDropdown() },
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
            ) {
                options.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(it.name)
                        },
                        onClick = {
                            dismissDropdown()
                            select(it)
                            onSelect(it)
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = if (selected == it) MaterialTheme.colorScheme.tertiary
                            else Color.White
                        )
                    )
                }
            }
        }
    }
}

private fun taskActions(
    task: Task,
    removeMessage: String,
    deleteMessage: String,
    onRemove: (Task) -> Unit,
    onDelete: (Task) -> Unit
) = mutableListOf(
    Action(
        action = deleteMessage,
        iconId = TaskifyIcon.trashBin,
        color = Color.Red,
        onClick = { onDelete(task) }
    )
).apply {
    task.activeStatuses.firstOrNull()?.let {
        add(
            index = 0,
            element = Action(
                action = removeMessage,
                iconId = TaskifyIcon.cancel,
                color = Blue,
                onClick = { onRemove(task) }
            )
        )
    }
}.toList()

@Composable
private fun Tasks(
    tasks: List<Task>,
    editMode: Boolean,
    onClick: (Task) -> Unit,
    onLongClick: (Task) -> Unit,
    checked: (Task) -> Boolean,
    onCheckedChange: (Task, Boolean) -> Unit,
    onRemoveFromPlan: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    showSnackbar: (String) -> Unit,
    tasksState: LazyGridState,
    modifier: Modifier = Modifier
) {
    if (tasks.isNotEmpty()) {
        val removeMessage = stringResource(TaskManagementDictionary.removeFromPlan)
        val deleteMessage = stringResource(TaskManagementDictionary.delete)
        val afterRemoveMessage = stringResource(TaskManagementDictionary.removeTasksMessage)
        val afterDeleteMessage = stringResource(TaskManagementDictionary.deleteTasksMessage)
        val state = rememberTaskCardsState(tasks, tasks)
        val windowAdaptiveInfo = currentWindowAdaptiveInfo()

        LazyVerticalGrid(
            columns = GridCells.Fixed(TaskCardsDefaults.adaptiveGridColumnsCount(windowAdaptiveInfo)),
            modifier = modifier,
            state = tasksState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = rootContentBottomPadding())
        ) {
            taskCards(
                tasks = tasks,
                actions = {
                    taskActions(
                        task = it,
                        removeMessage = removeMessage,
                        deleteMessage = deleteMessage,
                        onRemove = { t ->
                            onRemoveFromPlan(t)
                            showSnackbar(afterRemoveMessage)
                        },
                        onDelete = { t ->
                            onDelete(t)
                            showSnackbar(afterDeleteMessage)
                        }
                    )
                },
                state = state,
                onClick = {
                    if (editMode) onCheckedChange(it, !checked(it))
                    else onClick(it)
                },
                onLongClick = {
                    if (!editMode) onLongClick(it)
                },
                swipeEnabled = !editMode
            ) { _, task, card ->
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        task.activeStatuses.firstOrNull()?.let {
                            Text(
                                text = "Set",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        card()
                    }
                    if (editMode) Checkbox(
                        checked = checked(task),
                        onCheckedChange = { onCheckedChange(task, it) },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditToolbar(
    selectAll: Boolean,
    removeEnabled: Boolean,
    deleteEnabled: Boolean,
    onCancel: () -> Unit,
    onRemove: () -> Unit,
    onDelete: () -> Unit,
    onSelectAll: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.End
            ),
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            AnimatedVisibility(
                visible = removeEnabled
            ) {
                RoundRectButton(
                    onClick = onRemove,
                    action = stringResource(TaskManagementDictionary.removeFromPlan),
                    iconId = TaskifyIcon.emptyNote,
                    colors = TaskifyButtonDefaults.colors(
                        containerColor = Color.White,
                        contentColor = Color.Red
                    ),
                    shape = RoundedCornerShape(100),
                )
            }
            RoundRectButton(
                onClick = onDelete,
                action = stringResource(TaskManagementDictionary.delete),
                iconId = TaskifyIcon.trashBin,
                colors = TaskifyButtonDefaults.colors(
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Red.copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(100),
                enabled = deleteEnabled
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(TaskManagementDictionary.selectAll),
                    fontSize = 10.sp
                )
                Checkbox(
                    checked = selectAll,
                    onCheckedChange = onSelectAll,
                    colors = TaskifyCheckboxDefaults.colors(
                        checkmarkColor = Color.White,
                        uncheckedColor = MaterialTheme.colorScheme.primary,
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
        RoundRectButton(
            onClick = onCancel,
            action = stringResource(TaskManagementDictionary.cancel),
            iconId = TaskifyIcon.cancel,
            shape = RoundedCornerShape(100),
            colors = TaskifyButtonDefaults.colors(
                containerColor = Color.Transparent,
                contentColor = Color.Red.copy(alpha = 0.8f)
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
    var value by remember { mutableStateOf("") }
    val sort = remember { SortState() }
    val filter = remember { FilterState() }
    var editMode by remember { mutableStateOf(false) }
    val tasks1 = remember {
        tasks.mapIndexed { i, t ->
            if (i < 3) t.copy(
                activeStatuses = emptyList()
            ) else t
        }.toMutableStateList()
    }
    val checkedTasks = remember {
        mutableStateListOf<Task>()
    }
    var selectAll by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    val snackbarState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        if (snackbarMessage.isNotEmpty()) snackbarState.showSnackbar(
            message = snackbarMessage,
            withDismissAction = true
        ).also {
            if (it == SnackbarResult.Dismissed) snackbarMessage = ""
        }
    }

    TaskifyTheme {
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarState
                )
            }
        ) { innerPadding ->
            Content(
                tasks = tasks1,
                onTaskClick = {},
                onTaskLongClick = { editMode = true },
                searchValue = value,
                onSearchValueChange = { t -> value = t },
                editMode = editMode,
                searchMode = false,
                onClear = { value = "" },
                onSearch = { value = "searching" },
                onAddClick = { value = "add" },
                sortState = sort,
                filterState = filter,
                onSortSelect = {
                    tasks1.clear()
                    tasks1.addAll(tasks.sort(it))
                },
                onFilterSelect = {
                    val t = tasks1.toList()
                    tasks1.clear()
                    tasks1.addAll(t.filter(it))
                },
                checked = { checkedTasks.contains(it) },
                onCheckedChange = { task, checked ->
                    if (checked) checkedTasks.add(task)
                    else checkedTasks.remove(task)
                },
                onRemoveTaskFromPlan = { tasks1.remove(it) },
                onDeleteTask = { tasks1.remove(it) },
                selectAll = selectAll,
                onSelectAll = {
                    selectAll = it
                    if (it) checkedTasks.addAll(tasks1)
                    else checkedTasks.clear()
                },
                onRemoveAllFromPlan = { tasks1.removeIf { it in checkedTasks } },
                onDeleteAllTasks = { tasks1.removeIf { it in checkedTasks } },
                removeAllEnabled = checkedTasks.isNotEmpty(),
                deleteAllEnable = checkedTasks.isNotEmpty(),
                onCancelEditMode = { editMode = false },
                showSnackbar = { snackbarMessage = it },
                confirmation = null,
                onConfirm = {},
                onDismissConfirmation = {},
                tasksState = rememberLazyGridState(),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}