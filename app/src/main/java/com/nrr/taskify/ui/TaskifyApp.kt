package com.nrr.taskify.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nrr.designsystem.component.Destination
import com.nrr.designsystem.component.NavigationScaffold
import com.nrr.designsystem.component.SlidingTextData
import com.nrr.designsystem.component.TaskifyTopAppBarDefaults
import com.nrr.designsystem.component.TopAppBar
import com.nrr.designsystem.theme.TaskifyTheme

@Composable
internal fun TaskifyApp(modifier: Modifier = Modifier) {

}

@Composable
internal fun TaskifyScaffold(
    topBarTitles: List<SlidingTextData>,
    topBarTitleIndex: Int,
    currentDestination: Destination,
    onDestinationChange: (Destination) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Scaffold(modifier) { innerPadding ->
        NavigationScaffold(
            onClick = onDestinationChange,
            modifier = Modifier.consumeWindowInsets(innerPadding),
            initialDestination = currentDestination
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Box(
                    modifier = Modifier.padding(
                        top = TaskifyTopAppBarDefaults.defaultTopBarHeight * 1.5f
                    )
                ) {
                    content()
                }
                TopAppBar(
                    titleIndex = topBarTitleIndex,
                    titles = topBarTitles
                )
            }
        }
    }
}

@Preview
@Composable
private fun TaskifyScaffoldPreview() {
    val vm: TaskifyViewModel = viewModel()
    TaskifyTheme {
        TaskifyScaffold(
            topBarTitles = vm.topBarTitles,
            topBarTitleIndex = vm.titleIndex,
            currentDestination = vm.currentDestination,
            onDestinationChange = vm::onDestinationChange
        ) {
            Text("A text")
        }
    }
}