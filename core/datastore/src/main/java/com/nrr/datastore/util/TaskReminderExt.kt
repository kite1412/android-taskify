package com.nrr.datastore.util

import com.nrr.datastore.ReminderTypeProto
import com.nrr.datastore.TaskReminderProto
import com.nrr.model.ReminderType
import com.nrr.model.TaskReminder
import kotlinx.datetime.Instant

fun TaskReminder.toTaskReminderProto(): TaskReminderProto =
    TaskReminderProto.newBuilder()
        .setActiveTaskId(activeTaskId)
        .setReminderType(ReminderTypeProto.entries[reminderType.ordinal])
        .setEpochMillis(date.toEpochMilliseconds())
        .build()

fun TaskReminderProto.toTaskReminder() = TaskReminder(
    activeTaskId = activeTaskId,
    reminderType = ReminderType.entries[reminderType.ordinal],
    date = Instant.fromEpochMilliseconds(epochMillis)
)
