# [HASH] — /next [day-begins-at-rollover-still-unrun]: the setup found a slot-derivation defect, so the watch was never meaningful and the item stays open

**Outcome: halted mid-drive.** The setup steps were driven on Alex's say-so and turned up a defect that makes the watch meaningless — the test task landed on the Today page before the boundary it was meant to cross. Filed as [tomorrow-task-lands-on-today-under-custom-boundary]. The item stays in Processed with nothing resumed from; a later session presents it fresh once that defect is understood.

Opened live as the walkthrough started, and appended to as each step happened, so an
interrupted drive still says what was done.

- 12:34 — Drive started. The phone is connected over wireless debugging and the
  build compiled this session is installed on it. No steps of this item had been
  recorded as done before now.

Every use of "Today" and "Tomorrow" below names a page in Taskflow, not a date.

- 12:35, read from the clock — the Today page's state noted before anything was
  changed: one active task, "go to chemist and get" (02/09), and the completed test
  task from the earlier audit in the tray.
- 12:36, read from the clock — Day begins at changed from 4:00 AM to 1:00 PM, the
  next whole hour at least ten minutes out from the 12:35 reading.
- 12:37, read from the clock — a task called "rollover-test" added from the Tomorrow
  page. The edit dialogue dated it 12 September and labelled the 11th as "Today",
  both correct under the 1:00 PM boundary.
- 12:37, read from the clock — **the task appeared on the Today page rather than the
  Tomorrow page**, carrying no date label. Reopening it confirmed its date is
  12 September while the strip still anchors "Today" on the 11th. Filed as
  [tomorrow-task-lands-on-today-under-custom-boundary].
- 12:38, read from the clock — drive halted there. What this item wants watched is a
  task crossing from the Tomorrow page onto the Today page at the boundary, and the
  task is already on the Today page before the boundary, so there is nothing left to
  watch. Day begins at is still 1:00 PM and "rollover-test" is still on the phone,
  both pending Alex's decision.
- 12:40, read from the clock — Day begins at set back to 4:00 AM on Alex's word, and
  the Settings row read back as 4:00 AM. "rollover-test" is deliberately left on the
  phone: with the boundary restored it is a task dated one day ahead, which is
  ordinary data, and removing it would need the bin drag target, whose fix shipped
  this session but has not been seen working on a device.

