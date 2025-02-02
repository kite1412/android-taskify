package com.nrr.summaries

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nrr.designsystem.theme.TaskifyTheme

@Composable
internal fun Content(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Header(
            onBackClick = onBackClick
        )
    }
}

@Preview
@Composable
private fun ContentPreview() {
    TaskifyTheme {
        Content(onBackClick = {})
    }
}