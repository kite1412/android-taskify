package com.nrr.notification

import android.content.Context
import androidx.work.WorkManager
import com.nrr.model.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduledTaskNotifierImpl @Inject constructor(
    @ApplicationContext context: Context
) : ScheduledTaskNotifier {
    val wm = WorkManager.getInstance(context)

    override fun scheduleReminder(
        task: Task,
        reminderType: ReminderType
    ) {

    }
}