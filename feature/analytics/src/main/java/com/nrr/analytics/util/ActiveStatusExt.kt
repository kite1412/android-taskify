package com.nrr.analytics.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import com.nrr.model.ActiveStatus
import com.nrr.ui.util.resolveProgressStatus
import com.nrr.ui.util.statusColor
import com.nrr.ui.util.statusNameId
import ir.ehsannarmani.compose_charts.models.Bars

@Composable
internal fun List<ActiveStatus>.periodBars() =
    sortedBy { it.period.ordinal }
        .groupBy { it.period }
        .map { (k, v) ->
            val status = { s: ActiveStatus ->
                resolveProgressStatus(
                    target = s.completedAt,
                    limit = s.dueDate
                )
            }

            Bars(
                label = k.toString(),
                values = v
                    .groupBy(status)
                    .map { (s, t) ->
                        Bars.Data(
                            label = stringResource(statusNameId(s)),
                            value = t.size.toDouble(),
                            color = SolidColor(statusColor(s))
                        )
                    }
            )
        }