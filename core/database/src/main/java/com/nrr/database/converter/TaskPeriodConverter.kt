package com.nrr.database.converter

import androidx.room.TypeConverter
import com.nrr.model.TaskPeriod

class TaskPeriodConverter {
    @TypeConverter
    fun intToTaskPeriod(value: Int?): TaskPeriod? =
        value?.let { TaskPeriod.entries[value] }

    @TypeConverter
    fun taskPeriodToInt(taskPeriod: TaskPeriod?): Int? =
        taskPeriod?.ordinal
}