package com.nrr.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nrr.database.converter.InstantConverter
import com.nrr.database.converter.TaskPeriodConverter
import com.nrr.database.converter.TaskPriorityConverter
import com.nrr.database.converter.TaskTypeConverter
import com.nrr.database.dao.ActiveTaskDao
import com.nrr.database.dao.TaskDao
import com.nrr.database.model.ActiveTaskEntity
import com.nrr.database.model.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        ActiveTaskEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    value = [
        InstantConverter::class,
        TaskPeriodConverter::class,
        TaskTypeConverter::class,
        TaskPriorityConverter::class
    ]
)
internal abstract class TaskifyDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun activeTaskDao(): ActiveTaskDao
}