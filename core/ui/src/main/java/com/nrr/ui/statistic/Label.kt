package com.nrr.ui.statistic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nrr.designsystem.component.AdaptiveText

@Composable
fun Label(
    name: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val style = MaterialTheme.typography.bodyMedium

        Box(
            modifier = Modifier
                .size(12.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )
        AdaptiveText(
            text = name,
            initialFontSize = style.fontSize,
            style = style,
            fontWeight = FontWeight.Bold,
            maxLines = 2
        )
    }
}