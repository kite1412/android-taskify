package com.nrr.planarrangement.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.nrr.model.TaskPeriod
import com.nrr.planarrangement.PlanArrangementScreen
import kotlinx.serialization.Serializable

@Serializable data class PlanArrangementRoute(val periodOrdinal: Int)

fun NavController.navigateToPlanArrangement(
    taskPeriod: TaskPeriod,
    navOptions: NavOptions? = null
) = navigate(
    route = PlanArrangementRoute(taskPeriod.ordinal),
    navOptions = navOptions
)

fun NavGraphBuilder.planArrangementScreen(
    onBackClick: () -> Unit
) {
    composable<PlanArrangementRoute> {
        PlanArrangementScreen(
            onBackClick = onBackClick
        )
    }
}