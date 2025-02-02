package com.nrr.summaries

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun SummariesScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SummariesViewModel = hiltViewModel()
) {

}