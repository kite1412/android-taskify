package com.nrr.summaries.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.nrr.summaries.SummariesScreen
import kotlinx.serialization.Serializable

@Serializable data object SummariesRoute

fun NavController.navigateToSummaries(
    navOptions: NavOptions? = null
) = navigate(SummariesRoute, navOptions)

fun NavGraphBuilder.summariesScreen(
    onBackClick: () -> Unit
) {
    composable<SummariesRoute> {
        SummariesScreen(
            onBackClick = onBackClick
        )
    }
}