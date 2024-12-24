package com.nrr.database

import androidx.room.RoomDatabase
import com.nrr.database.dao.ActiveTaskDao
import com.nrr.database.dao.TaskDao

// TODO mark as @Database
internal abstract class TaskifyDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun activeTaskDao(): ActiveTaskDao
}