# [HASH] — Kept as an [audit] with a stale build caveat corrected and one check split out as user work Claude cannot drive

Recorded 2026-09-06, 00:29.

The audit that ran on 2026-09-05 left five checks unreached, each for a stated reason. This item collects
them, and processing it changed three things.

**Its closing caveat was wrong and would have misled the run it warned.** It said the phone carries the
2026-09-04 APK, so that day's builds are not on the device. Checked on the phone at this decision step:
the Strategy page shows a single header with its Share action in the spine header's trailing slot, which
is what [strategy-page-double-header] built during the 2026-09-05 run. The device is carrying that run's
builds. Left in place, the caveat would have had the audit attributing anything it found to an old build.

**The bin drag target left the item and became [bin-drag-target-check], a `[user]` line.** The original
audit declined that check because steering an adb drag between two adjacent targets is guesswork; a
session trying it twice more this evening, while clearing up a test task, had both attempts read as a page
swipe — including one built from explicit `input motionevent` calls with a hold between them. Three
failures across two sessions makes it a capability limit rather than bad luck, so the check belongs to a
person's thumb. Its walkthrough's step 4 checks the clipboard, because pasting the task's text is what
distinguishes a real pass from having hit the neighbouring cut target — the exact confusion that has left
this unchecked twice.

Two things were added. The search question orphaned by deleting [search-omits-completed] — whether typing
narrows the completed history as well as the active list — since the empty-query half is now known good
and that half is not. And a note that the database currently holds no Projects at all, read off the
Strategy page's empty state, so a run does not stall assuming one exists.

**Queue changes:** kept into Processed cleared to run; [bin-drag-target-check] created as a `[user]` item,
cleared, cross-referenced both ways.
**Work processed:** kept — [verify-run-2026-08-31-remainder]; created — [bin-drag-target-check].
