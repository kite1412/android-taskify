package com.nrr.database

import androidx.room.withTransaction
import javax.inject.Inject

class RoomTransactionScope @Inject internal constructor(
    private val database: TaskifyDatabase
) {
    suspend operator fun <T> invoke(block: suspend () -> T) =
        database.withTransaction(block)
}