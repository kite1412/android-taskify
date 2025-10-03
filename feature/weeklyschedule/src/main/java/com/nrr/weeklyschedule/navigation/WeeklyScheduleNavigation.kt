package com.nrr.weeklyschedule.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.nrr.weeklyschedule.WeeklyScheduleScreen
import kotlinx.serialization.Serializable

@Serializable data object WeeklyScheduleRoute

fun NavController.navigateToWeeklySchedule(navOptions: NavOptions? = null) =
    navigate(
        route = WeeklyScheduleRoute,
        navOptions = navOptions
    )

fun NavGraphBuilder.weeklyScheduleScreen(
    onBackClick: () -> Unit
) {
    composable<WeeklyScheduleRoute> {
        WeeklyScheduleScreen(
            onBackClick = onBackClick
        )
    }
}