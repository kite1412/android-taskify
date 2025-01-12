package com.nrr.planarrangement

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nrr.designsystem.component.AdaptiveText
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.Blue
import com.nrr.designsystem.theme.CharcoalClay30
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.designsystem.util.TaskifyDefault
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.planarrangement.util.PlanArrangementDictionary
import com.nrr.ui.EmptyTasks
import com.nrr.ui.TaskCards
import com.nrr.ui.TaskPreviewParameter

@Composable
internal fun PlanArrangementScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlanArrangementViewModel = hiltViewModel()
) {
    val period = viewModel.period
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    Content(
        tasks = tasks,
        onTaskClick = {},
        onBackClick = onBackClick,
        period = period,
        onPeriodChange = viewModel::updatePeriod,
        modifier = modifier
    )
}

@Composable
private fun Content(
    tasks: List<Task>?,
    onTaskClick: (Task) -> Unit,
    onBackClick: () -> Unit,
    period: TaskPeriod,
    onPeriodChange: (TaskPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Header(
            onBackClick = onBackClick
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SelectTask()
                PeriodSelect(
                    period = period,
                    onPeriodChange = onPeriodChange
                )
            }
            when (tasks) {
                null -> Unit
                else -> if (tasks.isEmpty())
                    EmptyTasks(
                        message = stringResource(PlanArrangementDictionary.noTasks)
                    )
                else Tasks(
                    tasks = tasks,
                    onClick = onTaskClick
                )
            }
        }

    }
}

@Composable
private fun Header(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                painter = painterResource(TaskifyIcon.back),
                contentDescription = "back"
            )
        }
        AdaptiveText(
            text = stringResource(PlanArrangementDictionary.arrangePlan),
            initialFontSize = TaskifyDefault.HEADER_FONT_SIZE.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SelectTask(modifier: Modifier = Modifier) = Text(
    text = stringResource(PlanArrangementDictionary.selectTask),
    modifier = modifier,
    fontSize = 20.sp,
    color = Blue,
    fontWeight = FontWeight.Bold
)

@Composable
private fun PeriodSelect(
    period: TaskPeriod,
    onPeriodChange: (TaskPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPeriods by remember { mutableStateOf(false) }
    val periodStringId = { p: TaskPeriod ->
        when (p) {
            TaskPeriod.DAY -> PlanArrangementDictionary.today
            TaskPeriod.WEEK -> PlanArrangementDictionary.thisWeek
            TaskPeriod.MONTH -> PlanArrangementDictionary.thisMonth
        }
    }

    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = null
                ) {
                    showPeriods = !showPeriods
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            AdaptiveText(
                text = stringResource(periodStringId(period)),
                initialFontSize = 20.sp,
                maxLines = 1,
                fontWeight = FontWeight.Bold
            )
            Icon(
                painter = painterResource(TaskifyIcon.chevronDown),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        DropdownMenu(
            expanded = showPeriods,
            onDismissRequest = { showPeriods = false },
            modifier = Modifier.background(CharcoalClay30)
        ) {
            TaskPeriod.entries.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(periodStringId(it)),
                            color = if (it == period) Blue else Color.White
                        )
                    },
                    onClick = {
                        showPeriods = false
                        onPeriodChange(it)
                    }
                )
            }
        }
    }
}

@Composable
private fun Tasks(
    tasks: List<Task>,
    onClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    TaskCards(
        tasks = tasks,
        actions = { emptyList() },
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        onClick = onClick
    )
}

@Preview
@Composable
private fun ContentPreview(
    @PreviewParameter(TaskPreviewParameter::class)
    tasks: List<Task>
) {
    var period by remember { mutableStateOf(TaskPeriod.DAY) }

    TaskifyTheme {
        Content(
            tasks = tasks,
            onTaskClick = {},
            onBackClick = {},
            period = period,
            onPeriodChange = { period = it }
        )
    }
}