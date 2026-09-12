# 6d6267c — The leftover device checks driven on the phone: four passes, five findings, and a day boundary that cannot be set finely enough to test its own rollover

Session date and time: 2026-09-06, 22:08.

The 2026-09-05 device pass reached what it could and left five checks behind, each for a stated reason
rather than for want of time. This audit drove them on the connected phone, with Alex's permission asked
before adb was used at all — that channel reaches far past one app, so using it silently would be a consent
surprise.

**Four things passed, and a pass is a finding rather than work, so they live here rather than in the
queue.**

Typing a query narrows the completed history as well as the active list, in both directions. With one
active task and one completed under the day header "Sunday 6 September", the query `AUDIT` left the
completed row and its header and dropped the active one; `chemist` left the active row and dropped both the
completed row and its header. SPEC §Search and completed history holds, including the part about day
headers staying above the days that still have results.

A manually-dated one-off is not capped at thirty days: a task dated 14/02/2027 showed inside its Project's
card on Later with its DD/MM label. SPEC §Recurring tasks' cap is correctly confined to recurring
instances.

The side menu lists Search, Yesterday, Today, Tomorrow, Soon, Later and Strategy in spine order, then a
separated block of Settings, Help, Thanks, Report a bug and Turn on AI. SPEC §Side menu holds exactly.

A task dated 02/09 sits on Today showing its date with no overdue marking and no reordering — SPEC §Tasks
dated before today, incidental to this audit's list but observed while running it.

**Five findings were filed to Unprocessed**, which is an audit's whole product: the Strategy doc's Share
button doing nothing on an empty doc, Day begins at offering whole hours only, the rollover check still
unrun, Strategy doc edit persistence unreachable, and the Yesterday page untested with content.

**The item's premise about the rollover check was wrong, and that is the finding behind the finding.** It
described the check as needing "a wait, in the middle of a run" with a boundary "set a few minutes ahead".
The setting takes whole hours and nothing finer, so the shortest possible wait is up to sixty minutes on
the user's own phone, holding the only session there is. What looked like a check this run could absorb is
`[user]` work, and it was refiled as such.

**Two checks were blocked by something the audit could not work around, and the shape of the block matters
more than either check.** Strategy doc edit persistence needs a Project, the database had none, and a
Project created to test it could not afterwards be deleted — deleting one is a long-press drag onto a
target, which failed every time it was attempted. So running the check would have cost a permanent Project
in Alex's real database, which is worse than the check is worth. The Yesterday page was reached and read
against SPEC — it is a page, one swipe left of Today, with an empty state — but nothing had been completed
yesterday, so how it renders a populated list is untested, and a page whose entire content is that list is
not meaningfully verified by its empty state.

**The device was left exactly as it was found.** A task was completed and un-completed, its date moved to
14/02/2027 and returned, and the Day begins at dropdown opened and dismissed without selecting. No rows
were created, deliberately, for the deletion reason above.

**Files touched:** none — an audit reads and reports. Read: the app on a Pixel 6 running the 2026-09-05
build, against SPEC §Search and completed history, §Recurring tasks, §Side menu, §Tasks dated before today,
§Yesterday page, §Strategy doc and §Settings → Day begins at.

**Routed to Captures:** [strategy-share-silent-when-empty], [day-begins-at-hour-granularity],
[day-begins-at-rollover-still-unrun], [strategy-edit-persistence-blocked],
[yesterday-page-with-content-untested].

**Findings routing:** five filed as captures, none dropped on re-reading. No security, privacy or breach
risk was surfaced, so no red flag was filed.

**Depth:** full — the item's own reasoning about the rollover check was contested and overturned.

**Rule gate:** not needed — an audit reads and reports, and this one authored no standing rule.

**Advisory:** filed — forward-advisory.
