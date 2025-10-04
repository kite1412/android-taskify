package com.nrr.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.window.core.layout.WindowWidthSizeClass
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
    key: (index: Int, Task) -> Any = { _, t -> t.hashCode() },
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
        key = key
    ) { index, task ->
        if (showCard(task)) {
            with(state) {
                val key = key(index, task)
                val s = getState(key)
                LaunchedEffect(s.isOpen) {
                    if (s.isOpen && prevOpened == null) prevOpenedChange(key)
                    if (s.isOpen) openedChange(key)
                    if (prevOpened != opened) {
                        states[prevOpened]!!.reset()
                        prevOpenedChange(key)
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
    actions: (index: Int, Task) -> List<Action>,
    state: TaskCardsState,
    key: (index: Int, Task) -> Any = { _, t -> t.hashCode() },
    onClick: ((Task) -> Unit)? = null,
    onLongClick: ((Task) -> Unit)? = null,
    clickEnabled: (Int) -> Boolean = { onClick != null },
    showCard: (Task) -> Boolean = { true },
    swipeEnabled: Boolean = true,
    content: @Composable (index: Int, task: Task, taskCard: @Composable () -> Unit) -> Unit
) {
    itemsIndexed(
        items = tasks,
        key = key
    ) { index, task ->
        if (showCard(task)) {
            with(state) {
                val key = key(index, task)
                val s = getState(key)
                LaunchedEffect(s.isOpen) {
                    if (s.isOpen && prevOpened == null) prevOpenedChange(key)
                    if (s.isOpen) openedChange(key)
                    if (prevOpened != opened) {
                        states[prevOpened]!!.reset()
                        prevOpenedChange(key)
                    }
                }
                content(index, task) {
                    TaskCard(
                        task = task,
                        actions = actions(index, task),
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

fun LazyGridScope.taskCards(
    tasks: List<Task>,
    actions: (Task) -> List<Action>,
    state: TaskCardsState,
    key: (index: Int, Task) -> Any = { _, t -> t.hashCode() },
    onClick: ((Task) -> Unit)? = null,
    onLongClick: ((Task) -> Unit)? = null,
    clickEnabled: (Int) -> Boolean = { onClick != null },
    showCard: (Task) -> Boolean = { true },
    swipeEnabled: Boolean = true,
    content: @Composable (index: Int, task: Task, taskCard: @Composable () -> Unit) -> Unit
) {
    itemsIndexed(
        items = tasks,
        key = key
    ) { index, task ->
        if (showCard(task)) {
            with(state) {
                val key = key(index, task)
                val s = getState(key)
                LaunchedEffect(s.isOpen) {
                    if (s.isOpen && prevOpened == null) prevOpenedChange(key)
                    if (s.isOpen) openedChange(key)
                    if (prevOpened != opened) {
                        states[prevOpened]!!.reset()
                        prevOpenedChange(key)
                    }
                }
                content(index, task) {
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

object TaskCardsDefaults {
    @Composable
    fun adaptiveGridColumnsCount(windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()) =
        when (windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass) {
            WindowWidthSizeClass.COMPACT -> 1
            WindowWidthSizeClass.MEDIUM -> 2
            WindowWidthSizeClass.EXPANDED -> 3
            else -> 1
        }
}

@Composable
fun rememberTaskCardsState(tasks: List<Task>, vararg keys: Any?) = remember(keys) {
    TaskCardsState(tasks)
}

class TaskCardsState(
    tasks: List<Task>
) {
    var opened: Any? = null
        private set

    var prevOpened: Any? = null
        private set

    internal val states = mutableMapOf<Any, SwipeableState>()
        .apply {
            tasks.forEach {
                put(it.hashCode(), SwipeableState())
            }
        }

    internal fun getState(i: Any): SwipeableState {
        if (states[i] == null) states[i] = SwipeableState()
        return states[i]!!
    }

    internal fun prevOpenedChange(i: Any) {
        prevOpened = i
    }

    internal fun openedChange(i: Any) {
        opened = i
    }
}