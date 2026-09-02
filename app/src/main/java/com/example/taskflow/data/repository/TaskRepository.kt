package com.example.taskflow.data.repository

import com.example.taskflow.data.local.TaskDao
import com.example.taskflow.data.model.ScheduleSlot
import com.example.taskflow.data.model.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    suspend fun insert(task: Task): Long = taskDao.insert(task)

    suspend fun update(task: Task) = taskDao.update(task)

    suspend fun delete(task: Task) = taskDao.delete(task)

    suspend fun getById(id: Long): Task? = taskDao.getById(id)

    fun observeById(id: Long): Flow<Task?> = taskDao.observeById(id)

    fun observeActiveTopLevel(): Flow<List<Task>> = taskDao.observeActiveTopLevel()

    fun getTasksBySlot(slot: ScheduleSlot): Flow<List<Task>> = taskDao.getTasksBySlot(slot)

    fun getTasksByDate(date: Long): Flow<List<Task>> = taskDao.getTasksByDate(date)

    fun getDatedTasksByProject(projectId: Long): Flow<List<Task>> =
        taskDao.getDatedTasksByProject(projectId)

    fun getUndatedTasksByProject(projectId: Long): Flow<List<Task>> =
        taskDao.getUndatedTasksByProject(projectId)

    fun getSubtasks(parentId: Long): Flow<List<Task>> = taskDao.getSubtasks(parentId)

    fun observeAllSubtasks(): Flow<List<Task>> = taskDao.observeAllSubtasks()

    /**
     * Completes or un-completes one subtask and rolls the result up to its parent (SPEC §Parent
     * tasks expand/collapse instead of having a checkbox). A parent is complete exactly when all of
     * its children are, so its state is recomputed here rather than set by the user: completing the
     * last child completes the parent and sends it to the Completed tray, and un-completing any
     * child brings the parent back out of it.
     */
    suspend fun setSubtaskCompleted(subtaskId: Long, isCompleted: Boolean) {
        val subtask = taskDao.getById(subtaskId) ?: return
        val now = System.currentTimeMillis()
        taskDao.updateCompletion(subtaskId, isCompleted, if (isCompleted) now else null)
        val parentId = subtask.parentId ?: return
        val siblings = taskDao.getSubtasksList(parentId)
        val allDone = siblings.all { if (it.id == subtaskId) isCompleted else it.isCompleted }
        // The parent's stamp is the moment its last child was ticked, which is when the parent
        // actually became complete.
        taskDao.updateCompletion(parentId, allDone, if (allDone) now else null)
    }

    suspend fun getSubtasksList(parentId: Long): List<Task> = taskDao.getSubtasksList(parentId)

    fun getCompletedTasks(): Flow<List<Task>> = taskDao.getCompletedTasks()

    suspend fun updateSlotSortOrder(taskId: Long, newOrder: Int) =
        taskDao.updateSlotSortOrder(taskId, newOrder)

    suspend fun updateProjectSortOrder(taskId: Long, newOrder: Int) =
        taskDao.updateProjectSortOrder(taskId, newOrder)

    suspend fun updateProjectId(taskId: Long, projectId: Long?) =
        taskDao.updateProjectId(taskId, projectId)

    suspend fun updateDateAndSlot(taskId: Long, date: Long?, slot: ScheduleSlot?) =
        taskDao.updateDateAndSlot(taskId, date, slot)

    /**
     * Complete or un-complete a task, stamping the moment it happened (SPEC §JSON export and
     * import needs the date, not just the fact). Un-completing clears the stamp rather than
     * leaving a stale one behind.
     */
    suspend fun updateCompletion(taskId: Long, isCompleted: Boolean) =
        taskDao.updateCompletion(
            taskId,
            isCompleted,
            if (isCompleted) System.currentTimeMillis() else null,
        )

    suspend fun updateRecurrence(taskId: Long, rule: String?) =
        taskDao.updateRecurrence(taskId, rule)

    suspend fun getRecurringTasks(): List<Task> = taskDao.getRecurringTasks()

    /**
     * Completes or un-completes one instance of a recurring task, leaving every other instance
     * alone (SPEC §Recurring tasks). Reads the row, rewrites its completed-instance set, and writes
     * just that column back; a task with no repeat rule is ignored, since its completion is the
     * plain is_completed flag.
     */
    suspend fun setInstanceCompleted(taskId: Long, instanceDate: java.time.LocalDate, isCompleted: Boolean) {
        val task = taskDao.getById(taskId) ?: return
        if (task.recurrence == null) return
        val updated = task.withInstanceCompletion(instanceDate, isCompleted)
        taskDao.updateCompletedInstances(taskId, updated.completedInstances)
    }

    suspend fun getMaxSlotSortOrder(slot: ScheduleSlot): Int =
        taskDao.getMaxSlotSortOrder(slot)

    suspend fun getMaxProjectSortOrder(projectId: Long): Int =
        taskDao.getMaxProjectSortOrder(projectId)

    suspend fun getAll(): List<Task> = taskDao.getAll()
}
