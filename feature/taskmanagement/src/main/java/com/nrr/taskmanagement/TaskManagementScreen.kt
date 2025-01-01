package com.nrr.taskmanagement

import CustomizeState
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import com.nrr.designsystem.component.AdaptiveText
import com.nrr.designsystem.component.RoundRectButton
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.designsystem.util.TaskifyDefault
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.taskmanagement.util.TaskManagementDictionary
import com.nrr.ui.TaskCards
import com.nrr.ui.TaskPreviewParameter

@Composable
internal fun TaskManagementScreen(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskManagementViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    Content(
        tasks = tasks,
        tasksActions = { listOf() },
        onTaskClick = {},
        onTaskLongClick = {},
        searchValue = viewModel.searchValue,
        onSearchValueChange = viewModel::updateSearchValue,
        editMode = viewModel.editMode,
        onClear = viewModel::clearSearchValue,
        onSearch = viewModel::searchTask,
        onAddClick = onAddClick,
        sortState = TODO(),
        filterState = TODO(),
        onSortSelect = {},
        onFilterSelect = {}
    )
}

@Composable
private fun Content(
    tasks: List<Task>?,
    tasksActions: (Task) -> List<Action>,
    onTaskClick: (Task) -> Unit,
    onTaskLongClick: (Task) -> Unit,
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    editMode: Boolean,
    onClear: () -> Unit,
    onSearch: () -> Unit,
    onAddClick: () -> Unit,
    sortState: CustomizeState<Customize.Sort>,
    filterState: CustomizeState<Customize.Filter>,
    onSortSelect: (Customize.Sort) -> Unit,
    onFilterSelect: (Customize.Filter) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Header()
        Row(
            modifier = Modifier.height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SearchBar(
                value = searchValue,
                onValueChange = onSearchValueChange,
                editMode = editMode,
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
        Tasks(
            tasks = tasks,
            actions = tasksActions,
            editMode = editMode,
            onClick = onTaskClick,
            onLongClick = onTaskLongClick,
            modifier = Modifier.verticalScroll(rememberScrollState())
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
    onClear: () -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val fontSize = MaterialTheme.typography.bodyMedium.fontSize

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
                    focusManager.clearFocus()
                    onSearch()
                }
            ),
            enabled = !editMode
        )
        AnimatedContent(
            targetState = value.isEmpty(),
            label = "search bar icon"
        ) {
            Icon(
                painter = painterResource(if (it) TaskifyIcon.search else TaskifyIcon.add),
                contentDescription = if (it) "search" else "clear",
                tint = Color.Black,
                modifier = Modifier
                    .height(30.dp)
                    .rotate(if (it) 0f else 45f)
                    .then(
                        if (!it) Modifier.clickable(onClick = onClear)
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

@Composable
private fun Tasks(
    tasks: List<Task>?,
    actions: (Task) -> List<Action>,
    editMode: Boolean,
    onClick: (Task) -> Unit,
    onLongClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tasks != null) {
        if (tasks.isNotEmpty()) TaskCards(
            tasks = tasks,
            actions = actions,
            modifier = modifier,
            onClick = onClick,
            onLongClick = onLongClick,
            clickEnabled = { !editMode },
            swipeEnabled = !editMode,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            leadingIcon = {
                tasks[it].activeStatus?.let { status ->
                    AdaptiveText(
                        text = stringResource(
                            id = when (status.period) {
                                TaskPeriod.DAY -> {
                                    if (status.isDefault) TaskManagementDictionary.daily
                                    else TaskManagementDictionary.today
                                }
                                TaskPeriod.WEEK -> TaskManagementDictionary.weekly
                                TaskPeriod.MONTH -> TaskManagementDictionary.monthly
                            }
                        ),
                        initialFontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(0.15f),
                        maxLines = 1
                    )
                }
            },
            resetSwipes = editMode
        ) else {}
    } else {}
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

    TaskifyTheme {
        Scaffold { innerPadding ->
            Content(
                tasks = tasks,
                tasksActions = { Action.mocks },
                onTaskClick = {},
                onTaskLongClick = { editMode = true },
                searchValue = value,
                onSearchValueChange = { t -> value = t },
                editMode = editMode,
                onClear = { value = "" },
                onSearch = { value = "searching" },
                onAddClick = { value = "add" },
                sortState = sort,
                filterState = filter,
                onSortSelect = { editMode = false },
                onFilterSelect = { },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}