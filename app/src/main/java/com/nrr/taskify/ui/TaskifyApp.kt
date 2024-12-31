package com.nrr.taskify.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nrr.designsystem.component.Destination
import com.nrr.designsystem.component.NavigationScaffold
import com.nrr.designsystem.component.SlidingTextData
import com.nrr.designsystem.component.TaskifyTopAppBarDefaults
import com.nrr.designsystem.component.TopAppBar
import com.nrr.registration.RegistrationScreen
import com.nrr.taskify.navigation.TaskifyNavHost

@Composable
internal fun TaskifyApp(
    modifier: Modifier = Modifier,
    viewModel: TaskifyViewModel = hiltViewModel()
) {
    val registered by viewModel.registered.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier) { innerPadding ->
        AnimatedVisibility(
            visible = registered == true && viewModel.showContent,
            label = "main content",
            enter = slideInVertically(
                animationSpec = tween(durationMillis = viewModel.contentEnterDelay)
            ) { it },
            exit = slideOutVertically { it },
            modifier = Modifier.padding(innerPadding)
        ) {
            TaskifyScaffold(
                topBarTitles = viewModel.topBarTitles,
                topBarTitleIndex = viewModel.titleIndex,
                currentDestination = viewModel.currentDestination,
                onDestinationChange = viewModel::onDestinationChange
            ) {
                TaskifyNavHost()
            }
        }
        if (registered == false && viewModel.showContent) RegistrationScreen()
        SplashScreen(
            onCompleted = viewModel::dismissSplash,
            showSplash = viewModel.showSplash
        )
    }
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
    NavigationScaffold(
        onClick = onDestinationChange,
        modifier = modifier,
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