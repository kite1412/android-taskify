package com.nrr.summary

import com.nrr.model.TaskPeriod

interface SummariesGenerationScheduler {
    /**
     * Schedules summaries generation based on available [TaskPeriod]s.
     *
     * This method ensures the scheduling of summaries generation only happens once.
     */
    fun scheduleSummariesGeneration()
}