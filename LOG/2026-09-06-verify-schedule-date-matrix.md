# 6d6267c — /next [verify-schedule-date-matrix]: the date matrix verified on a device, and the item's `[user]` tag found to be wrong

**Outcome: done**, on 2026-09-06 between 12:04 and 12:12, driven by Claude over adb rather than handed to
Alex. All three slot cases pass.

## The tag was wrong, and the capability check caught it

The item is tagged `[user]`. The light capability check at the hand-off found every one of its steps is
Claude's: setting a date needs the date strip, and reading which page a task lands on needs the page. Both
were driven over adb earlier in this same session while running the
[verify-run-2026-08-31-remainder] audit, so the tool exists and is authenticated. The item was written
before that was established — its walkthrough opens "once the date picker ships", from a time when nobody
had driven the strip at all.

So it ran as ordinary work. The correction belongs to the queue rather than to this record; per the method
a build does not hand-edit the queue, so it is noted here for the close.

## What was run, and what it showed

The item's walkthrough says to create three tasks. That was not done: a task created here could not
afterwards be deleted, because steering a long-press drag onto the bin target over adb has failed three
times across two sessions (TOOLS.md). Instead one existing throwaway task, `AUDIT-completed-check`, was
re-dated through all three cases in turn and returned to its original date at the end. The claims checked
are identical and the database is left exactly as it was found.

- **2–7 days out renders on Soon with a DD/MM label.** Dated 9 September, three days out. The task
  appeared on Soon showing `09/09`. Pass.
- **8 or more days out renders on Later with a DD/MM label.** Dated 14 September — deliberately *exactly*
  eight days, the boundary itself rather than a comfortable distance past it. The task left Soon, which
  fell to its "Nothing coming up in the next week." empty state, and appeared inside the Unassigned
  Project's card on Later showing `14/09`. Pass, and the boundary is on the correct side.
- **A past date stays on Today, with DD/MM and no overdue marking.** Dated 5 September, the day before.
  The task sat on Today showing `05/09`, alongside the older `go to chemist and get` at `02/09`. No label,
  no reordering to the top. Pass.

This also serves as the regression check [tomorrow-no-date-label] wanted: the Soon and Later DD/MM labels
and the past-date Today label all still render, so removing Tomorrow's label did not disturb the others.

**One limit, stated rather than glossed.** The no-overdue-marking half was read from the accessibility
tree, which reports text. It confirms no overdue *label* exists — SPEC's actual claim — but it would not
reveal a colour difference. A screenshot would be needed for that, and none was taken here.

## Device state

Restored. `AUDIT-completed-check` is back on Today at `05/09`, which is where the run found it and where
[bin-drag-target-check] expects it. Nothing was created and nothing was deleted.
