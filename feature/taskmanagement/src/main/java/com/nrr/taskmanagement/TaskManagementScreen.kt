package com.nrr.taskmanagement

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.designsystem.util.TaskifyDefault
import com.nrr.taskmanagement.util.TaskManagementDictionary

@Composable
internal fun TaskManagementScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskManagementViewModel = hiltViewModel()
) {

}

@Composable
private fun Content(
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    editMode: Boolean,
    onClear: () -> Unit,
    onSearch: () -> Unit,
    onAddClick: () -> Unit,
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
            )
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
) = IconButton(
    onClick = {
        if (!editMode) onClick()
    },
    modifier = modifier.size(48.dp),
    colors = IconButtonDefaults.iconButtonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White
    )
) {
    Icon(
        painter = painterResource(TaskifyIcon.add),
        contentDescription = "add",
        tint = Color.White,
        modifier = Modifier
            .size(30.dp)
    )
}

@Preview
@Composable
private fun ContentPreview() {
    var value by remember { mutableStateOf("") }
    TaskifyTheme {
        Scaffold {
            Content(
                searchValue = value,
                onSearchValueChange = { t -> value = t },
                editMode = false,
                onClear = { value = "" },
                onSearch = { value = "searching" },
                onAddClick = { value = "add" },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            )
        }
    }
}