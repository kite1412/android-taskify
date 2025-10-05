package com.nrr.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrr.designsystem.component.Action
import com.nrr.designsystem.component.AdaptiveText
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
                            .then(
                                if (onClick != null || onLongClick != null) Modifier
                                    .combinedClickable(
                                        onLongClick = { onLongClick?.invoke(task) },
                                        onClick = { onClick?.invoke(task) },
                                        enabled = clickEnabled
                                    )
                                else Modifier
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

// typically used as the TaskCard's leadingIcon
@Composable
fun RowScope.TaskCardTimeIndicator(
    time: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    AdaptiveText(
        text = time,
        initialFontSize = style.fontSize,
        modifier = modifier
            .align(Alignment.CenterVertically),
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        style = style
    )
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