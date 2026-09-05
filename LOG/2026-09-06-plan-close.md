# [HASH] — /plan chat-level record: twelve entries processed, the cleared region 3 → 11, and three device checks that overturned what the queue believed

Recorded 2026-09-06, 00:29. This session ran across 2026-09-05 and 2026-09-06.

Twelve Unprocessed entries were processed — five kept into Processed, seven deleted with their content
relocated — and each has its own record under its slug. Two items were created ([bin-drag-target-check],
[runs-in-android-studio-decision]) and five held items were lifted at the opening, their blockers having
shipped and been verified on a device: the two date-matrix checks, free-tier Project delete and reorder,
and applying the Supabase migrations. One deliberately did not lift — the day-card layer waits on Search,
which shipped but failed its device check, so built-not-verified kept it below the line.

The cleared region went from 3 items to 11. Both cleared `[user]` items from the previous session —
creating the bug-report address, and running the instrumentation tests — were walked to done on
2026-09-05 and are removed from Processed here; their records already stand under their own slugs.

**Three checks run on the phone this session overturned things the queue believed**, and that is the
session's real work. The edit dialogue does have an outliner, so the audit's largest finding named the
wrong defect. Search does show the completed history, so that finding does not reproduce at all. And the
phone is carrying the current builds, not the older APK an audit item warned about. Reading alone would
have missed all three: in each case the code and the record disagreed, and only driving the device
settled it.

**A fourth belief fell to reading rather than driving:** the commit-hash backfill cannot corrupt a
placeholder written in prose, and cannot false-alarm on one either, so the capture worrying about it was
deleted on a false premise rather than deferred.

**Also in this chat:**

- **Claude's own hypothesis was wrong and is recorded as wrong.** The first explanation offered for the
  missing subtasks — that the audit's injected Enter could not fire a keyboard action handler — was
  plausible, and testing it on the phone disproved it. The real cause is a parse-and-re-render round trip.
  It matters because the wrong explanation would have closed the item as a false alarm.
- **Alex saw the cost of a proposal before it was written down.** Told that `gradlew` might work in
  Android Studio's terminal, she asked whether that meant `/next` runs would have to happen there instead
  of the desktop app, and whether a CLAUDE.md rule would be needed to say which items require it. Both
  right, and neither was in the item; the consequence became [runs-in-android-studio-decision] rather than
  a silent assumption inside the try.
- **A correction to a recommendation, made after the user had already agreed to it.** The plan for the
  test-run warning was to add it to [run-instrumentation-tests]'s walkthrough; that item turned out to be
  finished and leaving the queue, so the warning went to TOOLS.md instead and the change was reported
  rather than made silently.
- **Alex's Workspace admin address was scrubbed from three places in two tracked files**, with the
  residual accepted knowingly: git history keeps what is committed. Detail in that item's own record.
- **Two messages went out, both approved verbatim first.** To flintcraft.tech, the accounts and addresses
  Taskflow's work has stood up on that domain, with her admin address deliberately unnamed at her
  instruction. To No code method, a usability report on the queue mover's clear-guard, sent without
  proposing a fix and carrying the counterweight — this project's own record shows the accidental sweep
  the guard prevents. Both registered in `INBOX/sent.md`.
- **A throwaway task was left on Alex's phone** by the Search check, because deleting it over adb failed
  three times. [bin-drag-target-check]'s first step names it, so the cleanup rides work she will do anyway.
- **One question was for a different project entirely** — a changelog for porters — and nothing was filed.

**Queue changes:** twelve entries processed; five held items lifted; two items created; two completed
`[user]` items removed from Processed; `[user]` and `[audit]` lines batched to the end of the cleared
region and the readiness marker placed after [verify-run-2026-08-31-remainder]; `SPEC.md` §Settings
rewritten; one research finding filed with its index line.

**Work processed:** kept — [instrumentation-tests-uninstall-the-app], [edit-outliner-missing],
[verify-run-2026-08-31-remainder], [gradle-from-ide-terminal], [device-layout-clipping]. Deleted —
[export-before-first-end-to-end-test], [first-end-to-end-test-waits-on-subtasks],
[prose-hash-token-in-setup-entry], [search-omits-completed], [admin-address-in-committed-index-line],
[spec-ai-tier-location], [bug-report-address-is-bugs-at-flintcraft].

**Advisory:** filed — edit-outliner-missing
