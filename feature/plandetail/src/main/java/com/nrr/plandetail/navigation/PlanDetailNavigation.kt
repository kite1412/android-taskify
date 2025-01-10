package com.nrr.plandetail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.nrr.model.TaskPeriod
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
    onArrangePlanClick: () -> Unit
) {
    composable<PlanDetailRoute> {
        PlanDetailScreen(
            onBackClick = onBackClick,
            onArrangePlanClick = onArrangePlanClick
        )
    }
}