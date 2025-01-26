package com.nrr.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.nrr.designsystem.LocalDarkTheme

@Composable
fun Dialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmText: String,
    cancelText: String,
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    colors: DialogColors = TaskifyDialogDefaults.colors()
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
    title = title,
    icon = icon,
    text = text,
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

data class DialogColors(
    val containerColor: Color,
    val iconContainerColor: Color,
    val titleContentColor: Color,
    val textContentColor: Color,
    val confirmButtonColor: Color,
    val cancelButtonColor: Color
)

object TaskifyDialogDefaults {
    private val whiteOrBlack: Color
        @Composable get() = if (LocalDarkTheme.current) Color.White else Color.Black

    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.onBackground,
        iconContainerColor: Color = whiteOrBlack,
        titleContentColor: Color = whiteOrBlack,
        textContentColor: Color = whiteOrBlack,
        confirmButtonColor: Color = MaterialTheme.colorScheme.primary,
        cancelButtonColor: Color = Color.White
    ) = DialogColors(
        containerColor = containerColor,
        iconContainerColor = iconContainerColor,
        titleContentColor = titleContentColor,
        textContentColor = textContentColor,
        confirmButtonColor = confirmButtonColor,
        cancelButtonColor = cancelButtonColor
    )
}