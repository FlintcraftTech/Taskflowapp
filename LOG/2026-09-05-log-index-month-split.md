# [HASH] — /plan [log-index-month-split]: two entries merged into one scripted item, after git turned the blocking cost into nothing

Session date and time: 2026-09-05, afternoon planning run.

Two captures described the same problem a day apart. [log-index-month-split], filed at the 2026-09-04
close, recorded that `LOG/index.md` still holds every line back to May when an ended month should have
moved into its own file. [log-index-month-rollover-blocked], filed at this morning's close, recorded what
happened when a close actually tried: a line's month is read from the entry filename it ends with, and a
large block of lines point at entries whose filenames carry no date. Extracting only the identifiable
August lines would leave the index holding September plus undated older lines with August cut out of the
middle — worse than the file that exists, because the index reads newest-first.

Counted today to check rather than trust: 117 lines, 62 naming September entries, 17 naming August, 37
naming undated ones. The shape holds.

The premise underneath both of the blocked entry's proposed routes does not. Both were costed against
opening around thirty-eight entry files to read their dates. Git records when each file was added, so one
command dates every undated line without opening anything — tested against five undated entries, all
five returned a date. That turns a bulk hand restructure into a mechanical job, and it answers the
question the original entry had left open: it should be a script, because it is due again every month.

Merged rather than superseded — two accounts of one thing, so the host was rewritten to carry both and
the second entry deleted. It stays cited in the host's prose as the source of the findings, which is why
the queue tool's warning about a now-absent slug was left standing rather than repaired.

Accepted rather than solved: a git add-date is when an entry was committed, which can be a day after the
session it records. For sorting into months that is almost always the same answer; an entry within a day
of a month boundary could land in the wrong file.

**Queue changes:** [log-index-month-split] rewritten as a scripted item with its file list and moved to
Processed, cleared to run; [log-index-month-rollover-blocked] deleted from Unprocessed.

**Work processed:** kept — [log-index-month-split]. Deleted — [log-index-month-rollover-blocked].
