package com.nrr.taskify.navigation

import com.nrr.analytics.navigation.AnalyticsRoute
import com.nrr.designsystem.component.Destination
import com.nrr.taskmanagement.navigation.TaskManagementRoute
import com.nrr.todayplan.navigation.TodayPlanRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val destination: Destination,
    val route: KClass<*>
) {
    TODAY_PLAN(Destination.HOME, TodayPlanRoute::class),
    TASK_MANAGEMENT(Destination.TASKS, TaskManagementRoute::class),
    ANALYTICS(Destination.ANALYTICS, AnalyticsRoute::class)
}