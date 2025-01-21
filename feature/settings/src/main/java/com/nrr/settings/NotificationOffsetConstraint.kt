package com.nrr.settings

import com.nrr.model.TimeUnit

internal sealed class NotificationOffsetConstraint(
    val selectableOffset: List<Pair<TimeUnit, IntRange>>
) {
    data object Day : NotificationOffsetConstraint(
        selectableOffset = listOf(
            TimeUnit.MINUTES to 1..60,
            TimeUnit.HOURS to 1..12
        )
    )
    data object Week : NotificationOffsetConstraint(
        selectableOffset = listOf(
            TimeUnit.MINUTES to 1..60,
            TimeUnit.HOURS to 1..24,
            TimeUnit.DAYS to 1..4
        )
    )
    data object Month : NotificationOffsetConstraint(
        selectableOffset = listOf(
            TimeUnit.MINUTES to 1..60,
            TimeUnit.HOURS to 1..24,
            TimeUnit.DAYS to 1..15
        )
    )
}