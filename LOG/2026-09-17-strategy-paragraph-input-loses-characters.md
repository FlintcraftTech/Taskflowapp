# [HASH] — /plan [strategy-paragraph-input-loses-characters]: a real lost-update race found in the code, and the capture's own strongest evidence corrected as probably an adb artifact

**Kind: processed.** Recorded 2026-09-17 at 16:01, read from the clock, during a planning session.

The capture named its own cheapest next step — read `StrategyScreen.kt` and `StrategyViewModel.kt` before
any device work — and that read settled it.

**The mechanism.** The paragraph field's `value` is bound to `section.description`, a value fed from the
flow the database emits, and its `onValueChange` calls `setDescription`, which launches a coroutine doing a
repository read followed by an upsert. So the field displays a value one asynchronous round trip behind the
typing, and every keystroke starts an independent read-modify-write with no ordering. A keystroke arriving
before the previous round trip returns is appended to the stale value, losing the character before it; two
overlapping coroutines can interleave so the later write carries the earlier read. It is the only text
field in the app built this way — the edit dialogue's outliner holds its text locally and writes once.

**The capture's reasoning was partly wrong, and correcting it changed what the evidence proves.** It
treated the *substituted* character — `l` arriving as `m` at a 0.35-second spacing — as the strong
evidence, on the ground that silent dropping is a known `adb shell input text` behaviour but substitution
is not. That is backwards: a lost-update race drops and duplicates, it does not transform one letter into
another. A keycode delivered one off does exactly that. So the substitution is most likely a driver
artifact, and what the code read establishes is the dropping.

**What stays unestablished after the fix:** whether a person typing at human speed hits the race. Nobody
has tried, and a 0.4-second spacing was observed working, which is slower than most people type. The fix is
taken anyway on the item's own two grounds — the race is real whatever anyone has reproduced by thumb, and
the field does a database read and write per keypress for no benefit.

Refused: saving only on focus loss. It is the simplest way to remove the race and would quietly undo
something already proved — the 2026-09-12 audit confirmed text in this box survives a force-stop, and it
survives *because* of the per-keystroke write. Saving on a pause as well keeps that within about a second.
The pause length is stated in the item as a tunable constant with what it was derived from and what would
settle it, rather than a decision left for the build.

**Queue changes:** [strategy-paragraph-input-loses-characters] rewritten into a work item and cleared to
run — two files under `ui/strategy/`. No SPEC sentence goes wrong: SPEC describes the editor, not its save
timing.

**Work processed:** kept — [strategy-paragraph-input-loses-characters].
