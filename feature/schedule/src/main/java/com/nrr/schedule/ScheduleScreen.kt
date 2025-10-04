package com.nrr.schedule

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.nrr.model.TimeOffset
import com.nrr.schedule.util.ScheduleDictionary
import com.nrr.ui.Header
import com.nrr.ui.layout.MainLayout

@Composable
internal fun ScheduleScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    Content(
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@Composable
private fun Content(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MainLayout(modifier = modifier) {
        Header(
            title = stringResource(ScheduleDictionary.text),
            onBackClick = onBackClick
        )
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