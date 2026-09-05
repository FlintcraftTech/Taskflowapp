package com.example.taskflow.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Version 5 is the floor below which no data may be destroyed (see [TaskflowDatabase]), so from
 * here on every schema change carries a real migration written against the recorded schema in
 * app/schemas.
 *
 * This test proves the recorded schema is usable: it builds a version 5 database from the exported
 * JSON, writes a task into it, closes and reopens it, and asserts the task survived. It is also
 * where the next schema bump adds its own case — create at 5, run the new migration, assert the
 * row is still there afterwards.
 */
@RunWith(AndroidJUnit4::class)
class MigrationTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        TaskflowDatabase::class.java
    )

    @Test
    fun version5DataSurvivesCloseAndReopen() {
        helper.createDatabase(TEST_DB, 5).apply {
            // MigrationTestHelper builds the database from the recorded schema, so the seed callback
            // that inserts the system Unassigned Project on a real install does not run. The
            // tasks→projects foreign key needs that row before any task can be written.
            execSQL(
                "INSERT INTO projects (id, name, sort_order, is_system) VALUES (1, 'Unassigned', 0, 1)"
            )
            execSQL(
                "INSERT INTO tasks (" +
                    "title, notes, project_id, is_completed, project_suggestion_declined, " +
                    "slot_sort_order, project_sort_order, completed_instances" +
                    ") VALUES ('Survives the upgrade', '', 1, 0, 0, 0, 0, '')"
            )
            close()
        }

        // No migration to run yet — 5 is current — so this reopens and validates the schema.
        val reopened = helper.runMigrationsAndValidate(TEST_DB, 5, true)

        reopened.query("SELECT title FROM tasks").use { cursor ->
            assertEquals("the task written at version 5 should still be there", 1, cursor.count)
            assertTrue(cursor.moveToFirst())
            assertEquals("Survives the upgrade", cursor.getString(0))
        }
    }

    private companion object {
        const val TEST_DB = "migration-test"
    }
}
