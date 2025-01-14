package com.nrr.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.nrr.designsystem.theme.Blue
import com.nrr.designsystem.theme.TaskifyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    onDismissRequest: () -> Unit,
    onConfirm: (TimePickerState) -> Unit,
    confirmText: String,
    cancelText: String,
    title: String,
    modifier: Modifier = Modifier,
    state: TimePickerState = rememberTimePickerState(
        is24Hour = true
    ),
    desc: String? = null
) {
    TimePickerDialog(
        onDismiss = onDismissRequest,
        onConfirm = { onConfirm(state) },
        confirmText = confirmText,
        cancelText = cancelText,
        modifier = modifier,
        colors = TaskifyDialogDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primary,
            confirmButtonColor = Blue,
            titleContentColor = Color.White,
            textContentColor = Color.White
        ),
        title = title,
        desc = desc
    ) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                onSurfaceVariant = LocalContentColor.current
            ),
            typography = MaterialTheme.typography
        ) {
            TimeInput(
                state = state,
                colors = TimePickerDefaults.colors(
                    timeSelectorSelectedContainerColor = Color.White,
                    timeSelectorUnselectedContainerColor = Color.LightGray,
                    timeSelectorSelectedContentColor = Color.Black,
                    timeSelectorUnselectedContentColor = Color.Black
                )
            )
        }
    }
}

@Composable
private fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmText: String,
    cancelText: String,
    title: String,
    modifier: Modifier = Modifier,
    colors: DialogColors = TaskifyDialogDefaults.colors(),
    desc: String? = null,
    input: @Composable () -> Unit
) {
    Dialog(
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        confirmText = confirmText,
        cancelText = cancelText,
        modifier = modifier,
        colors = colors,
        text = {
            Column {
                input()
                desc?.let {
                    Text(it)
                }
            }
        },
        title = { Text(title) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun TimePickerPreview() {
    TaskifyTheme {
        TimePicker(
            onDismissRequest = {},
            onConfirm = {},
            confirmText = "Confirm",
            cancelText = "Cancel",
            title = "Title",
            desc = "Input a time"
        )
    }
}