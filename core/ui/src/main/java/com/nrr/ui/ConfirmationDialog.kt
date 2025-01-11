package com.nrr.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ConfirmationDialog(
    onDismiss: () -> Unit,
    title: String,
    confirmText: String,
    cancelText: String,
    confirmationDesc: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    colors: DialogColors = TaskifyDialogDefaults.colors()
) = Dialog(
    onDismiss = onDismiss,
    onConfirm = onConfirm,
    confirmText = confirmText,
    cancelText = cancelText,
    modifier = modifier,
    title = {
        Text(title)
    },
    icon = icon,
    text = {
        Text(confirmationDesc)
    },
    colors = colors
)