package com.nrr.datastore.util

import com.nrr.datastore.TimeUnitProto
import com.nrr.model.TimeUnit

internal fun TimeUnitProto.toTimeUnit() = TimeUnit.entries[ordinal]

internal fun TimeUnit.toTimeUnitProto() = TimeUnitProto.entries[ordinal]