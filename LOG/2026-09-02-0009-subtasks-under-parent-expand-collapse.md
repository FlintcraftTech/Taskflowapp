# [HASH] — build [0009-subtasks-under-parent-expand-collapse]: children nest under their parent, and completion rolls up from them

SPEC §Subtasks live under their parent treats a subtask as a sub-unit of its parent rather than an independent item: no date of its own, no Project of its own, no Schedule placement of its own. It renders wherever the parent lives and moves when the parent moves. And SPEC §Parent tasks expand/collapse instead of having a checkbox removes the parent's checkbox entirely — manually ticking a parent while its children are still open creates a state mismatch, so the parent's completion is derived from the children rather than entered by anyone.

The rendering needed every child at once rather than a query per visible parent, so the DAO gained one query returning all subtasks, which the view-model groups by parent id. That is cheaper than a flow per parent and simpler to reason about.

The roll-up lives in the repository, not the UI: completing a subtask writes the child, then recomputes the parent from all of its siblings. So completing the last child completes the parent and sends it to the tray, and un-completing any child brings the parent back out — one path, not two.

Two details that only appear where features meet. A completed parent keeps its children in the tray, so un-completing from there brings the whole group back. And a child ticked inside a *recurring* parent is an ordinary completion rather than an instance of anything — the row's instance date belongs to the parent, so passing it down would have routed the child's completion into the recurrence machinery, where it would have silently done nothing.

The tray also stopped showing date labels: what matters there is that the work is done, not when it was due, and a stale date beside a finished task is the reproach UX principle 4 refuses.

**Files touched:** app/src/main/java/com/example/taskflow/data/local/TaskDao.kt (observeAllSubtasks), data/repository/TaskRepository.kt (setSubtaskCompleted with the roll-up), ui/schedule/ScheduleViewModel.kt (children attached to their parent's row, completion routed through the roll-up, the tray carrying a completed parent's children, no date label in the tray), ui/schedule/SlotPage.kt and ui/schedule/LaterPage.kt (parent rows carry a chevron where a checkbox would be, children indented beneath).

**Routed to Captures:** none.

**Tick:** done, UNCONFIRMED: needs an Android Studio build and the device check (add two subtasks under a task; the parent shows an expand/collapse control rather than a checkbox; complete both and the parent completes; un-complete one and the parent returns with its children).
