package com.nrr.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nrr.database.converter.InstantConverter
import com.nrr.database.converter.TaskPeriodConverter
import com.nrr.database.converter.TaskPriorityConverter
import com.nrr.database.converter.TaskTypeConverter
import com.nrr.database.dao.ActiveTaskDao
import com.nrr.database.dao.TaskDao
import com.nrr.database.entity.ActiveTaskEntity
import com.nrr.database.entity.ActiveTaskSummaryEntity
import com.nrr.database.entity.SummaryGroupEntity
import com.nrr.database.entity.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        ActiveTaskEntity::class,
        ActiveTaskSummaryEntity::class,
        SummaryGroupEntity::class
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ],
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