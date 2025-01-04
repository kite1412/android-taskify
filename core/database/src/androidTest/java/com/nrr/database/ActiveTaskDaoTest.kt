package com.nrr.database

import android.util.Log
import com.nrr.model.TaskPeriod
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class ActiveTaskDaoTest : DBSetup() {
    @Test
    fun insertThenGetAllActiveTasks() = runTest {
        taskDao.insertTasks(listOf(MockData.taskEntity))
        activeTaskDao.insertActiveTasks(listOf(MockData.activeTaskEntity))
        val res = activeTaskDao.getAllByPeriod(TaskPeriod.DAY).first()
        res.forEach {
            Log.i(tag, it.toString())
        }
        assert(res.size == 1)
    }

    @Test
    fun insertThenDelete() = runTest {
        val id = taskDao.insertTasks(listOf(MockData.taskEntity))
        Log.i(tag, "inserted $id")
        val ids = activeTaskDao.insertActiveTasks(listOf(MockData.activeTaskEntity))
        Log.i(tag, "active inserted: ${ids[0]}")
        assert(
            activeTaskDao.deleteActiveTasks(
                listOf(MockData.activeTaskEntity.copy(id = ids[0]).taskId)
            ) == 1
        )
    }
}