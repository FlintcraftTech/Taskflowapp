package com.example.taskflow.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.taskflow.R
import com.example.taskflow.data.model.ScheduleSlot
import com.example.taskflow.ui.common.ReorderableColumn
import java.time.LocalDate

/**
 * One Schedule page: the slot's active tasks, each tappable (to edit) with a leading checkbox (to
 * complete). On Today, the greyed completed tray (SPEC §Completed task tray on Today) hangs below the
 * active list. An empty state shows only when there is nothing active *and* nothing in the tray.
 *
 * The active list is reorderable by drag (SPEC §Reorder within a Schedule slot) — long-press a row
 * and move it, and the new order persists per slot. The tray is not reorderable: completed tasks
 * carry no sequence the user has a use for.
 */
@Composable
fun SlotPage(
    slot: ScheduleSlot,
    tasks: List<ScheduleTaskUi>,
    completed: List<ScheduleTaskUi>,
    onToggleComplete: (Long, LocalDate?, Boolean) -> Unit,
    onTaskClick: (Long) -> Unit,
    onReorder: (List<Long>) -> Unit,
    modifier: Modifier = Modifier,
    onTaskDragStart: (Long) -> Unit = {},
    onTaskDragHorizontal: (Float) -> Unit = {},
    onTaskDragPosition: (Offset?) -> Unit = {},
    onTaskDragEnd: (Long) -> Unit = {},
) {
    val showTray = slot == ScheduleSlot.TODAY && completed.isNotEmpty()
    if (tasks.isEmpty() && !showTray) {
        EmptyState(slot, modifier)
        return
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        ReorderableColumn(
            items = tasks,
            // Keyed on ScheduleTaskUi.key, not the task id: a recurring task renders as several
            // rows and they would otherwise collide on one key (SPEC §Recurring tasks).
            keySelector = { it.key },
            onMove = { from, to ->
                val reordered = tasks.toMutableList().also { it.add(to, it.removeAt(from)) }
                // Distinct because a recurring task contributes several rows to one slot and the
                // stored order is per task, not per instance.
                onReorder(reordered.map { it.id }.distinct())
            },
            // The same gesture's sideways component drives the drag-between-screens behaviour
            // (SPEC §Drag a task between Schedule screens): the page turns under the held task and
            // dropping it on the new page reschedules it. One long-press drag does both.
            onDragStart = { onTaskDragStart(it.id) },
            onHorizontalDrag = { _, dx -> onTaskDragHorizontal(dx) },
            onDragPosition = onTaskDragPosition,
            onDragEnd = { onTaskDragEnd(it.id) },
        ) { task, isDragging ->
            TaskRow(
                task = task,
                completed = false,
                isDragging = isDragging,
                // The instance date belongs to the row's own task. A child ticked inside a
                // recurring parent is an ordinary completion, not an instance of anything.
                onToggle = { id, checked ->
                    onToggleComplete(id, if (id == task.id) task.instanceDate else null, checked)
                },
                onClick = { onTaskClick(task.id) },
            )
            HorizontalDivider()
        }
        if (showTray) {
            CompletedHeader()
            completed.forEach { task ->
                key("done-${task.key}") {
                    TaskRow(
                        task = task,
                        completed = true,
                        isDragging = false,
                        // The instance date belongs to the row's own task. A child ticked inside a
                // recurring parent is an ordinary completion, not an instance of anything.
                onToggle = { id, checked ->
                    onToggleComplete(id, if (id == task.id) task.instanceDate else null, checked)
                },
                        onClick = { onTaskClick(task.id) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

/**
 * One task row, and — when the task is a parent — its children indented beneath it.
 *
 * A parent has **no checkbox of its own**: its completion is derived from its children, so where a
 * checkbox would sit it carries an expand/collapse control instead (SPEC §Parent tasks
 * expand/collapse instead of having a checkbox). Ticking the last child completes the parent;
 * un-ticking any child brings it back.
 *
 * [onToggle] is called with the id of whatever was actually ticked, which for a child is the child.
 */
@Composable
private fun TaskRow(
    task: ScheduleTaskUi,
    completed: Boolean,
    isDragging: Boolean,
    onToggle: (Long, Boolean) -> Unit,
    onClick: () -> Unit,
) {
    var expanded by rememberSaveable(task.id) { mutableStateOf(false) }

    // A picked-up row lifts off the page so it reads as held rather than as merely highlighted.
    Surface(tonalElevation = if (isDragging) 4.dp else 0.dp) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick)
                    .padding(end = 16.dp, top = 4.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Top,
            ) {
                if (task.isParent) {
                    // Sized to a checkbox's footprint so parent and ordinary rows line up.
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { expanded = !expanded },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (expanded) "▾" else "▸",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    Checkbox(
                        checked = completed,
                        onCheckedChange = { checked -> onToggle(task.id, checked) },
                    )
                }
                // Title takes the available width and wraps to multiple lines when long.
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (completed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (completed) TextDecoration.LineThrough else null,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 12.dp),
                )
                // Date label (when shown) stays aligned to the top-right of the row.
                if (task.dateLabel != null) {
                    Text(
                        text = task.dateLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
            }
            if (expanded) {
                task.subtasks.forEach { child ->
                    key(child.id) {
                        SubtaskRow(child = child, onToggle = onToggle)
                    }
                }
            }
        }
    }
}

/** A child, indented under its parent. It carries no date and no Project of its own. */
@Composable
private fun SubtaskRow(child: ScheduleTaskUi, onToggle: (Long, Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, end = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Checkbox(
            checked = child.isCompleted,
            onCheckedChange = { checked -> onToggle(child.id, checked) },
        )
        Text(
            text = child.title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (child.isCompleted) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            textDecoration = if (child.isCompleted) TextDecoration.LineThrough else null,
            modifier = Modifier
                .weight(1f)
                .padding(top = 12.dp),
        )
    }
}

/** Divider-and-label that opens the greyed completed tray at the bottom of Today. */
@Composable
private fun CompletedHeader() {
    Text(
        text = "Completed",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

/**
 * A Schedule slot with nothing on it.
 *
 * Tone follows UX principle 4: an empty list is a normal state, not a failure. Nothing here chides
 * the user or urges them to add something, and the wording differs per slot because the same
 * sentence means different things on Today and on Soon. Later has no plain empty state — it is
 * always a list of Project cards (see LaterPage).
 */
@Composable
private fun EmptyState(slot: ScheduleSlot, modifier: Modifier = Modifier) {
    val message = when (slot) {
        ScheduleSlot.TODAY -> stringResource(R.string.empty_today)
        ScheduleSlot.TOMORROW -> stringResource(R.string.empty_tomorrow)
        ScheduleSlot.SOON -> stringResource(R.string.empty_soon)
        ScheduleSlot.LATER -> stringResource(R.string.empty_card_project)
    }
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // A quiet mark rather than an illustration: something to rest the eye on that does not
            // read as an error state or as a prompt to act.
            Text(
                text = "·",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp),
            )
        }
    }
}

internal fun ScheduleSlot.displayName(): String = when (this) {
    ScheduleSlot.TODAY -> "Today"
    ScheduleSlot.TOMORROW -> "Tomorrow"
    ScheduleSlot.SOON -> "Soon"
    ScheduleSlot.LATER -> "Later"
}
