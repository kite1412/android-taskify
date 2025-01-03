package com.nrr.taskify.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nrr.taskmanagement.navigation.taskManagementScreen
import com.nrr.todayplan.navigation.TodayPlanRoute
import com.nrr.todayplan.navigation.todayPlanScreen
import kotlinx.serialization.Serializable

// TODO remove later
@Serializable data object FakeAnalyticsRoute
@Serializable data object FakeProfileRoute

@Composable
fun TaskifyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = TodayPlanRoute,
        modifier = modifier
    ) {
        todayPlanScreen(
            onSettingClick = { /* TODO navigate to settings screen */ },
            onPlanForTodayClick = { /* TODO navigate to plan for today screen */ },
            onWeeklyClick = { /* TODO navigate to weekly screen */ },
            onMonthlyClick = { /* TODO navigate to monthly screen */ },
            onSetTodayTasksClick = { /* TODO navigate to set today tasks screen */ }
        )
        taskManagementScreen(
            onTaskClick = { /* TODO navigate to task detail screen */ }
        )
        composable<FakeAnalyticsRoute> {  }
        composable<FakeProfileRoute> {  }
    }
}