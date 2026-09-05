package com.example.taskflow.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.taskflow.data.model.Project
import com.example.taskflow.data.model.ScheduleSlot
import com.example.taskflow.data.model.StrategyEntry
import com.example.taskflow.data.model.Task
import com.example.taskflow.data.repository.ProjectRepository
import com.example.taskflow.data.repository.StrategyRepository
import com.example.taskflow.data.repository.TaskRepository
import com.example.taskflow.data.settings.SettingsRepository
import com.example.taskflow.data.settings.TaskflowSettings
import com.example.taskflow.domain.Recurrence
import com.example.taskflow.domain.SlotDeriver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * What an edit dialogue was opened for: an existing task, or a new task whose context is inherited
 * from the surface the FAB was pressed on (SPEC §Add a new task, capture-inherits-context).
 *
 * - [NewOnSlot] — pressed on a Schedule page. Today/Tomorrow auto-set the date; Soon/Later park the
 *   task undated on that slot. The view-model resolves slot → (date, slot) below. A task added from
 *   Later is undated and defaults to the Unassigned Project (it shows in the Later Unassigned card).
 */
sealed interface EditTarget {
    data class Existing(val taskId: Long) : EditTarget

    /**
     * [projectId] is the Project the new task inherits: normally null, meaning the system
     * Unassigned Project, but the focused Project while focus is on (SPEC §Focus on one Project
     * temporarily) — focus is the context the user is capturing in.
     */
    data class NewOnSlot(val slot: ScheduleSlot, val projectId: Long? = null) : EditTarget
}

/**
 * The edit dialogue's rendered state. Editable fields are [title], [projectId] and the date —
 * [selectedDate] is the tile the strip highlights (null = the "No date" tile), and [today] is the
 * strip's visual anchor. [dateLabel] is the same date as text, kept for callers that want a
 * one-line rendering. [projects] backs the Project picker, always including an "unassigned" choice.
 *
 * The task's `notes` column is deliberately absent here. The dialogue no longer shows a notes field
 * (SPEC §Edit a task), so it neither reads nor writes the column — an existing task keeps whatever
 * it already carries, and exports keep printing it.
 */
data class EditUiState(
    val loading: Boolean = false,
    val isNew: Boolean = true,
    val title: String = "",
    val projectId: Long? = null,
    val dateLabel: String = NO_DATE,
    val selectedDate: LocalDate? = null,
    val today: LocalDate = LocalDate.now(),
    val projects: List<Project> = emptyList(),
    // The task's repeat rule, or null for a one-off (SPEC §Recurring tasks). A repeat counts from
    // the task's own date, so the editor only offers one once a date is set.
    val recurrence: Recurrence? = null,
    // The outliner's text: the parent's title on the first line, one indented line per subtask
    // (SPEC §Edit dialogue: outliner-style typing for subtasks). [title] is its first line.
    val outline: String = "",
    // How dates are written as numbers, from Settings (SPEC §Settings → Date format). It does NOT
    // reach the date strip's tiles: those name the month rather than numbering it, so there is no
    // day/month order left to obey. Kept for [dateLabel] and any other numeric rendering.
    val datePattern: String = "dd/MM",
) {
    /** A repeat has to have something to repeat from, so the field is inert on an undated task. */
    val canSetRecurrence: Boolean get() = selectedDate != null
    /** Save is allowed only with a non-blank title — the lightest guard against empty captures. */
    val canSave: Boolean get() = title.isNotBlank()

    companion object {
        const val NO_DATE: String = "No date"
    }
}

/**
 * Backs one edit dialogue. For an existing task it loads the row once; for a new task it resolves the
 * inherited context (date/slot/projectId) from [target]. Save inserts (new) or updates (existing)
 * through [taskRepository]; the date is set, changed or cleared through the date strip's
 * [onDateSelected] and [onDateCleared].
 *
 * [clock] and [zone] are injectable so the today/tomorrow date resolution can be unit-tested without
 * a device. The date label uses DD/MM (SPEC default); batch 0013 adds the MM/DD setting.
 */
class EditTaskViewModel(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val strategyRepository: StrategyRepository,
    private val settingsRepository: SettingsRepository,
    private val target: EditTarget,
    private val clock: () -> Long = System::currentTimeMillis,
    private val zone: ZoneId = ZoneId.systemDefault(),
) : ViewModel() {

    // Dates are written the way Settings says (SPEC §Settings → Date format), and "today" is the
    // user's own day boundary (SPEC §Settings → Day begins at) — the same anchor the Schedule uses,
    // so the strip's Today tile and the Today page never disagree about which day it is.
    private val settings: TaskflowSettings get() = settingsRepository.read()

    private val dateFormatter: DateTimeFormatter
        get() = DateTimeFormatter.ofPattern(settings.dateFormat.pattern)

    /** Mutable editing fields plus the read-only resolved date/slot the saved task carries. */
    private data class Form(
        // The outliner's whole text. The parent's title is its first line; the rest are subtasks.
        val outline: String = "",
        // The subtask rows already in the database, in order, so an edit can be matched onto them
        // by position rather than deleting and re-creating them (see ChildSync).
        val childIds: List<Long> = emptyList(),
        val childTitles: List<String> = emptyList(),
        val projectId: Long? = null,
        val date: Long? = null,
        val slot: ScheduleSlot? = null,
        val isNew: Boolean = true,
        val loaded: Boolean = true,
        val recurrence: Recurrence? = null,
    )

    private val form = MutableStateFlow(initialForm())

    /** The existing row, held so save can copy it without losing untouched columns. */
    private var original: Task? = null

    val uiState: StateFlow<EditUiState> =
        combine(form, projectRepository.getAllOrdered()) { f, projects ->
            EditUiState(
                loading = !f.loaded,
                isNew = f.isNew,
                title = Outline.parse(f.outline).parentTitle,
                outline = f.outline,
                projectId = f.projectId,
                dateLabel = f.date?.let {
                    Instant.ofEpochMilli(it).atZone(zone).toLocalDate().format(dateFormatter)
                } ?: EditUiState.NO_DATE,
                selectedDate = f.date?.let { Instant.ofEpochMilli(it).atZone(zone).toLocalDate() },
                today = SlotDeriver.logicalDate(clock(), zone, settings.dayBeginsAtHour),
                projects = projects,
                recurrence = f.recurrence,
                datePattern = settings.dateFormat.pattern,
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            EditUiState(loading = target is EditTarget.Existing, isNew = target !is EditTarget.Existing),
        )

    init {
        if (target is EditTarget.Existing) {
            viewModelScope.launch {
                val task = taskRepository.getById(target.taskId)
                if (task != null) {
                    original = task
                    val children = taskRepository.getSubtasksList(task.id)
                    form.value = Form(
                        outline = Outline(task.title, children.map { it.title }).render(),
                        childIds = children.map { it.id },
                        childTitles = children.map { it.title },
                        projectId = task.projectId,
                        date = task.date,
                        slot = task.slot,
                        isNew = false,
                        loaded = true,
                        recurrence = Recurrence.parse(task.recurrence),
                    )
                }
            }
        }
    }

    /** Resolves the starting form for a new task from its inherited context; existing loads in init. */
    private fun initialForm(): Form = when (target) {
        is EditTarget.Existing -> Form(isNew = false, loaded = false)
        is EditTarget.NewOnSlot -> {
            val today = SlotDeriver.logicalDate(clock(), zone, settings.dayBeginsAtHour)
            // Null unless the user is focused on a Project, in which case that is the context they
            // are capturing in and the new task belongs to it.
            val project = target.projectId
            when (target.slot) {
                // Today/Tomorrow auto-set the date (the task is dated, slot is derived from the date).
                // Noon anchors the date safely inside the logical day, clear of the day-begins-at edge.
                ScheduleSlot.TODAY ->
                    Form(isNew = true, date = noonEpoch(today), projectId = project)
                ScheduleSlot.TOMORROW ->
                    Form(isNew = true, date = noonEpoch(today.plusDays(1)), projectId = project)
                // Soon/Later park the task undated on that slot — no forced date (SPEC §Add a new task).
                ScheduleSlot.SOON ->
                    Form(isNew = true, slot = ScheduleSlot.SOON, projectId = project)
                ScheduleSlot.LATER ->
                    Form(isNew = true, slot = ScheduleSlot.LATER, projectId = project)
            }
        }
    }

    private fun noonEpoch(date: LocalDate): Long =
        date.atTime(12, 0).atZone(zone).toInstant().toEpochMilli()

    /**
     * The outliner's text changed. Every keystroke is normalised — first line flush left, later
     * lines carrying exactly one indent — which is what makes pressing Enter produce a subtask
     * rather than a second unindented line the user then has to indent themselves.
     */
    fun onOutlineChange(value: String) {
        form.value = form.value.copy(outline = Outline.normalise(value))
    }

    fun onProjectChange(projectId: Long?) {
        form.value = form.value.copy(projectId = projectId)
    }

    /**
     * Tapping a tile in the date strip. The stored `slot` is cleared to null because a dated task
     * derives its slot from its date (SPEC §Data model) — leaving a parked slot behind would keep
     * the task pinned to Soon or Later while its date said otherwise. Noon anchors the date safely
     * inside the logical day, clear of the day-begins-at edge, matching the new-task path above.
     */
    fun onDateSelected(date: LocalDate) {
        form.value = form.value.copy(date = noonEpoch(date), slot = null)
    }

    /**
     * Tapping the strip's "No date" tile. The task drops out of the day and Soon lists and into its
     * Project's card on Later (SPEC §Move between Schedule and Project), which is what parking it on
     * LATER expresses — an undated task not parked on Soon renders inside its Project's card.
     */
    fun onDateCleared() {
        // Clearing the date also drops any repeat: a rule counts from the task's date, so a repeat
        // with nothing to count from would generate no instances and read as a silent no-op.
        form.value = form.value.copy(date = null, slot = ScheduleSlot.LATER, recurrence = null)
    }

    /** Sets or clears the repeat rule (null = a one-off task again). SPEC §Recurring tasks. */
    fun onRecurrenceChange(recurrence: Recurrence?) {
        form.value = form.value.copy(recurrence = recurrence)
    }

    /**
     * Creates a Project from a typed name and selects it for the task being edited, so the task files
     * into the new Project on save (SPEC §Create or delete a Project). A name is all creation asks for;
     * a blank name is ignored. The Project is appended to the end of the order (max + 1) so it appears
     * last on Later (above the pinned Unassigned card) and as a new heading at the end of the Strategy
     * doc; an empty Strategy entry is created alongside so its paragraph exists to be written later.
     *
     * Relocated here from AppViewModel.createProject: the editor's picker is the only Project-creation
     * surface, and this view-model already owns the projectId the new id must set, so creation lives
     * where its one caller and its one output both already are.
     */
    fun createProjectAndSelect(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            val nextOrder = projectRepository.getMaxSortOrder() + 1
            val projectId = projectRepository.insert(Project(name = trimmed, sortOrder = nextOrder))
            strategyRepository.upsert(StrategyEntry(projectId = projectId))
            form.value = form.value.copy(projectId = projectId)
        }
    }

    /** Inserts (new) or updates (existing), then calls [onSaved] on the main scope. No-op if blank. */
    fun save(onSaved: () -> Unit) {
        val f = form.value
        val outline = Outline.parse(f.outline)
        if (outline.parentTitle.isBlank()) return
        viewModelScope.launch {
            if (f.isNew) {
                val parentId = taskRepository.insert(
                    Task(
                        title = outline.parentTitle,
                        // No Project picked → the system Unassigned Project. projectId is non-null:
                        // every task has a Project home (see Task / Project.UNASSIGNED_PROJECT_ID).
                        projectId = f.projectId ?: Project.UNASSIGNED_PROJECT_ID,
                        date = f.date,
                        slot = f.slot,
                        slotSortOrder = nextSlotSortOrder(f.date, f.slot),
                        projectSortOrder = if (f.projectId != null) {
                            taskRepository.getMaxProjectSortOrder(f.projectId) + 1
                        } else {
                            0
                        },
                        recurrence = f.recurrence?.serialize(),
                    )
                )
                // Subtasks typed in on a brand-new task are inserted under it once it has an id.
                // They inherit the parent's Project and carry no date or slot of their own
                // (SPEC §Subtasks live under their parent).
                outline.children.forEachIndexed { index, childTitle ->
                    taskRepository.insert(
                        Task(
                            title = childTitle,
                            projectId = f.projectId ?: Project.UNASSIGNED_PROJECT_ID,
                            parentId = parentId,
                            slotSortOrder = index,
                        ),
                    )
                }
            } else {
                val o = original ?: return@launch
                var updated = o.copy(
                    title = outline.parentTitle,
                    // `notes` is not copied here on purpose: the dialogue no longer edits it, so the
                    // task keeps whatever the column already held and an existing note survives.
                    // No Project picked → Unassigned (projectId is non-null; see the insert path above).
                    projectId = f.projectId ?: Project.UNASSIGNED_PROJECT_ID,
                    date = f.date,
                    slot = f.slot,
                    recurrence = f.recurrence?.serialize(),
                    // Changing the rule invalidates the ticked-off instances: they were dates the
                    // old rhythm produced, and keeping them would silently hide days the new one
                    // lands on. Turning a repeat off clears them for the same reason.
                    completedInstances = if (f.recurrence?.serialize() == o.recurrence) {
                        o.completedInstances
                    } else {
                        ""
                    },
                )
                // A date change moves the task across Schedule surfaces (SPEC §Move between Schedule
                // and Project), so it lands at the bottom of wherever it arrives rather than keeping
                // a position that belonged to the slot it left.
                if (f.date != o.date || f.slot != o.slot) {
                    updated = updated.copy(slotSortOrder = nextSlotSortOrder(f.date, f.slot))
                }
                // Refiling an undated task to a different Project appends it to that Project's
                // below-card list (SPEC §Move between Schedule and Project). Dated tasks keep their
                // order — they show in the card by Schedule position, not project_sort_order.
                if (f.projectId != null && f.projectId != o.projectId && updated.date == null) {
                    updated = updated.copy(
                        projectSortOrder = taskRepository.getMaxProjectSortOrder(f.projectId) + 1,
                    )
                }
                taskRepository.update(updated)
                syncChildren(o.id, f, outline.children)
            }
            onSaved()
        }
    }

    /**
     * Writes the edited outline's child lines onto the subtask rows already in the database.
     *
     * Matching is by position (see [ChildSync]): renaming the second line edits the second row
     * rather than destroying it and making a new one, so a subtask keeps its completion state
     * through a rename. Only the lines that actually changed are written.
     */
    private suspend fun syncChildren(parentId: Long, f: Form, editedChildren: List<String>) {
        val sync = ChildSync.of(f.childIds, f.childTitles, editedChildren)
        sync.updates.forEach { (id, title) ->
            val child = taskRepository.getById(id) ?: return@forEach
            taskRepository.update(child.copy(title = title))
        }
        sync.insertions.forEachIndexed { index, title ->
            taskRepository.insert(
                Task(
                    title = title,
                    projectId = f.projectId ?: Project.UNASSIGNED_PROJECT_ID,
                    parentId = parentId,
                    slotSortOrder = f.childIds.size + index,
                ),
            )
        }
        sync.deletions.forEach { id ->
            val child = taskRepository.getById(id) ?: return@forEach
            taskRepository.delete(child)
        }
        // A parent left with no children is an ordinary task again, and gets its checkbox back —
        // which follows on its own, since a row renders a chevron only when it has children.
        // Its completion is no longer derived, so a parent that had rolled up to complete would
        // otherwise stay stuck complete with nothing under it to un-complete.
        if (editedChildren.isEmpty() && f.childIds.isNotEmpty()) {
            taskRepository.updateCompletion(parentId, false)
        }
    }

    /**
     * The bin target, dropped on from inside the dialogue: the subtask line goes. It is removed
     * from the outline rather than deleted from the database, so it disappears the same way a line
     * the user backspaced away would, and the row itself goes on save — one path, not two.
     */
    fun deleteChildLine(childIndex: Int) {
        val outline = Outline.parse(form.value.outline)
        if (childIndex !in outline.children.indices) return
        val remaining = outline.children.toMutableList().also { it.removeAt(childIndex) }
        form.value = form.value.copy(
            outline = Outline(outline.parentTitle, remaining).render(),
        )
    }

    /**
     * The cut target, dropped on from inside the dialogue: the subtask's text goes to the device
     * clipboard and the line leaves the outline. A subtask has no children of its own, so what goes
     * out is a single line — pasteable back into any outliner, or into a notes app.
     *
     * The text reaches the clipboard through [onCut] before the line is removed, so a failure to
     * reach it cannot lose the text.
     */
    fun cutChildLine(childIndex: Int, onCut: (String) -> Unit) {
        val outline = Outline.parse(form.value.outline)
        val child = outline.children.getOrNull(childIndex) ?: return
        onCut(child)
        deleteChildLine(childIndex)
    }

    /**
     * The promote target, dropped on from inside the dialogue. A line the user has typed but never
     * saved has no row to promote, so it is written first and then promoted — otherwise the target
     * would silently do nothing on exactly the subtask the user just created.
     */
    fun promoteChildLine(childIndex: Int) {
        val f = form.value
        val outline = Outline.parse(f.outline)
        if (childIndex !in outline.children.indices) return
        val parentId = original?.id ?: return
        viewModelScope.launch {
            syncChildren(parentId, f, outline.children)
            val childId = taskRepository.getSubtasksList(parentId).getOrNull(childIndex)?.id
                ?: return@launch
            promoteSubtask(childId)
            // The promoted line leaves the outline, and the form's picture of the saved children
            // is rebuilt from the database so a later save does not re-create it.
            val remaining = taskRepository.getSubtasksList(parentId)
            form.value = form.value.copy(
                outline = Outline(outline.parentTitle, remaining.map { it.title }).render(),
                childIds = remaining.map { it.id },
                childTitles = remaining.map { it.title },
            )
        }
    }

    /**
     * Makes a subtask a task in its own right (SPEC §Drag-target icons — the promote target). It
     * takes the parent's date and Project and lands at the bottom of wherever that puts it, which
     * is what "this child should stand on its own" means in placement terms.
     */
    private suspend fun promoteSubtask(childId: Long) {
        val child = taskRepository.getById(childId) ?: return
        val parent = child.parentId?.let { taskRepository.getById(it) } ?: return
        taskRepository.update(
            child.copy(
                parentId = null,
                projectId = parent.projectId,
                date = parent.date,
                slot = parent.slot,
                slotSortOrder = nextSlotSortOrder(parent.date, parent.slot),
            ),
        )
        // The parent may now have no children left, in which case it stops being a parent and its
        // completion stops being derived — so a rolled-up complete is released rather than stuck.
        if (taskRepository.getSubtasksList(parent.id).isEmpty()) {
            taskRepository.updateCompletion(parent.id, false)
        }
    }

    /**
     * Append-to-bottom order for a new task on whichever slot it lands. The DAO's max-sort-order is
     * keyed on the stored `slot` column, so for a dated task (slot null, slot derived from the date)
     * this appends below any parked tasks on that slot; the precise dated-row ordering is owned by
     * batch 0012, so this best-effort value is deliberate, not exact.
     */
    private suspend fun nextSlotSortOrder(date: Long?, slot: ScheduleSlot?): Int {
        val targetSlot = when {
            date != null -> SlotDeriver.slotForDate(date, clock(), zone, settings.dayBeginsAtHour)
            slot != null -> slot
            else -> return 0
        }
        return taskRepository.getMaxSlotSortOrder(targetSlot) + 1
    }

    companion object {
        fun factory(
            taskRepository: TaskRepository,
            projectRepository: ProjectRepository,
            strategyRepository: StrategyRepository,
            settingsRepository: SettingsRepository,
            target: EditTarget,
        ) = viewModelFactory {
            initializer {
                EditTaskViewModel(
                    taskRepository,
                    projectRepository,
                    strategyRepository,
                    settingsRepository,
                    target,
                )
            }
        }
    }
}
