# 9b577c4 — The orphaned worktree carrying retired instructions is deleted, and its git registration pruned

Date: 2026-09-05 11:16

`.claude/worktrees/optimistic-ellis-3ec0fa/` held a July snapshot of Taskflow's pre-migration
layout, an early copy of the user's Claude instructions as `claude.md.md`, and a copy of the
no-code-method project — 67 files, 344K. It was found at the end of the 2026-09-03 planning session
when the user asked whether a `UX.md` still existed.

What made it worth removing rather than ignoring: the risk is not exposure. It sits under `.claude/`,
which `.gitignore` excludes, so none of it was published. The risk is a future session grepping this
folder for guidance, finding instructions to search `UX.md`, compare it against `MANIFEST.md` and
file things in `BACKLOG.md` — three retired doc types — and following them. That is the same failure
the TOOLS.md correction addressed in the same session: stale text nobody deleted, read as current.
Moving it into `archive/` instead was refused, because that folder is tracked and it would commit a
stale copy of an entire project into a public repository.

**The item required a coverage check before the deletion, because git holds nothing here.** The
folder's own `.git` was a pointer to a repository at a path that no longer exists, so "delete it,
history keeps it" is false. The check ran and passed: the app files are the stock Android Studio
Views template, superseded by the Compose app; `UX.md`, `BACKLOG.md` and `MANIFEST.md` each have a
**later** version of the same document in the frozen archive one folder up (272 lines against 209,
323 against 194, and MANIFEST differing only in line endings); and `claude.md.md` is an early copy
of instructions whose live versions are the current CLAUDE.md layers.

**What the check accepted, stated rather than glossed.** Those earlier drafts are not reproduced
byte-for-byte anywhere, so that particular snapshot is gone. It was judged acceptable because none
of it carries live intent: the July `UX.md` describes a Google-Tasks-backed Taskflow that was
deliberately abandoned, and `BACKLOG.md`'s planning questions are all resolved in SPEC.

**The second half is what stops a recurring error.** The 2026-09-04 close printed
`error: failed to delete '.git/worktrees/optimistic-ellis-3ec0fa': Permission denied` — this
repository still held an administrative directory for that worktree, and git tried to prune it on
every commit and could not. Both halves are now done: the folder is gone, `.git/worktrees` is empty,
and `git worktree prune` exits clean.

Files touched:
- `.claude/worktrees/optimistic-ellis-3ec0fa/` — deleted
- `.git/worktrees/optimistic-ellis-3ec0fa/` — removed, then `git worktree prune` run clean

Routed to Captures: none.

Tick: done, confirmed by inspection. **One half of the item's own observation does not pass, and was
written wider than the work:** it expected a grep for `UX.md` across the project to return nothing
outside the item's text. It does not — `UX.md` is still named in 22 archived spec files, in
CLAUDE.md's migration note, and in three LOG entries. Those are historical references rather than
live instructions, so the misreading risk this item exists to remove is gone.
