package com.nrr.analytics

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
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
import com.nrr.designsystem.util.TaskifyDefault
import com.nrr.model.ActiveStatus
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.ui.TaskPreviewParameter
import com.nrr.ui.statistic.Label
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Pie

@Composable
internal fun AnalyticsScreen(
    modifier: Modifier = Modifier,
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    if (loading) CircularProgressIndicator()
    else Content(
        tasks = tasks!!
    )
}

@Composable
private fun Content(
    tasks: List<Task>,
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = bottomBarHeight?.height ?: 0.dp)
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
                .padding(start = 8.dp)
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
                textStyle = style
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
        tasks = tasks
    )
}