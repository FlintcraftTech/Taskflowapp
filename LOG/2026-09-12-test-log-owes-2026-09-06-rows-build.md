# [HASH] — /next [test-log-owes-2026-09-06-rows]: the TEST-LOG rule widened to any source, and twenty-one owed rows backfilled

Written 2026-09-12 at 13:05, read from the clock.

This project's CLAUDE.md carries a standing rule that TEST-LOG.md is the running test history. The /next run of 2026-09-06 produced more test outcomes than any session so far and wrote none of them.

**Why it was missed, and what was settled about it on 2026-09-12.** The rule's trigger was a build running tests. That run's testing happened inside an `[audit]` item and inside `[user]` walk-throughs, so the trigger and the run's shape never met. Settled with Alex: the table records a test outcome whoever produced it — a build, a review pass, or a step she performed on the device. The value of a running history is being the one place that says what has actually been exercised, and one that silently omits the checks a person ran is worse than none, because it reads as complete. **Refused: leaving the rule narrow and backfilling the rows anyway**, which moves the ambiguity into the file, where the next session guesses again.

Everything needed was already in the 2026-09-06 session's LOG entries, so nothing had to be re-run. The rows cover that run's passes (search narrowing the completed history, a far-future dated task appearing in Later, the side menu matching the spine, a past-dated task sitting on Today unmarked), its failures and blocks (the Strategy Share button, the whole-hour day boundary, the unrun rollover, unreachable Strategy edit persistence, the untested Yesterday page), the three-slot date matrix, the far-future task inside a real Project's card, the three Supabase checks and the `42501` failure that preceded them, and the compile.

**One backfilled row is a correction rather than a transcription.** Row 062 records the Strategy Share button doing nothing on an empty doc, which the 2026-09-06 audit read as a visible control silently failing. Re-reading the source on 2026-09-12 showed the button already carries `enabled = strategySections.isNotEmpty()`, so it was correctly disabled and the tap fired nothing — which is why the logcat was empty. The row says so, rather than leaving a failure on the record that has since been explained.

Rows 073 and 074 record the two compiles, 2026-09-06's and this session's.

**Files touched:** `CLAUDE.md` (the TEST-LOG paragraph's last sentence, widened from "when a build runs tests" to any source) and `TEST-LOG.md` (rows 054–074).

**Routed to Captures:** none.

**Verification:** done, confirmed. CLAUDE.md's TEST-LOG paragraph no longer conditions recording on a build, and a grep of TEST-LOG.md for `2026-09-06` returns rows covering each outcome the item listed.
