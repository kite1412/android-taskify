package com.nrr.notification.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nrr.data.repository.TaskRepository
import com.nrr.notification.util.cancelNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

const val COMPLETE_ACTION_ACTION = "com.nrr.notification.COMPLETE_ACTION_ACTION"

@AndroidEntryPoint
class CompleteActionReceiver : BroadcastReceiver() {
    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        CoroutineScope(Dispatchers.Default).launch {
            val id = intent.getIntExtra(ACTIVE_STATUS_ID_KEY, 0)
                .takeIf { it > 0 } ?: return@launch

            context.cancelNotification(id)
            taskRepository.getActiveTasksByIds(listOf(id.toLong()))
                .firstOrNull()
                ?.firstOrNull()
                ?.let {
                    taskRepository.setActiveTaskAsCompleted(it)
                }
        }
    }

    internal companion object {
        const val ACTIVE_STATUS_ID_KEY = "activeStatusId"
    }
}

internal fun Context.completeActionPendingIntent(
    activeStatusId: Int
) = PendingIntent.getBroadcast(
    this,
    activeStatusId,
    Intent(this, CompleteActionReceiver::class.java).apply {
        action = COMPLETE_ACTION_ACTION
        putExtra(CompleteActionReceiver.ACTIVE_STATUS_ID_KEY, activeStatusId)
    },
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
)