package com.nrr.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.nrr.designsystem.theme.TaskifyTheme
import kotlinx.coroutines.delay

@Composable
fun SlidingText(
    texts: List<SlidingTextData>,
    modifier: Modifier = Modifier,
    textIndex: Int = 0
) {
    if (texts.isNotEmpty()) {
        Box(modifier = modifier) {
            texts.forEachIndexed { i, d ->
                AnimatedVisibility(
                    visible = i == textIndex,
                    enter = d.transitionSpec.targetContentEnter,
                    exit = d.transitionSpec.initialContentExit
                ) {
                    Text(
                        text = d.text,
                        fontSize = d.fontSize,
                        fontWeight = d.fontWeight,
                        maxLines = 1,
                        overflow = TextOverflow.Clip
                    )
                }
            }
        }
    }
}

data class SlidingTextData(
    val text: String,
    val fontSize: TextUnit = 24.sp,
    val fontWeight: FontWeight = FontWeight.Bold,
    val transitionSpec: ContentTransform =
        slideInVertically { it } + fadeIn() togetherWith slideOutVertically { -it } + fadeOut()
)

@Preview
@Composable
private fun SlidingTextPreview() {
    val texts = listOf(
        SlidingTextData("Taskify"),
        SlidingTextData("a text"),
        SlidingTextData("another text"),
    )
    var currentTextIndex by remember { mutableIntStateOf(0) }
    TaskifyTheme {
        LaunchedEffect(true) {
            while (true) {
                delay(2000)
                if (currentTextIndex < texts.size - 1) currentTextIndex++
                else currentTextIndex = 0
            }
        }
        SlidingText(
            texts = texts,
            textIndex = currentTextIndex
        )
    }
}