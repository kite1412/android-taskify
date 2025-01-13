package com.nrr.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrr.designsystem.component.Action
import com.nrr.designsystem.component.Swipeable
import com.nrr.designsystem.component.SwipeableState
import com.nrr.designsystem.component.rememberSwipeableState
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.model.Task

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskCard(
    task: Task,
    actions: List<Action>,
    modifier: Modifier = Modifier,
    swipeableState: SwipeableState = rememberSwipeableState(),
    onClick: ((Task) -> Unit)? = null,
    onLongClick: ((Task) -> Unit)? = null,
    clickEnabled: Boolean = onClick != null,
    swipeEnabled: Boolean = true,
    swipeableKeys: Array<Any?>? = null,
    additionalContent: (@Composable BoxScope.() -> Unit)? = null,
    header: @Composable (BoxScope.() -> Unit)? = null,
    bottom: @Composable (BoxScope.() -> Unit)? = null,
    leadingIcon: @Composable (RowScope.() -> Unit)? = null
) {
    val swipeableClip = 10.dp

    Column(modifier = modifier) {
        header?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.End)
            ) {
                it.invoke(this)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            leadingIcon?.invoke(this)
            Swipeable(
                actions = actions,
                modifier = Modifier
                    .weight(1f),
                state = swipeableState,
                actionButtonsBorderShape = RoundedCornerShape(swipeableClip),
                actionConfirmation = true,
                swipeEnabled = swipeEnabled,
                keys = swipeableKeys
            ) { m ->
                Box(modifier = m) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onLongClick = { onLongClick?.invoke(task) },
                                onClick = { onClick?.invoke(task) },
                                enabled = clickEnabled
                            )
                            .clip(RoundedCornerShape(swipeableClip))
                            .background(task.color())
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color.White)
                                .padding(12.dp)
                        ) {
                            Icon(
                                painter = painterResource(task.iconId()),
                                contentDescription = task.taskType.name,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Black
                            )
                        }
                        Column {
                            Text(
                                text = task.title,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            task.description?.let { t ->
                                with (MaterialTheme.typography.bodySmall.fontSize.value) {
                                    Text(
                                        text = t,
                                        fontSize = this.sp,
                                        lineHeight = (this + 2f).sp,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                    additionalContent?.invoke(this)
                }
            }
        }
        bottom?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.End)
            ) {
                it.invoke(this)
            }
        }
    }
}


// TODO fully migrate to LazyListScope.taskCards
@Composable
fun TaskCards(
    tasks: List<Task>,
    actions: (Task) -> List<Action>,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    userScrollEnabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onClick: ((Task) -> Unit)? = null,
    onLongClick: ((Task) -> Unit)? = null,
    clickEnabled: (Int) -> Boolean = { onClick != null },
    showCard: (Task) -> Boolean = { true },
    swipeEnabled: Boolean = true,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    header: @Composable (BoxScope.(index: Int) -> Unit)? = null,
    spacer: @Composable (BoxScope.(index: Int) -> Unit)? = null,
    leadingIcon: @Composable (RowScope.(index: Int) -> Unit)? = null,
    additionalContent: @Composable (BoxScope.(Task) -> Unit)? = null,
    resetSwipes: Any? = null
) {
    val states = remember(resetSwipes, tasks) {
        tasks.indices.map { SwipeableState() }
    }
    var prevOpened by remember(resetSwipes, tasks) {
        mutableIntStateOf(-1)
    }
    var opened by remember(resetSwipes, tasks) {
        mutableIntStateOf(-1)
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        state = state,
        contentPadding = contentPadding,
        userScrollEnabled = userScrollEnabled
    ) {
        itemsIndexed(
            items = tasks,
            key = { _, t -> t.activeStatuses.firstOrNull()?.id ?: t.id }
        ) { index, task ->
            if (showCard(task)) {
                val s = states[index]
                LaunchedEffect(s.isOpen) {
                    if (s.isOpen && prevOpened == -1) prevOpened = index
                    if (s.isOpen) opened = index
                    if (prevOpened != opened) {
                        states[prevOpened].reset()
                        prevOpened = index
                    }
                }
                TaskCard(
                    task = task,
                    actions = actions(task),
                    swipeableState = s,
                    onClick = { onClick?.invoke(task) },
                    onLongClick = { onLongClick?.invoke(task) },
                    clickEnabled = clickEnabled(index),
                    swipeEnabled = swipeEnabled,
                    additionalContent = additionalContent?.let {
                        { it.invoke(this, task) }
                    },
                    swipeableKeys = arrayOf(tasks),
                    header = if (header != null) {
                        {
                            header.invoke(this, index)
                        }
                    } else null,
                    bottom = if (spacer != null) {
                        {
                            spacer.invoke(this, index)
                        }
                    } else null,
                    leadingIcon = if (leadingIcon != null) {
                        {
                            leadingIcon.invoke(this, index)
                        }
                    } else null
                )
            }
        }
    }
}

@Preview
@Composable
private fun TaskCardPreview(
    @PreviewParameter(TaskPreviewParameter::class)
    tasks: List<Task>
) {
    val task = @Composable { s: Boolean ->
        TaskCard(
            task = tasks[0],
            actions = listOf(
                Action(
                    action = "Delete",
                    iconId = TaskifyIcon.home,
                    onClick = {},
                    color = Color.Red
                )
            )
        )
    }
    TaskifyTheme {
        Column {
            repeat(2) {
                task(it == 1)
            }
        }
    }
}

@Preview
@Composable
private fun TaskCardsPreview(
    @PreviewParameter(TaskPreviewParameter::class)
    tasks: List<Task>
) {
    TaskifyTheme {
        TaskCards(
            tasks = tasks,
            actions = {
                listOf(
                    Action(
                        action = "Delete",
                        iconId = TaskifyIcon.home,
                        onClick = {},
                        color = Color.Red
                    )
                )
            },
            verticalArrangement = Arrangement.spacedBy(250.dp)
        )
    }
}