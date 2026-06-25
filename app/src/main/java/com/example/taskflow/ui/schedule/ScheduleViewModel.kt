package com.example.taskflow.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.taskflow.data.model.Project
import com.example.taskflow.data.model.ScheduleSlot
import com.example.taskflow.data.model.Task
import com.example.taskflow.data.repository.ProjectRepository
import com.example.taskflow.data.repository.TaskRepository
import com.example.taskflow.domain.SlotDeriver
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** One task as the Schedule view renders it: title plus an optional date label (null = no label). */
data class ScheduleTaskUi(
    val id: Long,
    val title: String,
    val dateLabel: String?,
)

/**
 * One Project's card on Later (SPEC §Schedule view — Later grouped by Project). Holds the Project's
 * far-future dated tasks and its undated tasks; an empty card (no tasks) still renders. [isUnassigned]
 * marks the system Unassigned card, which is pinned to the bottom of the list.
 */
data class LaterProjectCard(
    val projectId: Long,
    val projectName: String,
    val isUnassigned: Boolean,
    val tasks: List<ScheduleTaskUi>,
)

/**
 * Per-slot lists for Today/Tomorrow/Soon, the Project-grouped cards for Later, plus the Today
 * completed tray. Later is not a flat list — it groups by Project (SPEC §Schedule view).
 */
data class ScheduleUiState(
    val today: List<ScheduleTaskUi> = emptyList(),
    val tomorrow: List<ScheduleTaskUi> = emptyList(),
    val soon: List<ScheduleTaskUi> = emptyList(),
    val laterCards: List<LaterProjectCard> = emptyList(),
    // Completed top-level tasks, newest-completed-first is owned by 0012; for now query order.
    // Rendered only at the bottom of Today (SPEC §Completed task tray on Today).
    val completed: List<ScheduleTaskUi> = emptyList(),
) {
    /** The flat list for a near slot. Later renders as [laterCards], so it returns empty here. */
    fun forSlot(slot: ScheduleSlot): List<ScheduleTaskUi> = when (slot) {
        ScheduleSlot.TODAY -> today
        ScheduleSlot.TOMORROW -> tomorrow
        ScheduleSlot.SOON -> soon
        ScheduleSlot.LATER -> emptyList()
    }
}

/**
 * Exposes the four Schedule slots plus the Today completed tray. Today/Tomorrow/Soon are flat,
 * date-ordered lists; Later is grouped into one expand/collapse card per Project (SPEC §Schedule
 * view), in the Project order the Strategy doc owns, with the system Unassigned card pinned last.
 *
 * Bucketing stays read-only/derived (batch 0002): each dated task's slot is derived from its date,
 * and undated tasks are placed by their parked slot (Soon stays flat; Later — including undated
 * tasks with no slot at all — flows into their Project's card). [setCompleted] flips a task's state,
 * which the active/completed queries pick up so it leaves its slot/card and joins the tray.
 *
 * [clock] and [zone] are injectable so the bucketing logic can be unit-tested without a device.
 * The date label uses DD/MM (SPEC default); batch 0013 adds the MM/DD setting.
 */
class ScheduleViewModel(
    private val repository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val clock: () -> Long = System::currentTimeMillis,
    private val zone: ZoneId = ZoneId.systemDefault(),
) : ViewModel() {

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM")

    val uiState: StateFlow<ScheduleUiState> =
        combine(
            repository.observeActiveTopLevel(),
            repository.getCompletedTasks(),
            projectRepository.getAllOrdered(),
        ) { active, completed, projects ->
            buildState(active, projects, clock()).copy(
                completed = completed.map { ScheduleTaskUi(id = it.id, title = it.title, dateLabel = null) },
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ScheduleUiState())

    /** Complete or un-complete a task — the source of both leaving a slot and joining the tray. */
    fun setCompleted(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch { repository.updateCompletion(taskId, isCompleted) }
    }

    private fun buildState(tasks: List<Task>, projects: List<Project>, now: Long): ScheduleUiState {
        val near = mutableMapOf<ScheduleSlot, MutableList<Task>>()
        val laterTasks = mutableListOf<Task>()
        for (task in tasks) {
            val date = task.date
            if (date != null) {
                // Dated task: slot derived from the date. Far-future (8+ days) flows into Later.
                val slot = SlotDeriver.slotForDate(date, now, zone)
                if (slot == ScheduleSlot.LATER) laterTasks.add(task)
                else near.getOrPut(slot) { mutableListOf() }.add(task)
            } else {
                // Undated parked task. Soon stays a flat list; everything else undated (parked on
                // Later, or never given a slot) belongs in its Project's Later card (SPEC §Data model).
                if (task.slot == ScheduleSlot.SOON) near.getOrPut(ScheduleSlot.SOON) { mutableListOf() }.add(task)
                else laterTasks.add(task)
            }
        }
        return ScheduleUiState(
            today = near[ScheduleSlot.TODAY].orderedForSlot(ScheduleSlot.TODAY, now),
            tomorrow = near[ScheduleSlot.TOMORROW].orderedForSlot(ScheduleSlot.TOMORROW, now),
            soon = near[ScheduleSlot.SOON].orderedForSlot(ScheduleSlot.SOON, now),
            laterCards = laterCards(laterTasks, projects, now),
        )
    }

    /**
     * Build the Later cards: one per user Project in the Strategy-doc order [projects], each holding
     * that Project's Later tasks (empty cards included), then the system Unassigned card pinned last.
     */
    private fun laterCards(laterTasks: List<Task>, projects: List<Project>, now: Long): List<LaterProjectCard> {
        val byProject: Map<Long, List<Task>> = laterTasks.groupBy { it.projectId }
        val userCards = projects.map { project ->
            LaterProjectCard(
                projectId = project.id,
                projectName = project.name,
                isUnassigned = false,
                tasks = byProject[project.id].orderedForCard(now),
            )
        }
        val unassignedCard = LaterProjectCard(
            projectId = Project.UNASSIGNED_PROJECT_ID,
            projectName = Project.UNASSIGNED_PROJECT_NAME,
            isUnassigned = true,
            tasks = byProject[Project.UNASSIGNED_PROJECT_ID].orderedForCard(now),
        )
        return userCards + unassignedCard
    }

    private fun List<Task>?.orderedForSlot(slot: ScheduleSlot, now: Long): List<ScheduleTaskUi> {
        if (this == null) return emptyList()
        // Stable order: stored slot_sort_order first (the query already sorts by it), with date
        // then id as tiebreakers so tasks pulled into Today from different origin slots have a
        // deterministic order. The precise append-to-bottom-on-rollover order is owned by 0012.
        return this
            .sortedWith(compareBy({ it.slotSortOrder }, { it.date ?: Long.MIN_VALUE }, { it.id }))
            .map { task -> task.toUi(slot, now) }
    }

    /** A card's own task order: project_sort_order, with id as a stable tiebreaker. */
    private fun List<Task>?.orderedForCard(now: Long): List<ScheduleTaskUi> {
        if (this == null) return emptyList()
        return this
            .sortedWith(compareBy({ it.projectSortOrder }, { it.id }))
            .map { task -> task.toUi(ScheduleSlot.LATER, now) }
    }

    private fun Task.toUi(slot: ScheduleSlot, now: Long): ScheduleTaskUi =
        ScheduleTaskUi(
            id = id,
            title = title,
            dateLabel = dateLabelFor(this, slot, now),
        )

    /**
     * Soon/Later rows show their date when they have one. Tomorrow rows show no date: a Tomorrow
     * task always carries exactly tomorrow's date, so the label is redundant with the page title.
     * Today rows show a date only when it has slipped into the past — the only signal that a task is
     * "still there" from a past day, with no overdue label or colour (UX principle 4). On Later
     * cards, undated tasks carry no date (null) and far-future dated tasks show their DD/MM date.
     */
    private fun dateLabelFor(task: Task, slot: ScheduleSlot, now: Long): String? {
        val date = task.date ?: return null
        val show = when (slot) {
            ScheduleSlot.TODAY -> SlotDeriver.isBeforeToday(date, now, zone)
            ScheduleSlot.TOMORROW -> false
            else -> true
        }
        if (!show) return null
        return Instant.ofEpochMilli(date).atZone(zone).toLocalDate().format(dateFormatter)
    }

    companion object {
        fun factory(repository: TaskRepository, projectRepository: ProjectRepository) = viewModelFactory {
            initializer { ScheduleViewModel(repository, projectRepository) }
        }
    }
}
