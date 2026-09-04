# [HASH] — /plan [notes-versus-subtasks]: the Notes field removed from the edit dialogue, and the invisible subtask feature split off from it

The user's capture from 2026-09-02, from looking at the dialogue on the device: a Notes field they never
asked for, and no visible subtasks. Both halves checked out and they are separate problems sharing a
screen.

**Notes.** Where it came from is thinner than SPEC suggested. The earliest trace in the record is the
0005 build entry, listing "title, notes, editable Project incl. unassigned, read-only date" as what it
built, with no decision recorded anywhere about wanting notes. It arrived as part of a minimum-viable
editor and was later written into SPEC as though it had been chosen — so the user not recognising it is
evidence, not forgetfulness.

The user settled it: Notes comes out. The reason is the app's own premise — it exists to reduce the
weight of a task list, and a free-text box invites putting work into describing work. The counter-argument
from their own capture, that a task sometimes carries an address or a phone number, lost because a
subtask line holds "123 Fake Street" perfectly well.

Dropping the database column was refused, and the reason is worth carrying: it is a schema change, and
[durable-local-data] is about to make version 5 the floor below which no data may be destroyed, so a
removal would need a real migration written and tested. Taking the field out of the UI costs nothing,
breaks no export and is reversible.

**Subtasks.** They shipped in the 2026-08-31 run and say nothing on screen. An earlier draft of the field
carried the hint "Press Enter to add a subtask", dropped when the single text box became a
line-per-field outliner — which is how the only affordance disappeared.

**Queue changes:** [notes-versus-subtasks] deleted, split into [notes-out-of-edit-dialogue] and
[subtask-affordance-in-edit-dialogue]. SPEC edited in the same session: §Edit a task now says there is
no notes field and why, and the refile promise in §Move between Schedule and Project reads "subtasks and
recurrence" in both of its sentences.

**Work processed:** deleted after splitting — [notes-versus-subtasks].
