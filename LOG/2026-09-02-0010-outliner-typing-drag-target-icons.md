# [HASH] — build [0010-outliner-typing-drag-target-icons]: the outliner became a field per line, because the drag handles needed something to hang from

SPEC §Edit dialogue: outliner-style typing for subtasks wants adding and breaking down subtasks to happen through ordinary typing rather than a separate mode — capturing a parent with children as fast as typing a paragraph, since that is the highest-frequency edit anyone makes when capturing work. SPEC §Drag-target icons wants a row of targets whenever a task is picked up, with **promote** offered on dialogue subtask drags only.

**The item's two requirements pulled against each other, and resolving that decided the shape.** It asks for one text area behaving like a paragraph editor, *and* for child lines to carry drag handles while the parent line does not. A single multi-line text box has no per-line anchor to hang a handle on. So the editor is a field per line — parent flush left with no handle, each child indented with one — with Enter opening a subtask line below and Backspace on an empty child merging it away, and focus following the text so the cursor lands where the typing continues. It still types like a paragraph; it is simply not one box. The handles are what the promote and bin targets need in order to exist at all, so they decided the structure.

Parsing and rendering were kept apart from the composable, because that is the part with rules worth stating: the first non-blank line is the parent whatever its indentation, every later non-blank line is a child indented or not, and blank lines are dropped so pressing Enter and thinking better of it leaves nothing behind.

Children are matched onto existing rows **by position**, not by text. Renaming the second subtask should edit that row rather than destroy one and create another, which would throw away its completion state.

The drag targets resolve hover themselves, because only they know where the icons ended up on screen — each records its own bounds as it lays out and the drag position is tested against them. That is why the drag primitive was extended to report the finger's window position.

A parent left with no children reverts to an ordinary task with a checkbox on its own, since a row renders a chevron only when it has children. Its completion is explicitly released at that moment: it is no longer derived, so a parent that had rolled up to complete would otherwise be stuck complete with nothing beneath it to un-complete.

**Files touched:** app/src/main/java/com/example/taskflow/ui/edit/Outliner.kt (new — Outline parse/render/normalise, ChildSync positional matching, the OutlinerField composable), ui/common/DragTargets.kt (new — the target set and the hover-testing row pinned top-right), ui/common/Reorderable.kt (reports the finger's window position), ui/edit/EditTaskViewModel.kt (outline replaces the plain title, children synced on save, deleteChildLine and promoteChildLine, promoteSubtask), ui/edit/EditTaskScreen.kt (title field replaced by the outliner, target row over the dialogue), ui/schedule/ScheduleViewModel.kt (deleteTask behind the bin target).

**Routed to Captures:** none at the time. The dropped affordance this build introduced — the hint telling the user Enter makes a subtask, lost when the single box became a field per line — was found on the device two days later and is part of [notes-versus-subtasks].

**Tick:** done, UNCONFIRMED: needs an Android Studio build and the device check (typing behaves as an outline, Enter making a child line and Backspace merging one away; dragging a task reveals the icon row; bin deletes; promote makes a child a top-level task carrying the parent's date, and the emptied parent shows a checkbox again).
