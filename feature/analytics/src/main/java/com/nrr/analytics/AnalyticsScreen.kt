package com.nrr.analytics

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.nrr.analytics.util.taskTypePieData
import com.nrr.designsystem.util.TaskifyDefault
import com.nrr.model.Task
import com.nrr.ui.TaskPreviewParameter
import com.nrr.ui.statistic.Label
import ir.ehsannarmani.compose_charts.PieChart
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

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Header()
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            section(
                sectionName = context.getString(AnalyticsDictionary.tasks),
                content = {
                    TasksSection(
                        tasks = tasks
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
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            headerContent?.invoke()
        }
    }
    item {
        Column(
            modifier = Modifier.padding(start = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
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
private fun ColumnScope.TasksSection(
    tasks: List<Task>,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SectionField(
            name = stringResource(AnalyticsDictionary.totalTasks),
            value = tasks.size.toString()
        )
        SectionField(
            name = stringResource(AnalyticsDictionary.assigned),
            value = tasks.sumOf { it.activeStatuses.count() }.toString()
        )
    }
    PieChartStatistic(
        data = tasks.taskTypePieData()
    )
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