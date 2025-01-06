package com.nrr.designsystem.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RoundRectButton(
    onClick: () -> Unit,
    action: String,
    modifier: Modifier = Modifier,
    iconId: Int? = null,
    shape: Shape = RoundedCornerShape(8.dp),
    fontSize: Int = MaterialTheme.typography.bodyMedium.fontSize.value.toInt(),
    enabled: Boolean = true,
    colors: ButtonColors = TaskifyButtonDefaults.colors()
) = TextButton(
    onClick = onClick,
    colors = colors,
    shape = shape,
    enabled = enabled,
    modifier = modifier
) {
    Text(
        text = action,
        fontSize = fontSize.sp,
    )
    iconId?.let {
        Icon(
            painter = painterResource(it),
            contentDescription = "add",
            modifier = Modifier
                .size((fontSize * 1.5).dp)
        )
    }
}

object TaskifyButtonDefaults {
    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.primary,
        contentColor: Color = Color.White,
        disabledContainerColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
        disabledContentColor: Color = Color.White.copy(alpha = 0.7f)
    ) = ButtonDefaults.textButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContentColor = disabledContentColor,
        disabledContainerColor = disabledContainerColor
    )
}