package com.nrr.taskdetail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.nrr.taskdetail.TaskDetailScreen
import kotlinx.serialization.Serializable

@Serializable data class TaskDetailRoute(val taskId: Long?)

fun NavController.navigateToTaskDetail(
    taskId: Long?,
    navOptions: NavOptions? = null
) = navigate(
    route = TaskDetailRoute(taskId),
    navOptions = navOptions
)

fun NavGraphBuilder.taskDetailScreen(
    onBackClick: () -> Unit
) {
    composable<TaskDetailRoute> {
        TaskDetailScreen(
            onBackClick = onBackClick
        )
    }
}