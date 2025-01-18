package com.nrr.notification

import com.nrr.notification.model.ReminderType
import com.nrr.notification.model.TaskFiltered
import com.nrr.notification.model.TaskWithReminder
import com.nrr.notification.util.gson
import kotlinx.datetime.Clock
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GsonTest {
    @Test
    fun conversion_isCorrect() {
        val now = Clock.System.now()
        val taskWithReminder = TaskWithReminder(
            task = TaskFiltered(
                id = 1,
                title = "Test",
                startDate = now,
                dueDate = now
            ),
            reminderType = ReminderType.START
        )
        val json = gson {
            toJson(taskWithReminder)
        }
        val taskFromGson = gson {
            fromJson(json, TaskWithReminder::class.java)
        }
        println(taskWithReminder)
        println(taskFromGson)
        assert(taskWithReminder == taskFromGson)
    }
}