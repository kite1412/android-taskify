package com.nrr.notification.receiver

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.nrr.data.repository.TaskRepository
import com.nrr.notification.AlarmManagerScheduledTaskNotifier
import com.nrr.notification.R
import com.nrr.notification.util.createNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ScheduledTaskReceiver : BroadcastReceiver() {
    @Inject
    lateinit var taskRepository: TaskRepository

    private val notificationId = 1

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

        val id = intent.getLongExtra(AlarmManagerScheduledTaskNotifier.DATA_KEY, 0L)
        CoroutineScope(Dispatchers.Main).launch {
            val task = id.takeIf { it > 0 }?.let {
                taskRepository.getActiveTasksByIds(listOf(it))
                    .firstOrNull()?.firstOrNull() ?: return@launch
            } ?: return@launch

            val notification = context.createNotification {
                setContentTitle(task.title)
                setContentText(task.description)
                setSmallIcon(R.drawable.app_icon_small)
            }

            NotificationManagerCompat.from(context)
                .notify(
                    notificationId,
                    notification
                )
        }
    }
}