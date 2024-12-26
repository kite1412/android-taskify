package com.nrr.todayplan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.nrr.todayplan.TodayPlanScreen
import kotlinx.serialization.Serializable

@Serializable data object TodayPlanRoute

fun NavController.navigateToTodayPlan(navOptions: NavOptions) = navigate(
    route = TodayPlanRoute,
    navOptions = navOptions
)

fun NavGraphBuilder.todayPlanScreen(
    onSettingClick: () -> Unit
) {
    composable<TodayPlanRoute> {
        TodayPlanScreen(
            onSettingClick = onSettingClick
        )
    }
}