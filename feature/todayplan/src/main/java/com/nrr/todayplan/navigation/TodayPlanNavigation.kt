package com.nrr.todayplan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.nrr.model.TaskPeriod
import com.nrr.todayplan.TodayPlanScreen
import kotlinx.serialization.Serializable

@Serializable data object TodayPlanRoute

fun NavController.navigateToTodayPlan(navOptions: NavOptions) = navigate(
    route = TodayPlanRoute,
    navOptions = navOptions
)

fun NavGraphBuilder.todayPlanScreen(
    onSettingClick: () -> Unit,
    onPlanForTodayClick: (TaskPeriod) -> Unit,
    onWeeklyClick: (TaskPeriod) -> Unit,
    onMonthlyClick: (TaskPeriod) -> Unit,
    onSetTodayTasksClick: () -> Unit
) {
    composable<TodayPlanRoute> {
        TodayPlanScreen(
            onSettingClick = onSettingClick,
            onPlanForTodayClick = onPlanForTodayClick,
            onWeeklyClick = onWeeklyClick,
            onMonthlyClick = onMonthlyClick,
            onSetTodayTasksClick = onSetTodayTasksClick
        )
    }
}