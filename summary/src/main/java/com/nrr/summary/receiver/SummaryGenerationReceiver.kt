package com.nrr.summary.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import com.nrr.model.TaskPeriod
import com.nrr.summary.DefaultSummariesGenerationScheduler
import com.nrr.summary.worker.SummaryGenerationWorker.Companion.enqueuePeriodicSummaryGeneration

const val SUMMARY_GENERATION_ACTION = "com.nrr.summary.SUMMARY_GENERATION"

class SummaryGenerationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val workManager = WorkManager.getInstance(context)
        val taskPeriod = intent.getIntExtra(TASK_PERIOD_ORDINAL_KEY, -1)
            .takeIf { it >= 0 }
            ?.let { TaskPeriod.entries[it] } ?: return

        workManager.enqueuePeriodicSummaryGeneration(
            uniqueWorkName = DefaultSummariesGenerationScheduler.getUniqueWorkName(taskPeriod),
            taskPeriod = taskPeriod
        )
    }

    companion object {
        const val TASK_PERIOD_ORDINAL_KEY = "task_period_ordinal"
    }
}