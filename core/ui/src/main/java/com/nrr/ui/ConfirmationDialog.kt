package com.nrr.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

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
    colors: ConfirmationDialogColors = ConfirmationDialogDefaults.colors()
) = AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
        TextButton(
            onClick = onConfirm,
            colors = ButtonDefaults.textButtonColors(
                contentColor = colors.confirmButtonColor
            )
        ) {
            Text(confirmText)
        }
    },
    modifier = modifier,
    title = {
        Text(title)
    },
    icon = icon,
    text = {
        Text(confirmationDesc)
    },
    dismissButton = {
        TextButton(
            onClick = onDismiss,
            colors = ButtonDefaults.textButtonColors(
                contentColor = colors.cancelButtonColor
            )
        ) {
            Text(cancelText)
        }
    },
    containerColor = colors.containerColor,
    iconContentColor = colors.iconContainerColor,
    titleContentColor = colors.titleContentColor,
    textContentColor = colors.textContentColor
)

data class ConfirmationDialogColors(
    val containerColor: Color,
    val iconContainerColor: Color,
    val titleContentColor: Color,
    val textContentColor: Color,
    val confirmButtonColor: Color,
    val cancelButtonColor: Color
)

object ConfirmationDialogDefaults {
    private val whiteOrBlack: Color
        @Composable get() = if (isSystemInDarkTheme()) Color.White else Color.Black

    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.onBackground,
        iconContainerColor: Color = whiteOrBlack,
        titleContentColor: Color = whiteOrBlack,
        textContentColor: Color = whiteOrBlack,
        confirmButtonColor: Color = MaterialTheme.colorScheme.primary,
        cancelButtonColor: Color = Color.White
    ) = ConfirmationDialogColors(
        containerColor = containerColor,
        iconContainerColor = iconContainerColor,
        titleContentColor = titleContentColor,
        textContentColor = textContentColor,
        confirmButtonColor = confirmButtonColor,
        cancelButtonColor = cancelButtonColor
    )
}