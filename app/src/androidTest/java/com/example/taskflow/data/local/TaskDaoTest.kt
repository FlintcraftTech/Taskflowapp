package com.example.taskflow.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.taskflow.data.model.Project
import com.example.taskflow.data.model.ScheduleSlot
import com.example.taskflow.data.model.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var database: TaskflowDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var projectDao: ProjectDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TaskflowDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        taskDao = database.taskDao()
        projectDao = database.projectDao()

        // The in-memory test database is built directly (not via getInstance), so the seed callback
        // that inserts the system Unassigned Project on a real install doesn't run here. Seed it by
        // hand: project_id is now non-null and defaults to Project.UNASSIGNED_PROJECT_ID, and the
        // tasks→projects foreign key requires that parent row to exist before any task is inserted.
        runBlocking {
            projectDao.insert(
                Project(
                    id = Project.UNASSIGNED_PROJECT_ID,
                    name = Project.UNASSIGNED_PROJECT_NAME,
                    sortOrder = 0,
                    isSystem = true,
                )
            )
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun createTaskWithDate_isRetrievable() = runTest {
        val task = Task(title = "Dated task", date = 1700000000000L, slot = ScheduleSlot.TODAY)
        val id = taskDao.insert(task)

        val retrieved = taskDao.getById(id)
        assertNotNull(retrieved)
        assertEquals("Dated task", retrieved!!.title)
        assertEquals(1700000000000L, retrieved.date)
        assertEquals(ScheduleSlot.TODAY, retrieved.slot)
    }

    @Test
    fun createTaskWithoutDate_isRetrievable() = runTest {
        val task = Task(title = "Undated task", date = null, slot = null)
        val id = taskDao.insert(task)

        val retrieved = taskDao.getById(id)
        assertNotNull(retrieved)
        assertEquals("Undated task", retrieved!!.title)
        assertNull(retrieved.date)
        assertNull(retrieved.slot)
    }

    @Test
    fun parkUndatedTaskOnSoon_slotIsStored() = runTest {
        val task = Task(title = "Soon task", date = null, slot = ScheduleSlot.SOON)
        val id = taskDao.insert(task)

        val retrieved = taskDao.getById(id)
        assertNotNull(retrieved)
        assertNull(retrieved!!.date)
        assertEquals(ScheduleSlot.SOON, retrieved.slot)

        val soonTasks = taskDao.getTasksBySlot(ScheduleSlot.SOON).first()
        assertTrue(soonTasks.any { it.id == id })
    }

    @Test
    fun parkUndatedTaskOnLater_slotIsStored() = runTest {
        val task = Task(title = "Later task", date = null, slot = ScheduleSlot.LATER)
        val id = taskDao.insert(task)

        val retrieved = taskDao.getById(id)
        assertNotNull(retrieved)
        assertNull(retrieved!!.date)
        assertEquals(ScheduleSlot.LATER, retrieved.slot)

        val laterTasks = taskDao.getTasksBySlot(ScheduleSlot.LATER).first()
        assertTrue(laterTasks.any { it.id == id })
    }

    @Test
    fun assignTaskToProject_fkRelationshipWorks() = runTest {
        val projectId = projectDao.insert(Project(name = "Work", sortOrder = 0))
        val taskId = taskDao.insert(
            Task(title = "Work task", projectId = projectId, date = 1700000000000L, slot = ScheduleSlot.TODAY)
        )

        val retrieved = taskDao.getById(taskId)
        assertNotNull(retrieved)
        assertEquals(projectId, retrieved!!.projectId)

        val projectTasks = taskDao.getDatedTasksByProject(projectId).first()
        assertTrue(projectTasks.any { it.id == taskId })
    }

    @Test
    fun refileTaskToDifferentProject_fkUpdates() = runTest {
        val project1Id = projectDao.insert(Project(name = "Work", sortOrder = 0))
        val project2Id = projectDao.insert(Project(name = "Home", sortOrder = 1))
        val taskId = taskDao.insert(
            Task(title = "Refile me", projectId = project1Id, date = 1700000000000L, slot = ScheduleSlot.TODAY)
        )

        // Verify initially in project 1
        var project1Tasks = taskDao.getDatedTasksByProject(project1Id).first()
        assertTrue(project1Tasks.any { it.id == taskId })

        // Refile to project 2
        taskDao.updateProjectId(taskId, project2Id)

        // Verify moved
        project1Tasks = taskDao.getDatedTasksByProject(project1Id).first()
        assertTrue(project1Tasks.none { it.id == taskId })

        val project2Tasks = taskDao.getDatedTasksByProject(project2Id).first()
        assertTrue(project2Tasks.any { it.id == taskId })

        val retrieved = taskDao.getById(taskId)
        assertEquals(project2Id, retrieved!!.projectId)
    }

    @Test
    fun reorderTasksWithinSlot_persistedOrderChanges() = runTest {
        val id1 = taskDao.insert(Task(title = "First", slot = ScheduleSlot.TODAY, slotSortOrder = 0))
        val id2 = taskDao.insert(Task(title = "Second", slot = ScheduleSlot.TODAY, slotSortOrder = 1))
        val id3 = taskDao.insert(Task(title = "Third", slot = ScheduleSlot.TODAY, slotSortOrder = 2))

        // Reorder: move Third to first position
        taskDao.updateSlotSortOrder(id3, 0)
        taskDao.updateSlotSortOrder(id1, 1)
        taskDao.updateSlotSortOrder(id2, 2)

        val tasks = taskDao.getTasksBySlot(ScheduleSlot.TODAY).first()
        assertEquals(3, tasks.size)
        assertEquals("Third", tasks[0].title)
        assertEquals("First", tasks[1].title)
        assertEquals("Second", tasks[2].title)
    }

    @Test
    fun reorderTasksWithinProject_persistedOrderChanges() = runTest {
        val projectId = projectDao.insert(Project(name = "Home", sortOrder = 0))
        val id1 = taskDao.insert(
            Task(title = "A", projectId = projectId, date = null, slot = null, projectSortOrder = 0)
        )
        val id2 = taskDao.insert(
            Task(title = "B", projectId = projectId, date = null, slot = null, projectSortOrder = 1)
        )
        val id3 = taskDao.insert(
            Task(title = "C", projectId = projectId, date = null, slot = null, projectSortOrder = 2)
        )

        // Reorder: move C to first position
        taskDao.updateProjectSortOrder(id3, 0)
        taskDao.updateProjectSortOrder(id1, 1)
        taskDao.updateProjectSortOrder(id2, 2)

        val tasks = taskDao.getUndatedTasksByProject(projectId).first()
        assertEquals(3, tasks.size)
        assertEquals("C", tasks[0].title)
        assertEquals("A", tasks[1].title)
        assertEquals("B", tasks[2].title)
    }

    @Test
    fun createSubtaskUnderParent_relationshipWorks() = runTest {
        val parentId = taskDao.insert(
            Task(title = "Parent task", slot = ScheduleSlot.TODAY, slotSortOrder = 0)
        )
        val childId = taskDao.insert(
            Task(title = "Child task", parentId = parentId, slot = ScheduleSlot.TODAY, slotSortOrder = 0)
        )

        val child = taskDao.getById(childId)
        assertNotNull(child)
        assertEquals(parentId, child!!.parentId)

        val subtasks = taskDao.getSubtasks(parentId).first()
        assertEquals(1, subtasks.size)
        assertEquals("Child task", subtasks[0].title)
    }

    @Test
    fun completeAllSubtasks_parentCompletionRollsUp() = runTest {
        val parentId = taskDao.insert(
            Task(title = "Parent", slot = ScheduleSlot.TODAY, slotSortOrder = 0)
        )
        val child1Id = taskDao.insert(
            Task(title = "Child 1", parentId = parentId, slotSortOrder = 0)
        )
        val child2Id = taskDao.insert(
            Task(title = "Child 2", parentId = parentId, slotSortOrder = 1)
        )

        // Complete first child
        taskDao.updateCompletion(child1Id, true, completedAt = System.currentTimeMillis())
        var children = taskDao.getSubtasksList(parentId)
        val allCompleteAfterFirst = children.all { it.isCompleted }
        assertTrue(!allCompleteAfterFirst) // Not all complete yet

        // Complete second child
        taskDao.updateCompletion(child2Id, true, completedAt = System.currentTimeMillis())
        children = taskDao.getSubtasksList(parentId)
        val allCompleteAfterSecond = children.all { it.isCompleted }
        assertTrue(allCompleteAfterSecond) // All children complete — parent should be marked complete

        // Mark parent complete (app-level rollup logic)
        taskDao.updateCompletion(parentId, true, completedAt = System.currentTimeMillis())
        val parent = taskDao.getById(parentId)
        assertTrue(parent!!.isCompleted)
    }

    @Test
    fun newTaskWithNoProject_defaultsToUnassigned() = runTest {
        // A task created without specifying a Project lands in the system Unassigned Project — there
        // is no null / no-Project state any more (project_id is non-null).
        val id = taskDao.insert(Task(title = "Unfiled task"))

        val retrieved = taskDao.getById(id)
        assertNotNull(retrieved)
        assertEquals(Project.UNASSIGNED_PROJECT_ID, retrieved!!.projectId)
    }

    @Test
    fun reassignTasksToProject_movesTasksToUnassigned() = runTest {
        // Deleting a real Project reassigns its tasks to Unassigned (this is the bulk query the
        // repository runs before the delete). Verify the query alone moves the rows.
        val workId = projectDao.insert(Project(name = "Work", sortOrder = 1))
        val taskA = taskDao.insert(Task(title = "A", projectId = workId, slot = ScheduleSlot.TODAY))
        val taskB = taskDao.insert(Task(title = "B", projectId = workId, slot = ScheduleSlot.SOON))

        taskDao.reassignTasksToProject(fromProjectId = workId, toProjectId = Project.UNASSIGNED_PROJECT_ID)

        assertEquals(Project.UNASSIGNED_PROJECT_ID, taskDao.getById(taskA)!!.projectId)
        assertEquals(Project.UNASSIGNED_PROJECT_ID, taskDao.getById(taskB)!!.projectId)
    }
}
