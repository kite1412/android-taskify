package com.nrr.settings.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.nrr.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable data object SettingsRoute

fun NavController.navigateToSettings(navOptions: NavOptions? = null) =
    navigate(route = SettingsRoute, navOptions = navOptions)

fun NavGraphBuilder.settingsScreen(
    onBackClick: () -> Unit
) {
    composable<SettingsRoute>(
        enterTransition = {
            slideInHorizontally { it }
        },
        exitTransition = {
            slideOutHorizontally { it }
        }
    ) {
        SettingsScreen(
            onBackClick = onBackClick
        )
    }
}