# PENDING — /next chat-level record: three fixes shipped and an audit run, then five user steps that put Supabase live, overturned a belief the queue had held for three sessions, and proved Claude's shell was never the problem

Session date and time: 2026-09-06, 22:12. The chat opened in the morning, was resumed in the evening, and
the plugin was updated between the two halves — 1.22.0-test1 to 1.22.0-test2. The rules and `next.md` were
re-read at the resume and diffed against the versions the run started under; the changes were refinements
and none reversed anything already done.

The run's own work is in the four entries beside this one — [instrumentation-tests-uninstall-the-app],
[edit-outliner-missing], [device-layout-clipping] and [verify-run-2026-08-31-remainder] — and the five
`[user]` items have their own entries, written live as they were driven. This record carries what belongs
to none of them.

## Also in this chat

**Two cleared items were dropped from the run before any file was touched.** [project-delete-later] and
[project-reorder-strategy] name no files and do not say what changes inside any, so the self-scoping step
halted on them. Alex agreed to drop both from this run with the queue left untouched, and the remaining
nine went ahead. Filed afterwards as [two-cleared-items-underspecified], because nothing in the queue
recorded why they were skipped and the next run would meet the same wall.

**The scope-lock earned its keep twice.** It refused an edit to `ScheduleScreen.kt`, which
[device-layout-clipping] had not listed, and refused a hand-edit to QUEUE.md when a lint flag tempted one.
The first was a genuine mis-naming in the item and was resolved by adding the file with Alex's approval;
the second stands, and two capture headings keep their awkward wording for a planning session to fix.

**Three `[user]` tags turned out to be wrong, in three different ways.** [verify-schedule-date-matrix] was
Claude's work all along — the capability check found every step drivable over adb, and it ran as ordinary
work. [bin-drag-target-check] was filed as user work because adb had failed at it three times; Alex's own
thumb then failed identically, so it was never user work either, it was a defect. And
[supabase-apply-cloud-migrations] was correctly tagged, for the reason the tag exists: every route into her
Supabase project needs a credential Claude is barred from handling.

**A belief the queue had held for three sessions was overturned by one gesture.** TOOLS.md had recorded, on
this session's own evidence earlier in the day, that a long-press drag cannot be steered between adjacent
targets over adb. Alex reproduced the failure by hand. The line was corrected in place rather than left for
a later session, because it was actively telling future sessions the fault was theirs. The lesson written
alongside it: repeated failures of one tool are evidence about the thing being driven just as readily as
about the driver, and assuming the driver cost a wrongly-tagged item and three sessions of misreading.

**Claude's inability to compile this app turned out to be narrower than recorded.** Gradle runs fine from
Android Studio's integrated terminal; the loopback failure belongs to the shell these sessions run in, not
to the machine. That build also compiled the session's own four changed files, which is why two items ship
UNCONFIRMED on behaviour alone rather than on whether they build at all.

**Grants were applied by hand to Alex's live Supabase database**, on her approval, after she was shown the
exact statements and told plainly it was a write rather than a read. They exist on the database and in no
file — the one gap between the repository and the working project, recorded in
[cloud-schema-missing-grants].

**A `svc power stayon true` was set on her phone** after the screen locked four times mid-run, with her
approval, and set back to false when the device work ended.

**Alex named a Project she actually wanted rather than accept a throwaway.** [verify-far-future-project-card]
needed a user Project, and one created for testing could not have been deleted afterwards. Put to her on
that item's own turn, she named **Family** — so the check ran on real data and the Project stays because she
wants it. Her description of how she wants to use it produced [rotating-roster-recurrence]; the people in
it are described by relationship rather than named, per the scrub checklist.

**Mail from the Throughliner project** was read and archived: this project's report on the queue tool's
clear-guard was right, the fix is queued, and a route through the guard already exists on the installed
build — one `--move <slug> AFTER <last-cleared-slug> --marker-after <slug>` per item. Planning technique
rather than work, so nothing was acted on. No reply was owed.

**One correction Claude made to itself mid-session:** the `[user]` walkthrough for
[supabase-apply-cloud-migrations] contained test steps that could not have detected the failure they
existed to catch, because the Supabase SQL editor bypasses row-level security. Checked against Supabase's
own documentation, rewritten live, and filed as [rls-test-steps-bypass-rls] so the queue entry stops
carrying the broken version. Correcting it is what found [cloud-schema-missing-grants], which was the
session's most consequential result.

**Wind-down re-scan:** covered by the /rescan run immediately before this close, which filed five captures.
Nothing happened between it and the close but the close's own invocation.

**Memory check:** every run the files record is still in view.

**Files touched:** none of its own — this entry records the chat rather than a work item. `TOOLS.md` gained
three further lines across the session beyond those [instrumentation-tests-uninstall-the-app] wrote: the
correction above, the `uiautomator` stale-dump trap, the silent `adb input text` failure, and the Gradle
result.

**Routed to Captures:** [rls-test-steps-bypass-rls], [cloud-schema-missing-grants],
[rotating-roster-recurrence], [drag-eaten-by-page-swipe], [two-cleared-items-underspecified],
[project-delete-later-premise-may-be-gone], [test-log-owes-2026-09-06-rows],
[setup-log-entry-malformed-placeholder], [session-start-clock-eleven-hours-out].

**`[user]` item outcomes:** [supabase-apply-cloud-migrations] done; [verify-schedule-date-matrix] done;
[verify-far-future-project-card] done; [gradle-from-ide-terminal] done — all four removed from Processed.
[bin-drag-target-check] halted mid-drive at step 2, the gesture it tests being broken; it stays in the
queue and cannot pass until [drag-eaten-by-page-swipe] ships.

**Advisory:** filed — forward-advisory, replacing a spent one.
