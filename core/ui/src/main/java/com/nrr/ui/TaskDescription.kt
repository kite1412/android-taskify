package com.nrr.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nrr.ui.util.UIDictionary


@Composable
fun TaskDescription(
    description: String,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState()
) {
    val descTextStyle = MaterialTheme.typography.bodyLarge

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(UIDictionary.description),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((descTextStyle.lineHeight.value * 8).dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = description.takeIf { it.isNotEmpty() }
                    ?: stringResource(UIDictionary.noDescription),
                modifier = Modifier.verticalScroll(scrollState),
                fontSize = descTextStyle.fontSize,
                color = if (description.isNotEmpty()) Color.Unspecified else Color.Gray,
                fontStyle = if (description.isNotEmpty()) FontStyle.Normal else FontStyle.Italic
            )
        }
    }
}