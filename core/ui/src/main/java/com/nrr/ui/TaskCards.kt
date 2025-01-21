package com.nrr.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.nrr.designsystem.component.Action
import com.nrr.designsystem.component.SwipeableState
import com.nrr.model.Task

@Deprecated(
    message = "Use the other overload version instead",
    replaceWith = ReplaceWith(
        "taskCards(tasks, " +
                "actions, " +
                "state, " +
                "onClick, " +
                "onLongClick, " +
                "clickEnabled, " +
                "showCard, " +
                "swipeEnabled, " +
                "content" +
                ")"
    )
)
//TODO delete
fun LazyListScope.taskCards(
    tasks: List<Task>,
    actions: (Task) -> List<Action>,
    state: TaskCardsState,
    onClick: ((Task) -> Unit)? = null,
    onLongClick: ((Task) -> Unit)? = null,
    clickEnabled: (Int) -> Boolean = { onClick != null },
    showCard: (Task) -> Boolean = { true },
    swipeEnabled: Boolean = true,
    header: @Composable (BoxScope.(index: Int) -> Unit)? = null,
    spacer: @Composable (BoxScope.(index: Int) -> Unit)? = null,
    leadingIcon: @Composable (RowScope.(index: Int) -> Unit)? = null,
    additionalContent: @Composable (BoxScope.(Task) -> Unit)? = null
) {
    itemsIndexed(
        items = tasks,
        key = { _, t -> t.hashCode() }
    ) { index, task ->
        if (showCard(task)) {
            with(state) {
                val s = getState(task.hashCode())
                LaunchedEffect(s.isOpen) {
                    if (s.isOpen && prevOpened == -1) prevOpenedChange(task.hashCode())
                    if (s.isOpen) openedChange(task.hashCode())
                    if (prevOpened != opened) {
                        states[prevOpened]!!.reset()
                        prevOpenedChange(task.hashCode())
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

fun LazyListScope.taskCards(
    tasks: List<Task>,
    actions: (Task) -> List<Action>,
    state: TaskCardsState,
    onClick: ((Task) -> Unit)? = null,
    onLongClick: ((Task) -> Unit)? = null,
    clickEnabled: (Int) -> Boolean = { onClick != null },
    showCard: (Task) -> Boolean = { true },
    swipeEnabled: Boolean = true,
    content: @Composable (index: Int, taskCard: @Composable () -> Unit) -> Unit
) {
    itemsIndexed(
        items = tasks,
        key = { _, t -> t.hashCode() }
    ) { index, task ->
        if (showCard(task)) {
            with(state) {
                val s = getState(task.hashCode())
                LaunchedEffect(s.isOpen) {
                    if (s.isOpen && prevOpened == -1) prevOpenedChange(task.hashCode())
                    if (s.isOpen) openedChange(task.hashCode())
                    if (prevOpened != opened) {
                        states[prevOpened]!!.reset()
                        prevOpenedChange(task.hashCode())
                    }
                }
                content(index) {
                    TaskCard(
                        task = task,
                        actions = actions(task),
                        swipeableState = s,
                        onClick = { onClick?.invoke(task) },
                        onLongClick = { onLongClick?.invoke(task) },
                        clickEnabled = clickEnabled(index),
                        swipeEnabled = swipeEnabled,
                        swipeableKeys = arrayOf(tasks)
                    )
                }
            }
        }
    }
}

@Composable
fun rememberTaskCardsState(tasks: List<Task>, vararg keys: Any?) = remember(keys) {
    TaskCardsState(tasks)
}

class TaskCardsState(
    tasks: List<Task>
) {
    var opened = -1
        private set

    var prevOpened = -1
        private set

    internal val states = mutableMapOf<Int, SwipeableState>()
        .apply {
            tasks.forEach {
                put(it.hashCode(), SwipeableState())
            }
        }

    internal fun getState(i: Int): SwipeableState {
        if (states[i] == null) states[i] = SwipeableState()
        return states[i]!!
    }

    internal fun prevOpenedChange(i: Int) {
        prevOpened = i
    }

    internal fun openedChange(i: Int) {
        opened = i
    }
}