package com.nrr.database

import android.util.Log
import com.nrr.model.TaskPeriod
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class ActiveTaskDaoTest : DBSetup() {
    @Test
    fun insert_then_get_all_active_tasks() = runTest {
        taskDao.insertTask(MockData.taskEntity)
        activeTaskDao.insertActiveTask(MockData.activeTaskEntity)
        val res = activeTaskDao.getAllByPeriod(TaskPeriod.DAY).first()
        res.forEach {
            Log.i(tag, it.toString())
        }
        assert(res.size == 1)
    }
}