package com.nrr.taskify.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
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
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nrr.designsystem.component.Destination
import com.nrr.designsystem.component.NavigationScaffold
import com.nrr.designsystem.component.SlidingTextData
import com.nrr.designsystem.component.TaskifyTopAppBarDefaults
import com.nrr.designsystem.component.TopAppBar
import com.nrr.designsystem.util.TaskifyDefault
import com.nrr.registration.RegistrationScreen
import com.nrr.taskify.MainActivity
import com.nrr.taskify.navigation.TaskifyNavHost
import com.nrr.ui.LocalSafeAnimateContent
import com.nrr.ui.LocalSnackbarHostState
import com.nrr.ui.SnackbarHostStateWrapper

@Composable
internal fun TaskifyApp(
    modifier: Modifier = Modifier,
    viewModel: TaskifyViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
    appState: TaskifyAppState = rememberTaskifyAppState(navController)
) {
    val registered by viewModel.registered.collectAsStateWithLifecycle()
    val snackbarHostStateWrapper = remember {
        SnackbarHostStateWrapper(
            snackbarHostState = SnackbarHostState(),
            coroutineScope = viewModel.viewModelScope
        )
    }
    val intentData = (LocalActivity.current as MainActivity).intent.data

    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostStateWrapper,
        LocalSafeAnimateContent provides (viewModel.safeToAnimate || intentData != null)
    ) {
        val enterSpec = slideInVertically(
            animationSpec = tween(durationMillis = viewModel.contentEnterDelay)
        ) { it }
        val exitSpec = slideOutVertically { it }

        Scaffold(
            modifier = modifier,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostStateWrapper.snackbarHostState,
                    snackbar = { TaskifySnackbar(it) }
                )
            },
            contentColor = LocalContentColor.current
        ) { innerPadding ->
            AnimatedVisibility(
                visible = registered == true || intentData != null,
                label = "main content",
                enter = enterSpec,
                exit = exitSpec,
                modifier = Modifier.padding(innerPadding)
            ) {
                TaskifyScaffold(
                    topBarTitles = viewModel.topBarTitles,
                    topBarTitleIndex = viewModel.titleIndex,
                    currentDestination = appState.currentTopLevelDestination?.destination
                        ?: appState.currentDes ?: Destination.HOME,
                    onDestinationChange = appState::navigateToTopLevelDestination,
                    showScaffoldComponents = appState.currentTopLevelDestination != null,
                    applyContentPadding = appState.applyContentPadding
                ) {
                    TaskifyNavHost(navController)
                }
            }
            AnimatedVisibility(
                visible = registered == false && viewModel.showContent,
                label = "registration content",
                enter = enterSpec,
                exit = exitSpec
            ) {
                RegistrationScreen()
            }
            SplashScreen(
                onCompleted = viewModel::dismissSplash,
                showSplash = viewModel.showSplash && intentData == null
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
    applyContentPadding: Boolean = true,
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
                .padding(
                    if (applyContentPadding)
                        TaskifyDefault.CONTENT_PADDING.dp
                    else 0.dp
                )
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