# 0bd8c64 — /next chat-level record, 2026-09-05: six builds, a device audit, two [user] items driven, and a test run that uninstalled the app

Written 2026-09-05, 22:10. The per-item records are the six `-build` entries, the audit entry
`2026-09-05-verify-run-2026-08-31.md`, and the two `[user]` records named below. This carries what
belongs to the chat rather than to any one item.

## The `[user]` items

**[run-instrumentation-tests] — done.** Walked to its end; record in
`2026-09-05-run-instrumentation-tests-build.md`. The first attempt did not run because it did not
compile: `TaskDaoTest.kt` called `updateCompletion` with two arguments where the DAO has taken three
since completion timestamps were added. The tests had been uncompilable for some time and nothing had
noticed, because nobody had ever run them — the gap the item existed to close, found by closing it. The
fix was folded in with Alex's agreement after adding that file to the run's scope; the second attempt
gave 20 tests, 0 failures, `MigrationTest` among them.

**[bug-report-email-address] — done.** Record in `2026-09-05-bug-report-email-address.md`. The address
is `bugs@flintcraft.tech`, a Workspace alias, tested by a real send from an outside account.

**[first-end-to-end-test] — deferred**, on Alex's own word and with her own condition: not until subtasks
exist, since a day's real use without any way to break a task into steps would fill the notes with an
absence already known and already filed. Recorded as [first-end-to-end-test-waits-on-subtasks] so the
ordering survives this chat.

## The incident: the test run uninstalled the app

Hours after the instrumentation tests ran, Alex said Taskflow was no longer on her phone. It was not: the
package was confirmed absent before it was reinstalled. A Gradle connected-test run installs the app and
the test package, runs them, and removes both — nothing else in the day touched the installation.

Losing the app takes the Room database with it, and the database is the single source of truth for
everything the user has. Filed as the uncleared red flag [instrumentation-tests-uninstall-the-app].

Two things then happened that matter more than the incident. The app was reinstalled from the afternoon's
APK over adb, and **Android Auto Backup restored the database by itself** — her task came back without
the JSON being needed. And an export had been taken at 14:09 anyway, so a second copy existed. Both her
14:09 export and a scrubbed single-task restore file were sent to her regardless.

That bears directly on a question she raised earlier in the same run: whether she can count on tasks
staying in the app. The honest answer given at the time named what is established — `MigrationTest`
passing, the destructive fallback narrowed to versions 1–4, export/import working — and what is not:
that no future migration is proven correct, that reinstalls were never scoped, and that there is no cloud
sync. Then the run's own testing wiped the device and two independent safety nets caught it. The answer
is better evidenced than it was, and it was evidenced the hard way.

**Claude's own error to record: the advice given her an hour earlier was that the export was her safety
net, and then this run's testing was what removed the app.** The export existed only because a different
test needed one. That was luck rather than design, which is why the red flag says so.

## Also in this chat

**Two things Alex was right about and Claude was wrong about.** She asked whether the GitHub accounts
were "all just the same" after being told a post would go out under the wrong identity — `gh auth status`
prints "Logged in to … FlintCraftTech", which is an *organisation*, while the authenticated user is her
own account. She was right; the warning was wrong, and `gh api user` is the check that answers it. And
she asked for the Android Studio plugin's intended sidebar control to be researched before a bug was
reported about arrow keys — which was a guess about a control the documentation never claims. Two drafts
were discarded on that research, including one whose central request already exists as `Ctrl+Tab`.

**A Claude Code issue was filed at her request**, anthropics/claude-code#92295, under her own account:
`Ctrl+Tab` cycles every session in every project rather than the working set, which with fifteen projects
is as unusable as no shortcut. It cites her earlier #83699, the same design point from the display side.

**Research filed:** `workshop/resources/research/claude-code-jetbrains-plugin-capabilities.md`, with its
index line. The Android Studio plugin exposes no code-execution tool, so the `[user]` flavor on build and
test items stands; what it does add is that Claude can read the IDE's inspection errors, which is exactly
the class of failure that stopped the test run. The one untested route it surfaced — whether `gradlew`
works from the IDE's own integrated terminal, a different shell from the one the loopback failure was
recorded against — is filed as [gradle-from-ide-terminal].

**Getting the plugin working needed two wrong paths handed over before one was verified by running it.**
The CLI is at `.local\bin`, and PowerShell needs the call operator and quotes because the profile name
contains a space. Both TOOLS.md lines written from this now carry the verified form.

**A second Claude Code session now runs on this project**, inside Android Studio. Alex was told plainly
that captures filed in one are invisible to the other, and that workflow commands should stay in one chat
at a time.

**Scrub note.** Two personal email addresses were rewritten out of the bug-report record before it stood,
since the repository is public and, unlike the bug-report address, they are not meant to be published.
The queue lint's credential scan flags `bugs@flintcraft.tech` in QUEUE.md on every commit touching that
entry; that is expected, since the address exists to be printed in a shipped app. Everything written this
session was checked against the scrub checklist and that credential scan, and against nothing more —
neither can tell whether ordinary prose names a real person or a real situation.

**One discipline slip, left for planning.** The queue lint flags [edit-outliner-missing]'s heading for
starting with "The". A build may not hand-edit QUEUE.md and the queue tool does not rename, so it stands
until /plan.

**Routed to Captures** (run-level, beyond the audit's five): [instrumentation-tests-uninstall-the-app]
(uncleared red flag), [bug-report-address-is-bugs-at-flintcraft], [gradle-from-ide-terminal],
[export-before-first-end-to-end-test], [first-end-to-end-test-waits-on-subtasks],
[admin-address-in-committed-index-line].

**Advisory:** filed — [forward-advisory]
