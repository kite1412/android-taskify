package com.nrr.analytics.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.nrr.analytics.AnalyticsScreen
import kotlinx.serialization.Serializable

@Serializable data object AnalyticsRoute

fun NavController.navigateToAnalytics(
    navOptions: NavOptions? = null
) = navigate(
    route = AnalyticsRoute,
    navOptions = navOptions
)

fun NavGraphBuilder.analyticsScreen() {
    composable<AnalyticsRoute> {
        AnalyticsScreen()
    }
}