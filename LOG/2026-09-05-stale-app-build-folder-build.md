# 0bd8c64 — Deleted the stale app/build folder the output move left behind

`app/build` had been sitting in the project since 2026-09-01, holding 53 MB that Google Drive kept
syncing for nothing. It was left there by [project-out-of-drive], which pointed Gradle's output at
`C:\builds\taskflow\app`: relocating where a tool writes says nothing about the folder it used to write,
so the old one simply stopped being touched and stayed.

That is what made it worth a queue line rather than a passing tidy-up. A folder nothing writes to any
more never announces its own staleness — it would have sat there indefinitely, and 53 MB of
constantly-synced files inside a Drive folder is half of what [project-out-of-drive] set out to stop.

Planning refused folding this into another item and placed it first in the cleared region instead: the
candidates were unrelated, and a folder deletion buried inside the LOG index script or the Strategy
header fix makes both harder to read. The claims were also re-checked before clearing a deletion rather
than trusted from the capture — present, 53 MB, git-ignored by `app/.gitignore`, nothing tracked inside,
and the live output confirmed at `C:\builds\taskflow\app`.

The build re-ran those checks and deleted it. `app/build` is gone; the compiled output and APK are where
[project-out-of-drive] put them.

**Files touched:** `app/build` (deleted).

**Routed to Captures:** none.

**Tick:** done, confirmed.
