# PENDING — Three device facts written into TOOLS.md, the first of them a warning that running the project's own tests wipes the user's data

Session date and time: 2026-09-06, 22:05. This session ran across a single day, in two halves separated by
several hours.

The item existed because of a question Alex asked on 2026-09-05 — whether she can count on her tasks
staying in the app — and the answer on the current setup was no, in a way nobody had noticed. A connected
instrumentation test run installs the app, runs the tests, and uninstalls both, taking the Room database
with it. The database is the single source of truth for everything she has (SPEC §UX principle 2), there is
no cloud sync yet, and the day it happened nothing was lost only because an export had been taken hours
earlier for an unrelated test. That was luck, and the item's job was to make sure the next session meets
the fact before it touches the phone rather than after.

**The red flag this item carried was cleared by informed acceptance, not by a fix, and the carry-through is
recorded here.** Alex was told plainly on 2026-09-05 that a connected test run removes the app and its
database, that this project's AGP offers no setting to stop it, that a JSON export before each run is the
protection, and that Android Auto Backup is a second net which worked once, unplanned, and has never been
deliberately tested. She chose to keep running the tests on that basis. The substantive clearing record was
written at the /plan close that cleared it.

Two further facts were folded in rather than filed separately, because they are the same work — one file,
one line each, one build — and because a session about to drive the phone meets all three at once. Both
came from driving the device during planning on 2026-09-06: that an adb long-press drag could not be
steered between two adjacent targets, and that the phone re-locks after a few minutes of inactivity.

**The second of those was corrected later the same session, and the correction is the more useful record.**
Alex performed the drag with her own thumb during [bin-drag-target-check] and got the identical failure, so
the line was rewritten in place: it was never an adb limitation, it was a real app defect faithfully
reproduced three times and misread three times. The TOOLS.md entry now carries both the retraction and the
lesson — that repeated failures of one tool are evidence about the thing being driven, not only about the
driver. Two more lines were added to the same file later in the session, on the same principle of writing
an environment fact at the moment it is learned.

Where the warning goes was itself a decision with a rejected alternative. The capture proposed adding it to
[run-instrumentation-tests]'s walkthrough; that item had been walked to done the same day and leaves the
queue, so a warning written there would have been deleted within the day. TOOLS.md is where environment
facts live and is read before the device is touched, so it went there instead.

**Files touched:** `TOOLS.md`.

**Routed to Captures:** none from this item.

**Depth:** full — an alternative was seriously weighed and lost, recorded above.

**Rule gate:** not needed — the item authored no standing rule; it recorded facts.

**Tick:** done, confirmed. The observation the item named was run: a grep of TOOLS.md for
`androidTest-results` returns the existing results-path line with the uninstall line adjacent to it, a grep
for `motionevent` returns the drag line, and a grep for `re-locks` returns the lock line.

**Advisory:** filed — forward-advisory.
