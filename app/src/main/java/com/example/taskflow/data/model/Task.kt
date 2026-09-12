package com.example.taskflow.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["project_id"],
            // project_id is non-null (every task belongs to a Project — its own, or the system
            // Unassigned one), so SET_NULL is invalid. Deleting a real Project reassigns its tasks
            // to Unassigned in ProjectRepository before the delete; RESTRICT is the backstop that
            // surfaces a bug loudly if that reassignment is ever skipped, rather than orphaning rows.
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["parent_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("project_id"),
        Index("parent_id"),
        Index("slot"),
        Index("date")
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,

    val notes: String = "",

    // Every task belongs to exactly one Project — never null. Defaults to the system Unassigned
    // Project (see Project.UNASSIGNED_PROJECT_ID) until the user files it into one of their own.
    @ColumnInfo(name = "project_id")
    val projectId: Long = Project.UNASSIGNED_PROJECT_ID,

    val date: Long? = null,

    val slot: ScheduleSlot? = null,

    @ColumnInfo(name = "parent_id")
    val parentId: Long? = null,

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,

    // When the task was completed, or null while it is not. Kept because an export has to carry it
    // (SPEC §JSON export and import) — without it, anything reading an export can tell that work
    // was finished but never when, which is most of what a completion is worth knowing. Cleared on
    // un-completion, so it never describes a task that is open again.
    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null,

    @ColumnInfo(name = "project_suggestion_declined")
    val projectSuggestionDeclined: Boolean = false,

    @ColumnInfo(name = "slot_sort_order")
    val slotSortOrder: Int = 0,

    @ColumnInfo(name = "project_sort_order")
    val projectSortOrder: Int = 0,

    // A repeat rule in the form Recurrence.serialize() writes, or null for a one-off task. The
    // task's own `date` is the anchor the rule counts from, so a recurring task is always dated.
    // Instances are derived from this rather than stored as rows — see Recurrence's class comment.
    @ColumnInfo(name = "recurrence")
    val recurrence: String? = null,

    // Which instance dates of a recurring task the user has already completed, as comma-separated
    // ISO dates (2026-08-31,2026-09-07). Completing one instance adds one entry here and leaves the
    // rest of the tail untouched (SPEC §Recurring tasks). Meaningless — and always empty — on a
    // one-off task, whose completion is the plain `is_completed` flag.
    @ColumnInfo(name = "completed_instances")
    val completedInstances: String = "",

    // An ordered roster for a recurring task: one label per line, empty for a task with no rotation
    // (SPEC §Recurring tasks). Where it has one, each instance shows the task's title with the next
    // name from the list, and the list advances when an instance is completed rather than when its
    // date passes — so a week the user does not get to costs that occasion but not that person's
    // turn. The roster lives on the task rather than on the Project: a Project is an area of the
    // user's life, not a thing with members.
    @ColumnInfo(name = "roster", defaultValue = "")
    val roster: String = ""
) {
    /** The roster's labels, in order. Empty for a task with no rotation. */
    val rosterLabels: List<String>
        get() = roster.lines().map { it.trim() }.filter { it.isNotEmpty() }

    /** The completed instance dates, parsed. Empty for a one-off task. */
    val completedInstanceDates: Set<java.time.LocalDate>
        get() = completedInstances.split(',')
            .filter { it.isNotBlank() }
            .mapNotNull { runCatching { java.time.LocalDate.parse(it.trim()) }.getOrNull() }
            .toSet()

    /** This task with [date] added to or removed from its completed-instance set. */
    fun withInstanceCompletion(date: java.time.LocalDate, isCompleted: Boolean): Task {
        val updated = if (isCompleted) completedInstanceDates + date else completedInstanceDates - date
        return copy(completedInstances = updated.sorted().joinToString(",") { it.toString() })
    }
}
