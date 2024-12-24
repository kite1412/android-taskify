package com.nrr.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.nrr.database.dao.ActiveTaskDao
import com.nrr.database.dao.TaskDao
import org.junit.After
import org.junit.Before

internal open class DBSetup {
    private lateinit var db: TaskifyDatabase
    lateinit var taskDao: TaskDao
    lateinit var activeTaskDao: ActiveTaskDao
    val tag = "TaskifyRoomTest"

    @Before
    fun initDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            TaskifyDatabase::class.java
        ).build()
        taskDao = db.taskDao()
        activeTaskDao = db.activeTaskDao()
    }

    @After
    fun closeDb() = db.close()
}