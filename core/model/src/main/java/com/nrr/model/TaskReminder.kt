package com.nrr.model

import kotlinx.datetime.Instant

data class TaskReminder(
    val activeTaskId: Long,
    val reminderType: ReminderType,
    val date: Instant
)
