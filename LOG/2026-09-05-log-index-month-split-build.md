# [HASH] — LOG index month split scripted and run, moving 55 lines out of index.md

The method's rule is that a month which has ended moves out of `LOG/index.md` into its own file, so a
planning session's opening read stays short as the archive grows. Nothing had ever been moved: the index
held every line back to May.

**What had stopped it, and why it stopped mattering.** A close earlier in the month tried the move and
halted. A line's month is read from the entry filename it ends with, and many entries carry no date in
their filename — the numbered batch records and other pre-convention ones. Moving only the identifiable
August lines would have left the index holding September plus a block of undated older lines with August
cut out of the middle, which is worse than the file that existed, because the index reads newest-first
and that ordering would break. Both routes proposed at the time — dating the undated lines by hand, or
abandoning rollover — were costed against opening around thirty-eight entry files to read their dates.

That premise was false, and checking it is what unblocked the work: git records when each entry file was
added, so one command dates every undated line without opening anything. Five undated entries were
tested and all five returned a date. That turned a bulk hand restructure into a mechanical job, and it
also answered the question the original entry had left open — it should be a script, because it is due
again every month. Doing it by hand was refused for that reason: the next close would rediscover the
same obstacle.

Accepted rather than solved, and recorded in the script: a git add-date is when the entry was committed,
which can be a day after the session it records. For sorting into months that is almost always the same
answer; an entry within a day of a month boundary could land in the wrong file.

The script dates each line from its filename first, then from the line's own text, then from git, and
leaves anything it cannot date in place rather than guessing. It is re-runnable, and it reads the newest
month present in the file rather than the clock, so a run in a quiet month cannot empty the index of the
lines people are still reading.

**Files touched:** `scripts/split_log_index.py` (created), `LOG/index.md`, `LOG/index-2026-08.md`,
`LOG/index-2026-06.md`, `LOG/index-2026-05.md` (all created). No July file — there were no July entries.

**Routed to Captures:** none.

**Tick:** done, confirmed — all four of the item's observations checked: `index.md` holds September only,
every moved line appears exactly once, the total across `LOG/index*.md` is unchanged at 129, and a second
run moved nothing.
