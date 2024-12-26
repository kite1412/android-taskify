package com.nrr.todayplan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nrr.designsystem.component.AdaptiveText
import com.nrr.designsystem.component.CircularTaskProgressIndicator
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.CharcoalClay
import com.nrr.designsystem.theme.PastelGreen
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.designsystem.theme.lightOrangeGradient
import com.nrr.designsystem.util.drawRoundedShadow
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.todayplan.util.TodayPlanDictionary

@Composable
internal fun TodayPlanScreen(
    onSettingClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TodayPlanViewModel = hiltViewModel()
) {
    val todayPlan by viewModel.todayPlan.collectAsStateWithLifecycle()
    val username by viewModel.username.collectAsStateWithLifecycle()

    Content(
        username = username,
        todayTasks = todayPlan,
        onPlanForTodayClick = { /* TODO */ },
        onSettingClick = onSettingClick,
        modifier = modifier
    )
}

@Composable
private fun Content(
    username: String,
    todayTasks: List<Task>,
    onPlanForTodayClick: () -> Unit,
    onSettingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        GreetingHeader(username)
        Row(
            modifier = Modifier.height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlanForToday(
                modifier = Modifier.weight(0.9f),
                onClick = onPlanForTodayClick
            )
            IconButton(
                onClick = onSettingClick,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    painter = painterResource(TaskifyIcon.setting),
                    contentDescription = "setting",
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
        TodayProgress(todayTasks)
    }
}

@Composable
private fun GreetingHeader(
    username: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val initialNameSize = 32

        Column(
            modifier = Modifier.weight(0.6f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdaptiveText(
                text = "${stringResource(TodayPlanDictionary.greeting)}, $username",
                initialFontSize = 24.sp,
                maxLines = 1
            )
            AdaptiveText(
                text = stringResource(TodayPlanDictionary.question),
                initialFontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                lineHeight = (initialNameSize + 2).sp
            )
        }
        if (username.isNotEmpty()) Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(PastelGreen)
                .size((initialNameSize * 2).dp)
        ) {
            Text(
                text = username[0].uppercase(),
                modifier = Modifier.align(Alignment.Center),
                fontSize = initialNameSize.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun PlanForToday(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cornerRadius = 10.dp
    val density = LocalDensity.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawRoundedShadow(cornerRadius = with(density) {
                CornerRadius(x = cornerRadius.toPx(), y = cornerRadius.toPx())
            })
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.linearGradient(
                    colors = lightOrangeGradient
                )
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(TaskifyIcon.calendar3d),
            contentDescription = "calendar illustration",
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = stringResource(TodayPlanDictionary.planForToday),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TodayProgress(
    todayTasks: List<Task>,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val cornerRadius = 20.dp
    val completed = todayTasks.filter {
        it.activeStatus?.period == TaskPeriod.DAY && it.activeStatus?.isCompleted == true
    }
    val textColor = Color.White
    val progress = completed.size / todayTasks.size.toFloat()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawRoundedShadow(
                cornerRadius = with(density) {
                    CornerRadius(x = cornerRadius.toPx(), y = cornerRadius.toPx())
                },
                alpha = 0.4f
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(CharcoalClay)
            .padding(
                horizontal = 24.dp,
                vertical = 48.dp
            )
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (todayTasks.isEmpty()) Text(
            text = stringResource(TodayPlanDictionary.noTasksToday),
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            color = textColor
        )
        if (todayTasks.isNotEmpty()) Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(TodayPlanDictionary.todayProgress),
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 16.sp
                )
                Text(
                    text = "${(progress * 100).toInt()}% ${stringResource(TodayPlanDictionary.completed)}",
                    fontWeight = FontWeight.SemiBold,
                    color = textColor,
                    fontSize = 12.sp
                )
            }
            Text(
                text = "${todayTasks.size - completed.size} ${stringResource(TodayPlanDictionary.tasksLeft)}",
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(horizontal = 8.dp),
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                fontSize = 12.sp
            )
        }
        if (todayTasks.isNotEmpty()) CircularTaskProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(100.dp),
            strokeWidth = 6.dp
        ) {
            Text(
                text = "${completed.size}/${todayTasks.size}",
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
private fun ContentPreview() {
    TaskifyTheme {
        Content(
            username = "Kite1412",
            todayTasks = (1..12).map {
                Task.mock.copy(
                    activeStatus = Task.mock.activeStatus?.copy(
                        isCompleted = it > 5
                    )
                )
            },
            onPlanForTodayClick = {},
            onSettingClick = {},
            modifier = Modifier.padding(32.dp)
        )
    }
}