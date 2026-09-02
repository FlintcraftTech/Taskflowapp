package com.example.taskflow.data.transfer

import com.example.taskflow.data.local.ProjectDao
import com.example.taskflow.data.local.StrategyEntryDao
import com.example.taskflow.data.local.TaskDao
import com.example.taskflow.data.model.Project
import com.example.taskflow.data.model.Task

/**
 * JSON export and the two imports (SPEC §JSON export and import).
 *
 * The **replacing** import and the **additive** one are separate operations with separate names,
 * not one operation with a switch: one of them destroys the user's data and the other cannot, and
 * a checkbox beside a destructive action is how people lose their task list.
 */
class TransferRepository(
    private val taskDao: TaskDao,
    private val projectDao: ProjectDao,
    private val strategyEntryDao: StrategyEntryDao,
) {

    /** The whole database as export JSON. */
    suspend fun exportJson(): String = TaskflowJson.encode(
        TaskflowExport(
            projects = projectDao.getAllIncludingSystem(),
            tasks = taskDao.getAll(),
            strategy = strategyEntryDao.getAllOrderedList(),
        ),
    )

    /**
     * Replace everything with the file's contents. Destructive by design — the user is warned
     * before this runs. The system Unassigned Project is re-seeded rather than imported blindly, so
     * a file that somehow lacks it still leaves every task with a Project to belong to.
     */
    suspend fun importReplacing(json: String) {
        val export = TaskflowJson.decode(json)
        taskDao.deleteAll()
        projectDao.deleteAllUserProjects()
        // Projects first: the tasks→projects foreign key needs the parent rows present.
        export.projects.filterNot { it.isSystem }.forEach { projectDao.insert(it) }
        // Parents before children, so the tasks→tasks foreign key resolves.
        val (parents, children) = export.tasks.partition { it.parentId == null }
        parents.forEach { taskDao.insert(it) }
        children.forEach { taskDao.insert(it) }
        export.strategy.forEach { strategyEntryDao.upsert(it) }
    }

    /**
     * Add the file's tasks to what is already here, changing nothing that exists. Each incoming
     * task is filed into the Project named in the file, that Project created if it is missing.
     *
     * A task resembling one already present is inserted anyway rather than de-duplicated: a
     * duplicate is an annoyance the user deletes in one gesture, while a wrongly-skipped task is
     * work that silently never arrived. That was settled in planning on 2026-08-25 and it is why
     * this path shows no warning — nothing is lost by running it.
     *
     * Ids are dropped on the way in, so an incoming task cannot overwrite an existing row that
     * happens to share its id.
     */
    suspend fun importAdding(json: String) {
        val export = TaskflowJson.decode(json)
        val existingByName = projectDao.getAllIncludingSystem().associateBy { it.name }
        val projectIdForIncoming = mutableMapOf<Long, Long>()

        export.projects.forEach { incoming ->
            val existing = existingByName[incoming.name]
            projectIdForIncoming[incoming.id] = if (existing != null) {
                existing.id
            } else {
                projectDao.insert(
                    Project(
                        name = incoming.name,
                        sortOrder = projectDao.getMaxSortOrder() + 1,
                        isSystem = false,
                    ),
                )
            }
        }

        // Parents first, so a child can be pointed at its parent's newly assigned id.
        val newIdForIncoming = mutableMapOf<Long, Long>()
        val (parents, children) = export.tasks.partition { it.parentId == null }
        (parents + children).forEach { incoming ->
            val newId = taskDao.insert(
                incoming.copy(
                    id = 0,
                    projectId = projectIdForIncoming[incoming.projectId]
                        ?: Project.UNASSIGNED_PROJECT_ID,
                    parentId = incoming.parentId?.let { newIdForIncoming[it] },
                ),
            )
            newIdForIncoming[incoming.id] = newId
        }
    }
}
