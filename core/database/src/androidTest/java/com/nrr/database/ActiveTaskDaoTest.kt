package com.nrr.database

import android.util.Log
import com.nrr.model.TaskPeriod
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class ActiveTaskDaoTest : DBSetup() {
    @Test
    fun insertThenGetAllActiveTasks() = runTest {
        taskDao.insertTask(MockData.taskEntity)
        activeTaskDao.insertActiveTask(MockData.activeTaskEntity)
        val res = activeTaskDao.getAllByPeriod(TaskPeriod.DAY).first()
        res.forEach {
            Log.i(tag, it.toString())
        }
        assert(res.size == 1)
    }

    @Test
    fun insertThenDelete() = runTest {
        val id = taskDao.insertTask(MockData.taskEntity)
        Log.i(tag, "inserted $id")
        val aId = activeTaskDao.insertActiveTask(MockData.activeTaskEntity)
        Log.i(tag, "active inserted: $aId")
        assert(
            activeTaskDao.deleteActiveTask(MockData.activeTaskEntity.copy(id = aId)) == 1
        )
    }
}