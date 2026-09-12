# d6ac7e8 — /plan chat-level record: sixteen entries processed, the queue re-pointed at a month of real use, and three items whose premise turned out to be false

This session ran across 2026-09-06, 2026-09-07, 2026-09-08 and 2026-09-12, and the
plugin moved from 1.22.0-test2 to 1.22.0-test5 partway through, which is why the
governing docs were re-read mid-session.

The cleared region went from 3 to 12. What shaped the session more than the count
was how often an item's stated premise did not survive contact with the code or the
record. [drag-eaten-by-page-swipe] blamed a library for something Taskflow does on
purpose. [strategy-share-silent-when-empty] asked for a guard that already existed.
[strategy-edit-persistence-blocked] was blocked by a condition that had resolved
itself hours after it was filed. [setup-log-entry-malformed-placeholder] was already
done. [session-start-clock-eleven-hours-out] reported a defect that never happened.
Five of sixteen, each of which would have produced wrong or wasted work if built as
written.

**Also in this chat:**

- **Alex's correction about item summaries, and the report it produced.** She
  stopped a summary that leaned on a phrase lifted from a queue entry, saying
  plainly that she does not read the queue and the method is not supposed to assume
  she does. That was sent to the method's own project as a defect report, with her
  approval on the exact text. Re-reading the docs after the version change showed
  1.22.0-test5 had already closed it: the summary rule now bars a term the entry's
  own text introduces and requires it explained on first use.
- **A second report, on the changed `Blocked by:` semantics for captures.** Two
  captures set aside for sound reasons came straight back in one run, one of them in
  the session it was held. Alex asked first whether a setup run would fix it, which
  ruled out the innocent explanation — the project records format epoch 5 and the
  plugin's is also 5 — and turned a guess into evidence. Sent with her approval, and
  framed as a deliberate clause with a counterweight rather than as a bug.
- **Mail from the flintcraft.tech project, triaged and archived.** It corrects an
  earlier finding this project sent: Netlify cannot receive inbound domain
  transfers, only send them out. Nothing Taskflow depends on changes — the Workspace
  tenant and the bug-report alias are untouched — and the correction was written
  into the domain research file so it is not rediscovered.
- **Alex's month-of-use decision**, given late in the session: she wants a month of
  daily use, changing the app as she goes, before considering publishing, and the
  publishing blockers live in another project waiting on her financial and tax
  position. That re-dated [business-registration-for-play-account] and became a
  standing ordering instruction for future sessions.
- **A correction Claude made and withdrew.** A timezone hypothesis for the wrong
  clock line was drafted into a report; Alex's explanation — a days-old session
  picked up at random — was better and the report was never sent.
- **A SPEC gap found by the closing gate.** Reporting a bug by email had been
  decided on 2026-08-31 and never described in SPEC; §Side menu gained the sentence
  with Alex's approval before the commit.

**Queue changes:** cleared region 3 → 12; sixteen entries processed; five deleted;
three new items filed and cleared ([verify-edit-outliner-fix],
[emulator-for-instrumented-tests], and [rotating-roster-free-choice] filed and
held); four stale `Blocked by:` references repaired; the human stops batched at the
end of the cleared region.

**Work processed:** kept — [drag-eaten-by-page-swipe], [cloud-schema-missing-grants],
[runs-in-android-studio-decision], [day-begins-at-rollover-still-unrun],
[strategy-edit-persistence-blocked], [rotating-roster-recurrence],
[test-log-owes-2026-09-06-rows], [strategy-share-silent-when-empty],
[yesterday-page-with-content-untested], [verify-edit-outliner-fix],
[emulator-for-instrumented-tests]. Deleted —
[project-delete-later-premise-may-be-gone], [two-cleared-items-underspecified],
[rls-test-steps-bypass-rls], [day-begins-at-hour-granularity],
[session-start-clock-eleven-hours-out], [setup-log-entry-malformed-placeholder].

**Routed to Captures:** [rotating-roster-free-choice].

**Advisory:** filed — [forward-advisory]
