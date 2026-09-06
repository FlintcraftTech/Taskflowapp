package com.example.taskflow.ui.schedule

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskflow.TaskflowApplication
import com.example.taskflow.data.model.ScheduleSlot
import com.example.taskflow.ui.common.DragTarget
import com.example.taskflow.ui.common.DragTargetRow
import com.example.taskflow.ui.history.SearchScreen
import com.example.taskflow.ui.history.YesterdayScreen
import com.example.taskflow.ui.navigation.SpinePage
import com.example.taskflow.ui.strategy.StrategyScreen
import com.example.taskflow.ui.strategy.StrategyViewModel
import kotlinx.coroutines.launch

/**
 * The navigation spine: the four Schedule slots (Today, Tomorrow, Soon, Later) as a left-to-right
 * HorizontalPager (SPEC §Schedule view, UX principle 3). Today is the default open page; swiping
 * moves between adjacent pages, and the drawer can jump to any slot by driving the shared
 * [pagerState]. Later is grouped by Project (see [LaterPage]); Projects have no page of their own.
 *
 * Header: the current page's name, centred in a fixed-width frame (as wide as the longest label),
 * sliding in the direction of travel, flanked by chevrons that hide at the spine's ends. The menu
 * key sits in the top-left corner the layout leaves clear. The end corner holds the pick-up delete
 * target while a task is held, and otherwise a page's own action where it has one — only Strategy
 * does, with its Share button.
 */
@Composable
fun ScheduleScreen(
    pagerState: PagerState,
    onMenuClick: () -> Unit,
    onTaskClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    focusedProjectId: Long? = null,
    onFocusProject: (Long?) -> Unit = {},
) {
    val context = LocalContext.current
    val app = context.applicationContext as TaskflowApplication
    val viewModel: ScheduleViewModel =
        viewModel(
            factory = ScheduleViewModel.factory(
                app.taskRepository,
                app.projectRepository,
                app.settingsRepository,
            ),
        )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    // The Strategy doc's view-model is obtained here rather than inside StrategyScreen, because the
    // spine header — which now carries the doc's Share action — is a sibling of the pager and cannot
    // reach into the page. Same factory call, one level up.
    val strategyViewModel: StrategyViewModel = viewModel(
        factory = StrategyViewModel.factory(app.projectRepository, app.strategyRepository),
    )
    val strategySections by strategyViewModel.sections.collectAsStateWithLifecycle()

    // Drag-between-screens state (SPEC §Drag a task between Schedule screens). While a task is held,
    // sideways movement accumulates here; crossing DRAG_PAGE_THRESHOLD turns the page under it, and
    // releasing on a page other than the one it started on reschedules it to that page's slot.
    var dragOriginPage by remember { mutableStateOf<Int?>(null) }
    var dragSideways by remember { mutableFloatStateOf(0f) }

    // Drag-target state (SPEC §Drag-target icons). The row appears whenever a task is held, and the
    // target under the finger is remembered so the drop can act on it. From a Schedule screen the
    // target is the bin — promote belongs to subtask drags inside the edit dialogue.
    var draggedTaskId by remember { mutableStateOf<Long?>(null) }
    var dragPosition by remember { mutableStateOf<Offset?>(null) }
    var hoveredTarget by remember { mutableStateOf<DragTarget?>(null) }
    val clipboard = LocalClipboardManager.current

    // The view-model owns the filtering; the caller owns the value, because capture needs it too.
    LaunchedEffect(focusedProjectId) { viewModel.setFocusedProject(focusedProjectId) }
    val focusedProjectName = uiState.laterCards
        .firstOrNull { it.projectId == focusedProjectId }
        ?.projectName

    Column(modifier = modifier.fillMaxSize()) {
        SpineHeader(
            page = SpinePage.entries[pagerState.currentPage],
            hasPrevious = pagerState.currentPage > 0,
            hasNext = pagerState.currentPage < SpinePage.entries.lastIndex,
            focusedProjectName = focusedProjectName,
            onExitFocus = { onFocusProject(null) },
            onMenuClick = onMenuClick,
            onPrevious = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
            onNext = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
            trailing = if (SpinePage.entries[pagerState.currentPage] == SpinePage.STRATEGY) {
                {
                    TextButton(
                        onClick = {
                            val share = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    strategyViewModel.renderMarkdown(strategySections),
                                )
                            }
                            context.startActivity(Intent.createChooser(share, null))
                        },
                        enabled = strategySections.isNotEmpty(),
                    ) {
                        Text("Share")
                    }
                }
            } else {
                null
            },
        )
        HorizontalDivider()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
        HorizontalPager(
            state = pagerState,
            // The neighbouring pages stay composed so a task dragged across the boundary keeps the
            // gesture alive — the row's pointer input lives in the page it started on, and a
            // disposed page cancels the drag mid-move.
            beyondViewportPageCount = 1,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            val slot = SpinePage.entries[page].slot
            if (slot == null) {
                // A page with no Schedule slot is not a list of tasks to put things on — it is
                // history. Which one it is comes from the page itself.
                when (SpinePage.entries[page]) {
                    SpinePage.SEARCH -> SearchScreen(
                        onNavigateToSlot = { destination ->
                            val target = SpinePage.entries.first { it.slot == destination }
                            scope.launch { pagerState.animateScrollToPage(target.ordinal) }
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                    SpinePage.YESTERDAY -> YesterdayScreen(modifier = Modifier.fillMaxSize())
                    SpinePage.STRATEGY -> StrategyScreen(
                        sections = strategySections,
                        onDescriptionChange = strategyViewModel::setDescription,
                        modifier = Modifier.fillMaxSize(),
                    )
                    else -> Unit
                }
            } else if (slot == ScheduleSlot.LATER) {
                // Later is grouped by Project — a list of expand/collapse cards, not a flat list.
                LaterPage(
                    cards = uiState.laterCards,
                    onToggleComplete = viewModel::setCompleted,
                    onTaskClick = onTaskClick,
                    onReorder = viewModel::reorderCard,
                    onFocusProject = { onFocusProject(it) },
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                SlotPage(
                    slot = slot,
                    tasks = uiState.forSlot(slot),
                    completed = uiState.completed,
                    onToggleComplete = viewModel::setCompleted,
                    onTaskClick = onTaskClick,
                    onReorder = viewModel::reorderSlot,
                    modifier = Modifier.fillMaxSize(),
                    onTaskDragStart = { taskId ->
                        dragOriginPage = page
                        dragSideways = 0f
                        draggedTaskId = taskId
                    },
                    onTaskDragPosition = { dragPosition = it },
                    onTaskDragHorizontal = { dx ->
                        dragSideways += dx
                        // A push past the threshold turns one page, and the accumulator resets so a
                        // continued push can turn the next. The spine's ends simply don't move.
                        if (dragSideways <= -DRAG_PAGE_THRESHOLD &&
                            pagerState.currentPage < SpinePage.entries.lastIndex
                        ) {
                            dragSideways = 0f
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else if (dragSideways >= DRAG_PAGE_THRESHOLD && pagerState.currentPage > 0) {
                            dragSideways = 0f
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                        }
                    },
                    onTaskDragEnd = { taskId ->
                        // A drop on a target wins over a drop on a page: the user aimed at the bin,
                        // so rescheduling the task to whichever page happens to be under it would
                        // be acting on the gesture they didn't make.
                        when (hoveredTarget) {
                            DragTarget.BIN -> viewModel.deleteTask(taskId)
                            DragTarget.CUT -> viewModel.cutTask(taskId) { text ->
                                clipboard.setText(AnnotatedString(text))
                            }
                            else -> {
                                val origin = dragOriginPage
                                val landed = pagerState.currentPage
                                val landedSlot = SpinePage.entries[landed].slot
                                // A page with no slot is not a drop destination — dropping a task
                                // on Yesterday would have to mean something, and it doesn't.
                                if (origin != null && origin != landed && landedSlot != null) {
                                    viewModel.rescheduleToSlot(taskId, landedSlot)
                                }
                            }
                        }
                        dragOriginPage = null
                        dragSideways = 0f
                        draggedTaskId = null
                        dragPosition = null
                        hoveredTarget = null
                    },
                )
            }
        }
            // Pinned to the top-right corner the spine header keeps clear (SPEC §Drag-target icons).
            DragTargetRow(
                visible = draggedTaskId != null,
                targets = listOf(DragTarget.BIN, DragTarget.CUT),
                dragPosition = dragPosition,
                onHoverChange = { hoveredTarget = it },
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
    }
}

/** The longest spine label — sizes the title frame so it doesn't resize as the word changes. */
private val LONGEST_LABEL: String = SpinePage.entries.maxByOrNull { it.title.length }!!.title

/**
 * How far sideways a held task must be pushed before the page turns under it, in pixels. Set well
 * above an incidental wobble during a vertical reorder, and well below a deliberate sweep toward
 * the edge, so the two halves of the same gesture stay distinguishable.
 */
private const val DRAG_PAGE_THRESHOLD = 140f

@Composable
private fun SpineHeader(
    page: SpinePage,
    hasPrevious: Boolean,
    hasNext: Boolean,
    focusedProjectName: String?,
    onExitFocus: () -> Unit,
    onMenuClick: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
) {
    val focused = focusedProjectName != null
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // While focused the bar takes a distinct colour, so the user can always see that they
            // are looking at one area rather than everything (SPEC §Focus on one Project
            // temporarily). Focus announcing itself is what makes it a lens rather than a mode.
            .background(
                if (focused) {
                    MaterialTheme.colorScheme.tertiaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                },
            )
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Three slots laid out side by side rather than stacked on top of each other. The end slot
        // used to be positioned over the centred title, so a focused Project's name drew across the
        // next-page chevron — the header read "AU>DIT-PROJ" with a ten-character name, and a longer
        // one buried the chevron entirely. Giving each slot its own horizontal space means the name
        // yields to the chevron however long it is, by construction rather than by fitting.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Menu key in the cleared top-left corner (SPEC §Side menu).
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable(onClick = onMenuClick),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "☰",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            // Current-page title flanked by chevrons, centred in whatever the two ends leave.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f),
            ) {
                Chevron(
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous day",
                    visible = hasPrevious,
                    onClick = onPrevious,
                )
                AnimatedSpineTitle(page = page)
                Chevron(
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next day",
                    visible = hasNext,
                    onClick = onNext,
                )
            }
            // The end slot. While focused it carries the Project's name with an X, so leaving focus
            // is always one tap away; otherwise it carries the page's own action. Only Strategy
            // supplies one, and the corner is otherwise the pick-up delete target's — which claims
            // it only while a task is held, and Strategy holds headings and paragraphs rather than
            // tasks, so the two never collide.
            if (focusedProjectName != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    // Capped so a long Project name cannot squeeze the title and chevrons out of
                    // the middle; past the cap the name is ellipsised instead.
                    modifier = Modifier
                        .widthIn(max = FOCUS_CHIP_MAX_WIDTH)
                        .padding(end = 4.dp),
                ) {
                    Text(
                        text = focusedProjectName,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(onClick = onExitFocus),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "✕",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                }
            } else if (trailing != null) {
                Box(
                    modifier = Modifier.padding(end = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    trailing()
                }
            } else {
                // Balances the menu key, so with nothing at the end the title stays optically
                // centred in the bar exactly as it did before this became a Row.
                Spacer(modifier = Modifier.size(48.dp))
            }
        }
    }
}

// A focused Project's name gets at most this much of the bar. Derived from the bar's other
// occupants rather than picked: the 48.dp menu key, two chevrons and the title frame need the
// middle, and the X inside this chip is 40.dp of it — so the name itself keeps a readable ~90.dp
// and ellipsises past that instead of pushing anything aside.
private val FOCUS_CHIP_MAX_WIDTH = 136.dp

/** The current page name, in a fixed-width frame, sliding in the same direction as a page move. */
@Composable
private fun AnimatedSpineTitle(page: SpinePage) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.clipToBounds()) {
        // Invisible widest label fixes the frame width so the title doesn't reflow per word.
        Text(
            text = LONGEST_LABEL,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.alpha(0f),
        )
        AnimatedContent(
            targetState = page,
            transitionSpec = {
                // Forward (to a later page): word leaves left, next enters from the right — the same
                // direction the page content travels. Backward mirrors it.
                val dir = if (targetState.ordinal > initialState.ordinal) 1 else -1
                // The incoming title starts before the outgoing has fully left (delay < duration),
                // so the motion reads as one continuous slide rather than two separate beats. The
                // crossfade across that brief overlap keeps it soft — the outgoing is fading out as
                // the incoming fades in, so the two never appear as solid titles colliding in the
                // centre (the original overlap bug).
                val duration = 180
                val delay = 100
                (slideInHorizontally(
                    animationSpec = tween(durationMillis = duration, delayMillis = delay),
                ) { width -> dir * width } + fadeIn(tween(durationMillis = duration, delayMillis = delay)))
                    .togetherWith(
                        slideOutHorizontally(
                            animationSpec = tween(durationMillis = duration),
                        ) { width -> -dir * width } + fadeOut(tween(durationMillis = duration)),
                    )
            },
            label = "spineTitle",
        ) { current ->
            Text(
                text = current.title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** A tappable chevron with a fixed 48dp touch target, sitting immediately beside the title. The
 *  Material chevron icon is centred in the box, which the enclosing Row centres vertically against
 *  the title. When [visible] is false it keeps its space (so the title stays centred) but shows
 *  nothing — used at the spine's ends. */
@Composable
private fun Chevron(icon: ImageVector, contentDescription: String, visible: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .then(if (visible) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (visible) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
