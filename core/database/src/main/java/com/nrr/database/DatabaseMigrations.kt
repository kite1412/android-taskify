package com.nrr.database

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec

internal object DatabaseMigrations {
    @DeleteColumn(
        tableName = "active_tasks",
        columnName = "is_completed"
    )
    class Schema2To3 : AutoMigrationSpec
}