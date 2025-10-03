package com.nrr.plandetail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.notification.receiver.DEEP_LINK_URI_PATTERN
import com.nrr.plandetail.PlanDetailScreen
import kotlinx.serialization.Serializable

@Serializable data class PlanDetailRoute(val periodOrdinal: Int)

fun NavController.navigateToPlanDetail(
    taskPeriod: TaskPeriod,
    navOptions: NavOptions? = null
) = navigate(
    route = PlanDetailRoute(taskPeriod.ordinal),
    navOptions = navOptions
)

fun NavGraphBuilder.planDetailScreen(
    onBackClick: () -> Unit,
    onArrangePlanClick: (TaskPeriod) -> Unit,
    onActiveTaskClick: (Task) -> Unit,
    onWeeklyScheduleClick: () -> Unit
) {
    composable<PlanDetailRoute>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DEEP_LINK_URI_PATTERN
            }
        )
    ) {
        PlanDetailScreen(
            onBackClick = onBackClick,
            onArrangePlanClick = onArrangePlanClick,
            onActiveTaskClick = onActiveTaskClick,
            onWeeklyScheduleClick = onWeeklyScheduleClick
        )
    }
}