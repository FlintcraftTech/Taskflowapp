package com.example.taskflow.ui.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskflow.TaskflowApplication
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * The task edit dialogue (SPEC §Edit a task), shown full-screen over the spine or a Project view.
 * Edits title, notes, and Project (including "unassigned"); the date is displayed but read-only this
 * batch — the side-scrolling date picker that makes it editable is batch 0006.
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
            target,
        ),
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
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
            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Notes") },
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
        }
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
            dateFormatter = DATE_TILE_FORMATTER,
            onSelectDate = onSelectDate,
            onClearDate = onClearDate,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// DD/MM, the SPEC default. The MM/DD alternative is the Settings → Date format item.
private val DATE_TILE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM")

private const val UNASSIGNED = "Unassigned"
private const val NEW_PROJECT = "New Project"
