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
import com.example.taskflow.data.settings.SettingsRepository
import com.example.taskflow.data.settings.TaskflowSettings
import com.example.taskflow.domain.Recurrence
import com.example.taskflow.domain.SlotDeriver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * One row as the Schedule view renders it: title plus an optional date label (null = no label).
 *
 * [instanceDate] is set only for an instance of a recurring task, and is the day that instance
 * falls on. A recurring task is one database row that renders as many rows, so the id alone no
 * longer identifies what the user tapped — completing "this Monday" has to say *which* Monday
 * (SPEC §Recurring tasks). [key] is what list rendering and completion both address a row by.
 */
data class ScheduleTaskUi(
    val id: Long,
    val title: String,
    val dateLabel: String?,
    val instanceDate: LocalDate? = null,
    val isRecurring: Boolean = false,
    // A parent's children, in their own order. Empty on an ordinary task. A parent renders an
    // expand/collapse control in place of a checkbox and its children nest beneath it on whichever
    // surface the parent lives on (SPEC §Subtasks live under their parent).
    val subtasks: List<ScheduleTaskUi> = emptyList(),
    val isCompleted: Boolean = false,
) {
    val key: String get() = if (instanceDate == null) "$id" else "$id@$instanceDate"

    /** A parent is a task with children — the one thing that swaps its checkbox for a chevron. */
    val isParent: Boolean get() = subtasks.isNotEmpty()
}

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
    /**
     * How many of this Project's tasks are living on a near-term Schedule slot rather than in the
     * card. Read only by the empty-state copy: a card can be empty because the Project has nothing
     * in it, or because everything in it is dated for the next few days and sits on the schedule,
     * and "nothing in here yet" is false in the second case.
     *
     * It is deliberately **not** rendered on a card that has tasks. Putting a count on every card
     * would change what every card shows, against SPEC §Schedule view's statement that the small
     * peek is what keeps Later a calm overview rather than a wall of tasks.
     */
    val nearTermTaskCount: Int = 0,
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
 * tasks with no slot at all — flows into their Project's card).
 *
 * A **recurring** task is the one row that renders as several: its rule is expanded here into every
 * instance falling inside the next [Recurrence.HORIZON_DAYS] days, and each instance is bucketed by
 * its own date like any other dated task (SPEC §Recurring tasks). The cap applies to generated
 * instances only — a manually dated one-off task is never capped and still shows in Later however
 * far out it is. Completion is per instance: [setCompleted] carries the instance date, so ticking
 * one Monday leaves the rest of the tail alone.
 *
 * [clock] and [zone] are injectable so the bucketing logic can be unit-tested without a device.
 * The date label uses DD/MM (SPEC default); batch 0013 adds the MM/DD setting.
 */
class ScheduleViewModel(
    private val repository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val settingsRepository: SettingsRepository,
    private val clock: () -> Long = System::currentTimeMillis,
    private val zone: ZoneId = ZoneId.systemDefault(),
) : ViewModel() {

    /** The settings in force right now. Read synchronously where a callback needs them mid-action. */
    private fun currentSettings(): TaskflowSettings = settingsRepository.read()

    private fun formatter(settings: TaskflowSettings): DateTimeFormatter =
        DateTimeFormatter.ofPattern(settings.dateFormat.pattern)

    /**
     * The settings, re-emitted each time the day-begins-at boundary passes.
     *
     * Slot placement is derived from dates against "today", so when the boundary passes, Tomorrow's
     * tasks become Today's with nothing in the database changing (SPEC §Schedule view). Without
     * this the screen would sit on yesterday's buckets until something else happened to redraw it —
     * a task left on Tomorrow at 4 AM with the app still open. Coming back to the app is covered
     * separately: collection restarts on resume, which re-reads the clock.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val settingsWithBoundary: Flow<TaskflowSettings> =
        settingsRepository.settings.flatMapLatest { settings ->
            flow {
                while (true) {
                    emit(settings)
                    delay(millisUntilNextBoundary(settings.dayBeginsAtHour))
                }
            }
        }

    /**
     * The Project the user is temporarily focused on, or null (SPEC §Focus on one Project
     * temporarily). Held in memory and never written anywhere: focus is a this-afternoon thing, and
     * a lens that cannot survive a relaunch cannot become how the user lives in the app — which is
     * what makes it acceptable beside UX principle 3, where a persistent filter would not be.
     *
     * The caller owns the value and passes it in, because capture also needs it: a task added while
     * focused belongs to the focused Project.
     */
    private val focusedProjectId = MutableStateFlow<Long?>(null)

    fun setFocusedProject(projectId: Long?) {
        focusedProjectId.value = projectId
    }

    val uiState: StateFlow<ScheduleUiState> =
        combine(
            repository.observeActiveTopLevel(),
            repository.getCompletedTasks(),
            projectRepository.getAllOrdered(),
            repository.observeAllSubtasks(),
            combine(settingsWithBoundary, focusedProjectId) { settings, focus -> settings to focus },
        ) { active, completed, projects, subtasks, settingsAndFocus ->
            val (settings, focus) = settingsAndFocus
            val now = clock()
            val childrenByParent = subtasks.groupBy { it.parentId }
            // Focus filters the flat near-term slots only. Later is left alone — it is already
            // organised by area, so filtering it would leave one card on a page of cards.
            val visible = if (focus == null) active else active.filter { it.projectId == focus }
            buildState(active, projects, now, childrenByParent, settings, focus).copy(
                completed = completedTray(visible, completed, now, childrenByParent, settings, focus),
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ScheduleUiState())

    /** How long until the next day-begins-at boundary, always at least a second away. */
    private fun millisUntilNextBoundary(dayStartHour: Int): Long {
        val now = Instant.ofEpochMilli(clock()).atZone(zone)
        val todayBoundary = now.toLocalDate().atTime(dayStartHour, 0).atZone(zone)
        val next = if (now.isBefore(todayBoundary)) todayBoundary else todayBoundary.plusDays(1)
        return (next.toInstant().toEpochMilli() - clock()).coerceAtLeast(1_000L)
    }

    /**
     * Complete or un-complete what the user tapped. For an ordinary task that is the row's own
     * completion flag — the source of both leaving a slot and joining the tray. For an instance of
     * a recurring task it is that one date being ticked off, with every other instance untouched.
     */
    fun setCompleted(taskId: Long, instanceDate: LocalDate?, isCompleted: Boolean) {
        viewModelScope.launch {
            if (instanceDate != null) {
                repository.setInstanceCompleted(taskId, instanceDate, isCompleted)
                return@launch
            }
            // A subtask's completion rolls up to its parent rather than standing alone, so it goes
            // through the repository's roll-up path (SPEC §Parent tasks expand/collapse).
            val task = repository.getById(taskId)
            if (task?.parentId != null) {
                repository.setSubtaskCompleted(taskId, isCompleted)
            } else {
                repository.updateCompletion(taskId, isCompleted)
            }
        }
    }

    /**
     * Persist a new order for one Schedule slot (SPEC §Reorder within a Schedule slot). The caller
     * hands over the task ids in their new order — the drag primitive works in rows, and the page
     * has already folded a recurring task's several rows back down to one id per task.
     *
     * The whole visible list is rewritten to 0..n-1 rather than the moved row alone being nudged:
     * sort positions drift as tasks arrive, leave and are pulled into Today from other slots, so
     * writing the sequence outright is what makes the order the user just set the order they get.
     */
    fun reorderSlot(orderedIds: List<Long>) {
        viewModelScope.launch {
            orderedIds.forEachIndexed { index, id -> repository.updateSlotSortOrder(id, index) }
        }
    }

    /**
     * The same, for the task order inside one Later Project card. The ids identify their own
     * Project, so no Project argument is needed — the card's rows all belong to one.
     */
    fun reorderCard(orderedIds: List<Long>) {
        viewModelScope.launch {
            orderedIds.forEachIndexed { index, id -> repository.updateProjectSortOrder(id, index) }
        }
    }

    /**
     * Drop a dragged task onto a different Schedule slot (SPEC §Drag a task between Schedule
     * screens). Rescheduling is a date change, not a stored-slot change, because the Schedule
     * derives placement from dates — so landing on Today writes today's date, Tomorrow tomorrow's,
     * Soon two days out and Later eight, which are the first dates that fall inside each slot.
     *
     * The one exception is an undated task: it stays undated and parks on the new slot instead,
     * since giving it a date would be the app deciding a commitment the user never made. Parking is
     * only meaningful on Soon and Later (SPEC §Data model), so dropping an undated task on Today or
     * Tomorrow does date it — that is what those pages mean.
     *
     * A parent's children travel with it as one unit. They carry no date or slot of their own
     * (SPEC §Subtasks live under their parent), so nothing needs rewriting on them; moving the
     * parent moves the whole group by construction.
     */
    fun rescheduleToSlot(taskId: Long, target: ScheduleSlot) {
        viewModelScope.launch {
            val task = repository.getById(taskId) ?: return@launch
            val today = SlotDeriver.logicalDate(clock(), zone, currentSettings().dayBeginsAtHour)
            val parksUndated = task.date == null &&
                (target == ScheduleSlot.SOON || target == ScheduleSlot.LATER)

            if (parksUndated) {
                repository.updateDateAndSlot(taskId, null, target)
            } else {
                val date = when (target) {
                    ScheduleSlot.TODAY -> today
                    ScheduleSlot.TOMORROW -> today.plusDays(1)
                    ScheduleSlot.SOON -> today.plusDays(2)
                    ScheduleSlot.LATER -> today.plusDays(8)
                }
                // Slot is cleared: a dated task derives its slot from its date, and a leftover
                // parked slot would pin it where its date says it no longer belongs.
                repository.updateDateAndSlot(taskId, noonEpoch(date), null)
            }
            // It arrives at the bottom of wherever it lands rather than keeping a position that
            // belonged to the slot it left.
            repository.updateSlotSortOrder(taskId, repository.getMaxSlotSortOrder(target) + 1)
        }
    }

    /**
     * Delete a task dropped on the bin target (SPEC §Drag-target icons). A parent's children go
     * with it — the tasks→tasks foreign key cascades, which is the same "a parent and its children
     * are one unit" rule the rest of the app follows.
     */
    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            val task = repository.getById(taskId) ?: return@launch
            repository.delete(task)
        }
    }

    /**
     * Cut a task to the device clipboard (SPEC §Drag-target icons). The task leaves Taskflow and
     * its content goes out as plain text — a parent and its children as one indented block, which
     * is the same shape the edit dialogue's outliner reads back, so a Taskflow-to-Taskflow cut and
     * paste round-trips its structure.
     *
     * The text is handed to [onCut] rather than written here: the clipboard is a UI-layer service,
     * and the view-model has no business holding one. Writing it out happens **before** the delete,
     * so a failure to reach the clipboard cannot leave the task destroyed with nothing to paste.
     *
     * SPEC records the accepted risk plainly: if another app overwrites the clipboard between the
     * cut and the paste, the task is gone for good. The user took that trade for the simplicity of
     * the device's own clipboard.
     */
    fun cutTask(taskId: Long, onCut: (String) -> Unit) {
        viewModelScope.launch {
            val task = repository.getById(taskId) ?: return@launch
            val children = repository.getSubtasksList(task.id)
            val text = buildString {
                append(task.title)
                children.forEach { append('\n').append(CUT_INDENT).append(it.title) }
            }
            onCut(text)
            repository.delete(task)
        }
    }

    private fun buildState(
        tasks: List<Task>,
        projects: List<Project>,
        now: Long,
        childrenByParent: Map<Long?, List<Task>>,
        settings: TaskflowSettings,
        focusedProject: Long?,
    ): ScheduleUiState {
        val hour = settings.dayBeginsAtHour
        val near = mutableMapOf<ScheduleSlot, MutableList<ScheduleTaskUi>>()
        val laterRows = mutableListOf<Pair<Task, ScheduleTaskUi>>()
        // Counted per Project as the buckets are filled, so an empty card can say whether its
        // Project is genuinely empty or merely has everything out on the schedule.
        val nearTermCountByProject = mutableMapOf<Long, Int>()

        for (task in tasks) {
            val date = task.date
            val rule = Recurrence.parse(task.recurrence)

            if (rule != null && date != null) {
                // A repeat: one row per instance inside the horizon, each bucketed by its own date.
                for (instance in instancesOf(task, rule, date, now, settings)) {
                    val slot = SlotDeriver.slotForDate(instance.epochMillis, now, zone, hour)
                    val ui = task.toUi(slot, now, instance.date, childrenByParent, settings)
                    if (slot == ScheduleSlot.LATER) {
                        laterRows.add(task to ui)
                    } else {
                        near.getOrPut(slot) { mutableListOf() }.add(ui)
                        nearTermCountByProject.merge(task.projectId, 1, Int::plus)
                    }
                }
                continue
            }

            if (date != null) {
                // Dated one-off: slot derived from the date. Far-future (8+ days) flows into Later,
                // uncapped — the horizon above bounds generated instances, not hand-dated tasks.
                val slot = SlotDeriver.slotForDate(date, now, zone, hour)
                val ui = task.toUi(slot, now, null, childrenByParent, settings)
                if (slot == ScheduleSlot.LATER) laterRows.add(task to ui)
                else near.getOrPut(slot) { mutableListOf() }.add(ui)
            } else {
                // Undated parked task. Soon stays a flat list; everything else undated (parked on
                // Later, or never given a slot) belongs in its Project's Later card (SPEC §Data model).
                if (task.slot == ScheduleSlot.SOON) {
                    near.getOrPut(ScheduleSlot.SOON) { mutableListOf() }
                        .add(task.toUi(ScheduleSlot.SOON, now, null, childrenByParent, settings))
                    nearTermCountByProject.merge(task.projectId, 1, Int::plus)
                } else {
                    laterRows.add(
                        task to task.toUi(ScheduleSlot.LATER, now, null, childrenByParent, settings),
                    )
                }
            }
        }

        // Focus narrows the near-term slots to one Project's tasks. It is applied here, after
        // bucketing, so the Later cards and the per-Project counts above still see everything.
        fun focused(rows: List<ScheduleTaskUi>?): List<ScheduleTaskUi> {
            val ordered = rows.orderedForSlot(tasks)
            if (focusedProject == null) return ordered
            val idsInProject = tasks.filter { it.projectId == focusedProject }.map { it.id }.toSet()
            return ordered.filter { it.id in idsInProject }
        }

        return ScheduleUiState(
            today = focused(near[ScheduleSlot.TODAY]),
            tomorrow = focused(near[ScheduleSlot.TOMORROW]),
            soon = focused(near[ScheduleSlot.SOON]),
            laterCards = laterCards(laterRows, projects, nearTermCountByProject),
        )
    }

    /** One generated occurrence of a recurring task: the day it falls on, and that day as millis. */
    private data class Instance(val date: LocalDate, val epochMillis: Long)

    /**
     * The instances of [task] to render. The window opens at today and runs
     * [Recurrence.HORIZON_DAYS] forward, so a yearly reminder stays invisible until the world is
     * within a month of it (SPEC §Recurring tasks). Instances the user has already ticked off drop
     * out here — the same route an ordinary completed task takes out of its slot — except for
     * today's, which stays visible so it can reach the Completed tray.
     *
     * The window starts at today rather than at the anchor: a repeat is a rhythm, and a month of
     * uncompleted past Mondays piled onto Today would be exactly the nagging UX principle 4 refuses.
     */
    private fun instancesOf(
        task: Task,
        rule: Recurrence,
        anchorMillis: Long,
        now: Long,
        settings: TaskflowSettings,
    ): List<Instance> {
        val hour = settings.dayBeginsAtHour
        val today = SlotDeriver.logicalDate(now, zone, hour)
        val anchor = SlotDeriver.logicalDate(anchorMillis, zone, hour)
        val done = task.completedInstanceDates
        return rule.instancesBetween(anchor, today, today.plusDays(Recurrence.HORIZON_DAYS))
            .filter { it !in done }
            .map { Instance(it, noonEpoch(it)) }
    }

    private fun noonEpoch(date: LocalDate): Long =
        date.atTime(12, 0).atZone(zone).toInstant().toEpochMilli()

    /**
     * The Today tray: ordinary completed tasks, plus any instance of a recurring task the user
     * ticked off today. A recurring row is never globally completed, so it never reaches the
     * completed query — its instance completions are read off the active rows instead.
     */
    private fun completedTray(
        active: List<Task>,
        completed: List<Task>,
        now: Long,
        childrenByParent: Map<Long?, List<Task>>,
        settings: TaskflowSettings,
        focusedProject: Long?,
    ): List<ScheduleTaskUi> {
        val today = SlotDeriver.logicalDate(now, zone, settings.dayBeginsAtHour)
        // While focused, the tray shows that Project's completions and no others — the focused
        // view should be consistent top to bottom (SPEC §Focus on one Project temporarily).
        @Suppress("NAME_SHADOWING")
        val completed = if (focusedProject == null) {
            completed
        } else {
            completed.filter { it.projectId == focusedProject }
        }
        // A completed parent keeps its children in the tray, so un-completing one from there brings
        // the whole group back out (SPEC §Completed task tray on Today).
        // No date label in the tray: what matters there is that the work is done, not when it was
        // due, and a stale date beside a finished task is exactly the reproach UX principle 4 refuses.
        val oneOffs = completed.map {
            it.toUi(ScheduleSlot.TODAY, now, null, childrenByParent, settings).copy(dateLabel = null)
        }
        val instances = active
            .filter { it.recurrence != null && today in it.completedInstanceDates }
            .map {
                ScheduleTaskUi(
                    id = it.id,
                    title = it.title,
                    dateLabel = null,
                    instanceDate = today,
                    isRecurring = true,
                )
            }
        return oneOffs + instances
    }

    /**
     * Build the Later cards: one per user Project in the Strategy-doc order [projects], each holding
     * that Project's Later rows (empty cards included), then the system Unassigned card pinned last.
     */
    private fun laterCards(
        laterRows: List<Pair<Task, ScheduleTaskUi>>,
        projects: List<Project>,
        nearTermCountByProject: Map<Long, Int>,
    ): List<LaterProjectCard> {
        val byProject: Map<Long, List<Pair<Task, ScheduleTaskUi>>> =
            laterRows.groupBy { it.first.projectId }
        val userCards = projects.map { project ->
            LaterProjectCard(
                projectId = project.id,
                projectName = project.name,
                isUnassigned = false,
                tasks = byProject[project.id].orderedForCard(),
                nearTermTaskCount = nearTermCountByProject[project.id] ?: 0,
            )
        }
        val unassignedCard = LaterProjectCard(
            projectId = Project.UNASSIGNED_PROJECT_ID,
            projectName = Project.UNASSIGNED_PROJECT_NAME,
            isUnassigned = true,
            tasks = byProject[Project.UNASSIGNED_PROJECT_ID].orderedForCard(),
            nearTermTaskCount = nearTermCountByProject[Project.UNASSIGNED_PROJECT_ID] ?: 0,
        )
        return userCards + unassignedCard
    }

    /**
     * Stable order for a flat slot: the owning task's stored slot_sort_order first, then the row's
     * own instance date, then id. The instance date is what keeps a repeat's several rows in
     * chronological order among themselves rather than in an arbitrary one.
     */
    private fun List<ScheduleTaskUi>?.orderedForSlot(tasks: List<Task>): List<ScheduleTaskUi> {
        if (this == null) return emptyList()
        val sortOrder = tasks.associate { it.id to it.slotSortOrder }
        return sortedWith(
            compareBy(
                { sortOrder[it.id] ?: 0 },
                { it.instanceDate ?: LocalDate.MIN },
                { it.id },
            ),
        )
    }

    /** A card's own row order: project_sort_order, with instance date and id as tiebreakers. */
    private fun List<Pair<Task, ScheduleTaskUi>>?.orderedForCard(): List<ScheduleTaskUi> {
        if (this == null) return emptyList()
        return sortedWith(
            compareBy(
                { it.first.projectSortOrder },
                { it.second.instanceDate ?: LocalDate.MIN },
                { it.first.id },
            ),
        ).map { it.second }
    }

    /**
     * A task as one row. Its children come along, since a subtask has no surface of its own — it
     * renders nested under its parent wherever the parent lives (SPEC §Subtasks live under their
     * parent). A child carries no date, Project or slot, so it needs none of the bucketing above.
     */
    private fun Task.toUi(
        slot: ScheduleSlot,
        now: Long,
        instanceDate: LocalDate?,
        childrenByParent: Map<Long?, List<Task>>,
        settings: TaskflowSettings,
    ): ScheduleTaskUi =
        ScheduleTaskUi(
            id = id,
            title = title,
            dateLabel = dateLabelFor(this, slot, now, instanceDate, settings),
            instanceDate = instanceDate,
            isRecurring = recurrence != null,
            isCompleted = isCompleted,
            subtasks = childrenByParent[id]
                ?.sortedWith(compareBy({ it.slotSortOrder }, { it.id }))
                ?.map { child ->
                    ScheduleTaskUi(
                        id = child.id,
                        title = child.title,
                        dateLabel = null,
                        isCompleted = child.isCompleted,
                    )
                }
                .orEmpty(),
        )

    /**
     * Soon/Later rows show their date when they have one. Tomorrow rows show no date: a Tomorrow
     * task always carries exactly tomorrow's date, so the label is redundant with the page title.
     * Today rows show a date only when it has slipped into the past — the only signal that a task is
     * "still there" from a past day, with no overdue label or colour (UX principle 4). On Later
     * cards, undated tasks carry no date (null) and far-future dated tasks show their DD/MM date.
     *
     * An instance of a repeat labels its *own* date rather than the task's anchor date, which is
     * what makes four Mondays in Later readable as four different days.
     */
    private fun dateLabelFor(
        task: Task,
        slot: ScheduleSlot,
        now: Long,
        instanceDate: LocalDate?,
        settings: TaskflowSettings,
    ): String? {
        val date = instanceDate?.let { noonEpoch(it) } ?: task.date ?: return null
        val show = when (slot) {
            ScheduleSlot.TODAY -> SlotDeriver.isBeforeToday(date, now, zone, settings.dayBeginsAtHour)
            ScheduleSlot.TOMORROW -> false
            else -> true
        }
        if (!show) return null
        return Instant.ofEpochMilli(date).atZone(zone).toLocalDate().format(formatter(settings))
    }

    companion object {
        /** Matches the outliner's own indent, so cut text pastes back with its hierarchy intact. */
        private const val CUT_INDENT: String = "  "

        fun factory(
            repository: TaskRepository,
            projectRepository: ProjectRepository,
            settingsRepository: SettingsRepository,
        ) = viewModelFactory {
            initializer { ScheduleViewModel(repository, projectRepository, settingsRepository) }
        }
    }
}
