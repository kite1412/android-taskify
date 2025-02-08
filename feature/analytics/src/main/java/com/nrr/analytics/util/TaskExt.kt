package com.nrr.analytics.util

import androidx.compose.runtime.Composable
import com.nrr.model.Task
import com.nrr.ui.color
import com.nrr.ui.toStringLocalized
import ir.ehsannarmani.compose_charts.models.Pie

@Composable
internal fun List<Task>.taskTypePieData() =
    groupBy { it.taskType }
        .map { (t, l) ->
            val label = t.toStringLocalized()
            Pie(
                label = label,
                data = l.size.toDouble(),
                color = t.color()
            )
        }