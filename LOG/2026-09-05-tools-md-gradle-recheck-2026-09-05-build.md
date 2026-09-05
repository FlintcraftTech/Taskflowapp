# 0bd8c64 — TOOLS.md records that moving the build output out of Google Drive did not fix the Gradle failure

One dated line, and the negative result is the useful half.

TOOLS.md carries the Google Drive explanation as an explicit hypothesis rather than a finding: that the
project living inside Drive causes the "Unable to delete directory" file lock, and that moving it out
would probably end that problem. A later session reading that beside the loopback line could reasonably
infer that [project-out-of-drive] would have cured the shell failure as well.

It did not. `gradlew` was run from Claude's shell on 2026-09-05 — at the decision step of
[run-instrumentation-tests], to test whether that item was still genuinely `[user]` work — and died with
the same `Unable to establish loopback connection` recorded on 2026-08-31, with Gradle's output by then
living at `C:\builds\taskflow`. The two failures are unrelated, and that has now been tried rather than
reasoned about.

Rewriting or replacing the 2026-08-31 line was refused: it is a finding with its own date and its own
detail of everything tried, and today's run confirms it rather than overturning it. TOOLS.md's format is
one dated line per fact, so this is an addition.

**Files touched:** `TOOLS.md`.

**Routed to Captures:** none.

**Tick:** done, confirmed — read back, the dated line is present and the 2026-08-31 loopback line is
unchanged.
