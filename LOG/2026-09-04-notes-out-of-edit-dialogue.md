# 9e24ba7 — /plan [notes-out-of-edit-dialogue]: the buildable half of the Notes decision, with the column deliberately left in place

Split from [notes-versus-subtasks] this session; the decision and its reasoning are in
`2026-09-04-notes-versus-subtasks.md` rather than restated here.

What this item carries beyond that decision is two exclusions, written apart from its Files list because
getting either wrong is expensive. `Task.notes` is **not** dropped — no schema change, no migration —
so the JSON export keeps writing and reading the key and existing exports stay valid. And `LifeArea`
carries an unrelated field of the same name, holding Claude's working understanding of an area of life;
a change that catches it has gone wrong.

**Queue changes:** [notes-out-of-edit-dialogue] filed and moved into Processed, cleared to run.

**Work processed:** kept, cleared to run — [notes-out-of-edit-dialogue].
