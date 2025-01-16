package com.nrr.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.nrr.designsystem.component.AdaptiveText
import com.nrr.ui.util.UIDictionary

@Composable
fun TaskTitle(
    title: String,
    modifier: Modifier = Modifier,
    initialFontSize: TextUnit = 28.sp,
    maxLines: Int = 2
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(UIDictionary.title),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        AdaptiveText(
            text = title,
            initialFontSize = initialFontSize,
            fontWeight = FontWeight.Bold,
            maxLines = maxLines
        )
    }
}