package com.example.taskflow.ui.edit

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

/**
 * The text shape behind the edit dialogue's outliner (SPEC §Edit dialogue: outliner-style typing
 * for subtasks): a task and its subtasks are one block of text — the parent's title on the top
 * line, each child indented beneath it — so breaking work down happens through ordinary typing
 * rather than in a separate mode.
 *
 * Parsing and rendering live here, apart from the composable, because they are the part with rules
 * worth stating and testing: what counts as indented, what an empty line means, and what happens to
 * a stray second unindented line.
 */
data class Outline(
    val parentTitle: String,
    val children: List<String>,
) {
    /** The editable text: parent on line 1, one indented line per child. */
    fun render(): String = buildString {
        append(parentTitle)
        children.forEach { child ->
            append('\n')
            append(INDENT)
            append(child)
        }
    }

    companion object {
        /** Two spaces. Short enough to backspace through in one press, wide enough to read. */
        const val INDENT: String = "  "

        /**
         * Reads the edited text back into a parent and its children.
         *
         * The first line is the parent, whatever its indentation — a user who indents everything
         * still has a task rather than an orphaned list. Every later line is a child, indented or
         * not: a line the user typed without the indent is far more likely to be a child they
         * meant than a second parent this dialogue has no way to hold.
         *
         * **Empty lines are kept.** A child is empty at the exact moment it is created — pressing
         * Enter opens a blank line for the user to type into — so dropping empty lines here would
         * discard every new subtask before it could be typed into, which is precisely what it used
         * to do. Abandoning a half-typed line still leaves nothing behind, but that now happens on
         * save (see EditTaskViewModel), which is the moment the rule was actually written for.
         */
        fun parse(text: String): Outline {
            val lines = text.split('\n').map { it.trim() }
            if (lines.isEmpty()) return Outline("", emptyList())
            return Outline(parentTitle = lines.first(), children = lines.drop(1))
        }

        /**
         * Normalises text as it is typed: the first line sits flush left and every later line
         * carries exactly one indent. This is what makes Enter produce a child — the new line is
         * indented for the user rather than by them — and what stops the indentation drifting as
         * lines are merged and split.
         *
         * Blank lines are left alone rather than indented, so a half-typed new line does not
         * acquire whitespace the user then has to delete.
         */
        fun normalise(text: String): String =
            text.split('\n').mapIndexed { index, line ->
                val bare = line.trimStart()
                when {
                    index == 0 -> bare
                    bare.isEmpty() -> ""
                    else -> INDENT + bare
                }
            }.joinToString("\n")
    }
}

/**
 * How the edited outline maps onto the child rows already in the database.
 *
 * Children are matched **by position**, not by text: renaming the second subtask should edit that
 * row rather than delete one and create another, which would throw away its completion state. Lines
 * beyond the existing children are new rows; existing rows beyond the lines are deleted.
 */
data class ChildSync(
    val updates: List<Pair<Long, String>>,
    val insertions: List<String>,
    val deletions: List<Long>,
) {
    companion object {
        fun of(existingIds: List<Long>, existingTitles: List<String>, edited: List<String>): ChildSync {
            val updates = mutableListOf<Pair<Long, String>>()
            existingIds.forEachIndexed { index, id ->
                val newTitle = edited.getOrNull(index) ?: return@forEachIndexed
                if (newTitle != existingTitles.getOrNull(index)) updates.add(id to newTitle)
            }
            return ChildSync(
                updates = updates,
                insertions = edited.drop(existingIds.size),
                deletions = existingIds.drop(edited.size),
            )
        }
    }
}

/**
 * The outliner as the user meets it (SPEC §Edit dialogue: outliner-style typing for subtasks): the
 * parent's title on the top line, each subtask on its own indented line beneath, and the whole
 * thing behaving like ordinary typing — Enter at the end of a line starts a subtask, backspace at
 * the start of one merges it into the line above.
 *
 * It is a field per line rather than one multi-line box, because **child lines carry drag handles
 * and the parent line does not** (SPEC §Drag-target icons). A single text box has no per-line
 * anchor to hang a handle on, so the shape of the editor follows from the promote and bin targets
 * needing something to grab.
 *
 * Focus moves the way the text does: splitting a line focuses the new one, merging focuses the line
 * the text merged into. Without that the user types Enter and finds the cursor still on the line
 * above, which is the tell that an outliner is faked.
 */
@Composable
fun OutlinerField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onChildDragStart: (index: Int) -> Unit = {},
    onChildDragPosition: (Offset?) -> Unit = {},
    onChildDragEnd: (index: Int) -> Unit = {},
) {
    val outline = Outline.parse(text)
    val lines = listOf(outline.parentTitle) + outline.children

    // Which line to put the cursor on after the next change, or -1 for "leave focus alone".
    var focusTarget by remember { mutableIntStateOf(-1) }
    val focusRequesters = remember(lines.size) { List(lines.size) { FocusRequester() } }
    var handleOrigin by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(focusTarget, lines.size) {
        if (focusTarget in focusRequesters.indices) {
            focusRequesters[focusTarget].requestFocus()
            focusTarget = -1
        }
    }

    fun emit(newLines: List<String>) {
        onTextChange(Outline(newLines.firstOrNull().orEmpty(), newLines.drop(1)).render())
    }

    Column(modifier = modifier.fillMaxWidth()) {
        lines.forEachIndexed { index, line ->
            key(index) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        // Child lines are indented; the parent sits flush left.
                        .padding(start = if (index == 0) 0.dp else 24.dp),
                ) {
                    if (index > 0) {
                        // The drag handle a subtask is picked up by. The parent line has none: it
                        // is already a task in its own right, so neither target applies to it.
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .onGloballyPositioned { handleOrigin = it.boundsInWindow().topLeft }
                                .pointerInput(index) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = { start ->
                                            onChildDragPosition(handleOrigin + start)
                                            onChildDragStart(index - 1)
                                        },
                                        onDrag = { change, _ ->
                                            change.consume()
                                            onChildDragPosition(handleOrigin + change.position)
                                        },
                                        onDragEnd = {
                                            onChildDragPosition(null)
                                            onChildDragEnd(index - 1)
                                        },
                                        onDragCancel = {
                                            onChildDragPosition(null)
                                        },
                                    )
                                },
                        ) {
                            Text(
                                text = "⠿",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    OutlinedTextField(
                        value = line,
                        onValueChange = { edited ->
                            // A paste arrives here as a value carrying newlines, since the field
                            // itself is single-line. Multi-line text is spliced in as separate
                            // lines, so a block cut out of Taskflow — or a list copied from a
                            // notes app — rebuilds as a task and its subtasks rather than landing
                            // as one run-on title (SPEC §Drag-target icons, the cut/paste flow).
                            if (edited.contains('\n')) {
                                val pasted = edited.split('\n').map { it.trim() }.filter { it.isNotEmpty() }
                                val updated = lines.toMutableList()
                                if (pasted.isEmpty()) {
                                    updated[index] = ""
                                } else {
                                    updated[index] = pasted.first()
                                    updated.addAll(index + 1, pasted.drop(1))
                                }
                                emit(updated)
                            } else {
                                emit(lines.toMutableList().also { it[index] = edited })
                            }
                        },
                        label = if (index == 0) {
                            { Text("Task") }
                        } else {
                            null
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                // Enter (rendered as Next on a single-line field) opens a subtask
                                // below this line and puts the cursor in it.
                                val updated = lines.toMutableList().also { it.add(index + 1, "") }
                                focusTarget = index + 1
                                emit(updated)
                            },
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequesters[index])
                            .onPreviewKeyEvent { event ->
                                val isBackspaceDown =
                                    event.type == KeyEventType.KeyDown && event.key == Key.Backspace
                                // Backspace on an empty child removes that line and returns the
                                // cursor to the one above — the merge, in the only case where
                                // nothing would be lost by it.
                                if (isBackspaceDown && index > 0 && line.isEmpty()) {
                                    val updated = lines.toMutableList().also { it.removeAt(index) }
                                    focusTarget = index - 1
                                    emit(updated)
                                    true
                                } else {
                                    false
                                }
                            },
                    )
                }
            }
        }
    }
}
