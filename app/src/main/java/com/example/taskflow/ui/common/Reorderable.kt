package com.example.taskflow.ui.common

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

/**
 * The app's one drag primitive: a vertical list whose rows the user can pick up by long-press and
 * drag into a new position (SPEC §Reorder within a Schedule slot).
 *
 * It is written once and used on both reorderable surfaces — the flat Schedule slots and the task
 * list inside a Later Project card — because they are the same interaction and only differ in what
 * the new order is persisted as. Writing the flat case first and nesting it afterwards is how a
 * list primitive ends up with flat-list assumptions baked into it, so the nested case is handled
 * here from the start:
 *
 * - **Variable row heights.** Nothing assumes a fixed row height. Each row reports its own measured
 *   height as it is laid out, and the drag walks those actual heights to decide when the dragged
 *   row has passed its neighbour. A wrapped two-line title therefore behaves like any other row.
 * - **Nested scrolling.** This is a plain [Column], not a lazy list. A lazy list inside a Later
 *   card would sit inside the page's own vertical scroll with no bounded height to measure itself
 *   against, and its own scrolling would fight both the page and the drag. A Schedule slot holds a
 *   day's worth of tasks and a card holds one Project's, so laziness buys little here and costs the
 *   nesting behaviour it would break.
 * - **The horizontal axis is left to the caller.** [onHorizontalDrag] receives the sideways
 *   component of the same gesture, so dragging a task off the side of the page (to reschedule it
 *   onto another slot) composes with reordering rather than needing a second, competing gesture.
 *
 * Reordering is shown live and committed once: the rows rearrange under the finger against a local
 * copy of the order, and [onMove] is called with the final from/to only on release. Calling it on
 * every crossing would round-trip through the database mid-gesture and fight the finger.
 */
@Composable
fun <T> ReorderableColumn(
    items: List<T>,
    // Named keySelector rather than key so it does not shadow Compose's own key() below, which the
    // rows need in order to keep their state as they change position.
    keySelector: (T) -> Any,
    onMove: (fromIndex: Int, toIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    onDragStart: (T) -> Unit = {},
    onHorizontalDrag: (T, Float) -> Unit = { _, _ -> },
    // Where the finger is, in window coordinates, or null when nothing is held. The drag-target
    // icons hover-test against this; reordering itself has no use for it.
    onDragPosition: (Offset?) -> Unit = {},
    onDragEnd: (T) -> Unit = {},
    itemContent: @Composable (item: T, isDragging: Boolean) -> Unit,
) {
    // The order shown right now. Reset whenever the incoming list changes, which includes the
    // recomposition that follows a committed move — so the committed order replaces the local one
    // rather than the two drifting apart.
    var order by remember(items) { mutableStateOf(items) }
    var draggingKey by remember(items) { mutableStateOf<Any?>(null) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    val heights = remember { mutableStateMapOf<Any, Int>() }
    // Each row's top-left in window coordinates, so a pointer position local to a row can be
    // reported in the window's frame — which is the only frame the drag targets share with it.
    val origins = remember { mutableStateMapOf<Any, Offset>() }

    val currentOnMove by rememberUpdatedState(onMove)
    val haptics = LocalHapticFeedback.current

    Column(modifier = modifier) {
        order.forEach { item ->
            val itemKey = keySelector(item)
            val isDragging = draggingKey == itemKey
            key(itemKey) {
                Column(
                    modifier = Modifier
                        .onGloballyPositioned {
                            heights[itemKey] = it.size.height
                            origins[itemKey] = it.boundsInWindow().topLeft
                        }
                        // The dragged row rides above its neighbours and follows the finger; every
                        // other row sits where the live reordering has already put it.
                        .zIndex(if (isDragging) 1f else 0f)
                        .offset { IntOffset(0, if (isDragging) dragOffsetY.roundToInt() else 0) }
                        .pointerInput(itemKey) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = { start ->
                                    draggingKey = itemKey
                                    dragOffsetY = 0f
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onDragPosition((origins[itemKey] ?: Offset.Zero) + start)
                                    onDragStart(item)
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    onDragPosition(
                                        (origins[itemKey] ?: Offset.Zero) + change.position,
                                    )
                                    onHorizontalDrag(item, dragAmount.x)
                                    dragOffsetY += dragAmount.y
                                    val settled =
                                        rearrange(order, itemKey, dragOffsetY, heights, keySelector)
                                    order = settled.order
                                    dragOffsetY = settled.remainingOffset
                                },
                                onDragEnd = {
                                    commit(items, order, itemKey, keySelector, currentOnMove)
                                    draggingKey = null
                                    dragOffsetY = 0f
                                    onDragPosition(null)
                                    onDragEnd(item)
                                },
                                onDragCancel = {
                                    // Snap back: the local order is rebuilt from the source list,
                                    // so an interrupted drag leaves nothing half-applied.
                                    order = items
                                    draggingKey = null
                                    dragOffsetY = 0f
                                    onDragPosition(null)
                                    onDragEnd(item)
                                },
                            )
                        },
                ) {
                    itemContent(item, isDragging)
                }
            }
        }
    }
}

/** The order after any crossings the current drag offset has earned, and the offset left over. */
private data class Rearranged<T>(val order: List<T>, val remainingOffset: Float)

/**
 * Moves the dragged row past as many neighbours as [offsetY] covers, using each neighbour's own
 * measured height rather than an assumed row height. A neighbour is passed once the row has
 * travelled over half of it — the usual halfway rule, which makes the swap happen where the eye
 * expects it. Each swap consumes that neighbour's height from the offset, so the row keeps
 * following the finger rather than jumping.
 */
private fun <T> rearrange(
    order: List<T>,
    draggingKey: Any,
    offsetY: Float,
    heights: Map<Any, Int>,
    keyOf: (T) -> Any,
): Rearranged<T> {
    val working = order.toMutableList()
    var offset = offsetY
    // The dragged row is found by key rather than by position: it has usually already moved.
    var index = working.indexOfFirst { keyOf(it) == draggingKey }
    if (index < 0) return Rearranged(order, offsetY)

    while (true) {
        if (offset > 0 && index < working.lastIndex) {
            // A neighbour that has not been laid out yet has no measured height, so there is
            // nothing to compare against and the drag simply waits for it.
            val next = heights[keyOf(working[index + 1])] ?: break
            if (offset < next / 2f) break
            working.add(index + 1, working.removeAt(index))
            offset -= next
            index += 1
        } else if (offset < 0 && index > 0) {
            val previous = heights[keyOf(working[index - 1])] ?: break
            if (-offset < previous / 2f) break
            working.add(index - 1, working.removeAt(index))
            offset += previous
            index -= 1
        } else {
            break
        }
    }
    return Rearranged(working, offset)
}

/** Reports the net move once, on release: where the row started in the source list, and where it ended. */
private fun <T> commit(
    source: List<T>,
    shown: List<T>,
    draggingKey: Any,
    key: (T) -> Any,
    onMove: (Int, Int) -> Unit,
) {
    val from = source.indexOfFirst { key(it) == draggingKey }
    val to = shown.indexOfFirst { key(it) == draggingKey }
    if (from >= 0 && to >= 0 && from != to) onMove(from, to)
}
