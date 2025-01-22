package com.nrr.settings.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.nrr.settings.Menu
import com.nrr.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable data class SettingsRoute(
    val menu: Menu? = null
)

fun NavController.navigateToSettings(
    menu: Menu? = null,
    navOptions: NavOptions? = null
) = navigate(
    route = SettingsRoute(menu = menu),
    navOptions = navOptions
)

fun NavGraphBuilder.settingsScreen(
    onBackClick: () -> Unit
) {
    composable<SettingsRoute>(
        enterTransition = {
            slideInHorizontally { it } + fadeIn()
        },
        exitTransition = {
            slideOutHorizontally { it } + fadeOut()
        }
    ) {
        SettingsScreen(
            onBackClick = onBackClick
        )
    }
}