package com.nrr.taskdetail

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.nrr.designsystem.theme.Blue
import com.nrr.taskdetail.util.TaskDetailDictionary

internal enum class ConfirmationType(
    @StringRes val title: Int,
    @StringRes val confirmText: Int,
    @StringRes val cancelText: Int,
    @StringRes val confirmationDesc: Int,
    val confirmColor: Color
) {
    CANCEL_EDIT(
        title = TaskDetailDictionary.discardChanges,
        confirmText = TaskDetailDictionary.discard,
        cancelText = TaskDetailDictionary.cancel,
        confirmationDesc = TaskDetailDictionary.cancelEditMessage,
        confirmColor = Blue
    ),
    DELETE_TASK(
        title = TaskDetailDictionary.deleteTask,
        confirmText = TaskDetailDictionary.delete,
        cancelText = TaskDetailDictionary.cancel,
        confirmationDesc = TaskDetailDictionary.deleteConfirmation,
        confirmColor = Color.Red
    );
}