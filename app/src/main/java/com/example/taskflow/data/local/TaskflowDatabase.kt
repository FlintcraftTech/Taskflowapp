package com.example.taskflow.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taskflow.data.model.LifeArea
import com.example.taskflow.data.model.Project
import com.example.taskflow.data.model.StrategyEntry
import com.example.taskflow.data.model.Task

@Database(
    entities = [Task::class, Project::class, StrategyEntry::class, LifeArea::class],
    // v2: project_id is now non-null and every task points at a Project. The schema adds the
    // system "Unassigned" Project (projects.is_system) that unassigned tasks default to. Pre-release
    // with no real users, so the bump uses destructive migration rather than a hand-written one.
    // v3: tasks gain `recurrence` (the repeat rule, null on a one-off) and `completed_instances`
    // (which dates of a repeat the user has ticked off). Same destructive-migration reasoning.
    // v4: tasks gain `completed_at` — an export has to carry when work was finished, not just that
    // it was (SPEC §JSON export and import).
    // v5: the `life_areas` table, which only Claude ever touches through MCP (SPEC §Strategy doc).
    version = 5,
    exportSchema = true
)
abstract class TaskflowDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun projectDao(): ProjectDao
    abstract fun strategyEntryDao(): StrategyEntryDao
    abstract fun lifeAreaDao(): LifeAreaDao

    companion object {
        @Volatile
        private var INSTANCE: TaskflowDatabase? = null

        fun getInstance(context: Context): TaskflowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskflowDatabase::class.java,
                    "taskflow_database"
                )
                    // Version 5 is the floor: data at v5 or later must survive an app upgrade, so
                    // every schema change from here carries a real migration written against the
                    // recorded schema in app/schemas. Only versions 1 to 4 may still be thrown
                    // away — no data anyone wants exists at those versions.
                    .fallbackToDestructiveMigrationFrom(dropAllTables = true, 1, 2, 3, 4)
                    .addCallback(SeedCallback)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Ensures the single system "Unassigned" Project exists. It must be present before any task
         * is inserted, because [Task.projectId] is non-null and defaults to
         * [Project.UNASSIGNED_PROJECT_ID] and the tasks→projects foreign key requires the parent row.
         *
         * Seeded in [onOpen] rather than onCreate/onDestructiveMigration deliberately: onOpen is the
         * only callback that always runs with the schema fully in place. onDestructiveMigration fires
         * mid-migration — after the old tables are dropped but before the new ones are recreated — so
         * an INSERT there hits a missing table and crashes the migration. onOpen runs on every path
         * (fresh create, destructive recreate, and normal open) once the tables exist. INSERT OR
         * IGNORE makes the per-open insert idempotent — it only writes the row when it's absent.
         */
        private val SeedCallback = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                db.execSQL(
                    "INSERT OR IGNORE INTO projects (id, name, sort_order, is_system) " +
                        "VALUES (${Project.UNASSIGNED_PROJECT_ID}, '${Project.UNASSIGNED_PROJECT_NAME}', 0, 1)"
                )
            }
        }
    }
}
