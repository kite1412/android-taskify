package com.nrr.model

import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class TimeOffset(
    val value: Int,
    val timeUnit: TimeUnit
) {
    fun toDuration() =
        value.toDuration(
            unit = when (timeUnit) {
                TimeUnit.MINUTES -> DurationUnit.MINUTES
                TimeUnit.HOURS -> DurationUnit.HOURS
                TimeUnit.DAYS -> DurationUnit.DAYS
            }
        )

    companion object {
        val Default = TimeOffset(1, TimeUnit.MINUTES)
    }
}