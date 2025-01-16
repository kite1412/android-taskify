package com.nrr.ui.picker.time

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.ui.Dialog
import com.nrr.ui.DialogColors
import com.nrr.ui.TaskifyDialogDefaults

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
    desc: (@Composable () -> Unit)? = null,
    dialogColors: DialogColors = TaskifyDialogDefaults.colors()
) {
    TimePickerDialog(
        onDismiss = onDismissRequest,
        onConfirm = { onConfirm(state) },
        confirmText = confirmText,
        cancelText = cancelText,
        modifier = modifier,
        colors = dialogColors,
        title = title,
        desc = desc,
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
    desc: (@Composable () -> Unit)? = null,
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                input()
                desc?.invoke()
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
            title = "Title"
        )
    }
}