# [HASH] — /plan [recurring-completions-in-history]: completed occasions of a repeating task are invisible after the day, which is where the single-pick design's whole payoff was supposed to live

**Kind: processed.** Recorded 2026-09-17 at 16:01, read from the clock, during a planning session.

Split out of [roster-as-subtasks] the moment the gap was found, and its reasoning depends on that item's —
recorded there.

The gap: a completed occasion of a repeating task appears in the Completed tray at the bottom of the Today
page, on the day it was completed, and nowhere else ever again. Reading the three surfaces that list
completed work — Search, the day-detail card and the Yesterday page — showed all three miss occasions for
one structural reason: each selects tasks where `isCompleted` is true and `completedAt` is not null, then
groups by the logical date of `completedAt`. A recurring occasion is neither, because the parent is not
complete and the occasion is a date held against it. None of the three is behaving wrongly.

That mattered more than a missing feature, because Alex's whole justification for putting no rotation logic
in the app is that she reads her history back and judges evenness herself. Without this, the picks the
other item records are written and never seen.

It also settled the question the capture had left open — whether occasions belong on the existing surfaces
or need one of their own. They belong on the existing ones. Refused: a surface of its own, on SPEC §Search
and completed history's own stated ground that two boxes make the user guess which to open; a third list
for the one kind of task whose history she most wants would reintroduce exactly that. Refused too: fixing
each of the three view models separately, which would drift apart the first time one was touched — hence a
single shared derivation used by all three.

Placed immediately after [roster-as-subtasks] rather than held against it: both are Kotlin, one compile
covers both, and this item is useless on its own since until that one ships there are no picks to show. A
`Blocked by:` line would have hidden it from the run that should build it.

**Queue changes:** [recurring-completions-in-history] rewritten from the split-out capture into a work item
and cleared to run — six files under `ui/history/`, one new. SPEC §Search and completed history gains the
sentence saying an occasion is listed as its own row, showing the picked subtask where there is one.

**Work processed:** kept — [recurring-completions-in-history].
