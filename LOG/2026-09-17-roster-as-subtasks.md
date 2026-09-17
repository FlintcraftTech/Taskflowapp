# [HASH] — /plan [roster-as-subtasks]: the rotating roster replaced by single-pick subtasks, with the app deciding nothing about whose turn it is

**Kind: processed.** Recorded 2026-09-17 at 16:01, read from the clock, during a planning session.

Alex's design, and the reversal is the point of it. The roster that shipped on 2026-09-12 holds an ordered
list of labels on the task and advances through them by counting completions — the app decides whose turn
is next. Her replacement: the thing that rotates is a **subtask**, not a name, and the user handles the
rotation as they see fit. Once a task repeats, an option turns its subtask list into a single-pick list;
completing one subtask completes that occasion of the parent. Nothing advances, nothing tracks a position.

Her reasoning, given in her own words during the session: "call family" with a person under it is one
example of what rotates, not the shape of it; and evenness is something she judges by reading her own
history, not something the app should maintain. That reframed the open question the earlier capture had
been stuck on — the capture asked *where* a choice would be presented given the app never interrupts, and
the answer is that no choice is presented at all, because the subtask list is already on screen.

Two sub-decisions were settled with her, each of which could have gone the other way.

**The visible roster comes out; the `roster` column stays.** A dead text field in the edit dialogue is
something she meets every time she opens a task during the month of real use ahead of her, so it goes. A
dead column is invisible. Refused: dropping the column now — under Room that means rebuilding the table
and copying every row, a destructive migration on the phone holding the only live copy of her real tasks,
for tidiness nobody can see. It can ride a later schema change that has its own reason.

**The pick is recorded by widening what is already stored**, not by adding a table. Each repeating task
already holds a comma-separated list of the dates its occasions were completed; that encoding widens so an
entry may optionally carry the subtask picked with it. Existing entries keep meaning what is true of them —
done, no pick recorded. Refused: a separate table of completed occasions, which is tidier and costs a
schema version, a migration copying existing completions, and a rewrite of every read site, to serve a
query nothing performs yet.

One correction to something told to Alex mid-discussion, recorded because it changed her picture: storing
the pick needs no structural change, but the single-pick **mode** does — it is a new flag on the task,
which is an added column and so a schema 7. Adding a column is a one-line operation; that is why it is
taken while the drop above is not.

**Queue changes:** [roster-as-subtasks] rewritten from a rough capture into a full work item and cleared
to run — nine files, schema 6 → 7. SPEC §Recurring tasks' roster paragraph replaced with the single-pick
description; SPEC §Parent tasks expand/collapse gains the exception to the all-subtasks-complete roll-up.
[recurring-completions-in-history] split out and placed immediately after it;
[run-migration-test-on-emulator] placed after both.

**Work processed:** kept — [roster-as-subtasks].
