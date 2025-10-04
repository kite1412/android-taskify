package com.nrr.schedule.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.nrr.model.TaskPeriod
import com.nrr.schedule.ScheduleScreen
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleRoute(
    val periodOrdinal: Int
)

fun NavController.navigateToSchedule(
    period: TaskPeriod,
    navOptions: NavOptions? = null
) = navigate(
    route = ScheduleRoute(period.ordinal),
    navOptions = navOptions
)

fun NavGraphBuilder.scheduleScreen(
    onBackClick: () -> Unit
) {
    composable<ScheduleRoute> {
        ScheduleScreen(
            onBackClick = onBackClick
        )
    }
}