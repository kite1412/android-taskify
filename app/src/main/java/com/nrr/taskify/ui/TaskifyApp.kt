package com.nrr.taskify.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nrr.designsystem.component.Destination
import com.nrr.designsystem.component.NavigationScaffold
import com.nrr.designsystem.component.SlidingTextData
import com.nrr.designsystem.component.TaskifyTopAppBarDefaults
import com.nrr.designsystem.component.TopAppBar
import com.nrr.registration.RegistrationScreen
import com.nrr.taskify.navigation.TaskifyNavHost
import com.nrr.ui.LocalSnackbarHostState

@Composable
internal fun TaskifyApp(
    modifier: Modifier = Modifier,
    viewModel: TaskifyViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
    appState: TaskifyAppState = rememberTaskifyAppState(navController)
) {
    val registered by viewModel.registered.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(value = LocalSnackbarHostState provides snackbarHostState) {
        Scaffold(
            modifier = modifier,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = { TaskifySnackbar(it) }
                )
            }
        ) { innerPadding ->
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
                    currentDestination = appState.currentTopLevelDestination?.destination
                        ?: appState.currentDes ?: Destination.HOME,
                    onDestinationChange = appState::navigateToTopLevelDestination,
                    showScaffoldComponents = appState.currentTopLevelDestination != null
                ) {
                    TaskifyNavHost(navController)
                }
            }
            if (registered == false && viewModel.showContent) RegistrationScreen()
            SplashScreen(
                onCompleted = viewModel::dismissSplash,
                showSplash = viewModel.showSplash
            )
        }
    }
}

@Composable
internal fun TaskifyScaffold(
    topBarTitles: List<SlidingTextData>,
    topBarTitleIndex: Int,
    currentDestination: Destination,
    onDestinationChange: (Destination) -> Unit,
    modifier: Modifier = Modifier,
    showScaffoldComponents: Boolean = true,
    content: @Composable () -> Unit
) {
    NavigationScaffold(
        onClick = onDestinationChange,
        modifier = modifier,
        showNavBar = showScaffoldComponents,
        currentDestination = currentDestination
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Box(
                modifier = Modifier.padding(
                    top = if (showScaffoldComponents)
                        TaskifyTopAppBarDefaults.defaultTopBarHeight * 1.5f else 0.dp
                )
            ) {
                content()
            }
            AnimatedVisibility(
                visible = showScaffoldComponents,
                enter = slideInHorizontally { -it },
                exit = slideOutVertically { -it } + fadeOut()
            ) {
                TopAppBar(
                    titleIndex = topBarTitleIndex,
                    titles = topBarTitles
                )
            }
        }
    }
}