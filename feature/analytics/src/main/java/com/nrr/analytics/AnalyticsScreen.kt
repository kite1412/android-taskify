package com.nrr.analytics

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nrr.analytics.util.AnalyticsDictionary
import com.nrr.analytics.util.periodBars
import com.nrr.analytics.util.taskTypePieData
import com.nrr.designsystem.LocalScaffoldComponentSizes
import com.nrr.designsystem.ScaffoldComponent
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.Gold
import com.nrr.designsystem.theme.PastelOrange
import com.nrr.designsystem.util.TaskifyDefault
import com.nrr.model.ActiveStatus
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.model.TaskSummary
import com.nrr.ui.TaskPreviewParameter
import com.nrr.ui.statistic.Label
import com.nrr.ui.stringStatusId
import com.nrr.ui.util.resolveProgressStatus
import com.nrr.ui.util.statusColor
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Pie
import kotlin.math.min

private typealias Line = Pair<Int, Color>

@Composable
internal fun AnalyticsScreen(
    modifier: Modifier = Modifier,
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val summaries by viewModel.summaries.collectAsStateWithLifecycle()

    if (loading) CircularProgressIndicator()
    else Content(
        tasks = tasks!!,
        taskSummaries = summaries?.flatMap { it.tasks } ?: emptyList(),
        modifier = modifier
    )
}

@Composable
private fun Content(
    tasks: List<Task>,
    taskSummaries: List<TaskSummary>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bottomBarHeight = LocalScaffoldComponentSizes.current[ScaffoldComponent.BOTTOM_NAVIGATION_BAR]

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Header()
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = bottomBarHeight?.height?.plus(16.dp) ?: 0.dp
            )
        ) {
            section(
                sectionName = context.getString(AnalyticsDictionary.tasks),
                content = {
                    TasksSection(
                        tasks = tasks
                    )
                }
            )
            section(
                sectionName = context.getString(AnalyticsDictionary.assignedTasks),
                content = {
                    AssignedTasksSection(
                        activeTasks = tasks.flatMap { it.activeStatuses }
                    )
                }
            )
            section(
                sectionName = context.getString(AnalyticsDictionary.taskInsights),
                content = {
                    TaskInsightsSection(
                        taskSummaries = taskSummaries
                    )
                }
            )
        }
    }
}

@Composable
private fun Header(
    modifier: Modifier = Modifier
) = Text(
    text = stringResource(AnalyticsDictionary.analytics),
    fontSize = TaskifyDefault.HEADER_FONT_SIZE.sp,
    fontWeight = FontWeight.Bold
)

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.section(
    sectionName: String,
    content: @Composable ColumnScope.() -> Unit,
    headerContent: (@Composable () -> Unit)? = null
) {
    stickyHeader {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = sectionName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(
                        color = MaterialTheme.colorScheme.primary,
                        offset = Offset(
                            x = -4f,
                            y = 4f
                        )
                    )
                )
            )
            headerContent?.invoke()
        }
    }
    item {
        Column(
            modifier = Modifier
                .padding(
                    start = 8.dp,
                    top = 8.dp,
                    bottom = 40.dp
                )
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

@Composable
private fun ColumnScope.TasksSection(
    tasks: List<Task>,
    modifier: Modifier = Modifier
) {
    SectionFields(
        nameValues = listOf(
            stringResource(AnalyticsDictionary.totalTasks) to tasks.size.toString(),
            stringResource(AnalyticsDictionary.assigned) to
                tasks.sumOf { it.activeStatuses.count() }.toString()
        )
    )
    PieChartStatistic(
        data = tasks.taskTypePieData()
    )
}

@Composable
private fun ColumnScope.AssignedTasksSection(
    activeTasks: List<ActiveStatus>,
    modifier: Modifier = Modifier
) {
    val (day, rest) = activeTasks.partition { it.period == TaskPeriod.DAY }
    val (week, month) = rest.partition { it.period == TaskPeriod.WEEK }
    val tasksSize = @Composable { size: Int ->
        stringResource(AnalyticsDictionary.tasksSize, size)
    }

    SectionFields(
        nameValues = listOf(
            stringResource(AnalyticsDictionary.day) to
                tasksSize(day.size),
            stringResource(AnalyticsDictionary.week) to
                tasksSize(week.size),
            stringResource(AnalyticsDictionary.month) to
                tasksSize(month.size)
        )
    )
    ColumnChartStatistic(
        data = activeTasks.periodBars()
    )
}

@Composable
private fun ColumnScope.TaskInsightsSection(
    taskSummaries: List<TaskSummary>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (taskSummaries.isNotEmpty()) {
            TaskInsight(
                name = stringResource(AnalyticsDictionary.frequentlyAssigned)
            ) {
                FrequentlyAssigned(
                    taskSummaries = taskSummaries
                )
            }
            TaskInsight(
                name = stringResource(AnalyticsDictionary.pastAssignments)
            ) {
                PastAssignments(
                    taskSummaries = taskSummaries
                )
            }
        }
    }
}

@Composable
private inline fun TaskInsight(
    name: String,
    modifier: Modifier = Modifier,
    content: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Box(
            modifier = Modifier.padding(start = 4.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun FrequentlyAssigned(
    taskSummaries: List<TaskSummary>,
    modifier: Modifier = Modifier
) {
    val sorted by rememberUpdatedState(
        taskSummaries
            .groupBy { it.title }
            .toList()
            .sortedByDescending { it.second.size }
            .map { it.second.size to it.second }
    )
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f
    )

    AnimatedContent(
        targetState = expanded,
        modifier = modifier.clickable(
            indication = null,
            interactionSource = null
        ) {
            expanded = !expanded
        },
        transitionSpec = {
            fadeIn() + slideInVertically { 0 } togetherWith
                fadeOut() + slideOutVertically {
                    if (targetState) it else -it
                }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            sorted
                .slice(0 until (if (!it) min(3, sorted.size) else sorted.size))
                .forEachIndexed { i, (s, t) ->
                    Rank(
                        rank = i + 1,
                        title = t.first().title,
                        values = listOf(
                            stringResource(AnalyticsDictionary.total) to s.toString(),
                            stringResource(AnalyticsDictionary.completed) to run {
                                val completed = t.filter { it.completedAt != null }
                                val late = completed.filter {
                                    it.dueDate != null && it.completedAt!! > it.dueDate!!
                                }
                                "${completed.size}" + if (late.isNotEmpty())
                                    " (${late.size} ${stringResource(AnalyticsDictionary.late)})"
                                else ""
                            }
                        )
                    )
                }
            if (sorted.size > 3) Icon(
                painter = painterResource(TaskifyIcon.chevronDown),
                contentDescription = "expand or dismiss",
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable(
                        indication = null,
                        interactionSource = null
                    ) {
                        expanded = !expanded
                    }
                    .rotate(rotation)
            )
        }
    }
}

@Composable
private fun PastAssignments(
    taskSummaries: List<TaskSummary>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val grouped by rememberUpdatedState(
        newValue = taskSummaries.groupBy {
            resolveProgressStatus(
                target = it.completedAt,
                limit = it.dueDate
            )
        }
    )
    val lines by remember {
        derivedStateOf {
            grouped.map {
                it.value.size to statusColor(it.key)
            }
        }
    }
    val statuses by remember {
        derivedStateOf {
            grouped.map { (k ,v) ->
                Triple(
                    first = context.getString(v.first().stringStatusId()),
                    second = statusColor(k),
                    third = v.size
                )
            }
        }
    }
    val style = MaterialTheme.typography.bodyMedium

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ColoredLine(
            lines = lines
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(AnalyticsDictionary.total) + ": ${taskSummaries.size}",
                style = style
            )
            statuses.forEach {
                Text(
                    text = it.first + ": " + it.third.toString(),
                    style = style.copy(
                        color = it.second
                    )
                )
            }
        }
    }
}

@Composable
private fun ColoredLine(
    lines: List<Line>,
    modifier: Modifier = Modifier
) {
    val fixedLines = remember(lines) {
        val total = lines.sumOf { it.first }

        lines.map {
            it.first / total.toFloat() to it.second
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(100))
    ) {
        fixedLines.forEach {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(it.first)
                    .background(it.second)
            )
        }
    }
}

@Composable
private fun Rank(
    rank: Int,
    title: String,
    values: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    val titleStyle = MaterialTheme.typography.bodyLarge.copy(
        fontWeight = FontWeight.Bold,
        color = when (rank) {
            1 -> Gold
            2 -> Color.LightGray
            3 -> PastelOrange
            else -> LocalContentColor.current
        }
    )

    Row(
        modifier = modifier
    ) {
        Text(
            text = "$rank. ",
            style = titleStyle
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = titleStyle
            )
            values.forEach { (k ,v) ->
                Text(
                    text = "$k: $v",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ColumnChartStatistic(
    data: List<Bars>,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        val style = MaterialTheme.typography.bodyMedium.copy(
            color = LocalContentColor.current
        )

        ColumnChart(
            data = data,
            modifier = Modifier
                .fillMaxWidth()
                .height(maxWidth / 1.5f),
            barProperties = BarProperties(
                thickness = 8.dp,
                cornerRadius = Bars.Data.Radius.Rectangle(
                    topRight = 4.dp,
                    topLeft = 4.dp
                )
            ),
            labelProperties = LabelProperties(
                enabled = true,
                textStyle = style,
                rotation = LabelProperties.Rotation(
                    degree = 0f
                )
            ),
            indicatorProperties = HorizontalIndicatorProperties(
                textStyle = style,
                contentBuilder = {
                    it.toInt().toString()
                },
                count = IndicatorCount.StepBased(4.0)
            ),
            labelHelperProperties = LabelHelperProperties(
                textStyle = style
            )
       )
    }
}

@Composable
private fun PieChartStatistic(
    data: List<Pie>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PieChart(
            data = data,
            modifier = Modifier.size(150.dp)
        )
        Column(
            modifier = Modifier.padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            data.forEach {
                Label(
                    name = it.label!! + ": ${it.data.toInt()}",
                    color = it.color
                )
            }
        }
    }
}

@Composable
private fun SectionFields(
    nameValues: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        nameValues.forEach { (n, v) ->
            SectionField(
                name = n,
                value = v
            )
        }
    }
}

@Composable
private fun SectionField(
    name: String,
    value: String,
    modifier: Modifier = Modifier
) = Text(
    text = "$name: $value",
    style = MaterialTheme.typography.bodyMedium
)

@Preview
@Composable
private fun ContentPreview(
    @PreviewParameter(TaskPreviewParameter::class)
    tasks: List<Task>
) {
    Content(
        tasks = tasks,
        taskSummaries = emptyList()
    )
}