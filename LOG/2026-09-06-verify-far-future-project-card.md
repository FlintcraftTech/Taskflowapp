# PENDING — /next [verify-far-future-project-card]: run against a real Project Alex wanted anyway, rather than a throwaway that could not be deleted

**Outcome: done**, on 2026-09-06 between 12:28 and 12:47. Driven by Claude over adb, with Alex deciding the
one thing that was hers to decide.

## The precondition, and how it was settled

The item needs a Project of the user's own; the database had only the system Unassigned one. Creating a
Project purely to test it would have left a Project nobody could remove, since deleting one is a long-press
drag onto a target and steering that over adb has failed three times across two sessions (TOOLS.md). That
was put to Alex on this item's own turn rather than filtered out in advance, and she resolved it by naming
a Project she actually wants: **Family**, for keeping up with her immediate family. So the check ran on
real data and the Project stays because she wants it, not because it is stuck.

The item's other half was already proven incidentally earlier in the session — a far-future dated task
does render inside a Project card with a DD/MM label, seen at `14/09` and at `14/02` for a date five months
out. What was genuinely untested, and is what ran here, is that this works for a **user** Project rather
than for Unassigned.

## What was run

`AUDIT-completed-check` was refiled into Family and dated 20 September, fourteen days out. On Later:

- The **Family** card appeared holding one task, `AUDIT-completed-check`, showing `20/09`. Pass — this is
  the item's whole claim.
- **Unassigned** sat below it, pinned to the bottom with its own empty state, which is SPEC §Schedule
  view's ordering rule holding with a user Project present for the first time.

Three further behaviours were confirmed on the way, none of them this item's job:

- Creating a Project from the edit dialogue's picker files the task into it immediately, and the new
  Project is appended to the end of the order — Family appeared above the pinned Unassigned card
  (SPEC §Create or delete a Project).
- The Strategy doc gained a **Family** heading with a placeholder paragraph, so the mechanical structure
  follows Project creation without anything else being touched (SPEC §Strategy doc).
- With the task moved back out, the Family card renders as an empty card rather than disappearing, which
  is SPEC's "every Project the user has appears, even one with nothing in it".

## Two things worth carrying forward

**The date tiles really are clipped, confirmed visually.** A screenshot of the edit dialogue on this build
shows the month row cut through horizontally along its lower half on every tile — "Sept" losing its lower
half. That is [device-layout-clipping]'s first half reproduced on the build the phone carries, and it
matches the cause fixed earlier in this session: a fixed tile height too short for three lines of content.
The fix is not on the phone, so this is confirmation of the defect rather than of the repair.

**`input text` into a dialogue field can silently do nothing.** The first attempt to create the Project
typed "Family" and pressed Create, and no Project was made: the text never reached the field, so Create
stayed disabled and the tap fell on a dead control. Nothing reported an error, and the dropdown afterwards
looked exactly as it had before. The second attempt screenshotted the dialogue after typing and before
pressing anything, saw the name in the field and Create enabled, and worked. Screenshot between typing and
committing when driving a dialogue this way.

## Device state

`AUDIT-completed-check` is back on Today at `05/09` in Unassigned, where the run found it and where
[bin-drag-target-check] expects it. `go to chemist and get` is untouched at `02/09`. The **Family** Project
remains by Alex's choice, empty, with its Strategy heading. Nothing else was added or removed.
