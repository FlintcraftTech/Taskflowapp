# [HASH] — The Notes box leaves the edit dialogue, and the column stays where it is

Date: 2026-09-05 11:13

The user captured this on 2026-09-02 looking at the dialogue on the device: they did not understand
why there was a Notes field they never asked for. Checking the record during planning showed their
not recognising it was evidence rather than forgetfulness — the earliest trace is a build entry
listing "title, notes, editable Project incl. unassigned, read-only date" as what it built, with no
decision recorded anywhere about wanting notes. It arrived as part of a minimum-viable editor and
was later written into SPEC as though it had been chosen.

Settled by the user: Notes comes out. Their reason is that the app exists to reduce the weight of a
task list, and a free-text box invites the user to put work into describing work. The
counter-argument in their own capture — that a task sometimes carries a real detail like an address
or a phone number — was weighed and lost, because a subtask line holds "123 Fake Street" perfectly
well and needs no field of its own.

**The column was deliberately not dropped**, and that refusal is the reason this item is cheap and
reversible. Dropping it is a schema change, and `[durable-local-data]` — shipped in this same run —
makes version 5 the floor below which no data may be destroyed, so a removal now needs a real
migration written and tested. Taking the field out of the UI costs nothing, breaks no export, and is
reversible by putting the field back.

**One alternative was weighed during the build and lost.** On the update path, `notes` was dropped
from the `o.copy()` call rather than written as an empty string. Copying it as blank would have
silently erased an existing task's note the first time it was edited — which is precisely what the
refusal above exists to prevent. The two exclusions the item stated apart were both checked by grep
after the edits: `Task.notes` and `TaskflowJson`'s notes key are unchanged, and `LifeArea.notes` —
a different field holding Claude's working understanding of an area of life — was not caught.

Files touched:
- `ui/edit/EditTaskScreen.kt` — the notes field removed, class comment rewritten
- `ui/edit/EditTaskViewModel.kt` — `notes` out of `EditUiState`, `Form`, the state mapping, the load
  path and the insert; `onNotesChange` deleted; the update path's `o.copy()` no longer names it,
  with a comment recording why

Routed to Captures: none.

Tick: done, UNCONFIRMED — needs a device to confirm the dialogue shows outliner, Project, date and
repeat and no notes box, that a JSON export still contains a notes key for every task, and that a
task which already had notes still carries them in that export.
