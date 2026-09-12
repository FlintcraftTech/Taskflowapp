# [HASH] — /plan [drag-eaten-by-page-swipe]: the pager was not eating the drag — Taskflow turns the page itself, so the target row's own area now suspends it

The capture blamed Compose's horizontal pager for swallowing the drag that should
carry a lifted task to the bin. Reading `ScheduleScreen.kt` showed otherwise:
`onTaskDragHorizontal` accumulates the sideways distance of a held task and calls
`animateScrollToPage` past a 140-pixel threshold. That is
[0008-drag-task-between-schedule-screens] working as designed. So this was never a
library defect but two wanted features sharing one axis — reaching a target and
turning a page are the same motion, and the page turn fires first.

Alex's correction shaped the fix. The recommendation offered to reserve a band at
the top of the screen for the targets; she pointed out the row already occupies
one, so the band is its own footprint rather than something new. The design is
therefore: while the drag position sits inside the bounds the row has already laid
itself out into, the page-turn step is skipped.

Refused: moving the targets to the bottom so reaching them is a downward motion —
it separates the axes but fights the "drag it away to get rid of it" instinct and
contradicts SPEC's "upper-right corner". Refused: raising the threshold or
delaying the page turn — it degrades rescheduling, which works, to fix something
else.

The capture's second symptom turned out not to be a fault at all. `DragTargetRow`
is aligned to the top-end of the content area and `BIN` is its first icon, so the
icons render at the upper-right of the task area with the bin on the left — which
is what Alex saw and what SPEC describes.

**Queue changes:** kept into Processed, cleared to run, and placed ahead of
[bin-drag-target-check], which cannot pass until it ships. [project-delete-later]
and [bin-drag-target-check] were both re-held against it.

**Work processed:** kept — [drag-eaten-by-page-swipe].

**Advisory:** filed — [forward-advisory]
