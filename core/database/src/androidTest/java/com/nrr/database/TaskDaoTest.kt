package com.nrr.database

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class TaskDaoTest : DBSetup() {
    @Test
    fun insert_then_get_all_task() = runTest {
        val id = taskDao.insertTask(MockData.taskEntity)
        assert(id == 1L)
        assert(taskDao.getAllTasks().first().size == 1)
    }
}