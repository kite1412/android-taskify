package com.nrr.database.converter

import androidx.room.TypeConverter
import com.nrr.model.TaskPriority

internal class TaskPriorityConverter {
    @TypeConverter
    fun intToTaskPriority(value: Int?): TaskPriority? =
        value?.let { TaskPriority.entries[value] }

    @TypeConverter
    fun taskPriorityToInt(taskPriority: TaskPriority?): Int? =
        taskPriority?.ordinal
}