package com.nrr.designsystem.component

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.nrr.designsystem.R
import com.nrr.designsystem.theme.TaskifyTheme
import kotlinx.coroutines.delay

@Composable
fun Swipeable(
    actions: List<Action>,
    modifier: Modifier = Modifier,
    state: SwipeableState = rememberSwipeableState(),
    minActionWidth: Dp = 50.dp,
    actionWidthFactor: Float = actions.size * 0.2f,
    actionButtonsBorderShape: Shape = RectangleShape,
    actionConfirmation: Boolean = false,
    swipeEnabled: Boolean = true,
    keys: Array<Any?>? = null,
    content: @Composable (Modifier) -> Unit,
) {
    val density = LocalDensity.current
    val animatedOffset by animateDpAsState(
        targetValue = state.offset,
        label = "swipeableContentOffset"
    )

    DisposableEffect(actions.size) {
        onDispose {
            state.reset()
        }
    }
    SubcomposeLayout(modifier = modifier.fillMaxWidth()) { c ->
        val actionWidth = if (actions.isNotEmpty()) with(density) {
            max(
                a = minActionWidth,
                b = c.maxWidth.toDp() / actions.size * actionWidthFactor
            )
        } else 0.dp

        state.maxOffset = -(actionWidth * actions.size)
        val swipeableContent = subcompose(
            slotId = "swipeableContent",
            content = {
                content(
                    if (actions.isNotEmpty()) Modifier
                        .offset(x = animatedOffset)
                        .pointerInput(swipeEnabled, *keys ?: emptyArray()) {
                            detectHorizontalDragGestures(
                                onDragEnd = state::onSwipeEnd
                            ) { _, dragAmount ->
                                if (swipeEnabled) with(density) {
                                    state.onSwipe(dragAmount.toDp())
                                }
                            }
                        }
                    else Modifier
                )
            }
        ).map {
            it.measure(c)
        }
        val constraints = c.copy(
            maxHeight = swipeableContent.maxOf { it.height }
        )

        layout(constraints.maxWidth, constraints.maxHeight) {
            if (actions.isNotEmpty()) subcompose("swipeableActions") {
                Row(
                    modifier = Modifier
                        .width(with(density) { constraints.maxWidth.toDp() })
                        .height(with(density) { constraints.maxHeight.toDp() })
                        .clip(actionButtonsBorderShape)
                        .background(actions.first().color),
                    horizontalArrangement = Arrangement.End
                ) {
                    actions.forEach { action ->
                        SwipeableAction(
                            action = action,
                            modifier = Modifier
                                .width(actionWidth)
                                .fillMaxHeight(),
                            needConfirmation = actionConfirmation,
                            confirmationKeys = arrayOf(state.isOpen)
                        )
                    }
                }
            }.forEach {
                it.measure(constraints).placeRelative(0, 0)
            }
            swipeableContent.forEach { it.placeRelative(0, 0) }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SwipeableAction(
    action: Action,
    modifier: Modifier = Modifier,
    needConfirmation: Boolean = false,
    confirmationKeys: Array<Any?>? = null
) {
    var showLabel by remember { mutableStateOf(false) }
    var onConfirmation by remember(confirmationKeys) { mutableStateOf(false) }
    val onClickWrapper = {
        if (!showLabel) {
            if (needConfirmation) {
                if (onConfirmation) action.onClick()
                else {
                    onConfirmation = true
                }
            } else action.onClick()
        }
    }
    LaunchedEffect(showLabel, onConfirmation) {
        if (showLabel) {
            delay(2000)
            showLabel = false
        } else if (onConfirmation) {
            delay(3000)
            onConfirmation = false
        }
    }
    AnimatedContent(
        targetState = showLabel || onConfirmation,
        label = "action $action",
        modifier = modifier
            .background(color = action.color)
            .combinedClickable(
                onLongClick = {
                    if (!showLabel && !onConfirmation) showLabel = true
                },
                onLongClickLabel = action.action,
                onClick = onClickWrapper
            )
    ) {
        Box {
            if (!it) Icon(
                painter = painterResource(action.iconId),
                contentDescription = action.action,
                modifier = Modifier
                    .size(action.iconSize.dp)
                    .align(Alignment.Center),
                tint = Color.White
            ) else AdaptiveText(
                text = action.action,
                modifier = Modifier.align(Alignment.Center),
                color = Color.White,
                initialFontSize = 10.sp,
                maxLines = 2,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 10.sp
            )
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun rememberSwipeableState(vararg keys: Any?) = remember(keys) {
    SwipeableState()
}

@Preview
@Composable
private fun SwipeablePreview() {
    TaskifyTheme {
        Swipeable(
            actions = Action.mocks,
            modifier = Modifier.padding(16.dp),
            swipeEnabled = false
        ) {
            Box(
                modifier = it
                    .height(50.dp)
                    .background(Color.Yellow.copy(alpha = 0.5f))
            ) {
                Text("Content", modifier = Modifier.background(Color.Green))
            }
        }
    }
}

data class Action(
    val action: String,
    val iconId: Int,
    val iconSize: Int = 24,
    val color: Color,
    val onClick: () -> Unit
) {
    companion object {
        val mocks = listOf(
            Action(
                action = "Home",
                iconId = R.drawable.home,
                color = Color.Red,
                onClick = {}
            ),
            Action(
                action = "Note",
                iconId = R.drawable.note,
                color = Color.Black,
                onClick = {}
            ),
            Action(
                action = "Home",
                iconId = R.drawable.home,
                color = Color.Red,
                onClick = {}
            )
        )
    }
}

class SwipeableState {
    var maxOffset: Dp = 0.dp
        internal set

    var offset by mutableStateOf(0.dp)
        private set

    var isOpen by mutableStateOf(false)
        private set

    internal fun onSwipe(dragAmount: Dp) {
        if (dragAmount < 0.dp) offset += dragAmount
        else if (offset < 0.dp) offset += dragAmount
    }

    internal fun onSwipeEnd() {
        if (offset < 0.dp && (!isOpen || offset < maxOffset)) swipeOpen()
        else reset()
    }

    fun reset() {
        offset = 0.dp
        isOpen = false
    }

    fun swipeOpen() {
        offset = maxOffset
        isOpen = true
    }
}