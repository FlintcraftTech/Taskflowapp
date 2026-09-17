# [HASH] — /plan [compile-rule-vs-multi-item-runs]: the compile rule settled in favour of what a run actually did, and given the failure case it never had

**Kind: processed.** Recorded 2026-09-17 at 16:01, read from the clock, during a planning session.

The rule written on 2026-09-12 says a build needing a compile stops, hands Alex the command for Android
Studio's terminal, and waits before ticking the item — one hand-over per item, read literally. The same
session's run then built four Kotlin items and handed over **one** compile covering all four, ticking them
only when it came back clean, having left every one of them in the queue until then.

Settled with Alex in favour of the run's behaviour: a run may cover consecutive code items with one
compile, provided not one is ticked or removed from the queue until it passes. Four identical hand-overs
interrupt her four times to learn the same thing once. What the rule must still forbid is the reading it
was written against — ticking code items unconfirmed and leaving the compile for later.

And the half the rule said nothing about: **on a failure** the run stops, nothing is ticked, it reads the
error to find which item caused it, fixes and re-hands where that is clear, and halts and says so where it
is not — including that the working tree holds every batched item's changes, because a failed compile does
not undo them.

The accepted cost is recorded rather than left to be discovered: batching means a failure does not say
which item broke it. Refused: a cap on how many items one compile may cover — any figure would be invented,
and the natural bound already exists in "consecutive code items within one run".

Queued rather than done in the session, because a planning session may not write this project's CLAUDE.md.
The precedent for changing it as a build is the item that wrote the rule in the first place.

**Queue changes:** [compile-rule-vs-multi-item-runs] rewritten into a work item and cleared to run at the
top of the cleared region — one file, `CLAUDE.md`, no compile needed.

**Work processed:** kept — [compile-rule-vs-multi-item-runs].
