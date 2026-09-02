package com.example.taskflow.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taskflow.data.model.ScheduleSlot
import com.example.taskflow.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Long): Task?

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun observeById(id: Long): Flow<Task?>

    // All active (incomplete) top-level tasks, ordered by slot_sort_order. The Schedule view
    // buckets these into Today/Tomorrow/Soon/Later by deriving each dated task's slot from its
    // date (see SlotDeriver), plus undated tasks parked on a slot. One query keeps bucketing in
    // one place rather than spreading date-range logic across SQL.
    @Query("SELECT * FROM tasks WHERE parent_id IS NULL AND is_completed = 0 ORDER BY slot_sort_order ASC")
    fun observeActiveTopLevel(): Flow<List<Task>>

    // Schedule queries — tasks by slot, ordered by slot_sort_order
    @Query("SELECT * FROM tasks WHERE slot = :slot AND parent_id IS NULL ORDER BY slot_sort_order ASC")
    fun getTasksBySlot(slot: ScheduleSlot): Flow<List<Task>>

    // Tasks by date (for dated tasks that derive their slot from their date)
    @Query("SELECT * FROM tasks WHERE date = :date AND parent_id IS NULL ORDER BY slot_sort_order ASC")
    fun getTasksByDate(date: Long): Flow<List<Task>>

    // Tasks for a project — dated (top card)
    @Query("SELECT * FROM tasks WHERE project_id = :projectId AND date IS NOT NULL AND parent_id IS NULL ORDER BY slot_sort_order ASC")
    fun getDatedTasksByProject(projectId: Long): Flow<List<Task>>

    // Tasks for a project — undated (below the card)
    @Query("SELECT * FROM tasks WHERE project_id = :projectId AND date IS NULL AND parent_id IS NULL ORDER BY project_sort_order ASC")
    fun getUndatedTasksByProject(projectId: Long): Flow<List<Task>>

    // Subtasks of a parent
    @Query("SELECT * FROM tasks WHERE parent_id = :parentId ORDER BY slot_sort_order ASC")
    fun getSubtasks(parentId: Long): Flow<List<Task>>

    // Every subtask in the database, parent and completion state included. The Schedule renders
    // children nested under their parents on whatever surface the parent lives on, so it needs all
    // of them at once — one query it can group by parent_id, rather than a Flow per visible parent.
    @Query("SELECT * FROM tasks WHERE parent_id IS NOT NULL ORDER BY slot_sort_order ASC")
    fun observeAllSubtasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE parent_id = :parentId ORDER BY slot_sort_order ASC")
    suspend fun getSubtasksList(parentId: Long): List<Task>

    // Completed tasks (for the completed tray on Today)
    @Query("SELECT * FROM tasks WHERE is_completed = 1 AND parent_id IS NULL ORDER BY slot_sort_order ASC")
    fun getCompletedTasks(): Flow<List<Task>>

    // Reorder within a slot: update slot_sort_order for a specific task
    @Query("UPDATE tasks SET slot_sort_order = :newOrder WHERE id = :taskId")
    suspend fun updateSlotSortOrder(taskId: Long, newOrder: Int)

    // Reorder within a project: update project_sort_order for a specific task
    @Query("UPDATE tasks SET project_sort_order = :newOrder WHERE id = :taskId")
    suspend fun updateProjectSortOrder(taskId: Long, newOrder: Int)

    // Refile a task to a different project. project_id is non-null in the schema (every task has a
    // Project home); the param stays nullable only to leave the existing repository signature
    // untouched — no caller passes null, and a null bind would be rejected by the NOT NULL column.
    @Query("UPDATE tasks SET project_id = :projectId WHERE id = :taskId")
    suspend fun updateProjectId(taskId: Long, projectId: Long?)

    // Bulk-reassign every task of one Project to another (used when a real Project is deleted, to
    // move its tasks onto the system Unassigned Project before the delete so none is orphaned — the
    // tasks→projects foreign key is RESTRICT). Reassigning by project_id covers subtasks too, since
    // a subtask shares its parent's project_id.
    @Query("UPDATE tasks SET project_id = :toProjectId WHERE project_id = :fromProjectId")
    suspend fun reassignTasksToProject(fromProjectId: Long, toProjectId: Long)

    // Update date and slot together (for rescheduling)
    @Query("UPDATE tasks SET date = :date, slot = :slot WHERE id = :taskId")
    suspend fun updateDateAndSlot(taskId: Long, date: Long?, slot: ScheduleSlot?)

    // Complete/uncomplete a task, stamping (or clearing) when it happened. The two always move
    // together — a completed_at left behind on a re-opened task would export as a lie.
    @Query("UPDATE tasks SET is_completed = :isCompleted, completed_at = :completedAt WHERE id = :taskId")
    suspend fun updateCompletion(taskId: Long, isCompleted: Boolean, completedAt: Long?)

    // Set or clear a task's repeat rule (see Recurrence.serialize; null makes it a one-off again).
    @Query("UPDATE tasks SET recurrence = :rule WHERE id = :taskId")
    suspend fun updateRecurrence(taskId: Long, rule: String?)

    // Rewrite a recurring task's completed-instance set. The whole set is written at once because
    // it is stored as one delimited column — Task.withInstanceCompletion produces the new value.
    @Query("UPDATE tasks SET completed_instances = :value WHERE id = :taskId")
    suspend fun updateCompletedInstances(taskId: Long, value: String)

    // Every recurring task, completed or not. A recurring task is never globally completed — its
    // is_completed stays 0 and each instance is ticked off in completed_instances — so the Schedule
    // picks these up through the ordinary active query; this exists for export and maintenance.
    @Query("SELECT * FROM tasks WHERE recurrence IS NOT NULL AND parent_id IS NULL")
    suspend fun getRecurringTasks(): List<Task>

    // Get max slot_sort_order for a slot (for appending new tasks)
    @Query("SELECT COALESCE(MAX(slot_sort_order), -1) FROM tasks WHERE slot = :slot AND parent_id IS NULL")
    suspend fun getMaxSlotSortOrder(slot: ScheduleSlot): Int

    // Get max project_sort_order for a project (for appending new tasks)
    @Query("SELECT COALESCE(MAX(project_sort_order), -1) FROM tasks WHERE project_id = :projectId AND parent_id IS NULL")
    suspend fun getMaxProjectSortOrder(projectId: Long): Int

    // All tasks (for export)
    @Query("SELECT * FROM tasks")
    suspend fun getAll(): List<Task>

    // Clears every task for a replacing import. Children go with their parents by cascade anyway;
    // deleting the lot outright avoids depending on the order rows happen to come back in.
    @Query("DELETE FROM tasks")
    suspend fun deleteAll()
}
