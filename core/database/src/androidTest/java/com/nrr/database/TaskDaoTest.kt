package com.nrr.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.nrr.database.dao.TaskDao
import com.nrr.database.model.TaskEntity
import com.nrr.model.TaskType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.After
import org.junit.Before
import org.junit.Test

class TaskDaoTest {
    private lateinit var db: TaskifyDatabase
    private lateinit var taskDao: TaskDao

    @Before
    fun initDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            TaskifyDatabase::class.java
        ).build()
        taskDao = db.taskDao()
    }

    @After
    fun closeDb() = db.close()

    @Test
    fun insert_then_get_all_task() = runTest {
        val id = taskDao.insertTask(
            TaskEntity(
                title = "Breakfast",
                description = "Test Description",
                createdAt = Clock.System.now(),
                updateAt = Clock.System.now(),
                taskType = TaskType.PERSONAL
            )
        )
        assert(id == 1L)
        assert(taskDao.getAllTasks().first().size == 1)
    }
}