package com.nrr.designsystem.component

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) = androidx.compose.material3.Checkbox(
    checked = checked,
    onCheckedChange = onCheckedChange,
    modifier = modifier.clip(CircleShape),
    colors = CheckboxDefaults.colors(
        checkmarkColor = Color.White,
        uncheckedColor = Color.White,
        checkedColor = MaterialTheme.colorScheme.primary
    )
)