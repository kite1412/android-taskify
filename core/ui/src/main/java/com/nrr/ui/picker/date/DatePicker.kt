package com.nrr.ui.picker.date

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrr.designsystem.component.TaskifyButtonDefaults
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.model.toLocalDateTime
import com.nrr.ui.picker.CustomSelectableDates
import kotlinx.datetime.Clock

// title padding from DatePicker Material 3
private val DatePickerTitlePadding =
    PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
    confirmText: String,
    cancelText: String,
    title: String,
    modifier: Modifier = Modifier,
    state: DatePickerState = rememberDefaultDatePickerState(
        selectableDates = SelectableDatesMonth
    ),
    colors: DatePickerColors = DatePickerDefaults.colors(),
    confirmColors: ButtonColors = TaskifyButtonDefaults.textButtonColors(),
    cancelColors: ButtonColors = TaskifyButtonDefaults.textButtonColors()
) {
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onConfirm(state.selectedDateMillis!!) },
                colors = confirmColors
            ) {
                Text(confirmText)
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = cancelColors
            ) {
                Text(cancelText)
            }
        },
        colors = colors
    ) {
        androidx.compose.material3.DatePicker(
            state = state,
            showModeToggle = false,
            title = {
                Text(
                    text = title,
                    modifier = Modifier.padding(DatePickerTitlePadding)
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberDefaultDatePickerState(
    selectableDates: CustomSelectableDates,
    initialSelectedDateMillis: Long? = null,
) = with(Clock.System.now()) {
    rememberDatePickerState(
        yearRange = with(toLocalDateTime()) {
            year..year
        },
        selectableDates = selectableDates,
        initialSelectedDateMillis = initialSelectedDateMillis
            ?: toEpochMilliseconds()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DatePickerPreview() {
    TaskifyTheme {
        DatePicker(
            onDismiss = {},
            onConfirm = {},
            confirmText = "Confirm",
            cancelText = "Cancel",
            title = "Select"
        )
    }
}