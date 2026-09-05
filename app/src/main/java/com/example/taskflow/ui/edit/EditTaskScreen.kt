package com.example.taskflow.ui.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskflow.TaskflowApplication
import com.example.taskflow.ui.common.DragTarget
import com.example.taskflow.ui.common.DragTargetRow
import com.example.taskflow.domain.Recurrence
import com.example.taskflow.domain.RecurrenceUnit
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * The task edit dialogue (SPEC §Edit a task), shown full-screen over the spine or a Project view.
 * Edits title, Project (including "unassigned"), and the date — the last through the embedded
 * side-scrolling date strip, which can set, change or clear it. There is no free-text notes field:
 * detail belonging to a task is written as a subtask line, and a box inviting the user to describe
 * their work would add weight to the list this app exists to lighten.
 *
 * The same dialogue serves *new* tasks: the FAB opens it with the surface's inherited context already
 * resolved by [EditTaskViewModel] (SPEC §Add a new task). Save inserts or updates, then [onClose].
 */
@Composable
fun EditTaskScreen(
    target: EditTarget,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val app = context.applicationContext as TaskflowApplication
    // A ViewModelStore scoped to this one dialogue open. EditTaskScreen leaves the composition when
    // the dialogue closes (editTarget → null in AppRoot) and re-enters on the next open, so this
    // remembered store — and the EditTaskViewModel it holds — is rebuilt every open. A new task
    // therefore opens with a blank form each time instead of reusing the previous add's typed-in
    // fields, and the onDispose clear stops the per-open stores from piling up across opens.
    val editStoreOwner = remember {
        object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore = ViewModelStore()
        }
    }
    DisposableEffect(Unit) {
        onDispose { editStoreOwner.viewModelStore.clear() }
    }
    val viewModel: EditTaskViewModel = viewModel(
        viewModelStoreOwner = editStoreOwner,
        factory = EditTaskViewModel.factory(
            app.taskRepository,
            app.projectRepository,
            app.strategyRepository,
            app.settingsRepository,
            target,
        ),
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Subtask drag state (SPEC §Drag-target icons). Inside the dialogue the targets are bin and
    // promote — promote is offered here and nowhere else, because a subtask is the only thing
    // there is to promote.
    var draggedChildIndex by remember { mutableStateOf<Int?>(null) }
    var dragPosition by remember { mutableStateOf<Offset?>(null) }
    var hoveredTarget by remember { mutableStateOf<DragTarget?>(null) }
    val clipboard = LocalClipboardManager.current

    Box(modifier = modifier.fillMaxSize()) {
    Column(modifier = Modifier.fillMaxSize()) {
        EditHeader(
            isNew = state.isNew,
            canSave = state.canSave,
            onClose = onClose,
            onSave = { viewModel.save(onClose) },
        )
        HorizontalDivider()

        if (state.loading) return@Column

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // The outliner: the task and its subtasks, edited by ordinary typing (SPEC §Edit
            // dialogue: outliner-style typing for subtasks). A subtask is picked up by its handle
            // to reach the bin and promote targets.
            OutlinerField(
                text = state.outline,
                onTextChange = viewModel::onOutlineChange,
                onChildDragStart = { draggedChildIndex = it },
                onChildDragPosition = { dragPosition = it },
                onChildDragEnd = { childIndex ->
                    when (hoveredTarget) {
                        DragTarget.BIN -> viewModel.deleteChildLine(childIndex)
                        DragTarget.PROMOTE -> viewModel.promoteChildLine(childIndex)
                        DragTarget.CUT -> viewModel.cutChildLine(childIndex) { text ->
                            clipboard.setText(AnnotatedString(text))
                        }
                        else -> Unit
                    }
                    draggedChildIndex = null
                    dragPosition = null
                    hoveredTarget = null
                },
                modifier = Modifier.fillMaxWidth(),
            )
            ProjectField(
                projectId = state.projectId,
                projects = state.projects.map { it.id to it.name },
                onProjectChange = viewModel::onProjectChange,
                onCreateProject = viewModel::createProjectAndSelect,
            )
            DateField(
                selectedDate = state.selectedDate,
                today = state.today,
                onSelectDate = viewModel::onDateSelected,
                onClearDate = viewModel::onDateCleared,
            )
            RepeatField(
                recurrence = state.recurrence,
                enabled = state.canSetRecurrence,
                anchor = state.selectedDate,
                onRecurrenceChange = viewModel::onRecurrenceChange,
            )
        }
    }
        DragTargetRow(
            visible = draggedChildIndex != null,
            targets = listOf(DragTarget.BIN, DragTarget.CUT, DragTarget.PROMOTE),
            dragPosition = dragPosition,
            onHoverChange = { hoveredTarget = it },
            modifier = Modifier.align(Alignment.TopEnd),
        )
    }
}

@Composable
private fun EditHeader(
    isNew: Boolean,
    canSave: Boolean,
    onClose: () -> Unit,
    onSave: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clickable(onClick = onClose),
            contentAlignment = Alignment.Center,
        ) {
            // Text glyph rather than a Material icon — the icon pack isn't a project dependency.
            Text(
                text = "←",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Text(
            text = if (isNew) "New task" else "Edit task",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onSave, enabled = canSave) {
            Text("Save")
        }
    }
}

/**
 * Editable Project picker: a button showing the current Project (or "Unassigned") plus a menu. The
 * menu's last entry, "New Project", opens a name-only card that creates a Project on the spot and
 * files the edited task into it (SPEC §Create or delete a Project).
 */
@Composable
private fun ProjectField(
    projectId: Long?,
    projects: List<Pair<Long, String>>,
    onProjectChange: (Long?) -> Unit,
    onCreateProject: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var showNewProjectCard by remember { mutableStateOf(false) }
    val currentName = projects.firstOrNull { it.first == projectId }?.second ?: UNASSIGNED

    Column {
        Text(
            text = "Project",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(currentName, modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                Text(if (expanded) "▾" else "▸")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text(UNASSIGNED) },
                    onClick = {
                        onProjectChange(null)
                        expanded = false
                    },
                )
                projects.forEach { (id, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            onProjectChange(id)
                            expanded = false
                        },
                    )
                }
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text(NEW_PROJECT) },
                    onClick = {
                        expanded = false
                        showNewProjectCard = true
                    },
                )
            }
        }
    }

    if (showNewProjectCard) {
        NewProjectCard(
            onConfirm = { name ->
                onCreateProject(name)
                showNewProjectCard = false
            },
            onDismiss = { showNewProjectCard = false },
        )
    }
}

/**
 * Name-only Project-creation card, foregrounded from the Project picker's "New Project" entry
 * (SPEC §Create or delete a Project — a name is all creation asks for). Confirm is disabled until a
 * non-blank name is typed; on confirm the caller creates the Project and selects it for the task.
 */
@Composable
private fun NewProjectCard(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(NEW_PROJECT) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Project name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name) }, enabled = name.isNotBlank()) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

/**
 * The date field: the side-scrolling date strip, embedded directly in the dialogue rather than
 * opened as a popup calendar (SPEC §Date picker — side-scrolling date strip), so the editing flow
 * stays continuous. Selecting or clearing writes straight to the form; nothing lands in the
 * database until Save.
 */
@Composable
private fun DateField(
    selectedDate: LocalDate?,
    today: LocalDate,
    onSelectDate: (LocalDate) -> Unit,
    onClearDate: () -> Unit,
) {
    Column {
        Text(
            text = "Date",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        DateStrip(
            selectedDate = selectedDate,
            today = today,
            onSelectDate = onSelectDate,
            onClearDate = onClearDate,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/**
 * The repeat field (SPEC §Recurring tasks). Four presets — never, daily, weekly, monthly — plus a
 * custom row that turns any of them into "every N". Weekly additionally offers weekday chips, so
 * "every weekday" and "Mondays and Thursdays" are both reachable without a second screen.
 *
 * The whole field is disabled on an undated task: a repeat counts from the task's own date, so
 * without one there is nothing for a rule to repeat from. That is stated on screen rather than left
 * for the user to infer from a control that silently does nothing.
 */
@Composable
private fun RepeatField(
    recurrence: Recurrence?,
    enabled: Boolean,
    anchor: LocalDate?,
    onRecurrenceChange: (Recurrence?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var custom by remember(recurrence) { mutableStateOf((recurrence?.interval ?: 1) > 1) }

    Column {
        Text(
            text = "Repeats",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (!enabled) {
            Text(
                text = "Give the task a date to repeat it.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            return@Column
        }
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(
                    text = recurrence?.describe() ?: REPEAT_NEVER,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                )
                Text(if (expanded) "▾" else "▸")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text(REPEAT_NEVER) },
                    onClick = {
                        onRecurrenceChange(null)
                        custom = false
                        expanded = false
                    },
                )
                RecurrenceUnit.entries.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text("Every ${unit.singular}") },
                        onClick = {
                            onRecurrenceChange(Recurrence(unit, interval = 1))
                            custom = false
                            expanded = false
                        },
                    )
                }
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Custom…") },
                    onClick = {
                        val base = recurrence ?: Recurrence(RecurrenceUnit.DAY, interval = 1)
                        onRecurrenceChange(base.copy(interval = maxOf(2, base.interval)))
                        custom = true
                        expanded = false
                    },
                )
            }
        }

        if (recurrence != null && custom) {
            IntervalStepper(
                recurrence = recurrence,
                onRecurrenceChange = onRecurrenceChange,
            )
        }
        if (recurrence?.unit == RecurrenceUnit.WEEK) {
            WeekdayChips(
                recurrence = recurrence,
                anchor = anchor,
                onRecurrenceChange = onRecurrenceChange,
            )
        }
    }
}

/** "Every [–] N [+] days" — the custom row. The interval floors at 1, which is the preset again. */
@Composable
private fun IntervalStepper(
    recurrence: Recurrence,
    onRecurrenceChange: (Recurrence) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp),
    ) {
        TextButton(
            onClick = { onRecurrenceChange(recurrence.copy(interval = maxOf(1, recurrence.interval - 1))) },
            enabled = recurrence.interval > 1,
        ) { Text("−") }
        Text(
            text = recurrence.interval.toString(),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        TextButton(
            onClick = { onRecurrenceChange(recurrence.copy(interval = recurrence.interval + 1)) },
        ) { Text("+") }
        Text(
            text = if (recurrence.interval == 1) recurrence.unit.singular else recurrence.unit.plural,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Which weekdays a weekly repeat lands on. Selecting none falls back to the anchor date's own
 * weekday, which is what an unedited "every week" already means — so the empty state is a real
 * choice rather than a broken one, and the caption says which day that is.
 */
@Composable
private fun WeekdayChips(
    recurrence: Recurrence,
    anchor: LocalDate?,
    onRecurrenceChange: (Recurrence) -> Unit,
) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            DayOfWeek.entries.forEach { day ->
                val selected = day in recurrence.daysOfWeek
                OutlinedButton(
                    onClick = {
                        val next = if (selected) {
                            recurrence.daysOfWeek - day
                        } else {
                            recurrence.daysOfWeek + day
                        }
                        onRecurrenceChange(recurrence.copy(daysOfWeek = next))
                    },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier.width(40.dp),
                ) {
                    Text(
                        text = day.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
        }
        if (recurrence.daysOfWeek.isEmpty() && anchor != null) {
            Text(
                text = "On ${anchor.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())}s.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

private const val REPEAT_NEVER = "Never"

private const val UNASSIGNED = "Unassigned"
private const val NEW_PROJECT = "New Project"
