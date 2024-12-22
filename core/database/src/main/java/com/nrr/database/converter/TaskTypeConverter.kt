package com.nrr.database.converter

import androidx.room.TypeConverter
import com.nrr.model.TaskType

internal class TaskTypeConverter {
    @TypeConverter
    fun intToTaskType(value: Int?): TaskType? =
        value?.let { TaskType.entries[value] }

    @TypeConverter
    fun taskTypeToInt(taskType: TaskType?): Int? =
        taskType?.ordinal
}