# [HASH] — /next [drag-eaten-by-page-swipe]: the drag-target row's own footprint no longer turns the page, so bin, cut and promote are reachable

Written 2026-09-12 at 13:05, read from the clock.

The row of bin and cut targets that appears when a task is picked up could not be reached: moving the finger toward it turned the page instead. Every target SPEC §Drag-target icons describes — bin, cut, and promote in the edit dialogue — was unreachable by the gesture SPEC gives for reaching them. Alex found it on 2026-09-06 with her own thumb on a real phone.

**The mechanism was misdiagnosed when it was captured, and the correction is the whole design.** The capture suspected Compose's pager was swallowing the drag. Reading `ScheduleScreen.kt` showed otherwise: Taskflow turns the page itself, on purpose. `onTaskDragHorizontal` accumulates a held task's sideways distance and calls `animateScrollToPage` past a 140-pixel threshold — [0008-drag-task-between-schedule-screens] working as built. So this is not a library defect but two wanted features sharing one axis, with the page turn firing first.

The fix: while the drag position sits inside the bounds the target row has already laid itself out into, the page-turn accumulator is skipped and the drag belongs to the targets. Below the row, sideways still reschedules exactly as before. No new affordance and no new tuned distance — the row already measured its icons for hover detection, so all that was added is reporting where it ended up, through a new `onBoundsChange` callback that also clears the bounds while the row is hidden.

**Refused: moving the targets to the bottom of the screen** so reaching them is a downward motion — it separates the axes cleanly but fights the "drag it away to get rid of it" instinct and contradicts SPEC's "upper-right corner". **Refused: raising the page threshold or making the page turn wait** — that degrades rescheduling, which works, to fix something else. **Refused too was Alex's own first reading of the recommendation**, that a band be reserved at the top: her correction is that the row already occupies one, so the band is its own footprint rather than something new.

The filing also corrected the record. Three adb attempts across two sessions had each registered as a page swipe and been written up as a limit of driving a device over adb — a line since removed from TOOLS.md. They were reproducing a broken gesture faithfully, not failing to reproduce a working one.

**Files touched:** `ui/common/DragTargets.kt` and `ui/schedule/ScheduleScreen.kt`.

**Routed to Captures:** none.

**Verification:** done, UNCONFIRMED — it compiles, but nobody has dragged a task up into the icon row on a device to see the page stay put. Two held items name this one as their blocker, [project-delete-later] and [bin-drag-target-check], and neither can be judged ready on a compile alone.
