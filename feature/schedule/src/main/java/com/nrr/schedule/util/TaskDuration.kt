package com.nrr.schedule.util

import com.nrr.model.Task
import java.util.UUID
import kotlin.time.Duration

internal data class TaskDuration(
    val task: Task,
    val duration: Duration,
    val uuid: String = UUID.randomUUID().toString()
)