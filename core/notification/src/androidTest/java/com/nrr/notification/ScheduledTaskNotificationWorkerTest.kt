package com.nrr.notification

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.nrr.notification.model.ReminderType
import com.nrr.notification.model.TaskFiltered
import com.nrr.notification.model.TaskWithReminder
import com.nrr.notification.rule.GrantPostNotificationPermissionRule
import com.nrr.notification.util.gson
import com.nrr.notification.worker.ScheduledTaskNotificationWorker
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ScheduledTaskNotificationWorkerTest {
    private lateinit var context: Context
    private lateinit var workerBuilder: TestListenableWorkerBuilder<ScheduledTaskNotificationWorker>
    private val now = Clock.System.now()
    private val taskWithReminder = TaskWithReminder(
        task = TaskFiltered(
            id = 1,
            title = "A task",
            startDate = now,
            dueDate = now
        ),
        reminderType = ReminderType.START
    )
    private fun inputData(data: TaskWithReminder = taskWithReminder) = workDataOf(
        ScheduledTaskNotificationWorker.DATA_KEY to gson {
            toJson(data)
        }
    )

    @JvmField
    @Rule(order = 0)
    val postNotificationPermission = GrantPostNotificationPermissionRule()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        workerBuilder =
            TestListenableWorkerBuilder<ScheduledTaskNotificationWorker>(context)
    }

    @Test
    fun withoutData_failure() {
        runBlocking {
            val res = workerBuilder.build().doWork()
            println(res)
            assert(res == ListenableWorker.Result.failure())
        }
    }

    @Test
    fun withData_success() {
        workerBuilder
            .setInputData(inputData())
            .build().run {
                runBlocking {
                    val res = doWork()
                    println(res)
                    assert(res == ListenableWorker.Result.success())
                }
            }
    }

    @Test
    fun withData_failure() {
        workerBuilder
            .setInputData(
                inputData(
                    data = taskWithReminder.copy(
                        task = taskWithReminder.task.copy(
                            dueDate = null
                        ),
                        reminderType = ReminderType.END
                    ),
                )
            )
            .build().run {
                runBlocking {
                    val res = doWork()
                    println(res)
                    assert(res == ListenableWorker.Result.failure())
                }
            }
    }
}