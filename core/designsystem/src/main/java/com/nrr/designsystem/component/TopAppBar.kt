package com.nrr.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.TaskifyTheme
import kotlinx.coroutines.delay

object TaskifyTopAppBarDefaults {
    val defaultTopBarHeight = 55.dp
    val defaultLogoWidth = 46.dp
    val defaultTitles = listOf(
        SlidingTextData("Taskify"),
        SlidingTextData("Boost your productivity", fontSize = 18.sp),
        SlidingTextData("Organize, prioritize, succeed", fontSize = 18.sp)
    )
}

@Composable
fun TopAppBar(
    titleIndex: Int,
    modifier: Modifier = Modifier,
    titles: List<SlidingTextData> = listOf()
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(TaskifyTopAppBarDefaults.defaultTopBarHeight)
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppLogo()
        SlidingText(
            texts = titles,
            textIndex = titleIndex,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun TopAppBarPreview() {
    var currentTitleIndex by remember { mutableIntStateOf(0) }
    val titles = TaskifyTopAppBarDefaults.defaultTitles
    TaskifyTheme {
        LaunchedEffect(true) {
            while (true) {
                delay(2000)
                if (currentTitleIndex < 2) currentTitleIndex++
                else currentTitleIndex = 0
            }
        }
        TopAppBar(
            titleIndex = currentTitleIndex,
            titles = titles
        )
    }
}

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(TaskifyIcon.appIcon),
        contentDescription = "taskify logo",
        modifier = modifier.size(
            height = TaskifyTopAppBarDefaults.defaultTopBarHeight,
            width = TaskifyTopAppBarDefaults.defaultLogoWidth
        )
    )
}