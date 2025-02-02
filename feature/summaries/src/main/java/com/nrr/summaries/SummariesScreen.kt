package com.nrr.summaries

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nrr.designsystem.component.AdaptiveText
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.Gray
import com.nrr.designsystem.util.TaskifyDefault
import com.nrr.model.Summary
import com.nrr.model.TaskPeriod
import com.nrr.model.toLocalDateTime
import com.nrr.model.toTimeString
import com.nrr.summaries.util.SummariesDictionary
import com.nrr.summaries.util.toStringLocalized
import com.nrr.ui.toDateStringLocalized
import com.nrr.ui.toMonthLocalized

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

    if (summaries != null) {
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
            modifier = modifier
        )
    }
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

internal fun LazyListScope.summaries(
    summaries: List<Summary>,
    onClick: (Summary) -> Unit,
    showIcon: Boolean
) = items(
    count = summaries.size,
    key = { summaries[it].id }
) {
    SummaryCard(
        summary = summaries[it],
        onClick = onClick,
        showIcon = showIcon
    )
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
                .padding(4.dp)
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
    summary: Summary?,
    modifier: Modifier = Modifier
) {
   if (summary != null) LazyColumn(
       modifier = modifier.fillMaxSize(),
       verticalArrangement = Arrangement.spacedBy(16.dp)
   ) {
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
           }
       }
       item {
           Details(summary)
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