package com.nrr.summaries.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nrr.summaries.SummariesScreen
import kotlinx.serialization.Serializable

@Serializable data object SummariesRoute

fun NavGraphBuilder.summariesScreen(
    onBackClick: () -> Unit
) {
    composable<SummariesRoute> {
        SummariesScreen(
            onBackClick = onBackClick
        )
    }
}