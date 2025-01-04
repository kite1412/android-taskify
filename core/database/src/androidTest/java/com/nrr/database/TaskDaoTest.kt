package com.nrr.database

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class TaskDaoTest : DBSetup() {
    @Test
    fun insertThenGetAllTask() = runTest {
        val id = taskDao.insertTasks(listOf(MockData.taskEntity))
        assert(id[0] == 1L)
        assert(taskDao.getAllTasks().first().size == 1)
    }
}