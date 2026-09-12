package com.example.taskflow.data.transfer

import com.example.taskflow.data.model.Project
import com.example.taskflow.data.model.ScheduleSlot
import com.example.taskflow.data.model.StrategyEntry
import com.example.taskflow.data.model.Task
import org.json.JSONArray
import org.json.JSONObject

/** A whole database, in the shape an export file carries. */
data class TaskflowExport(
    val projects: List<Project>,
    val tasks: List<Task>,
    val strategy: List<StrategyEntry>,
)

/**
 * Reads and writes the JSON export format (SPEC §JSON export and import).
 *
 * Hand-rolled over `org.json` rather than a serialization library: `org.json` ships with Android,
 * the format is three flat arrays, and adding a plugin and its codegen to the build for that would
 * be more machinery than the problem has.
 *
 * The format is deliberately explicit about what it carries. **Every exported task carries its
 * completion state and the date it was completed**, and a parent's is written as the value rolled
 * up from its children with the children's own states exported alongside — settled in planning on
 * 2026-08-25. Without completion state, anything reading an export can put work in but never learn
 * what happened to it.
 *
 * Unknown fields in a file are ignored and missing ones take their defaults, so a file written by
 * an older version still imports.
 */
object TaskflowJson {

    private const val VERSION = 1

    fun encode(export: TaskflowExport): String {
        val root = JSONObject()
        root.put("version", VERSION)

        val projects = JSONArray()
        export.projects.forEach { project ->
            projects.put(
                JSONObject()
                    .put("id", project.id)
                    .put("name", project.name)
                    .put("sortOrder", project.sortOrder)
                    .put("isSystem", project.isSystem),
            )
        }
        root.put("projects", projects)

        // A task's completion is rolled up from its children before export where it has any, so a
        // reader gets the derived truth rather than having to recompute it (and the children are
        // right here in the same array if it wants to).
        val childrenByParent = export.tasks.groupBy { it.parentId }
        val tasks = JSONArray()
        export.tasks.forEach { task ->
            val children = childrenByParent[task.id].orEmpty()
            val completed = if (children.isEmpty()) task.isCompleted else children.all { it.isCompleted }
            tasks.put(
                JSONObject()
                    .put("id", task.id)
                    .put("title", task.title)
                    .put("notes", task.notes)
                    .put("projectId", task.projectId)
                    .put("date", task.date ?: JSONObject.NULL)
                    .put("slot", task.slot?.name ?: JSONObject.NULL)
                    .put("parentId", task.parentId ?: JSONObject.NULL)
                    .put("isCompleted", completed)
                    .put("completedAt", task.completedAt ?: JSONObject.NULL)
                    .put("completedInstances", task.completedInstances)
                    .put("recurrence", task.recurrence ?: JSONObject.NULL)
                    .put("roster", task.roster)
                    .put("projectSuggestionDeclined", task.projectSuggestionDeclined)
                    .put("slotSortOrder", task.slotSortOrder)
                    .put("projectSortOrder", task.projectSortOrder),
            )
        }
        root.put("tasks", tasks)

        val strategy = JSONArray()
        export.strategy.forEach { entry ->
            strategy.put(
                JSONObject()
                    .put("projectId", entry.projectId)
                    .put("content", entry.content),
            )
        }
        root.put("strategy", strategy)

        return root.toString(2)
    }

    /** Parses an export file. Throws [org.json.JSONException] if the text is not one. */
    fun decode(text: String): TaskflowExport {
        val root = JSONObject(text)
        return TaskflowExport(
            projects = root.optJSONArray("projects").objects().map { it.toProject() },
            tasks = root.optJSONArray("tasks").objects().map { it.toTask() },
            strategy = root.optJSONArray("strategy").objects().map { it.toStrategyEntry() },
        )
    }

    private fun JSONArray?.objects(): List<JSONObject> {
        if (this == null) return emptyList()
        return (0 until length()).mapNotNull { optJSONObject(it) }
    }

    private fun JSONObject.toProject(): Project = Project(
        id = optLong("id", 0L),
        name = optString("name", ""),
        sortOrder = optInt("sortOrder", 0),
        isSystem = optBoolean("isSystem", false),
    )

    private fun JSONObject.toTask(): Task = Task(
        id = optLong("id", 0L),
        title = optString("title", ""),
        notes = optString("notes", ""),
        projectId = optLong("projectId", Project.UNASSIGNED_PROJECT_ID),
        date = if (isNull("date")) null else optLong("date"),
        slot = optString("slot", "").let { name ->
            ScheduleSlot.entries.firstOrNull { it.name == name }
        },
        parentId = if (isNull("parentId")) null else optLong("parentId"),
        isCompleted = optBoolean("isCompleted", false),
        completedAt = if (isNull("completedAt")) null else optLong("completedAt"),
        completedInstances = optString("completedInstances", ""),
        recurrence = if (isNull("recurrence")) null else optString("recurrence").takeIf { it.isNotBlank() },
        // Absent in an export taken before rotations existed, which reads as a task with no roster.
        roster = optString("roster", ""),
        projectSuggestionDeclined = optBoolean("projectSuggestionDeclined", false),
        slotSortOrder = optInt("slotSortOrder", 0),
        projectSortOrder = optInt("projectSortOrder", 0),
    )

    private fun JSONObject.toStrategyEntry(): StrategyEntry = StrategyEntry(
        projectId = optLong("projectId", 0L),
        content = optString("content", ""),
    )
}
