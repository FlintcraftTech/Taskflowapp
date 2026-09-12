package com.example.taskflow.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp

/**
 * One place a dragged task can be dropped (SPEC §Drag-target icons). The operations Taskflow
 * exposes on a picked-up task are conceptually parallel — each is "send this somewhere" — so they
 * share one gesture and one row of icons rather than each inventing its own affordance.
 */
enum class DragTarget(val glyph: String, val label: String) {
    /** Delete the task. */
    BIN("🗑", "Delete"),

    /** Remove it from Taskflow and put its content on the device clipboard. */
    CUT("✂", "Cut"),

    /** Make a subtask a task in its own right. Offered on dialogue subtask drags only. */
    PROMOTE("⬆", "Promote"),
}

/**
 * The row of drag targets that appears whenever a task is picked up, pinned to the top-right corner
 * the spine header deliberately leaves clear.
 *
 * It reports which target the finger is currently over via [hovered], so the caller can act on the
 * one under the finger when the drag ends. Hover is resolved here rather than by the caller because
 * only this composable knows where the icons ended up on screen — each records its own bounds as it
 * is laid out, and the drag position is tested against them.
 *
 * It also reports the area it has laid itself out into, via [onBoundsChange]. Reaching a target and
 * turning the page are the same sideways motion, and the page turn fires first, which left every
 * target unreachable; the caller suspends its page turn while the finger is inside this area. The
 * row reports where it actually ended up rather than the caller reserving a band of its own, so
 * there is no second measurement to keep in step with this one.
 *
 * Text glyphs rather than Material icons: the icon pack is not a project dependency.
 */
@Composable
fun DragTargetRow(
    visible: Boolean,
    targets: List<DragTarget>,
    dragPosition: Offset?,
    onHoverChange: (DragTarget?) -> Unit,
    modifier: Modifier = Modifier,
    onBoundsChange: (Rect?) -> Unit = {},
) {
    val bounds = remember { mutableMapOf<DragTarget, Rect>() }

    // While nothing is held the row is not on screen, so it claims no area at all.
    LaunchedEffect(visible) { if (!visible) onBoundsChange(null) }

    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut(), modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(8.dp)
                .onGloballyPositioned { onBoundsChange(it.boundsInWindow()) },
        ) {
            targets.forEach { target ->
                val hovered = dragPosition?.let { bounds[target]?.contains(it) } == true
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (hovered) {
                                MaterialTheme.colorScheme.errorContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                        )
                        .border(
                            width = if (hovered) 2.dp else 1.dp,
                            color = if (hovered) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.outlineVariant
                            },
                            shape = CircleShape,
                        )
                        .onGloballyPositioned { coordinates ->
                            bounds[target] = coordinates.boundsInWindow()
                        },
                ) {
                    Text(text = target.glyph, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }

    // Report the target under the finger, so the drop can act on it.
    val current = dragPosition?.let { position ->
        targets.firstOrNull { bounds[it]?.contains(position) == true }
    }
    LaunchedEffect(current) { onHoverChange(current) }
}
