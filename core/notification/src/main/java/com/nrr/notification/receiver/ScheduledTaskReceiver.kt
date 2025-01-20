package com.nrr.notification.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.nrr.data.repository.TaskRepository
import com.nrr.model.Task
import com.nrr.notification.AlarmManagerScheduledTaskNotifier
import com.nrr.notification.R
import com.nrr.notification.model.ReminderType
import com.nrr.notification.model.TaskWithReminder
import com.nrr.notification.model.toFiltered
import com.nrr.notification.util.NotificationDictionary
import com.nrr.notification.util.createNotification
import com.nrr.notification.util.getContent
import com.nrr.notification.util.getTitle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

const val DEEP_LINK_ACTIVE_TASK_ID_KEY = "taskId"
const val DEEP_LINK_PERIOD_ORDINAL_KEY = "periodOrdinal"
const val DEEP_LINK_SCHEME_AND_HOST = "com.nrr.taskify://plan"
const val DEEP_LINK_URI_PATTERN = "$DEEP_LINK_SCHEME_AND_HOST/{$DEEP_LINK_PERIOD_ORDINAL_KEY}/{$DEEP_LINK_ACTIVE_TASK_ID_KEY}"
const val TASK_REMINDER_ACTION = "com.nrr.notification.TASK_REMINDER"

@AndroidEntryPoint
class ScheduledTaskReceiver : BroadcastReceiver() {
    private val mainActivityName = "com.nrr.taskify.MainActivity"

    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val alarmManager = context
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            && !alarmManager.canScheduleExactAlarms()
        ) return

        val id = intent.getIntExtra(AlarmManagerScheduledTaskNotifier.DATA_KEY, 0)
        // TODO implement due notification
        CoroutineScope(Dispatchers.Main).launch {
            val task = id.takeIf { it > 0 }?.let {
                taskRepository.getActiveTasksByIds(listOf(it.toLong()))
                    .firstOrNull()?.firstOrNull() ?: return@launch
            } ?: return@launch

            val taskWithReminder = TaskWithReminder(task.toFiltered(), ReminderType.START)
            val notification = context.createNotification {
                val reminderType = taskWithReminder.reminderType
                val taskFiltered = taskWithReminder.task
                val title = context.getTitle(reminderType)
                val content = context.getContent(taskFiltered, reminderType)

                setContentTitle(title)
                setContentText(content)
                setSmallIcon(R.drawable.app_icon_small)
                setContentIntent(notificationContentIntent(context, task))
                addAction(
                    0,
                    context.getString(NotificationDictionary.remindLater),
                    context.remindLaterIntent(
                        activeStatusId = taskFiltered.id.toInt(),
                        reminderTypeOrdinal = ReminderType.valueOf(reminderType.name).ordinal
                    )
                )
                setAutoCancel(true)
            }

            NotificationManagerCompat.from(context)
                .notify(
                    taskWithReminder.task.id.toInt(),
                    notification
                )
        }
    }

    private fun notificationContentIntent(
        context: Context,
        task: Task
    ) = PendingIntent.getActivity(
        context,
        0,
        Intent().apply {
            action = Intent.ACTION_VIEW
            data = task.toDeepLinkUri()
            component = ComponentName(
                context.packageName,
                mainActivityName
            )
        },
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun Context.remindLaterIntent(
        activeStatusId: Int,
        reminderTypeOrdinal: Int
    ) = PendingIntent.getBroadcast(
        this,
        0,
        Intent(this, ScheduledTaskReceiver::class.java).apply {
            action = REMIND_LATER_ACTION
            putExtra(AlarmManagerScheduledTaskNotifier.DATA_KEY, activeStatusId)
            putExtra(RemindLaterReceiver.REMINDER_TYPE_ORDINAL_KEY, reminderTypeOrdinal)
        },
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun Task.toDeepLinkUri() = with(activeStatuses.first()) {
        "$DEEP_LINK_SCHEME_AND_HOST/${period.ordinal}/$id".toUri()
    }
}