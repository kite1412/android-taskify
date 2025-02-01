package com.nrr.summary.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import com.nrr.summary.DefaultSummariesGenerationScheduler
import com.nrr.summary.worker.SummariesGenerationWorker.Companion.enqueuePeriodSummariesGeneration

const val SUMMARIES_GENERATION_ACTION = "com.nrr.action.SUMMARIES_GENERATION"

class SummariesGenerationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        WorkManager.getInstance(context)
            .enqueuePeriodSummariesGeneration(
                uniqueWorkName = DefaultSummariesGenerationScheduler
                    .SUMMARIES_GENERATION_WORK_NAME
            )
    }
}