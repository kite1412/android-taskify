package com.nrr.datastore.util

import com.nrr.datastore.SummariesGenerationReportProto
import com.nrr.model.SummariesGenerationReport
import kotlinx.datetime.Instant

internal fun SummariesGenerationReport.toSummariesGenerationReportProto() =
    SummariesGenerationReportProto
        .newBuilder()
        .setLastGenerationUtcMillis(lastGenerationDate.toEpochMilliseconds())

internal fun SummariesGenerationReportProto.toSummariesGenerationReport() =
    SummariesGenerationReport(
        lastGenerationDate = Instant.fromEpochMilliseconds(lastGenerationUtcMillis)
    )