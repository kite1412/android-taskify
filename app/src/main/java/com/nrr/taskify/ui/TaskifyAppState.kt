package com.nrr.taskify.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.nrr.designsystem.component.Destination
import com.nrr.taskify.navigation.FakeAnalyticsRoute
import com.nrr.taskify.navigation.FakeProfileRoute
import com.nrr.taskify.navigation.TopLevelDestination
import com.nrr.taskmanagement.navigation.navigateToTaskManagement
import com.nrr.todayplan.navigation.navigateToTodayPlan

@Composable
fun rememberTaskifyAppState(navController: NavHostController) =
    remember(navController) {
        TaskifyAppState(navController)
    }

class TaskifyAppState(
    private val navController: NavController
) {
    private var previousNavDes: NavDestination? = null
    var currentDes: Destination? = null
        private set

    private val currentNavDestination: NavDestination?
        @Composable get() {
            val currentBackStackEntry = navController.currentBackStackEntryFlow
                .collectAsState(null)

            return currentBackStackEntry.value?.destination.also {
                it?.let {
                    previousNavDes = it
                }
            } ?: previousNavDes
        }

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() =
            TopLevelDestination.entries.firstOrNull {
                currentNavDestination?.hasRoute(it.route) == true
            }.also {
                it?.let { d ->
                    currentDes = d.destination
                }
            }

    fun navigateToTopLevelDestination(des: Destination) {
        if (currentDes == des) return
        val navOptions = navOptions {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            restoreState = true
        }
        when (des) {
            Destination.HOME -> navController.navigateToTodayPlan(navOptions)
            Destination.TASKS -> navController.navigateToTaskManagement(navOptions)
            Destination.ANALYTICS -> navController.navigate(FakeAnalyticsRoute, navOptions)
            Destination.PROFILE -> navController.navigate(FakeProfileRoute, navOptions)
        }
    }
}