package com.nrr.plandetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nrr.designsystem.icon.TaskifyIcon
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.designsystem.util.TaskifyDefault
import com.nrr.model.TaskPeriod
import com.nrr.model.toDateString
import com.nrr.model.toTimeString
import com.nrr.plandetail.util.PlanDetailDictionary
import com.nrr.ui.getCurrentLocale
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle

@Composable
internal fun PlanDetailScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlanDetailViewModel = hiltViewModel()
) {
    val period = viewModel.period
    Content(
        period = period,
        onBackClick = onBackClick,
        currentDate = viewModel.currentDate,
        modifier = modifier
    )
}

@Composable
private fun Content(
    period: TaskPeriod,
    onBackClick: () -> Unit,
    currentDate: Instant,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Header(
                period = period,
                onBackClick = onBackClick
            )
            RealTimeClock(currentDate)
        }
    }
}

@Composable
private fun Header(
    period: TaskPeriod,
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
        Text(
            text = stringResource(
                id = when (period) {
                    TaskPeriod.DAY -> PlanDetailDictionary.todayPlan
                    TaskPeriod.WEEK -> PlanDetailDictionary.weekPlan
                    TaskPeriod.MONTH -> PlanDetailDictionary.monthPlan
                }
            ),
            fontWeight = FontWeight.Bold,
            fontSize = TaskifyDefault.HEADER_FONT_SIZE.sp
        )
    }
}

@Composable
private fun RealTimeClock(
    instant: Instant,
    modifier: Modifier = Modifier
) {
    val locale = getCurrentLocale()
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = localDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, locale),
            fontWeight = FontWeight.Bold,
            fontSize = TaskifyDefault.HEADER_FONT_SIZE.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "${instant.toDateString()}\n" +
                    instant.toTimeString(withSecond = true),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = TaskifyDefault.HEADER_FONT_SIZE.sp
        )
    }
}

@Preview
@Composable
private fun ContentPreview() {
    var curDate by remember { mutableStateOf(Clock.System.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            curDate = Clock.System.now()
            delay(1000)
        }
    }
    TaskifyTheme {
        Content(
            period = TaskPeriod.DAY,
            onBackClick = {},
            currentDate = curDate
        )
    }
}