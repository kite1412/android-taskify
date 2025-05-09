package com.nrr.notification

import android.content.Context
import android.os.Build
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.nrr.model.ReminderType
import com.nrr.model.Task
import com.nrr.notification.model.Result
import com.nrr.notification.model.Result.Fail.Reason
import com.nrr.notification.model.Result.Success.Warning
import com.nrr.notification.model.TaskWithReminder
import com.nrr.notification.model.toFiltered
import com.nrr.notification.util.gson
import com.nrr.notification.worker.ScheduledTaskNotificationWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Deprecated(
    message = """
        WorkManager is not as reliable as AlarmManager for exact scheduling,
        use AlarmManager implementation instead.
    """
)
@Singleton
internal class WorkManagerScheduledTaskNotifier @Inject constructor(
    @ApplicationContext context: Context
) : ScheduledTaskNotifier {
    private val wm = WorkManager.getInstance(context)

    override suspend fun scheduleReminder(task: Task): Result {
        wm.cancelUniqueWork(task.id.toString())

        val filtered = task.toFiltered()
        val curDate = Clock.System.now()
        val startDelay = filtered.startDate - curDate
        if (startDelay < 0.seconds) return Result.Fail(Reason.START_DATE_IN_PAST)

        var warning: Warning? = null
        val workRequest = ScheduledTaskNotificationWorker.workRequest(
            inputData = workDataOf(
                ScheduledTaskNotificationWorker.DATA_KEY to gson {
                    toJson(TaskWithReminder(filtered, ReminderType.START))
                }
            )
        ) {
            //TODO handle api level 24-25
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setInitialDelay(startDelay.toJavaDuration())
            }
        }

        wm.beginUniqueWork(
            uniqueWorkName = filtered.id.toString(),
            existingWorkPolicy = ExistingWorkPolicy.REPLACE,
            request = workRequest
        ).apply {
            // TODO handle due date scheduling
            if (filtered.dueDate != null) {
                val endDelay = filtered.dueDate - curDate
                if (endDelay < 0.seconds) warning = Warning.END_REMINDER_IN_PAST

            } else warning = Warning.END_REMINDER_SKIPPED
        }.enqueue()

        return Result.Success(warning)
    }

    override fun cancelReminder(activeTask: Task) {
        activeTask.activeStatuses.firstOrNull()?.id?.let {
            wm.cancelUniqueWork(it.toString())
        }
    }
}