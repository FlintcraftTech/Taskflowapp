# [HASH] — /plan [subtask-affordance-in-edit-dialogue] and [first-end-to-end-test]: both lifted above the line, their shared blocker having been verified on a device rather than merely compiled

**Kind: processed.** Recorded 2026-09-17 at 16:01, read from the clock, during a planning session.
This entry covers two items settled by one check; [first-end-to-end-test] cites it rather than repeating it.

Both waited on [verify-edit-outliner-fix], the on-device check of the subtask outliner. Its record shows
that audit ran clean on every claim on 2026-09-12: Enter opens an indented subtask, it survives a save and
reopen, the parent renders with an expand control, and completing the child completes the parent.

That is the distinction the hold turned on, and it is worth stating because three other items in this queue
still fail it. The fix shipped on 2026-09-06 and **compiled**; compiling is not the same as anyone having
seen it work, and the method's rule holds an item whose blocker is built but unverified. What released
these two is that somebody watched it.

For the subtask hint, the concern was precise: a hint advertising a feature that does not work is worse
than the silence it replaces. That concern is now answered rather than merely aged out.

For the end-to-end test, the condition was Alex's own, given when she deferred the item during a run — not
until subtasks exist. A compile did not meet the condition she set; a watched check does.

Both items had also been left pointing at a blocker that no longer existed in the queue, having been
repointed on 2026-09-12 from the shipped fix to the check that verifies it — so the holds would never have
lifted on their own. The lift was narrated rather than asked: under the method, a blocker built and
verified is a fact read off the record, not a decision.

**Queue changes:** [subtask-affordance-in-edit-dialogue] and [first-end-to-end-test] both moved above the
cleared-to-run line, their `Blocked by:` lines dropped and what cleared them written into their prose.

**Work processed:** kept and cleared — [subtask-affordance-in-edit-dialogue], [first-end-to-end-test].
