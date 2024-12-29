package com.nrr.taskify.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.nrr.todayplan.navigation.TodayPlanRoute
import com.nrr.todayplan.navigation.todayPlanScreen

@Composable
fun TaskifyNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
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
    }
}