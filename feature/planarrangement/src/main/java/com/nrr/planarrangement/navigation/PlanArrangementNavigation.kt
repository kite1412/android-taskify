package com.nrr.planarrangement.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.nrr.model.TaskPeriod
import com.nrr.planarrangement.PlanArrangementScreen
import kotlinx.serialization.Serializable

@Serializable data class PlanArrangementRoute(
    val periodOrdinal: Int? = null,
    val activeStatusId: Long? = null,
    val taskId: Long? = null
)

// use case:
// taskPeriod: from plan detail
// activeStatusId: from task with active status
// taskId: from task without active status
fun NavController.navigateToPlanArrangement(
    taskPeriod: TaskPeriod? = null,
    activeStatusId: Long? = null,
    taskId: Long? = null,
    navOptions: NavOptions? = null
) {
    if (activeStatusId != null && taskId != null && taskPeriod != null)
        throw RuntimeException(
            "Cannot pass both taskPeriod, activeStatusId, and taskId to navigateToPlanArrangement"
        )
    navigate(
        route = PlanArrangementRoute(
            periodOrdinal = taskPeriod?.ordinal,
            activeStatusId = activeStatusId,
            taskId = taskId
        ),
        navOptions = navOptions
    )
}

fun NavGraphBuilder.planArrangementScreen(
    onBackClick: () -> Unit,
    onNewTaskClick: () -> Unit,
    onReminderSettingClick: () -> Unit
) {
    composable<PlanArrangementRoute> {
        PlanArrangementScreen(
            onBackClick = onBackClick,
            onNewTaskClick = onNewTaskClick,
            onReminderSettingClick = onReminderSettingClick
        )
    }
}