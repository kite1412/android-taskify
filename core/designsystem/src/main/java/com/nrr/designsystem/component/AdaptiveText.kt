package com.nrr.designsystem.component

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp

@Composable
fun AdaptiveText(
    text: String,
    initialFontSize: TextUnit,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    color: Color = Color.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    onSizeChange: ((TextUnit) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current
) {
    var fontSize by remember { mutableFloatStateOf(initialFontSize.value) }
    Text(
        text = text,
        modifier = modifier,
        fontSize = fontSize.sp,
        maxLines = maxLines,
        color = color,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        lineHeight = lineHeight.takeIf { it.isSpecified } ?: (fontSize + 4).sp,
        textAlign = textAlign,
        style = style,
        onTextLayout = {
            if (it.hasVisualOverflow) {
                fontSize--
                onSizeChange?.invoke(fontSize.sp)
            }
        }
    )
}

@Preview
@Composable
private fun AdaptiveTextPreview() {
    AdaptiveText(
        "A very long textttttt ttttttt ttttttt tttttttttt",
        initialFontSize = 30.sp,
        maxLines = 1
    )
}