package com.nrr.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nrr.designsystem.theme.TaskifyTheme

@Composable
internal fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {

}

@Composable
private fun Content(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {

    }
}

@Preview
@Composable
private fun ContentPreview() {
    TaskifyTheme {

    }
}