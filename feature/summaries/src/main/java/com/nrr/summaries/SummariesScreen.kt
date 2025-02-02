package com.nrr.summaries

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.Gray
import com.nrr.designsystem.util.TaskifyDefault
import com.nrr.model.Summary
import com.nrr.model.TaskPeriod
import com.nrr.model.toLocalDateTime
import com.nrr.summaries.util.SummariesDictionary
import com.nrr.ui.toDateStringLocalized
import com.nrr.ui.toMonthLocalized

@Composable
internal fun SummariesScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SummariesViewModel = hiltViewModel()
) {
    Content(
        onBackClick = onBackClick,
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
internal fun SummaryCard(
    summary: Summary,
    modifier: Modifier = Modifier
) {
    val title = when (summary.period) {
        TaskPeriod.DAY -> summary.startDate.toDateStringLocalized()
        TaskPeriod.WEEK -> summary.startDate.toLocalDateTime().dayOfWeek.toString() +
                " - " + summary.endDate.toDateStringLocalized()
        TaskPeriod.MONTH -> summary.startDate.toLocalDateTime().toMonthLocalized()
    }
    val completed = summary.tasks.filter {
        it.completedAt != null
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(SummariesDictionary.tasksCompleted) + "${completed.size}/${summary.tasks.size}",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Gray,
                fontStyle = FontStyle.Italic
            )
        )
    }
}