# [HASH] — build [0006-side-scrolling-date-picker]: found already shipped by a crashed session; two stale comments corrected

The item was the only path to setting a date at all, which is why two device checks were held against it. The run opened it expecting to build a horizontal date strip and found one already there: `DateStrip.kt` complete — tiles labelled per the date-format setting, today as the visual anchor, a linear fade with a floor so far tiles stay readable, a labelled "no date" tile at the left edge, and a month-jump row — with `EditTaskScreen` wiring it into the date field and `EditTaskViewModel` carrying `onDateSelected` and `onDateCleared`.

It arrived in commit baacb0b, the orphaned-build close: a session crashed mid-run, and its uncommitted work was swept into that commit without its queue item being ticked. So the item survived describing work that was already done.

What was actually wrong was the prose around it. Two KDoc comments still said the date was "displayed but read-only this batch" and "never changed here", written when that was true and left behind when it stopped being. Both were rewritten to describe the strip. Nothing else was touched.

The run recorded this rather than silently ticking it, because a queue item that outlives its own work is a fact about how the crashed session closed, not about this one.

**Files touched:** app/src/main/java/com/example/taskflow/ui/edit/EditTaskScreen.kt (KDoc line rewritten), app/src/main/java/com/example/taskflow/ui/edit/EditTaskViewModel.kt (class KDoc rewritten), app/src/main/java/com/example/taskflow/ui/edit/DateStrip.kt (read, unchanged).

**Routed to Captures:** none.

**Tick:** done, UNCONFIRMED: the code was already shipped in commit baacb0b before this run; its acceptance is a device check (open a task's dialogue, strip centred on today; tap a date three days out and save, task appears in Soon with that DD/MM label; reopen, tap "no date" and save, task leaves Schedule) which Claude cannot run — no compile or install from this shell (see TOOLS.md).
