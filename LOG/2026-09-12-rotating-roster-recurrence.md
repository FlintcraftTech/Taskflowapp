# [HASH] — /plan [rotating-roster-recurrence]: four questions settled, the roster advancing on completion rather than on the calendar, and the free-choice half split off

Alex's idea, raised 2026-09-06: one repeating task whose subject advances through
an ordered list each time it comes round, instead of repeating unchanged. The
failure it fixes is the one the app exists for — keeping in touch with four people
evenly means holding the rotation in your head. The workaround, four staggered
recurring tasks, drifts the moment one is completed late.

The capture named three open questions and settled none. All three were settled
with Alex, and a fourth was found in the source and settled too.

- The roster lives on the task. Refused: putting it on the Project, which would
  make a Project a thing with members where it is an area of the user's life.
- The free-choice position — a slot offering two names and waiting for a pick — is
  split into [rotating-roster-free-choice]. A rotation is deterministic; a choice
  has to interrupt the user and needs an answer for never being answered, and
  bundling them would make the straightforward half wait on the awkward one.
- The free tier gets it. The rotation is a list and a position in it, not
  intelligence. Refused: paid-only — Claude arranging a roster conversationally is
  a separate thing built on top.
- **The fourth, found while reading the code:** the roster advances on COMPLETION,
  not by date. `ScheduleViewModel.instancesOf` filters completed instances out and
  starts its window at the current day, so a missed instance is never re-shown —
  which means advancing by date would lose the occasion *and* skip that person's
  turn, on a week Alex happened to be ill. Advancing by completion loses the
  occasion and keeps the turn. Accepted cost: a rotation nobody does stops
  advancing and keeps showing one name.

A correction was owed and given during the discussion: the recommendation had
argued from missed tasks staying on the Today page, which is true of one-off tasks
but not of repeating ones, whose missed instances are dropped deliberately. The
finding strengthened the choice rather than changing it.

SPEC §Recurring tasks gained the roster paragraph, including the completion rule
and its cost.

**Queue changes:** kept into Processed, cleared to run, with an eight-file list, a
5 → 6 schema migration, and a line requiring an export or the emulator before its
migration test runs.

**Work processed:** kept — [rotating-roster-recurrence]; filed —
[rotating-roster-free-choice].
