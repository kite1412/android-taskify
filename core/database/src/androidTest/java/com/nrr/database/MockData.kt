package com.nrr.database

import com.nrr.database.entity.ActiveTaskEntity
import com.nrr.database.entity.TaskEntity
import com.nrr.model.TaskPeriod
import com.nrr.model.TaskPriority
import com.nrr.model.TaskType
import kotlinx.datetime.Clock

internal object MockData {
    val taskEntity = TaskEntity(
        title = "Breakfast",
        description = "Test Description",
        createdAt = Clock.System.now(),
        updateAt = Clock.System.now(),
        taskType = TaskType.PERSONAL
    )

    val activeTaskEntity = ActiveTaskEntity(
        taskId = 1,
        taskPriority = TaskPriority.NORMAL,
        taskPeriod = TaskPeriod.DAY,
        reminderSet = false,
        startDate = Clock.System.now(),
        dueDate = Clock.System.now(),
        isDefault = false,
        isSet = true,
        completedAt = null
    )
}