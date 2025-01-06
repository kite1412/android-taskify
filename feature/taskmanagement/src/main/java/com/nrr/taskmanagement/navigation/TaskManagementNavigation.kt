package com.nrr.taskmanagement.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.nrr.taskmanagement.TaskManagementScreen
import kotlinx.serialization.Serializable

@Serializable data object TaskManagementRoute

fun NavController.navigateToTaskManagement(navOptions: NavOptions) = navigate(
    route = TaskManagementRoute,
    navOptions = navOptions
)

fun NavGraphBuilder.taskManagementScreen(
    onTaskClick: (Long?) -> Unit,
) {
    composable<TaskManagementRoute> {
        TaskManagementScreen(
            onAddClick = { onTaskClick(null) },
            onTaskClick = { onTaskClick(it.id) }
        )
    }
}