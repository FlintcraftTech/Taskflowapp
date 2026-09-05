# 9e24ba7 — /plan [orphan-worktree-cleanup]: a dead worktree folder found by the user's question about a retired doc type, queued for removal with the check first

Surfaced by `/rescan` and processed in the same session. The user asked whether a `UX.md` still existed,
that doc type having been retired. It does not exist as a live doc here — the principles are a section
inside SPEC.md, and CLAUDE.md's migration note records `UX.md → SPEC.md`. But searching for it found a
real one at `.claude/worktrees/optimistic-ellis-3ec0fa/no-code-method/UX.md`, beside a `claude.md.md`
telling its reader to search `UX.md`, compare it against `MANIFEST.md`, and file things in `BACKLOG.md`.
All three are retired.

Three reads established that the folder is inert. Its `.git` is not a repository but a pointer to
`C:\Users\Alex\Desktop\Taskflowapp\.git` — a different user profile, at a path that no longer exists, so
git cannot read the directory at all. `git worktree list` here does not include it. And it is 344K, so
the Google Drive sync-weight argument first reached for does not apply and was withdrawn.

The part that made this a careful deletion rather than a quick one: because the parent repository is
gone, git holds nothing, so "delete it, history keeps it" is false here. Hence the item checks each part
against its live original before removing anything, and the deletion is the user's call made knowing
that.

Leaving it because it is gitignored and small was refused — being unpublished answers the privacy
question and not the misreading one, which is the actual risk. Moving it into this project's `archive/`
was refused too: that folder is tracked, so it would commit a stale copy of an entire project into a
public repository.

**Queue changes:** [orphan-worktree-cleanup] filed from the rescan and moved into Processed, cleared to
run.

**Work processed:** kept, cleared to run — [orphan-worktree-cleanup].
