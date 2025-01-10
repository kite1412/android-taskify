package com.nrr.taskify.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nrr.plandetail.navigation.navigateToPlanDetail
import com.nrr.plandetail.navigation.planDetailScreen
import com.nrr.taskdetail.navigation.navigateToTaskDetail
import com.nrr.taskdetail.navigation.taskDetailScreen
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
            onPlanForTodayClick = navController::navigateToPlanDetail,
            onWeeklyClick = navController::navigateToPlanDetail,
            onMonthlyClick = navController::navigateToPlanDetail,
            onSetTodayTasksClick = { /* TODO navigate to set today tasks screen */ }
        )
        taskManagementScreen(
            onTaskClick = navController::navigateToTaskDetail
        )
        taskDetailScreen(
            onBackClick = navController::popBackStack
        )
        planDetailScreen(
            onBackClick = navController::popBackStack,
            onArrangePlanClick = { /* TODO navigate to arrange plan screen */ }
        )
        composable<FakeAnalyticsRoute> {  }
        composable<FakeProfileRoute> {  }
    }
}