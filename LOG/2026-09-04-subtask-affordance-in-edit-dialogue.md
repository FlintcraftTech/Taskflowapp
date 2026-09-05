# 9e24ba7 — /plan [subtask-affordance-in-edit-dialogue]: a hint that Enter adds a subtask — then held at the close, because the behaviour it advertises has never been run

Split from [notes-versus-subtasks] this session; the decision and its reasoning are in
`2026-09-04-notes-versus-subtasks.md`.

The division between the two halves matters and is written into the item: removing Notes makes an
existing subtask line unmistakable, because nothing else on the screen looks like it, and does nothing
at all for a task that has no subtasks yet. That empty case is what this item covers.

**Cleared to run when written, then held at this close.** The close's rule against building on
unverified work caught it: this item's whole content is a hint advertising that Enter adds a subtask,
behaviour that shipped in [0010-outliner-typing-drag-target-icons] and has never been run on a device.
If it does not work, the hint advertises a feature that is not there — worse than the silence it
replaces. So it now waits on [verify-run-2026-08-31], the audit that checks exactly that, and sits
after it in build order.

**Queue changes:** [subtask-affordance-in-edit-dialogue] filed, moved into Processed cleared to run,
then moved below the readiness line at the close with `Blocked by: [verify-run-2026-08-31]`.

**Work processed:** kept, held below the line — [subtask-affordance-in-edit-dialogue].
