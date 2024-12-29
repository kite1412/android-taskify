package com.nrr.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.nrr.model.ActiveStatus
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.model.TaskPriority
import com.nrr.model.TaskType
import kotlinx.datetime.Clock
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class TaskPreviewParameter(
    override val values: Sequence<List<Task>> = sequenceOf(listOf(
        Task(
            id = 1,
            title = "Learn Android",
            description = "Learn Android Development",
            createdAt = Clock.System.now(),
            updateAt = Clock.System.now(),
            taskType = TaskType.LEARNING,
            activeStatus = ActiveStatus(
                id = 1,
                startDate = Clock.System.now(),
                dueDate = Clock.System.now(),
                priority = TaskPriority.HIGH,
                period = TaskPeriod.DAY,
                isSet = true,
                isDefault = true,
                isCompleted = true
            )
        ),
        Task(
            id = 2,
            title = "Reflection",
            description = "Self reflection",
            createdAt = Clock.System.now().plus(1.toDuration(DurationUnit.HOURS)),
            updateAt = Clock.System.now().plus(3.toDuration(DurationUnit.HOURS)),
            taskType = TaskType.REFLECTION,
            activeStatus = ActiveStatus(
                id = 2,
                startDate = Clock.System.now(),
                dueDate = Clock.System.now(),
                priority = TaskPriority.NORMAL,
                period = TaskPeriod.DAY,
                isSet = true,
                isDefault = false,
                isCompleted = false
            )
        ),
        Task(
            id = 3,
            title = "Work Out",
            description = "Doing work out at gym",
            createdAt = Clock.System.now().plus(5.toDuration(DurationUnit.HOURS)),
            updateAt = Clock.System.now().plus(7.toDuration(DurationUnit.HOURS)),
            taskType = TaskType.HEALTH,
            activeStatus = ActiveStatus(
                id = 3,
                startDate = Clock.System.now(),
                dueDate = Clock.System.now(),
                priority = TaskPriority.HIGH,
                period = TaskPeriod.DAY,
                isSet = true,
                isDefault = false,
                isCompleted = false
            )
        ),
        Task(
            id = 4,
            title = "Daily Scrum",
            description = "Daily scrum meeting",
            createdAt = Clock.System.now().plus(10.toDuration(DurationUnit.HOURS)),
            updateAt = Clock.System.now().plus(11.toDuration(DurationUnit.HOURS)),
            taskType = TaskType.WORK,
            activeStatus = ActiveStatus(
                id = 4,
                startDate = Clock.System.now(),
                dueDate = Clock.System.now(),
                priority = TaskPriority.CRITICAL,
                period = TaskPeriod.DAY,
                isSet = true,
                isDefault = true,
                isCompleted = false
            )
        )
    ))
) : PreviewParameterProvider<List<Task>>