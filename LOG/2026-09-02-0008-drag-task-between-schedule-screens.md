# 3defa43 — build [0008-drag-task-between-schedule-screens]: the page turns under a held task, and dropping it reschedules

SPEC §Drag a task between Schedule screens makes rescheduling spatial: pick a task up and move it, rather than opening an editor and adjusting a date field. Rescheduling is the most common edit anyone makes to a task list, so the gesture is worth more than the field.

This composes with [task-reorder-within-list] rather than competing with it. That item's primitive reports the sideways component of the same long-press drag, so one gesture does both jobs: vertical movement reorders within the slot, sideways movement past a threshold turns the page under the held task, and releasing on a page other than the one it started on reschedules it there. Building a second gesture for the second job would have put two drag detectors on the same row.

**Rescheduling is a date write, not a slot write**, because the Schedule derives placement from dates — landing on Today writes today, Tomorrow tomorrow's, Soon two days out and Later eight, which are the first dates falling inside each slot. The one exception is an undated task, which stays undated and parks on the new slot instead: giving it a date would be the app deciding a commitment the user never made. Parking is only meaningful on Soon and Later, so dropping an undated task on Today or Tomorrow does date it — that is what those pages mean.

A parent's children travel with it by construction rather than by code: they carry no date or slot of their own, so moving the parent moves the group.

The pager keeps its neighbouring pages composed. The dragged row's pointer input lives in the page it started on, and a disposed page cancels the drag mid-move — so without that, crossing the boundary would drop the task rather than carry it.

**Files touched:** app/src/main/java/com/example/taskflow/ui/schedule/ScheduleScreen.kt (sideways accumulation, page turn at threshold, reschedule on drop, neighbouring pages kept composed), ui/schedule/ScheduleViewModel.kt (rescheduleToSlot — the per-slot date write, undated parking, append-to-bottom on arrival), ui/schedule/SlotPage.kt (drag callbacks passed through).

**Routed to Captures:** none.

**Tick:** done, UNCONFIRMED: needs an Android Studio build and the device check (drag a dated task Today → Soon and its date becomes today + 2; drag an undated task to Later and it parks there still undated; a parent's children travel with it).
