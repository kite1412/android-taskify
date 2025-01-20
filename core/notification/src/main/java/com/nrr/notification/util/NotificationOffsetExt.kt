package com.nrr.notification.util

import com.nrr.model.NotificationOffset
import com.nrr.model.TimeUnit
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun NotificationOffset.toDuration() =
    value.toDuration(
        unit = when (timeUnit) {
            TimeUnit.MINUTES -> DurationUnit.MINUTES
            TimeUnit.HOURS -> DurationUnit.HOURS
            TimeUnit.DAYS -> DurationUnit.DAYS
        }
    )