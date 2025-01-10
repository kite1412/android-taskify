package com.nrr.plandetail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.nrr.plandetail.PlanDetailScreen
import kotlinx.serialization.Serializable

@Serializable data class PlanDetailRoute(val periodOrdinal: Int)

fun NavController.navigateToPlanDetail(
    periodOrdinal: Int,
    navOptions: NavOptions? = null
) = navigate(
    route = PlanDetailRoute(periodOrdinal),
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