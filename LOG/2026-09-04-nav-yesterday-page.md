# [HASH] — /plan [nav-yesterday-page]: the Yesterday page, carrying the spine plumbing the whole left half needs

One of four items split out of [nav-completed-history] this session; the reasoning for the split, and
for the hold that was written and withdrawn, is in `2026-09-04-nav-completed-history.md` rather than
restated here.

It goes first of the four because it is the simplest real page and because it carries the shared change
the others build on: `SpinePage` pairs every entry with a non-null `ScheduleSlot` today, and Yesterday
has no slot, so that field becomes nullable. Yesterday's bounds are computed from the day-begins-at
setting rather than midnight, which is what makes the page agree with the rest of the app.

Giving Yesterday its own `ScheduleSlot` was refused: it is a history view, not a place tasks can be
put, and a slot would make it a capture target, which SPEC §Add a new task rules out.

It also picks up the menu row folded in from [side-menu-spine-mismatch] — see
`2026-09-04-side-menu-spine-mismatch.md`.

**Queue changes:** [nav-yesterday-page] filed and moved into Processed, cleared to run.

**Work processed:** kept, cleared to run — [nav-yesterday-page].
