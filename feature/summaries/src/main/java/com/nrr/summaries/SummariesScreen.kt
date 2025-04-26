package com.nrr.summaries

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.nrr.designsystem.component.AdaptiveText
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.Gray
import com.nrr.designsystem.util.TaskifyDefault
import com.nrr.model.Summary
import com.nrr.model.TaskPeriod
import com.nrr.model.TaskSummary
import com.nrr.model.toLocalDateTime
import com.nrr.model.toTimeString
import com.nrr.summaries.util.SummariesDictionary
import com.nrr.summaries.util.toStringLocalized
import com.nrr.ui.color
import com.nrr.ui.statistic.summary.ColumnChartOption
import com.nrr.ui.statistic.summary.PieChartOption
import com.nrr.ui.statistic.summary.SummaryStatistics
import com.nrr.ui.statusColor
import com.nrr.ui.stringStatus
import com.nrr.ui.toDateStringLocalized
import com.nrr.ui.toMonthLocalized
import com.nrr.ui.toStringLocalized
import kotlin.math.roundToInt

@Composable
internal fun SummariesScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SummariesViewModel = hiltViewModel()
) {
    val period = viewModel.period
    val summary = viewModel.summary
    val showingDetail = viewModel.showingDetail
    val summaries by viewModel.summaries.collectAsStateWithLifecycle()
    val windowWidthClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

    if (summaries != null)
        if (windowWidthClass == WindowWidthSizeClass.COMPACT) {
            val backClick = {
                if (showingDetail) viewModel.updateShowingDetail(false)
                else onBackClick()
            }

            BackHandler(onBack = backClick)
            Content(
                onBackClick = backClick,
                summaries = summaries!!,
                period = period,
                onSummaryClick = viewModel::updateSummary,
                selectedSummary = summary,
                showingDetail = showingDetail,
                onPeriodClick = viewModel::updatePeriod,
                modifier = modifier
            )
        } else Content2Pane(
            summaries = summaries!!,
            onSummaryClick = viewModel::updateSummary,
            onBackClick = onBackClick,
            period = period,
            onPeriodClick = viewModel::updatePeriod,
            selectedSummary = summary!!,
            modifier = modifier
        )
}

@Composable
internal fun Header(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                painter = painterResource(TaskifyIcon.back),
                contentDescription = "back"
            )
        }
        Text(
            text = stringResource(SummariesDictionary.summaries),
            fontSize = TaskifyDefault.HEADER_FONT_SIZE.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
internal fun Summaries(
    summaries: List<Summary>,
    onClick: (Summary) -> Unit,
    showIcon: Boolean,
    selectedSummary: Summary?,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (summaries.isNotEmpty()) items(
            count = summaries.size,
            key = { summaries[it].hashCode() }
        ) {
            val summary = summaries[it]

            SummaryCard(
                summary = summary,
                onClick = onClick,
                showIcon = showIcon,
                selected = summary == selectedSummary && !showIcon
            )
        } else item {
            Text(
                text = stringResource(SummariesDictionary.noSummaries),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
internal fun SummaryCard(
    summary: Summary,
    showIcon: Boolean,
    onClick: (Summary) -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false
) {
    val title = getPeriodTitle(summary)
    val completed = summary.tasks.filter {
        it.completedAt != null
    }
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onBackground
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick(summary) }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdaptiveText(
                text = title,
                initialFontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(SummariesDictionary.tasksCompleted) + " ${completed.size}/${summary.tasks.size}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Gray,
                    fontStyle = FontStyle.Italic
                )
            )
        }
        if (showIcon) Icon(
            painter = painterResource(TaskifyIcon.chevronDown),
            contentDescription = "detail",
            modifier = Modifier
                .rotate(-90f)
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .padding(4.dp),
            tint = Color.White
        )
    }
}

@Composable
private fun getPeriodTitle(
    summary: Summary
) = when (summary.period) {
    TaskPeriod.DAY -> summary.startDate.toDateStringLocalized()
    TaskPeriod.WEEK -> summary.startDate.toLocalDateTime().dayOfMonth.toString() +
            " - " + summary.endDate.toDateStringLocalized()
    TaskPeriod.MONTH -> with(summary.startDate.toLocalDateTime()) {
        toMonthLocalized() + " $year"
    }
}

@Composable
internal fun SummaryDetail(
    summary: Summary,
    pieChartOption: PieChartOption,
    lineChartOption: ColumnChartOption,
    onPieChartOptionClick: (PieChartOption) -> Unit,
    onColumnChartOptionClick: (ColumnChartOption) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState()
) = LazyColumn(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(40.dp),
    state = state
) {
    detailHead(
        summary = summary
    )
    item {
        SummaryStatistics(
            summary = summary,
            pieChartOption = pieChartOption,
            lineChartOption = lineChartOption,
            onPieChartOptionClick = onPieChartOptionClick,
            onLineChartOptionClick = onColumnChartOptionClick
        )
    }
    taskSummaries(
        summary = summary
    )
}

internal fun LazyListScope.detailHead(
    summary: Summary?
) {
    if (summary != null) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(SummariesDictionary.period) +
                            " (${summary.period.toStringLocalized()})",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                AdaptiveText(
                    text = getPeriodTitle(summary),
                    initialFontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Details(summary)
            }
        }
    }
}

@Composable
private fun Details(
    summary: Summary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        DetailField(
            name = stringResource(SummariesDictionary.startDate),
            value = summary.startDate.toDateStringLocalized() + " " +
                    summary.startDate.toTimeString()
        )
        DetailField(
            name = stringResource(SummariesDictionary.endDate),
            value = summary.endDate.toDateStringLocalized() + " " +
                    summary.endDate.toTimeString()
        )
    }
}

@Composable
private fun DetailField(
    name: String,
    value: String,
    modifier: Modifier = Modifier
) = Text(
    text = buildAnnotatedString {
        withStyle(
            SpanStyle(
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            append("$name: ")
        }
        append(value)
    },
    style = MaterialTheme.typography.bodySmall
)

@Composable
internal fun PeriodsTab(
    period: TaskPeriod,
    onPeriodClick: (TaskPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(TaskPeriod.entries) {
                PeriodTabItem(
                    taskPeriod = it,
                    selected = period == it,
                    onClick = onPeriodClick
                )
            }
        }
    }
}

@Composable
private fun PeriodTabItem(
    taskPeriod: TaskPeriod,
    selected: Boolean,
    onClick: (TaskPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary
            else Color.Transparent
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) Color.White
            else MaterialTheme.colorScheme.primary
    )

    Text(
        text = taskPeriod.toStringLocalized(),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick(taskPeriod) }
            .background(backgroundColor)
            .padding(8.dp),
        color = contentColor
    )
}

internal fun LazyListScope.taskSummaries(
    summary: Summary,
    modifier: Modifier = Modifier
) = item {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(SummariesDictionary.tasks),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        SubcomposeLayout(
            modifier = Modifier.padding(start = 16.dp)
        ) { constraints ->
            val tasks = subcompose("tasks") {
                summary.tasks.sortedBy { it.startDate }.forEach {
                    TaskSummaryCard(
                        taskSummary = it,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }.map { it.measure(constraints) }
            val taskSpacePx = 16.dp.roundToPx()
            val dashedLine = subcompose("dashedLine") {
                VerticalDashedLine(
                    maxHeight = (tasks
                        .dropLast(1)
                        .sumOf { it.height + taskSpacePx } +
                            dashHeight.roundToPx() + dashSpace.roundToPx())
                        .toDp()
                )
            }.map { it.measure(constraints) }
            val fixedConstraints = constraints.copy(
                maxHeight = tasks.sumOf { it.height } + taskSpacePx * (tasks.size - 1)
            )

            layout(
                width = fixedConstraints.maxWidth,
                height = fixedConstraints.maxHeight
            ) {
                dashedLine.forEach {
                    it.place(0, 0)
                }
                var nextY = 0

                tasks.forEach {
                    it.place(
                        x = 12.dp.roundToPx(),
                        y = nextY
                    )
                    nextY += taskSpacePx + it.height
                }
            }
        }
    }
}

@Composable
private fun TaskSummaryCard(
    taskSummary: TaskSummary,
    modifier: Modifier = Modifier
) {
    val parentContentColor = LocalContentColor.current

    CompositionLocalProvider(
        LocalContentColor provides Gray
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val titleFontSize = 18

            Text(
                text = "~ " + taskSummary.title,
                fontSize = titleFontSize.sp,
                fontWeight = FontWeight.Bold,
                color = parentContentColor
            )
            Column(
                modifier = Modifier.padding(start = titleFontSize.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TaskSummaryField(
                    name = stringResource(SummariesDictionary.status),
                    value = taskSummary.stringStatus(),
                    valueColor = taskSummary.statusColor()
                )
                TaskSummaryField(
                    name = stringResource(SummariesDictionary.taskType),
                    value = taskSummary.taskType.toStringLocalized(),
                    valueColor = taskSummary.taskType.color()
                )
                TaskSummaryField(
                    name = stringResource(SummariesDictionary.startDate),
                    value = with(taskSummary.startDate) {
                        toDateStringLocalized() + " (${toTimeString()})"
                    }
                )
                taskSummary.dueDate?.let {
                    TaskSummaryField(
                        name = stringResource(SummariesDictionary.endDate),
                        value = with(it) {
                            toDateStringLocalized() + " (${toTimeString()})"
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskSummaryField(
    name: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = LocalContentColor.current
) = Text(
    text = buildAnnotatedString {
        append("$name:")
        withStyle(
            SpanStyle(color = valueColor)
        ) {
            append(" $value")
        }
    },
    style = MaterialTheme.typography.bodyMedium
)

private val dashHeight = 16.dp
private val dashSpace = 8.dp

@Composable
private fun VerticalDashedLine(
    maxHeight: Dp,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = Modifier.fillMaxHeight()
    ) {
        val dashWidth = 3.dp
        val dashTotal = (maxHeight / (dashHeight + dashSpace)).roundToInt()
        var nextY = 0f

        repeat(dashTotal) {
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(0f, nextY),
                size = Size(dashWidth.toPx(), dashHeight.toPx()),
                cornerRadius = CornerRadius(100f)
            )
            nextY += dashHeight.toPx() + dashSpace.toPx()
        }
    }
}