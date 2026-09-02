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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.taskflow.R
import com.example.taskflow.ui.common.ReorderableColumn
import java.time.LocalDate

/**
 * The Later page (SPEC §Schedule view — Later grouped by Project). Unlike the other Schedule slots,
 * Later is a vertical list of expand/collapse cards — one per Project, every Project shown (even
 * empty), with the system Unassigned card pinned to the bottom. The cards arrive already ordered by
 * [ScheduleViewModel] (Strategy-doc order, Unassigned last); this composable only renders them.
 *
 * A card **opens showing its first few tasks** — the peek — with the rest behind the expand/collapse
 * control, so Later reads as a calm overview of the user's areas of life rather than a wall of tasks
 * (SPEC §Schedule view). A card holding no more than the peek shows no control at all, since there
 * is nothing hidden to reveal.
 */
@Composable
fun LaterPage(
    cards: List<LaterProjectCard>,
    onToggleComplete: (Long, LocalDate?, Boolean) -> Unit,
    onTaskClick: (Long) -> Unit,
    onReorder: (List<Long>) -> Unit,
    onFocusProject: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // Before the user has made a Project of their own, Later holds only the pinned Unassigned
        // card. A line above it says what the page is for, so a nearly-blank screen reads as a
        // beginning rather than as something missing.
        if (cards.none { !it.isUnassigned }) {
            Text(
                text = stringResource(R.string.empty_later_no_projects),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
            )
        }
        cards.forEach { card ->
            key(card.projectId) {
                ProjectCard(
                    card = card,
                    onToggleComplete = onToggleComplete,
                    onTaskClick = onTaskClick,
                    onReorder = onReorder,
                    onFocusProject = onFocusProject,
                )
                HorizontalDivider()
            }
        }
    }
}

/**
 * One Project's card: a tappable header (name + task count + chevron) over an expandable body. The
 * body shows the Project's Later tasks, or a plain empty message when the Project has none — empty
 * cards still appear so the user sees every area of life on Later.
 */
@Composable
private fun ProjectCard(
    card: LaterProjectCard,
    onToggleComplete: (Long, LocalDate?, Boolean) -> Unit,
    onTaskClick: (Long) -> Unit,
    onReorder: (List<Long>) -> Unit,
    onFocusProject: (Long) -> Unit,
) {
    // Per-card, keyed by projectId so each card keeps its own state. False means "showing the
    // peek" rather than "showing nothing" — see the body below.
    var expanded by rememberSaveable(card.projectId) { mutableStateOf(false) }

    Surface(tonalElevation = if (card.isUnassigned) 0.dp else 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    // Tapping the header enters focus on this Project (SPEC §Focus on one Project
                    // temporarily) — deliberately distinct from the chevron, which expands the card.
                    .clickable { onFocusProject(card.projectId) }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = card.projectName,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (card.isUnassigned) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = card.tasks.size.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                // Text glyph rather than a Material icon — the icon pack isn't a project dependency
                // (see the spine-header chevron capture). ▾ = expanded, ▸ = more to show.
                // It carries its own click target, separate from the header, because the header is
                // not the expand control: SPEC §Schedule view keeps expand/collapse distinct.
                // A card with nothing hidden shows no chevron — there is nothing to reveal.
                if (card.tasks.size > PEEK_TASK_COUNT) {
                    Text(
                        text = if (expanded) "▾" else "▸",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable { expanded = !expanded },
                    )
                }
            }
            HorizontalDivider()
            if (card.tasks.isEmpty()) {
                EmptyCardMessage(
                    isUnassigned = card.isUnassigned,
                    nearTermTaskCount = card.nearTermTaskCount,
                )
            } else {
                // The peek: a card opens showing its first few tasks, and the expand control
                // reveals the rest (SPEC §Schedule view). The small peek is what keeps Later a calm
                // overview of the user's areas of life rather than a wall of tasks.
                val visible = if (expanded) card.tasks else card.tasks.take(PEEK_TASK_COUNT)
                // Within-card drag-reorder, on the same primitive the flat slots use — this is the
                // nested case it was designed against (SPEC §Reorder within a Schedule slot).
                // Reordering acts on what is visible: while a card shows only its peek, a row can be
                // moved within those rows, and expanding first is what reaches the rest.
                ReorderableColumn(
                    items = visible,
                    keySelector = { it.key },
                    onMove = { from, to ->
                        val reordered = visible.toMutableList().also { it.add(to, it.removeAt(from)) }
                        val movedIds = reordered.map { it.id }
                        // The peek is a window onto the card's list, so the tail beyond it keeps
                        // its own order and follows the reordered head.
                        val tailIds = card.tasks.drop(visible.size).map { it.id }
                        onReorder((movedIds + tailIds).distinct())
                    },
                ) { task, isDragging ->
                    TaskRow(
                        task = task,
                        isDragging = isDragging,
                        onToggleComplete = onToggleComplete,
                        onTaskClick = onTaskClick,
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

/** A Later-card task row: leading checkbox completes it; tapping the row opens its edit dialogue. */
@Composable
private fun TaskRow(
    task: ScheduleTaskUi,
    isDragging: Boolean,
    onToggleComplete: (Long, LocalDate?, Boolean) -> Unit,
    onTaskClick: (Long) -> Unit,
) {
    var expanded by rememberSaveable(task.id) { mutableStateOf(false) }

    // A picked-up row lifts off the card, the same signal the flat slots give.
    Surface(tonalElevation = if (isDragging) 4.dp else 0.dp) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTaskClick(task.id) }
                    .padding(end = 16.dp, top = 4.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Top,
            ) {
                if (task.isParent) {
                    // A parent has no checkbox — its completion rolls up from its children
                    // (SPEC §Parent tasks expand/collapse instead of having a checkbox).
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
                    // Tasks here are active (completed rows leave for the Today tray), so the box
                    // is unchecked, and checking it completes the task and sends it to the tray.
                    Checkbox(
                        checked = false,
                        onCheckedChange = { checked ->
                            onToggleComplete(task.id, task.instanceDate, checked)
                        },
                    )
                }
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 12.dp),
                )
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 32.dp, end = 16.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Checkbox(
                                checked = child.isCompleted,
                                // A child inside a recurring parent is an ordinary completion,
                                // not an instance of anything — hence the null instance date.
                                onCheckedChange = { checked ->
                                    onToggleComplete(child.id, null, checked)
                                },
                            )
                            Text(
                                text = child.title,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(top = 12.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * A card with nothing in it — which happens for two different reasons, and says which.
 *
 * A Project can have no tasks at all, or it can have tasks that are all dated for the next few days
 * and therefore living on the Schedule rather than in the card. "Nothing in here yet" is simply
 * false in the second case, and it was misleading on a real device: a Project created from a
 * Tomorrow-dated task showed an empty card while the task sat correctly on Tomorrow.
 *
 * The fix is distinguishing copy rather than a task count on every card. A count on every card
 * would change what every card shows, against SPEC §Schedule view's statement that the small peek
 * is what keeps Later a calm overview; this sentence costs nothing when the card is not empty,
 * because it only appears when it is.
 */
@Composable
private fun EmptyCardMessage(isUnassigned: Boolean, nearTermTaskCount: Int) {
    val message = when {
        nearTermTaskCount > 0 -> pluralStringResource(
            R.plurals.empty_card_on_schedule,
            nearTermTaskCount,
            nearTermTaskCount,
        )
        isUnassigned -> stringResource(R.string.empty_card_unassigned)
        else -> stringResource(R.string.empty_card_project)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** How many of a Project's tasks a card shows before the expand control (SPEC: "the first ~3"). */
private const val PEEK_TASK_COUNT = 3
