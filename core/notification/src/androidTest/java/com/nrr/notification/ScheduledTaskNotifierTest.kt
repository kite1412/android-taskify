package com.nrr.notification

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.testing.WorkManagerTestInitHelper
import com.nrr.model.ActiveStatus
import com.nrr.model.Task
import com.nrr.model.TaskPeriod
import com.nrr.model.TaskPriority
import com.nrr.model.TaskType
import com.nrr.notification.model.Result
import com.nrr.notification.model.Result.Success.Warning
import com.nrr.notification.rule.GrantPostNotificationPermissionRule
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class ScheduledTaskNotifierTest {
    private lateinit var notifier: ScheduledTaskNotifier
    private val task = Task(
        id = 1,
        title = "A task",
        description = null,
        createdAt = Clock.System.now(),
        updateAt = Clock.System.now(),
        taskType = TaskType.PERSONAL,
        activeStatuses = listOf(
            ActiveStatus(
                id = 1,
                startDate = Clock.System.now() + 3.seconds,
                dueDate = null,
                isCompleted = false,
                priority = TaskPriority.NORMAL,
                period = TaskPeriod.DAY,
                reminderSet = false,
                isSet = true,
                isDefault = false
            )
        )
    )

    @get:Rule(order = 0)
    val postNotificationPermission = GrantPostNotificationPermissionRule()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        notifier = WorkManagerScheduledTaskNotifier(context)

        WorkManagerTestInitHelper.initializeTestWorkManager(context)
    }

    @Test
    fun scheduleReminder_success() = runTest {
        val res = notifier.scheduleReminder(task)
        assert(res is Result.Success)
    }

    @Test
    fun scheduleReminder_dueDateNull_failure() = runTest {
        val res = notifier.scheduleReminder(task)
        assert(res is Result.Success && res.warning == Warning.END_REMINDER_SKIPPED)
    }

    @Test
    fun scheduleReminder_dueDateInPast_failure() = runTest {
        val res = notifier.scheduleReminder(
            task.copy(
                activeStatuses = listOf(
                    task.activeStatuses.first().copy(
                        dueDate = Clock.System.now() - 3.seconds
                    )
                )
            )
        )
        assert(res is Result.Success && res.warning == Warning.END_REMINDER_IN_PAST)
    }
}