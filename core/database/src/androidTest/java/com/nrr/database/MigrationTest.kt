package com.nrr.database

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test

class MigrationTest {
    private val testDatabase = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        TaskifyDatabase::class.java,
        listOf(DatabaseMigrations.Schema2To3()),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrateAll() {
        var db: SupportSQLiteDatabase? = null
        try {
            helper.createDatabase(testDatabase, 1).apply {
                close()
            }

            db = Room.databaseBuilder(
                InstrumentationRegistry.getInstrumentation().targetContext,
                TaskifyDatabase::class.java,
                testDatabase
            ).build().openHelper.writableDatabase

            assert(
                !isColumnExists(
                    db,
                    "active_tasks",
                    "is_completed"
                )
            )

            assert(
                isColumnExists(
                    db,
                    "active_task_summaries",
                    "task_type"
                )
            )
            assert(
                hasDefaultValue(
                    db,
                    "active_task_summaries",
                    "task_type",
                    "0"
                )
            )
        } finally {
            db?.close()
        }
    }

    private fun isColumnExists(
        db: SupportSQLiteDatabase,
        tableName: String,
        columnName: String
    ): Boolean {
        val cursor = db.query("""
            PRAGMA table_info($tableName)
        """.trimIndent())

        while (cursor.moveToNext()) {
            val column = cursor.getString(
                cursor.getColumnIndexOrThrow("name")
            )
            if (column == columnName) return true
        }

        return false
    }

    private fun hasDefaultValue(
        db: SupportSQLiteDatabase,
        tableName: String,
        columnName: String,
        expectedDefaultValue: String
    ): Boolean {
        val cursor = db.query("""
            PRAGMA table_info($tableName)
        """.trimIndent())

        while (cursor.moveToNext()) {
            val column = cursor.getString(
                cursor.getColumnIndexOrThrow("name")
            )
            if (column == columnName) {
                val defaultValue = cursor.getString(
                    cursor.getColumnIndexOrThrow("dflt_value")
                )
                return defaultValue == expectedDefaultValue
            }
        }

        return false
    }
}