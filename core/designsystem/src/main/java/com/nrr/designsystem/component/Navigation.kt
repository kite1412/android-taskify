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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.CharcoalClay
import com.nrr.designsystem.theme.TaskifyTheme
import kotlin.math.abs

enum class Destination(
    val id: Int,
    val label: String,
    var color: Color = Color.Black,
    var selectedColor: Color = Color.White,
    var indicatorColor: Color = CharcoalClay,
    var height: Dp = 40.dp,
    var width: Dp = 40.dp
) {
    HOME(
        id = TaskifyIcon.home,
        label = "Home"
    ),
    TASKS(
        id = TaskifyIcon.note,
        label = "Tasks"
    ),
    ANALYTICS(
        id = TaskifyIcon.chart,
        label = "Analytics"
    ),
    PROFILE(
        id = TaskifyIcon.profile,
        label = "Profile"
    );

    internal companion object {
        @SuppressLint("ComposableNaming")
        @Composable
        fun toComposable(
            selectedIndex: Int,
            prevSelectedIndex: Int,
            onClick: (Destination) -> Unit,
            modifier: Modifier = Modifier,
            showLabel: Boolean = false,
            horizontalAnimation: Boolean = true
        ) {
            entries.forEachIndexed { i, d ->
                NavigationItem(
                    data = d,
                    prevSelectedIndex = prevSelectedIndex,
                    selected = i == selectedIndex,
                    onClick = onClick,
                    modifier = modifier,
                    showLabel = showLabel,
                    horizontalAnimation = horizontalAnimation
                )
            }
        }
    }
}

// Adjust navigation item's styles based on app theme
@SuppressLint("ComposableNaming")
@Composable
private fun adjustNavigationData() {
    val darkTheme = isSystemInDarkTheme()
    val color = if (!darkTheme) Color.Black else Color.White
    val selectedColor = if (!darkTheme) Color.White else CharcoalClay
    val indicatorColor = if (!darkTheme) CharcoalClay else Color.White
    Destination.entries.forEach {
        it.color = color
        it.selectedColor = selectedColor
        it.indicatorColor = indicatorColor
    }
}

@Composable
private fun BottomNavigationBar(
    selectedIndex: Int,
    prevSelectedIndex: Int,
    onClick: (Destination) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Destination.toComposable(
            onClick = onClick,
            selectedIndex = selectedIndex,
            prevSelectedIndex = prevSelectedIndex
        )
    }
}

@Preview
@Composable
private fun BottomNavigationBarPreview() {
    var selectedIndex by remember { mutableIntStateOf(0) }
    var prevSelectedIndex by remember { mutableIntStateOf(0) }
    adjustNavigationData()
    TaskifyTheme {
        BottomNavigationBar(
            selectedIndex = selectedIndex,
            prevSelectedIndex = prevSelectedIndex,
            onClick = {
                prevSelectedIndex = selectedIndex
                selectedIndex = it.ordinal
            }
        )
    }
}

@Composable
private fun NavigationRail(
    selectedIndex: Int,
    prevSelectedIndex: Int,
    onClick: (Destination) -> Unit,
    modifier: Modifier = Modifier
) = androidx.compose.material3.NavigationRail {
    Destination.toComposable(
        onClick = onClick,
        selectedIndex = selectedIndex,
        prevSelectedIndex = prevSelectedIndex,
        horizontalAnimation = false
    )
}

@Preview
@Composable
private fun NavigationRailPreview() {
    var selectedIndex by remember { mutableIntStateOf(0) }
    var prevSelectedIndex by remember { mutableIntStateOf(0) }
    adjustNavigationData()
    TaskifyTheme {
        NavigationRail(
            selectedIndex = selectedIndex,
            prevSelectedIndex = prevSelectedIndex,
            onClick = {
                prevSelectedIndex = selectedIndex
                selectedIndex = it.ordinal
            }
        )
    }
}

@Composable
private fun NavigationDrawer(
    selectedIndex: Int,
    prevSelectedIndex: Int,
    onClick: (Destination) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) = PermanentNavigationDrawer(
    drawerContent = {
        ModalDrawerSheet {
            Column(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            ) {
                Destination.toComposable(
                    onClick = onClick,
                    selectedIndex = selectedIndex,
                    prevSelectedIndex = prevSelectedIndex,
                    showLabel = true,
                    horizontalAnimation = false
                )
            }
        }
    },
    modifier = modifier,
    content = content
)

@Preview
@Composable
private fun NavigationDrawerPreview() {
    var selectedIndex by remember { mutableIntStateOf(0) }
    var prevSelectedIndex by remember { mutableIntStateOf(0) }
    adjustNavigationData()
    TaskifyTheme {
        NavigationDrawer(
            onClick = {
                prevSelectedIndex = selectedIndex
                selectedIndex = it.ordinal
            },
            selectedIndex = selectedIndex,
            prevSelectedIndex = prevSelectedIndex
        ) {
            Text("A text")
        }
    }
}

@Composable
private fun NavigationItem(
    data: Destination,
    prevSelectedIndex: Int,
    selected: Boolean,
    modifier: Modifier = Modifier,
    horizontalAnimation: Boolean = true,
    showLabel: Boolean = false,
    onClick: (Destination) -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (selected) data.selectedColor else data.color,
        label = "icon color"
    )
    val contentTransform = indicatorAnimationLogic(
        indexInList = data.ordinal,
        prevSelectedIndex = prevSelectedIndex,
        horizontal = horizontalAnimation
    )
    val itemOuterSpace = 16.dp
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(
                width = if (!showLabel) data.width + itemOuterSpace else 200.dp /* TODO change */,
                height = data.height + itemOuterSpace
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
            if (selected) Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(100.dp))
                    .background(data.indicatorColor)
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = itemOuterSpace / 2),
            horizontalArrangement = Arrangement.spacedBy(if (showLabel) 8.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(data.id),
                contentDescription = data.label,
                modifier = Modifier
                    .size(data.height, data.width),
                tint = animatedColor
            )
            if (showLabel) Text(
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

@Composable
fun NavigationScaffold(
    onClick: (Destination) -> Unit,
    modifier: Modifier = Modifier,
    currentDestination: Destination = Destination.HOME,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    content: @Composable () -> Unit
) {
    var selectedIndex by rememberSaveable {
        mutableIntStateOf(currentDestination.ordinal)
    }
    var prevSelectedIndex by rememberSaveable {
        mutableIntStateOf(currentDestination.ordinal)
    }
    var changeByClick by rememberSaveable {
        mutableStateOf(false)
    }
    val onClickWrapper = { data: Destination ->
        prevSelectedIndex = selectedIndex
        selectedIndex = data.ordinal
        changeByClick = true
        onClick(data)
    }

    DisposableEffect(currentDestination) {
        if (currentDestination.ordinal != selectedIndex && !changeByClick) {
            prevSelectedIndex = selectedIndex
            selectedIndex = currentDestination.ordinal
        }
        onDispose {
            changeByClick = false
        }
    }
    adjustNavigationData()
    Box(modifier = modifier.fillMaxSize()) {
        when (windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass) {
            WindowWidthSizeClass.COMPACT -> {
                content()
                BottomNavigationBar(
                    selectedIndex = selectedIndex,
                    prevSelectedIndex = prevSelectedIndex,
                    onClick = onClickWrapper,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 30.dp)
                )
            }
            WindowWidthSizeClass.MEDIUM -> Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    selectedIndex = selectedIndex,
                    prevSelectedIndex = prevSelectedIndex,
                    onClick = onClickWrapper
                )
                content()
            }
            else -> NavigationDrawer(
                selectedIndex = selectedIndex,
                prevSelectedIndex = prevSelectedIndex,
                onClick = onClickWrapper,
                content = content
            )
        }
    }
}

@Preview
@Composable
private fun NavigationScaffoldPreview() {
    TaskifyTheme {
        NavigationScaffold(
            onClick = {}
        ) {
            Text("A text")
        }
    }
}