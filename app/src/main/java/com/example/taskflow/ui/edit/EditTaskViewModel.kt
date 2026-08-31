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
    data class NewOnSlot(val slot: ScheduleSlot) : EditTarget
}

/**
 * The edit dialogue's rendered state. Editable fields are [title], [notes], [projectId] and the
 * date — [selectedDate] is the tile the strip highlights (null = the "No date" tile), and [today]
 * is the strip's visual anchor. [dateLabel] is the same date as text, kept for callers that want a
 * one-line rendering. [projects] backs the Project picker, always including an "unassigned" choice.
 */
data class EditUiState(
    val loading: Boolean = false,
    val isNew: Boolean = true,
    val title: String = "",
    val notes: String = "",
    val projectId: Long? = null,
    val dateLabel: String = NO_DATE,
    val selectedDate: LocalDate? = null,
    val today: LocalDate = LocalDate.now(),
    val projects: List<Project> = emptyList(),
) {
    /** Save is allowed only with a non-blank title — the lightest guard against empty captures. */
    val canSave: Boolean get() = title.isNotBlank()

    companion object {
        const val NO_DATE: String = "No date"
    }
}

/**
 * Backs one edit dialogue. For an existing task it loads the row once; for a new task it resolves the
 * inherited context (date/slot/projectId) from [target]. Save inserts (new) or updates (existing)
 * through [taskRepository]; the date is never changed here (read-only this batch).
 *
 * [clock] and [zone] are injectable so the today/tomorrow date resolution can be unit-tested without
 * a device. The date label uses DD/MM (SPEC default); batch 0013 adds the MM/DD setting.
 */
class EditTaskViewModel(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val strategyRepository: StrategyRepository,
    private val target: EditTarget,
    private val clock: () -> Long = System::currentTimeMillis,
    private val zone: ZoneId = ZoneId.systemDefault(),
) : ViewModel() {

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM")

    /** Mutable editing fields plus the read-only resolved date/slot the saved task carries. */
    private data class Form(
        val title: String = "",
        val notes: String = "",
        val projectId: Long? = null,
        val date: Long? = null,
        val slot: ScheduleSlot? = null,
        val isNew: Boolean = true,
        val loaded: Boolean = true,
    )

    private val form = MutableStateFlow(initialForm())

    /** The existing row, held so save can copy it without losing untouched columns. */
    private var original: Task? = null

    val uiState: StateFlow<EditUiState> =
        combine(form, projectRepository.getAllOrdered()) { f, projects ->
            EditUiState(
                loading = !f.loaded,
                isNew = f.isNew,
                title = f.title,
                notes = f.notes,
                projectId = f.projectId,
                dateLabel = f.date?.let {
                    Instant.ofEpochMilli(it).atZone(zone).toLocalDate().format(dateFormatter)
                } ?: EditUiState.NO_DATE,
                selectedDate = f.date?.let { Instant.ofEpochMilli(it).atZone(zone).toLocalDate() },
                today = SlotDeriver.logicalDate(clock(), zone),
                projects = projects,
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
                    form.value = Form(
                        title = task.title,
                        notes = task.notes,
                        projectId = task.projectId,
                        date = task.date,
                        slot = task.slot,
                        isNew = false,
                        loaded = true,
                    )
                }
            }
        }
    }

    /** Resolves the starting form for a new task from its inherited context; existing loads in init. */
    private fun initialForm(): Form = when (target) {
        is EditTarget.Existing -> Form(isNew = false, loaded = false)
        is EditTarget.NewOnSlot -> {
            val today = SlotDeriver.logicalDate(clock(), zone)
            when (target.slot) {
                // Today/Tomorrow auto-set the date (the task is dated, slot is derived from the date).
                // Noon anchors the date safely inside the logical day, clear of the day-begins-at edge.
                ScheduleSlot.TODAY -> Form(isNew = true, date = noonEpoch(today))
                ScheduleSlot.TOMORROW -> Form(isNew = true, date = noonEpoch(today.plusDays(1)))
                // Soon/Later park the task undated on that slot — no forced date (SPEC §Add a new task).
                ScheduleSlot.SOON -> Form(isNew = true, slot = ScheduleSlot.SOON)
                ScheduleSlot.LATER -> Form(isNew = true, slot = ScheduleSlot.LATER)
            }
        }
    }

    private fun noonEpoch(date: LocalDate): Long =
        date.atTime(12, 0).atZone(zone).toInstant().toEpochMilli()

    fun onTitleChange(value: String) {
        form.value = form.value.copy(title = value)
    }

    fun onNotesChange(value: String) {
        form.value = form.value.copy(notes = value)
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
        form.value = form.value.copy(date = null, slot = ScheduleSlot.LATER)
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
        if (f.title.isBlank()) return
        viewModelScope.launch {
            if (f.isNew) {
                taskRepository.insert(
                    Task(
                        title = f.title.trim(),
                        notes = f.notes,
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
                    )
                )
            } else {
                val o = original ?: return@launch
                var updated = o.copy(
                    title = f.title.trim(),
                    notes = f.notes,
                    // No Project picked → Unassigned (projectId is non-null; see the insert path above).
                    projectId = f.projectId ?: Project.UNASSIGNED_PROJECT_ID,
                    date = f.date,
                    slot = f.slot,
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
            }
            onSaved()
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
            date != null -> SlotDeriver.slotForDate(date, clock(), zone)
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
            target: EditTarget,
        ) = viewModelFactory {
            initializer {
                EditTaskViewModel(taskRepository, projectRepository, strategyRepository, target)
            }
        }
    }
}
