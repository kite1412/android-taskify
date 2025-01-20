package com.nrr.datastore.util

import com.nrr.datastore.TimeUnitProto
import com.nrr.model.TimeUnit

fun TimeUnitProto.toTimeUnit() = TimeUnit.entries[ordinal]

fun TimeUnit.toTimeUnitProto() = TimeUnitProto.entries[ordinal]