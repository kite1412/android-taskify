package com.nrr.weeklyschedule

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.nrr.ui.Header
import com.nrr.ui.layout.MainLayout
import com.nrr.weeklyschedule.util.WeeklyScheduleDictionary

@Composable
internal fun WeeklyScheduleScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeeklyScheduleViewModel = hiltViewModel()
) {
    Content(
        onBackClick = onBackClick
    )
}

@Composable
private fun Content(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MainLayout(modifier = modifier) {
        Header(
            title = stringResource(WeeklyScheduleDictionary.text),
            onBackClick = onBackClick
        )
    }
}

@Preview
@Composable
private fun ContentPreview() {

}