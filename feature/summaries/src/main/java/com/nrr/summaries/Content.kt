package com.nrr.summaries

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrr.designsystem.theme.TaskifyTheme
import com.nrr.model.Summary
import com.nrr.model.TaskPeriod

@Composable
internal fun Content(
    summaries: List<Summary>,
    period: TaskPeriod,
    selectedSummary: Summary?,
    showingDetail: Boolean,
    onBackClick: () -> Unit,
    onSummaryClick: (Summary) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Header(
            onBackClick = onBackClick
        )
        AnimatedContent(
            targetState = !showingDetail,
            modifier = modifier.fillMaxSize(),
            transitionSpec = {
                fadeIn() + slideInHorizontally {
                    if (targetState) -it else it
                } togetherWith
                        fadeOut() + slideOutHorizontally {
                    if (targetState) it else -it
                }
            }
        ) {
            if (it) LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                summaries(
                    summaries = summaries,
                    onClick = onSummaryClick,
                    showIcon = true
                )
            } else SummaryDetail(
                summary = selectedSummary
            )
        }
    }
}

@Preview
@Composable
private fun ContentPreview() {
    TaskifyTheme {
        Content(
            onBackClick = {},
            summaries = listOf(),
            period = TaskPeriod.DAY,
            onSummaryClick = {},
            selectedSummary = null,
            showingDetail = false
        )
    }
}