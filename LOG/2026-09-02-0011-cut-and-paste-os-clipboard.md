# [HASH] — build [0011-cut-and-paste-os-clipboard]: cut writes an indented block to the device clipboard, and the outliner reads one back

SPEC §Drag-target icons routes cut and paste through the **device's own clipboard** rather than a Taskflow-internal one, so the user's existing muscle memory works. A cut task leaves Taskflow and its content goes out as plain text; a parent and its children go as one indented block, which is the same shape the outliner reads, so a Taskflow-to-Taskflow round trip keeps its structure and text pasted from anywhere else comes in as lines by the same rule.

Two orderings matter and both are deliberate. The text reaches the clipboard **before** the row is deleted, so a failure to reach it cannot leave the task destroyed with nothing to paste. And the clipboard write happens in the UI layer with the view-model handing the text out through a callback — a view-model holding a clipboard is a layer violation, and this way the rule stays visible.

Paste is handled where it actually arrives: the outliner's line fields are single-line, so pasted multi-line text shows up as one value change carrying newlines. That value is split and spliced in as separate lines rather than landing as a run-on title.

**The risk is accepted rather than mitigated, and SPEC says so**: if another app overwrites the clipboard between the cut and the paste, the cut task is gone for good. The user took that trade for the simplicity of the device's own clipboard, and the item records the alternative that lost — a recently-cut buffer held inside Taskflow — so it is not re-proposed.

**Files touched:** app/src/main/java/com/example/taskflow/ui/schedule/ScheduleViewModel.kt (cutTask — writes the indented block out, then deletes), ui/schedule/ScheduleScreen.kt (cut added to the screen targets, clipboard write), ui/edit/EditTaskViewModel.kt (cutChildLine), ui/edit/EditTaskScreen.kt (cut added to the dialogue targets), ui/edit/Outliner.kt (multi-line paste splices into separate lines).

**Routed to Captures:** none.

**Tick:** done, UNCONFIRMED: needs an Android Studio build and the device check (cut a parent with children, paste into a notes app and the indented text is all there; paste it back into an edit dialogue and the hierarchy rebuilds).
