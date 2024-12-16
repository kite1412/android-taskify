package com.nrr.designsystem.component

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nrr.designsystem.R
import com.nrr.designsystem.theme.CharcoalClay
import com.nrr.designsystem.theme.TaskifyTheme
import kotlin.math.abs

data class NavigationData(
    val id: Int,
    val label: String,
    val color: Color = Color.Black,
    val selectedColor: Color = Color.White,
    val indicatorColor: Color = CharcoalClay,
    val height: Dp = 40.dp,
    val width: Dp = 40.dp,
    val showLabel: Boolean = false
)

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Preview
@Composable
private fun BottomNavigationBarPreview() {
    val icons = listOf(
        NavigationData(
            id = R.drawable.home,
            label = "Home",
        ),
        NavigationData(
            id = R.drawable.note,
            label = "Tasks",
        ),
        NavigationData(
            id = R.drawable.chart,
            label = "Analytics",
        ),
        NavigationData(
            id = R.drawable.profile,
            label = "Profile",
        ),
    )
    var selectedIndex by remember { mutableIntStateOf(0) }
    var prevSelectedIndex by remember { mutableIntStateOf(selectedIndex) }
    TaskifyTheme {
        BottomNavigationBar {
            icons.forEachIndexed { i, d ->
                NavigationItem(
                    data = d,
                    prevSelectedIndex = prevSelectedIndex,
                    indexInList = i,
                    selected = selectedIndex == i,
                ) {
                    prevSelectedIndex = selectedIndex
                    selectedIndex = i
                }
            }
        }
    }
}

@Composable
private fun NavigationItem(
    data: NavigationData,
    indexInList: Int,
    prevSelectedIndex: Int,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (NavigationData) -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (selected) data.selectedColor else data.color,
        label = "icon color"
    )
    val contentTransform = indicatorAnimationLogic(indexInList, prevSelectedIndex)
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(
                width = data.width + 16.dp,
                height = data.height + 16.dp
            )
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) {
                onClick(data)
            }
    ) {
        AnimatedVisibility(
            visible = selected,
            enter = contentTransform.targetContentEnter,
            exit = contentTransform.initialContentExit,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(100.dp))
                    .background(data.indicatorColor)
            )
        }
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(if (data.showLabel) 8.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(data.id),
                contentDescription = data.label,
                modifier = Modifier
                    .size(data.height, data.width),
                tint = animatedColor
            )
            if (data.showLabel) Text(
                text = data.label,
                color = if (selected) data.selectedColor else data.color
            )
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
private fun indicatorAnimationLogic(
    indexInList: Int,
    prevSelectedIndex: Int,
    horizontal: Boolean = true
): ContentTransform {
    val initialOffset = { size: Int ->
        (if (indexInList > prevSelectedIndex) -size else size) * abs(indexInList - prevSelectedIndex)
    }
    return if (horizontal) slideInHorizontally { initialOffset(it) } togetherWith slideOutHorizontally { 0 }
    else slideInVertically { initialOffset(it) } togetherWith slideOutVertically { 0 }
}
