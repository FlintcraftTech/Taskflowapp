# 9e24ba7 — /plan [boundary-tick-already-shipped]: a held item confirmed finished in code and deleted, clearing the oldest lift signal in the queue

The capture reported that [schedule-day-boundary-tick] — sitting below the readiness line, held against
[0012-settings-day-begins-at] — had already shipped inside that item during the 2026-08-31 run. A build
cannot delete a queue item, so it was filed for a planning session to decide.

The claim was checked rather than taken at face value, because a capture's account of how something
works is a claim to test. Both sources agree. The LOG entry for the day-begins-at build says
[schedule-day-boundary-tick] was folded in "as that item's build block instructed" and calls it the more
interesting half. And the code carries it: `ScheduleViewModel.kt` has a `millisUntilNextBoundary(...)`
function and a loop that waits exactly that long and re-emits the settings, so Tomorrow's tasks
re-bucket onto Today at the boundary with nothing touching the database. Resume is covered free, because
collection restarts and re-reads the clock.

What the item still lacked was a device check, and deleting it loses nothing: [verify-run-2026-08-31]
already lists that check in those words — set day-begins-at a few minutes ahead and watch a Tomorrow
task move with the app open.

This also removed the oldest lift signal in the queue, which had been surfacing at the top of every
session opening since 2026-08-19.

**Queue changes:** [schedule-day-boundary-tick] deleted from Processed as finished work;
[boundary-tick-already-shipped] deleted from Unprocessed, its whole content having been the message that
the item was finished.

**Work processed:** deleted — [boundary-tick-already-shipped], and with it [schedule-day-boundary-tick].
