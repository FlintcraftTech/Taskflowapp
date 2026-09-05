# [HASH] — Deleted, does not reproduce: Search shows the completed history on the build now on the phone, and the cause of the audit's verdict is left unexplained rather than guessed

Recorded 2026-09-06, 00:29.

The 2026-09-05 audit recorded Search as a failure on both halves of SPEC §Search and completed history: an
empty query listed active tasks with no date headers, and a task completed minutes earlier appeared in
neither view. The capture itself flagged the contradiction — [nav-search-completed-history]'s record claims
both readings were honoured — and asked for the two to be read against each other before deciding whether
this was a regression, a build that never matched its record, or a query returning nothing.

Reading the code found no defect. `TaskDao.search` deliberately does not filter on completion state, and
its comment says why; `SearchViewModel` splits the results into active tasks and completed days correctly;
and the completion path does write the `completed_at` timestamp the history groups by.

Code and device disagreeing is not something reading can settle, so the check was run on the phone. An
empty Search page proved nothing — the database held no completed tasks at all — so a throwaway task was
created, completed, and Search reopened: the completed history rendered under a day header reading
"Saturday 5 September", with the matching active task listed above it. Both halves hold.

**The cause of the audit's verdict is deliberately not asserted.** The likeliest explanation is that the
audit drove an older APK and the phone has since been reinstalled from a newer one, but that was not
established, and writing a guess into the record would read exactly like a finding. What is established is
that the finding does not reproduce on the build Alex is running.

One half was not re-checked: whether typing a query narrows the completed history as well as the active
list. That went into [verify-run-2026-08-31-remainder] rather than staying in an item whose headline claim
had just been disproved.

A throwaway task was left on the phone by this check — deleting it over adb failed the same way the bin
target does — and [bin-drag-target-check]'s first step now names it.

**Queue changes:** capture deleted; the untested half added to [verify-run-2026-08-31-remainder].
**Work processed:** deleted — [search-omits-completed].
