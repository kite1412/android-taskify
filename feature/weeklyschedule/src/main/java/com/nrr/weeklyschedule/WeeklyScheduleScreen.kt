package com.nrr.weeklyschedule

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nrr.model.TimeOffset
import com.nrr.ui.Header
import com.nrr.ui.layout.MainLayout
import com.nrr.weeklyschedule.util.Day
import com.nrr.weeklyschedule.util.WeeklyScheduleDictionary

@Composable
internal fun WeeklyScheduleScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeeklyScheduleViewModel = hiltViewModel()
) {
    Content(
        selectedDay = viewModel.selectedDay,
        onBackClick = onBackClick,
        onDayClick = { viewModel.selectedDay = it },
        modifier = modifier
    )
}

@Composable
private fun Content(
    selectedDay: Day,
    onBackClick: () -> Unit,
    onDayClick: (Day) -> Unit,
    modifier: Modifier = Modifier
) {
    MainLayout(modifier = modifier) {
        Header(
            title = stringResource(WeeklyScheduleDictionary.text),
            onBackClick = onBackClick
        )
        Days(
            selectedDay = selectedDay,
            onDayClick = onDayClick
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Days(
    selectedDay: Day,
    onDayClick: (Day) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val shape = RoundedCornerShape(16.dp)
        Day.entries.forEach {
            val selected = selectedDay == it
            val background by animateColorAsState(
                targetValue = if (selected) MaterialTheme.colorScheme.primary
                    else Color.Transparent
            )
            val textColor by animateColorAsState(
                targetValue = if (selected) Color.White
                    else MaterialTheme.colorScheme.primary
            )

            Box(
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = shape
                    )
                    .clip(shape)
                    .background(background)
                    .clickable(
                        interactionSource = null,
                        indication = null
                    ) { onDayClick(it) }
                    .padding(
                        vertical = 8.dp,
                        horizontal = 16.dp
                    )
            ) {
                Text(
                    text = stringResource(it.stringId),
                    style = LocalTextStyle.current.copy(
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun OffsetBetweenTasks(
    timeOffset: TimeOffset,
    modifier: Modifier = Modifier
) {

}

@Preview
@Composable
private fun ContentPreview() {

}