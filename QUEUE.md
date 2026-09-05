# QUEUE

## Processed

> Vetted work, ready to build — worked top to bottom. Each piece of work is one
> item: a `#### ` heading naming it, a `[slug]` at the end of that heading line, and
> a short rationale beneath. A leading flavor tag names how it runs — none for a
> build (Claude edits files), `[audit]` for a review pass, `[user]` for a step only
> you can do. A security or privacy risk Claude surfaces lives here too, as a work
> item carrying a `Red flag · State: cleared/uncleared` marker. The line below marks
> how far down is cleared to build; anything below it is decided but not ready yet.

#### Delete the stale app/build folder left behind by the output move [stale-app-build-folder]

`app/build` is still in the project, dated 2026-09-01, holding roughly 59 MB that Google Drive keeps
syncing for nothing.

Found on 2026-09-04, immediately after [project-out-of-drive] shipped and its first build ran. That
item pointed Gradle's output at `C:\builds\taskflow\app`, and the build confirmed it — the new
location holds the compiled output and the APK. What it does not do is remove what was already
there: relocating where a tool writes says nothing about the folder it used to write.

The item's own observation expected `app\build` to be "gone or empty" and it is neither, which was
recorded against its tick rather than quietly passed. This is the other half.

It is git-ignored generated output, so deleting it loses nothing and no build depends on it. The
reason to bother: it is 59 MB of constantly-rewritten files inside a Drive-synced folder, which is
half of what [project-out-of-drive] was trying to stop, and it will sit there indefinitely because
nothing writes to it any more to make its staleness visible.

**Kept standalone in planning on 2026-09-05.** The capture suggested folding it into another piece of
work. Refused: the candidates are unrelated, and folding a folder deletion into the LOG index script or
the Strategy header fix makes both harder to read for no gain. It is one deletion a run disposes of in
seconds, so it is placed first in the cleared region instead.

**Re-checked on 2026-09-05 before clearing a deletion, rather than trusting the capture.** The folder is
still present, 53 MB — the capture said 59 MB, which is the same fact measured on a different day — and
last written 2026-09-01. `git check-ignore` confirms it is ignored by `app/.gitignore`, and git reports
nothing tracked inside it, so nothing is lost that git could not already not restore. The compiled
output and the APK live at `C:\builds\taskflow\app` since [project-out-of-drive], so no build depends on
this folder.

Files:
- `app/build/` — deleted outright. Generated, git-ignored, and no longer written to.

Observation: `app/build` is absent from the project, and `C:\builds\taskflow\app` still holds the
compiled output. The check reaches the one path named above.

Rests on, each read 2026-09-05: that `app/build` exists at 53 MB, that `app/.gitignore` ignores it, that
git tracks nothing inside it, and that Gradle's output now goes to `C:\builds\taskflow`. TOOLS.md also
records that deleting this folder from Claude's shell is what clears the Windows file-lock failure, so
the deletion is a route already exercised rather than a new one.

Filed 2026-09-04, 22:50, by /rescan at the end of a /next run.

#### Create the cloud schema and its Row Level Security policies [supabase-rls-policies]

Writes the SQL that creates Taskflow's four cloud tables and the Row Level Security policies that make
each row reachable only by the account it belongs to. It is the security model for everything that ever
reaches the cloud, written before any sync code exists.

Found on 2026-09-04 while driving [supabase-project-setup], at the create-project form's Security
block. Settled there with the user, and recorded in that item's LOG record: the Data API is on
because the app needs it, automatic exposure of new tables is OFF, and **automatic RLS is ON** — so
a new table arrives with Row Level Security enabled and no policies, which denies everything. That
is why this is not an uncleared red flag: there is no exposure today, and the default is closed
rather than open.

**Widened from a policies-only capture in planning on 2026-09-05, on Claude's recommendation.** As
captured it described policies for tables that nothing had created, so it could not say which files it
changed — there is no cloud schema in this repository at all. Refused: folding it into
[0018-cloud-sync-paid-tier] as a constraint that a policy ships with each table. That would leave the
security model to be decided part-way through a large sync build, which is the item's own argument
inverted. Owning the schema is what gives it a file list and puts the policies first.

**Why this matters more here than in an ordinary web app.** The publishable key ships inside the Android
app on every phone, so it is public by construction and cannot be treated as a secret. The policies are
the only thing standing between one account's key and every other account's tasks. RLS with no policies
denies the app too, which is why the policy has to be written in the same move as the table.

**Three things the capture left open, all settled on 2026-09-05.** A Taskflow account is a Supabase Auth
user, per [0020-remote-mcp-server], which already makes Supabase Auth the identity provider. A row is
tied to that identity by a `user_id` column matched against the authenticated caller. And the MCP server
reads rows through these same policies rather than through a key that bypasses them — a Supabase Edge
Function can run in a mode where the platform validates the caller's token and hands the function a
database client already scoped to that user, so the guarantee stays in the database instead of moving
into the server's own code.

**One schema decision this settles rather than passes on.** The local Room tables key on an
autoincrementing integer, which cannot be the cloud key: two devices on one account both generate id 1.
Cloud rows therefore carry a UUID primary key, and mapping a local id to it is
[0018-cloud-sync-paid-tier]'s work. Naming it here stops the sync build inventing a key shape around
code it has already written.

**The failure mode to write against, and it is silent.** For a caller with no session the identity
function returns null, and a policy phrased as a comparison against null evaluates to null rather than
false — phrased as a negation or an `or`, that widens to every row instead of none. Each policy is
written so the no-session case denies, and [supabase-apply-cloud-migrations] is what proves it on a real
database.

Files:
- new `supabase/migrations/0001_initial_schema.sql` — the four cloud tables mirroring the Room schema at
  version 5 (`tasks`, `projects`, `strategy_entries`, `life_areas`), each with a UUID primary key and a
  `user_id` column referencing the authenticated user, and each with Row Level Security enabled.
- new `supabase/migrations/0002_rls_policies.sql` — select, insert, update and delete policies on all
  four tables, scoped to authenticated callers and matching `user_id` against the caller's identity,
  each written so a null identity denies rather than matches.
- new `supabase/README.md` — what the two files are, that they are applied by
  [supabase-apply-cloud-migrations], and the naming note that `sb_publishable_` and `sb_secret_` have
  replaced the anon and service_role key names.

Reads but does not change: `app/schemas/com.example.taskflow.data.local.TaskflowDatabase/5.json`, the
recorded Room schema the cloud tables mirror.

Observation: every table created in `0001` carries a `user_id` column and an enable-RLS statement, and
`0002` carries a policy for each of the four operations on each of the four tables with none phrased so
that a null identity matches. Checkable by reading the two files. The check reaches the three files named
above. Proving the denial against a live database is [supabase-apply-cloud-migrations], which is where a
database exists.

Refused: a service role key for the MCP server, bypassing RLS and enforcing per-account access in the
server's own code. It moves the guarantee out of the database and into every call site that might
forget, and the research below shows it is not necessary.

Rests on, each read 2026-09-05 and recorded in
`workshop/resources/research/supabase-rls-and-edge-function-identity.md`: that the publishable key is
safe to expose only because RLS checks the caller's token against each table's policies; that the
per-user policy shape is a `user_id` comparison against the authenticated identity, scoped to
authenticated callers; that a no-session identity is null and can widen a carelessly phrased policy to
every row; and that an Edge Function can run RLS-scoped to its caller rather than bypassing RLS.
Supabase amends this on a cycle and the key names are mid-rename, so re-read before writing the SQL.

No SPEC edit is owed. SPEC §Claude integration via remote MCP already states that every tool call is
answered against that account's cloud data and no other's, and that a request the server cannot tie to
an authenticated account is refused rather than guessed at. That is the product truth; Row Level
Security is the mechanism serving it, which SPEC's own admission rule keeps out.

Filed 2026-09-04, 22:50, by /rescan at the end of a /next run.

#### Strategy page shows two headers now that it is on the spine [strategy-page-double-header]

Found while building [strategy-on-spine] on 2026-09-04. That item moved the Strategy doc from an
overlay shown over the spine to being the spine's rightmost page, and deliberately did not alter
`StrategyScreen.kt` — it names the file as read but not changed, on the reasoning that the screen is
rendered from a different place rather than rewritten.

The consequence is cosmetic and visible on the first look at the page. `StrategyScreen` carries its
own header row from its overlay days: a back arrow, the word "Strategy", and the share button. The
spine's own header sits above it and also names the page. So the user swiping right from Later meets
"Strategy" twice, one under the other, with a back arrow that duplicates what the system back gesture
already does.

Nothing is broken — the back arrow works, and it was wired to return to Today, which is the spine's
back rule (SPEC §Schedule view). It reads wrong rather than behaving wrong.

**Settled in planning on 2026-09-05.** The back arrow and the title go: the spine header already names
the page, and the arrow was wired to return to Today, which is exactly what the system back gesture does
under the spine rule. The share button moves into the spine header, which gains a trailing action slot —
empty on every page but Strategy.

**The constraint that made this a design question, and why it does not bite.** The spine header's
top-right corner was deliberately left clear at the original build for the drag-to-delete target SPEC
§Drag-target icons puts in the upper-right. That reservation is real, but it applies while a task is
being dragged, and the Strategy page holds headings and paragraphs rather than tasks — so on the one
page that uses the slot, the corner is never claimed. Refused: leaving the share button in
`StrategyScreen`'s own row with only the arrow and title removed — it is the smallest change and it
still leaves two header rows stacked, which is the thing being fixed. Refused: a share control at the
foot of the document — it hides the app's one sharing affordance below a scroll of the user's own
writing. The user chose the header slot over both.

**How the action reaches the header.** `StrategyScreen` builds its share intent today from its own
view-model's rendered Markdown, so the header cannot reach it where it sits. The view-model is obtained
in `ScheduleScreen` instead — the same factory call, moved up — and its sections passed down into
`StrategyScreen` as a parameter, which is what lets the header's Share action render the same text.
Ordinary state hoisting; named here so the build does not have to invent a route.

Files:
- `app/src/main/java/com/example/taskflow/ui/schedule/ScheduleScreen.kt` — `SpineHeader` gains an
  optional trailing composable slot, rendered in the top-right and absent when not supplied; the
  `SpinePage.STRATEGY` branch obtains `StrategyViewModel` and supplies a Share button as that slot,
  passing the sections into `StrategyScreen`.
- `app/src/main/java/com/example/taskflow/ui/strategy/StrategyScreen.kt` — its own header row and the
  divider beneath it removed entirely, along with the back arrow, the "Strategy" title, the Share
  button and the now-unused `onBack` parameter; sections arrive as a parameter rather than from a
  view-model obtained here.

Observation: on a device, swiping right from Later shows "Strategy" once, in the spine header, with a
Share button in the header's top-right and no back arrow on the page; tapping Share opens the share
sheet with the document's text; the system back gesture returns to Today; and the other spine pages
show no trailing control. The check reaches both files named above.

Rests on, each read 2026-09-05: that `SpineHeader` is a private composable in `ScheduleScreen.kt` with
its menu key at the start and a centred title flanked by chevrons, and nothing in its end corner; that
`ScheduleScreen.kt`'s pager renders `StrategyScreen` for `SpinePage.STRATEGY`; and that `StrategyScreen`
holds its own header row and obtains `StrategyViewModel` itself.

No SPEC edit is owed. SPEC §Strategy doc says a share button in the Strategy doc area shares the doc
through Android's share sheet, which stays true — where the button sits is UI mechanics, which SPEC's
intro excludes by design.

Filed rather than done, so the change is weighed against the rest of the queue and gets a file list
before anything is written. Filed 2026-09-04, 12:47, mid-run, after the 2026-09-04 close.

#### Script the LOG index month split, and run it [log-index-month-split]

Writes a small script that moves each ended month's index lines out of `LOG/index.md` into
`LOG/index-YYYY-MM.md`, and runs it. The method's rule is that a month that has ended moves out, leaving
the current month's lines behind, so a planning session's opening read stays short as the archive grows.
Nothing has ever been moved: the file holds every line back to May.

**Merged from two entries in planning on 2026-09-05** — this one, filed at the 2026-09-04 close, and
[log-index-month-rollover-blocked], filed at the 2026-09-05 close after a close tried the move and
stopped. The second is deleted; its findings are here.

**What stopped the close, and why it no longer stops this.** A line's month is read from the entry
filename it ends with, and many entries carry no date in their filename — the numbered `00NN-*.md` batch
records and other pre-convention ones. Counted on 2026-09-05: 117 lines, of which 62 name September
entries, 17 name August, and 37 name undated ones. Moving only the identifiable August lines would leave
the index holding September plus a block of undated older lines with August cut out of the middle, which
is worse than the file that exists, because the index reads newest-first and that ordering would break.

Both routes the blocked entry proposed — dating the undated lines by hand, or abandoning rollover and
letting the file grow — were costed against having to open around thirty-eight entry files to read their
dates. **That premise is false, checked on 2026-09-05:** git records when each entry file was added, so
one command dates every undated line without opening anything. Five undated entries were tested and all
five returned a date. That is what turns this from a bulk hand restructure into a mechanical job, and it
also answers the question the original entry left open — it should be a script, because it is due again
every month.

Accepted rather than solved: a git add-date is when the entry was committed, which can be a day after
the session it records. For sorting into months that is almost always the same answer; an entry within a
day of a month boundary could land in the wrong file. Worth knowing, not worth engineering around.

Files:
- new `scripts/split_log_index.py` — reads `LOG/index.md`, takes each line's entry filename, derives its
  month from a date in that filename where there is one and from `git log --diff-filter=A` where there
  is not, writes each ended month's lines to `LOG/index-YYYY-MM.md` newest-first, and leaves the current
  month's lines in place. Re-runnable: a month already split is left alone.
- `LOG/index.md` — the ended months' lines removed, September's kept.
- new `LOG/index-2026-08.md` and the earlier months' files the script produces.

Reads but does not change: the `LOG/` entry files, by filename only — the script reads git for their
dates rather than their contents.

Observation: `LOG/index.md` holds only lines naming September entries; every line that left it appears
exactly once in a `LOG/index-YYYY-MM.md` file; the total line count across `LOG/index*.md` equals what
`LOG/index.md` held before the run; and running the script a second time changes nothing. The check
reaches all the files named above.

Refused: doing this by hand. It is due again every month, and the next close would rediscover the same
obstacle.

Rests on, each read 2026-09-05: that `LOG/index.md` holds 117 lines with 37 naming undated entries; that
`git log --diff-filter=A` returns an add date for those entries; and that retrieval searches
`LOG/index*.md`, so nothing is lost to the move.

Filed at the 2026-09-04 close, 12:27, by Claude.

#### Record the 2026-09-05 Gradle re-check in TOOLS.md [tools-md-gradle-recheck-2026-09-05]

Adds one dated line to TOOLS.md saying that Gradle's loopback failure was re-tested on 2026-09-05 and
still fails, and that moving the build output out of Google Drive did not fix it.

Raised by /rescan on 2026-09-05 and processed in the same session. The re-check itself was run at the
decision step of [run-instrumentation-tests], to test whether that item was still genuinely `[user]`
work; it is, and the result is recorded in that item's prose. TOOLS.md is where a later session looks
for what this machine can do, so the fact belongs there too.

**Why the negative result is the useful half.** TOOLS.md carries the Google Drive explanation as an
explicit hypothesis rather than a finding — that the project living inside Drive causes the
"Unable to delete directory" file lock, and that moving it out would probably end that problem. A later
session reading that alongside the loopback line could reasonably infer that [project-out-of-drive]
would have cured the shell failure as well. It did not: the two failures are unrelated, and that has now
been tried rather than reasoned about.

Files:
- `TOOLS.md` — one line appended in the file's existing shape: that `gradlew` was run from Claude's
  shell on 2026-09-05 and died with the same `Unable to establish loopback connection`, that
  [project-out-of-drive] having moved Gradle's output to `C:\builds\taskflow` did not change it, and
  that the Drive hypothesis covers the file-lock failure rather than this one. Dated 2026-09-05 like
  every other line.

Observation: TOOLS.md carries a 2026-09-05 line naming the loopback failure and the output move, and the
existing 2026-08-31 loopback line is unchanged. The check reaches the one file named above.

Refused: rewriting or replacing the 2026-08-31 line. It is a finding with its own date and its own
detail of everything that was tried, and today's run confirms it rather than overturning it — TOOLS.md's
own format is one dated line per fact.

Rests on, read 2026-09-05: that `gradlew --no-daemon --no-watch-fs` fails from Claude's shell with
`Unable to establish loopback connection`, run today; and that TOOLS.md currently carries the loopback
finding and the Drive hypothesis as separate lines dated 2026-08-31 and 2026-09-01.

#### Report the queue digest's --next disagreeing with the fixed-median ladder [digest-next-recomputes-medians]

Drafts a method defect report to the No code method project about `queue_digest.py --next` recomputing
the section medians on every call, when plan.md's ladder requires them fixed for the whole pass, and
sends it once the user has approved the exact text.

Found on 2026-09-05 during a planning session, and processed in the same session at /rescan.

**What was seen.** plan.md fixes each section's median line count and median age at the opening and holds
them for the pass, and states the reason: recomputing mid-session lets an entry swell past the median and
re-enter a group already worked through, so the group stops shrinking. `queue_digest.py --next`
recomputes both from the section as it currently stands. As long items left Unprocessed during that
session, the medians fell, and `--next` three times offered an item that the opening's medians had placed
in a lower rung. Passing `--picked` did not change it: the disagreement is about which entries are in the
long-and-old group, not about the alternating rung's parity.

**Why it is worth reporting rather than working around.** The override happened only because the session
was holding the opening's medians and noticed the mismatch. A session that takes the tool's answer gets a
different processing order with nothing indicating it diverged from the documented ladder — the failure
is silent, and the tool is the thing the doc tells sessions to ask.

**What the report should not do: propose the fix as though it were obvious.** The script cannot know the
opening's medians unless it is told them, so the choices are roughly that `--next` takes them as
arguments, or that the digest reports the medians it used so a divergence is visible, or that plan.md
stops promising they are fixed. Which of those is right depends on the plugin's own design and is the No
code method project's call. Say what was seen and let them choose.

Files:
- new `../../No code method/INBOX/2026-09-05-from-taskflow-digest-next-recomputes-medians.md` — the
  report, in the shape the two sent this year use: what happened, the passage it contradicts, what
  seems to be missing, the counterweight, and what Taskflow is not claiming.
- `INBOX/sent.md` — one line at the top recording the send, per the file's newest-first shape.

Reads but does not change: `plan.md` and `queue_digest.py` under the installed plugin root, for the
exact wording of the fixed-median rule and the script's behaviour, so the report quotes rather than
paraphrases.

Observation: the report file exists in the No code method project's INBOX and `INBOX/sent.md` carries a
line naming it. The check reaches both files named above.

**Approval gate, and it is the standing rule rather than anything special to this item:** the exact text
is shown to the user and sent only on their explicit yes, because it leaves this project. The same gate
applied to the report sent earlier on 2026-09-05.

Refused: flavoring this `[user]`. It was first recommended that way and corrected in the same exchange —
Claude writes the file and can do every part of the work; needing the user's approval on outbound text is
the approval rule, not a reason the work is theirs.

Rests on, read 2026-09-05: that plan.md states both medians are computed at the opening and fixed for
that pass; and that `queue_digest.py --next` recomputed them mid-session, observed three times in one
session. The installed plugin is version 1.22.0-test1, so the report names that version.

#### [audit] Verify the rest of the 2026-08-31 run on a device [verify-run-2026-08-31]
Red flag · State: cleared

**Lifted above the readiness line on 2026-09-05.** It waited on [install-current-build-on-device], which
was driven to done on 2026-09-04: Android Studio compiled without deploying, so Claude installed the APK
over adb (with `-t`, the intermediates APK being test-only) and confirmed the install from dumpsys. The
twelve builds of that run are therefore on the phone, which is the condition this audit needed. Note
that the run which closed on 2026-09-05 shipped twelve further builds whose ticks also read UNCONFIRMED
— this item's list covers the 2026-08-31 run only, and the newer ones are not folded in here.

Drives the twelve unrun checks from that run against the app on a real phone, records a verdict for
each, and files a capture for anything that fails. Claude runs it over adb; nothing here is handed to
you.

**Reshaped from a `[user]` item on 2026-09-03**, on Claude's recommendation and your agreement, after a
capability check found what nobody had checked before. TOOLS.md says Claude cannot compile, test or
install this app — true of Gradle, and never true of the device. Read 2026-09-03: `adb` is present in
the Android SDK (not on PATH, which is why it read as absent), a phone is connected over wireless
debugging, and `com.example.taskflow` is installed on it. So Claude can tap, type, screenshot, restart
the app and inspect state. Almost every observable below is a pass/fail state check rather than a
judgement, and those are Claude's to run.

**Refused: leaving it as one `[user]` walkthrough.** Twelve areas of mechanical checking is hours of
someone's evening spent doing what a script can do, and the original item's own note asked whether it
should be split. Refused: splitting it into twelve items — the checks share one app session and one
setup, so twelve items would pay that cost twelve times.

**Out of scope, deliberately: how any of it looks.** Whether a screen reads well, whether an empty
state lands, whether something feels slow — that is [first-end-to-end-test], which already asks you to
note anything slow, confusing, ugly or surprising. This item answers "does it work", not "is it good".

**The risk, and how it is designed out.** Driving your phone means Claude sees your real tasks in
screenshots, and this repository is public — a screenshot committed here would publish whatever was on
screen, permanently. So evidence stays in the session scratchpad, which is outside the repository and
self-clearing; nothing from the device is written into a tracked file, and the database is never copied
off the phone. TEST-LOG.md records verdicts, never task content. Red flag cleared by that design rather
than by accepting the risk.

Nineteen work items shipped in that run and three things were checked on the device: the onboarding
screens' legibility, the blank New-task form, and the drawer's gesture behaviour. Everything else was
written, compiled once, and never exercised.

Each shipped item's LOG entry ends with an UNCONFIRMED tick naming its own check, so the checks
exist — but they exist scattered across nineteen files that nothing reads on a schedule. Without one
queue line collecting them, nothing surfaces them again, and the app accumulates features nobody has
watched work.

Unchecked, with the shortest observable for each:

- Recurring tasks — set one to repeat daily; instances appear across Today, Tomorrow and Soon and
  stop at 30 days; completing today's leaves tomorrow's; a one-off dated six months out still shows
  in Later. Its unit tests have also never been run.
- Subtasks — add two under a task; the parent shows a chevron rather than a checkbox; completing
  both completes the parent; un-completing one brings it back with its children.
- Drag-reorder — drag within a Schedule slot, and within a Later card; the order survives navigation
  and relaunch.
- Drag between screens — a dated task moves and takes the new slot's date; an undated one parks
  still undated.
- The outliner and the drag targets — Enter makes a subtask, Backspace merges one away, bin deletes,
  promote lifts a child out.
- Cut and paste — cut a parent with children, paste it into a notes app, paste it back.
- Settings, day begins at — set it a few minutes ahead and watch a Tomorrow task move with the app
  open.
- Settings, date format — switch to MM/DD and check every surface follows.
- JSON export and import — and this one matters more than its place in the list suggests: it is the
  only thing standing between a schema change and losing everything on the phone, and it has never
  been run once. See [durable-local-data].
- Strategy doc — edit a paragraph, confirm it survives a relaunch, confirm the share sheet opens.
- Empty states — an empty slot, an empty Project card, a card whose Project has only near-term
  tasks, and Later before any Project exists.
- Focus on a Project — enter from a card header, confirm the near-term slots filter and the top bar
  says so, add a task while focused, relaunch and confirm it opens unfocused.

Files:
- `TEST-LOG.md` — one row per check above, carrying the area, what was done, and a pass, fail or
  blocked verdict. Verdicts only; no task content.
- `QUEUE.md`, Unprocessed — one capture per failure, describing what was expected and what happened.
  An audit files findings and changes no product code.

Reads but does not change: the nineteen `LOG/` entries from that run, for each item's UNCONFIRMED tick
and the check it names; and `SPEC.md`, for the behaviour each check is measuring against.

Observation: TEST-LOG.md carries a row for every one of the twelve areas with a verdict, and every
failed row has a matching capture in QUEUE.md's Unprocessed section. The check reaches both files named
above.

Rests on, each read 2026-09-03: that `adb.exe` sits in the Android SDK's `platform-tools`; that a device
answers `adb devices`; and that `com.example.taskflow` is installed on it. A phone can be unplugged, so
re-check all three before starting rather than assuming them.

#### [user] First end-to-end test of Taskflow on a device [first-end-to-end-test]

Filed in planning on 2026-08-25, doing what [post-first-test-polish-review] asked for: that item waits on a real-world event rather than on a build, so the event becomes its own line and the review is held against it.

This is the first time the app is used as an app rather than checked feature by feature. What it produces is a set of notes, and those notes are the input to [post-first-test-polish-review], which decides which of them earn a SPEC entry and which fold into existing work.

Claude can install a build and drive taps over adb, but noticing that something feels wrong is the whole point of this and needs the user's own eyes, which is why it is a `[user]` line rather than a build.

**Run it after the currently cleared builds ship.** Run today and the notes fill with "there is no date picker", which is queued work rather than polish; run it after the cleared run and the notes are about how the thing actually feels. That is an ordering preference, not a blocker — nothing stops it being run earlier if the user wants to.

Walkthrough:
1. Install the current build on your device (ask a session to build and install it if it isn't already there).
2. Use the app for a normal day's worth of tasks — capture what you actually need to do, not test data.
3. Capture something from each of Today, Tomorrow, Soon and Later, so every add path gets used at least once.
4. File at least one task into a Project of your own, and complete at least one task so it reaches the Completed tray on Today.
5. Note anything that felt slow, confusing, ugly or surprising — one line each, no need to diagnose it. Roughness you would normally push past is exactly what to write down.
6. Bring the notes to a planning session and say the test has been done.

#### [user] Create the bug-report email address [bug-report-email-address]

Taskflow's Report-a-bug screen sends people to an email address, and that address does not exist yet.
This item creates it and names it, so the screen has something real to print.

The route was your call on 2026-08-31: a dedicated email address, chosen over a web form on
flintcraft.tech and a GitHub issue, which lost on audience fit and on the cost of standing them up —
most of Taskflow's users are not technical and will not have a GitHub account. That reasoning lives in
[help-thanks-report-content], where this step was buried as a clause until it was lifted out on
2026-09-03.

It is `[user]` work because it needs access to whatever hosts your domain's email, which is an account
of yours Claude has no reach into. The capability check on 2026-09-03 found no tool here that could
create a mailbox or an alias on your behalf.

Two things to decide while doing it, both yours and neither of them requiring a separate item:

- **Which domain.** flintcraft.tech is the obvious home given the bug-report web form was considered
  there, but nothing has decided it.
- **A real mailbox or a forwarding alias.** An alias pointing at an address you already read is less to
  maintain and cannot be forgotten; a separate mailbox keeps bug reports out of your main inbox. Either
  satisfies this item.

Walkthrough:

1. Sign in to whatever manages email for the domain you want to use. Look for: the page listing that
   domain's mailboxes or aliases.
2. Create an address for bug reports — something a stranger can read aloud and retype without error.
   Look for: the new address listed alongside the others.
3. Send a test message to it from an account it does not forward from, and confirm it arrives where you
   expect. Look for: the test message in the inbox you actually read.
4. Tell a planning session the address, so it can be written into [help-thanks-report-content]'s
   Report-a-bug wording.

Observable: nothing in this repository changes until the address is written into the queue, so
completion is not checkable from here — a later session asks rather than checks. Once the address is
recorded in [help-thanks-report-content], that is the evidence.

Held against this: [help-thanks-report-content] does not name it as a blocker, because that capture
waits on the MCP work as well and this address is only one sentence of it. The ordering is written
here instead: this must be done before [0022-help-thanks-report-a-bug-content] ships the screen.

#### [user] Run the instrumentation tests, MigrationTest first [run-instrumentation-tests]

The app's instrumentation tests have never been executed. `MigrationTest` was written on 2026-09-04
and has never run once; `TaskDaoTest` and `ProjectDaoTest` predate it and have no recorded run
either.

It is `[user]` work for the reason TOOLS.md records: Gradle cannot run from Claude's shell on this
machine, so anything that compiles is Android Studio's job and Android Studio is yours. Claude can
drive the device over adb and read results back, but cannot start the run.

**Re-checked on 2026-09-05 at the decision step, rather than taken from the record.** `gradlew` was run
from Claude's shell and failed with the same `Unable to establish loopback connection` recorded on
2026-08-31. Worth re-checking because [project-out-of-drive] has since moved the build output out of
Google Drive, and that could plausibly have fixed it; it did not, because the Drive hypothesis was about
the file-lock failure rather than this one.

**MigrationTest matters more than its size suggests.** [durable-local-data] made version 5 the floor
below which device data may not be destroyed, and that test is what proves the recorded schema in
`app/schemas` is actually usable — it creates a v5 database, writes a task, reopens it, and asserts
the task survived. Until it runs, the floor is a claim rather than a demonstrated fact, and the next
schema change will be written against a schema nobody has exercised.

Walkthrough:

1. Open the Taskflow project in Android Studio and connect your phone. Look for: your device in the
   dropdown at the top of the window.
2. Right-click the `androidTest` source folder in the project tree and choose the option to run all
   tests in it. Look for: a results panel listing each test with a pass or fail.
3. Tell a session the run has finished. Look for: nothing on your side — this step is a handover, and
   Claude goes looking for the results itself.
4. If Claude comes back saying it found no results file, read the panel yourself and say which tests
   passed and which failed. Look for: the failure text itself where anything failed — that is what a
   session needs, rather than a description of it.

Observable: Gradle writes instrumentation results as XML under the build output, which
[project-out-of-drive] moved to `C:\builds\taskflow`, so Claude looks under
`C:\builds\taskflow\app\outputs\` for an `androidTest-results` folder and reads the verdicts and any
failure text out of it. **Written as expected rather than as established**: that folder does not exist
today, because these tests have never run, so nothing has yet proved Android Studio's test run puts
results there. Step 4 is the fallback for exactly that. If the results are not readable, completion is
not checkable from here and a later session asks rather than checks.

Filed 2026-09-04, 22:50, by /rescan at the end of a /next run.

--- Cleared to run above this line ---

#### [user] Apply the cloud migrations and prove a cross-account read is denied [supabase-apply-cloud-migrations]
Blocked by: [supabase-rls-policies]

Runs the two SQL files [supabase-rls-policies] writes against the real Supabase project, then checks
that one account genuinely cannot read another's rows. Until this is done the policies are text in this
repository rather than rules on a database.

Split out of [supabase-rls-policies] in planning on 2026-09-05. Writing the SQL is Claude's work;
applying it is not. The capability check that day found no Supabase CLI on this machine, and the two
routes that exist both need a credential — the CLI needs a project access token, and the dashboard needs
your Google sign-in. Claude is barred from handling either, so the applying and the proving are yours.
Claude can read the SQL back and tell you what each statement is for at any point.

Walkthrough:

1. Open your Supabase dashboard and select the Taskflow project, then open the SQL editor. Look for: an
   empty query pane with a run button.
2. Ask a session to show you `supabase/migrations/0001_initial_schema.sql`, paste it into the pane and
   run it. Look for: a success message, and the four tables — `tasks`, `projects`, `strategy_entries`,
   `life_areas` — listed in the table editor.
3. Confirm each of those four shows Row Level Security as enabled. Look for: an RLS-enabled marker
   against every one of the four, not just some.
4. Paste and run `supabase/migrations/0002_rls_policies.sql` the same way. Look for: a success message,
   and policies listed against each of the four tables.
5. Create two test users in the project's authentication section, and insert one row into `tasks` under
   each. Look for: two rows, with different `user_id` values.
6. Signed in as the first test user, query `tasks`. Look for: exactly that user's own row, and not the
   other's. This is the check the whole item exists for — if both rows come back, the policies are not
   doing their job and a session needs to know before any real data goes near this.
7. Query `tasks` with no session at all. Look for: no rows. This is the null-identity case, which is the
   one that fails silently if a policy is phrased loosely.
8. Delete the two test users and their rows. Look for: an empty `tasks` table.
9. Tell a planning session what steps 6 and 7 returned. If either returned rows it should not have, say
   so exactly — the row count is what a session needs.

Observable: the tables and their policies exist inside your Supabase project, which is not visible from
this repository, so completion is not checkable from here — a later session asks rather than checks.
Stated plainly so nobody builds a check that cannot work.

Rests on, read 2026-09-05: that no Supabase CLI is installed on this machine, so the dashboard SQL editor
is the available route. Installing the CLI would not change who owns the credential.

#### Day-detail card layer, opened from a search result [nav-day-card-layer]
Blocked by: [nav-search-completed-history]

Tapping a search result, a group or a date header opens that single day as a **card in the foreground**,
moving on its own left-right axis rather than along the row of pages. It is the only place a completed
task can be edited or un-completed.

Split out of [nav-completed-history] on 2026-09-03. Held because the card is opened from the Search
page, which does not exist until [nav-search-completed-history] ships.

**Three interaction details settled with you on 2026-09-03**, the ones the August design deliberately
left until there was a real screen:

- **Back closes the card** and returns the user to the page they opened it from — the same "up one
  level" meaning the edit dialogue and the menu overlay already have. This is why
  [left-edge-swipe-collision] names the card layer among the things back closes.
- **Days with nothing completed are skipped**, landing on the previous day that has something. Shown
  empty, a dated card with nothing on it reads as a reproach, which UX principle 4 refuses.
- **The far end bounces.** Swiping right past the oldest day holding a completed task does nothing —
  no special case and no message, the same as the end of the row of pages.

Files:
- new `app/src/main/java/com/example/taskflow/ui/history/DayCardLayer.kt` and its ViewModel — the
  foreground card, its own horizontal pager over days that hold completed tasks, and the tap target
  that opens a task's edit dialogue. Swipe right brings the older day in from the left; swipe left
  brings the newer day in from the right; swiping left past day-before-yesterday carries the card layer
  and the Search page off together, landing on Yesterday.
- `app/src/main/java/com/example/taskflow/ui/navigation/AppRoot.kt` — hosts the layer above the pager
  and extends the existing `BackHandler` so an open card is closed by back before the spine rule
  applies.
- `app/src/main/java/com/example/taskflow/ui/history/SearchScreen.kt` — results, groups and date headers
  become tappable, opening the layer.

Observation: on a device, tapping a search result opens that day as a card over the page; swiping right
moves to the previous day that has completions, skipping any with none; swiping right at the oldest
such day does nothing; swiping left past day-before-yesterday returns to Yesterday with the card and
search page leaving together; back closes the card and returns to Search; and a task can be edited and
un-completed from the card. The check reaches the three files named above.

Refused: editing completed tasks from the search results list — SPEC keeps results read-only so a
tappable list of completed tasks does not become a second place to change things.

Rests on, read 2026-09-03: that `AppRoot.kt` owns the pager state and the `BackHandler`, and gates that
handler on `editTarget` and `overlay`.

#### Share a day, as PNG or Markdown [share-a-day]
Blocked by: [nav-day-card-layer]

A share button on a day screen shares that day's completed tasks, offered in two formats: **PNG** and
**Markdown**, through Android's standard share sheet.

Split out of [nav-completed-history] on 2026-09-03. Held because there is no day screen to put the
button on until [nav-day-card-layer] ships. It is the heaviest of the four despite being the smallest
feature, because the PNG half is not a text share.

Both formats were settled on 2026-08-21 per
`workshop/resources/research/android-share-format-png-vs-pdf.md`: PNG because it renders inline in a
chat thread rather than arriving as an attachment to open, and Markdown carried under the `text/plain`
MIME type, since almost no Android app declares `text/markdown` and using it would produce a
near-empty share sheet. PDF and plain-text-only were the alternatives and lost on that
arrival-behaviour reading.

Files:
- new `app/src/main/java/com/example/taskflow/ui/history/DayShare.kt` — builds both payloads: the
  Markdown text, and a PNG rendered from the day's content, written to a cache file and handed over as
  a content URI.
- `app/src/main/AndroidManifest.xml` — a `FileProvider` declaration, required because a PNG is shared
  as a file rather than as text.
- new `app/src/main/res/xml/file_paths.xml` — the cache path that provider exposes.
- `app/src/main/java/com/example/taskflow/ui/history/DayCardLayer.kt` — the share button and the choice
  between the two formats.

Reads but does not change: `app/src/main/java/com/example/taskflow/ui/strategy/StrategyScreen.kt`,
which already shares text via `Intent.ACTION_SEND` and `Intent.createChooser` — the pattern to follow
for the Markdown half, and the sibling SPEC §Strategy doc names as the existing share.

Observation: on a device, the share button on a day card offers both formats; choosing Markdown opens
the share sheet with that day's completed tasks as text; choosing PNG opens it with an image that
renders inline in a messaging app rather than arriving as a file to open. The check reaches the four
files named above.

Rests on, each read 2026-09-03: that `StrategyScreen.kt` shares text through `ACTION_SEND` and
`createChooser`; that no `FileProvider` is declared in the manifest today; and the format finding
above, filed 2026-08-21.

#### Subtasks in the edit dialogue have no affordance, so nobody finds them [subtask-affordance-in-edit-dialogue]
Blocked by: [verify-run-2026-08-31]

Adds a visible hint that pressing Enter in the edit dialogue's first line creates a subtask beneath it.

**Held at the 2026-09-04 close, by the rule against building on unverified work.** This item's whole
content is a hint advertising that Enter adds a subtask — behaviour that shipped in
[0010-outliner-typing-drag-target-icons] and has never been run on a device. If it does not work, the
hint advertises a feature that is not there, which is worse than the silence it replaces. So it waits
for the audit that checks it. Nothing else about the item is unready.

captured by you, 2026-09-02: subtasks shipped in the 2026-08-31 run and are, as you said, not visibly
there. Confirmed on 2026-09-03 — the first line is labelled "Task" and pressing Enter at the end of it
opens an indented subtask line, but nothing on screen says so. An earlier draft of that field carried
the hint "Press Enter to add a subtask" and it was dropped when the single text box became a
line-per-field outliner, which is how the only affordance disappeared. A feature that is built and
undiscoverable is close to not being built.

**Build it after [notes-out-of-edit-dialogue]** — both change the same file, and removing the Notes box
is what leaves the outliner as the only text area on the screen, which is half of what makes the
subtask lines legible. An ordering preference written here rather than a blocker, so the item stays
visible.

**What the two items each do, since they were one capture and the division matters.** Removing Notes
makes an existing subtask line unmistakable, because nothing else on the screen looks like it. It does
nothing at all for a task that has no subtasks yet, where there is nothing to look at — and that is the
case this item covers.

Files:
- `app/src/main/java/com/example/taskflow/ui/edit/EditTaskScreen.kt` — a hint on the outliner field,
  shown while the task has no subtasks and hidden once it has one, saying that Enter adds a subtask.

Observation: on a device, opening a task with no subtasks shows the hint; pressing Enter at the end of
the title creates an indented child line and the hint disappears; opening a task that already has
subtasks shows no hint. The check reaches the one file named above.

Refused: leaving it to be discovered. It shipped invisible, and the person who wrote it is the only one
who has ever found it.

Rests on, read 2026-09-03: that `EditTaskScreen.kt` renders the outliner with its first line labelled
"Task" and no hint text; and that Enter at the end of that line already creates a subtask, which is
[0010-outliner-typing-drag-target-icons]'s shipped behaviour and is itself listed as unverified in
[verify-run-2026-08-31].

#### [user] Create the Google Play subscription product for the paid tier [play-console-subscription-product]
Blocked by: [business-registration-for-play-account]

Taskflow's paid tier is sold as a Google Play subscription with a 30-day trial, and none of that can be
built or tested until the product exists in the Play Console. This is the step that creates it.

It is `[user]` work throughout: it registers a developer account under a legal identity and pays a $25
fee, and Claude is barred from creating accounts or entering payment details in any case. The
capability check on 2026-09-03 found no tool here that could do any part of it.

**The account is an organization account, settled by you on 2026-09-03.** You raised the underlying
question — whether the paid tier depends on your business being set up — and the answer is that it does
not: both account types can sell subscriptions on Google Play, so a registered business is not
required. What decided it was what the account publishes. The payments profile's legal name and address
appear on customer receipts, and a personal account's are the individual's own name and home address,
sent to every subscriber. **Refused: a personal account**, on exactly that ground; the cheaper and
faster route, and it publishes your home address to strangers. The account type cannot be changed by
editing a setting afterwards, which is why this was decided rather than defaulted.

**The profile that would carry those receipts is already an organisation profile.** Seen on 2026-09-04
on the Google Workspace checkout screen while driving [bug-report-email-address]: the payments profile
the purchase went through reads Flintcraft, Organisation, Australia. A Google payments profile is shared
across Google services — Workspace, Play, Ads — so it is the same object this account would bill
through, and the billing half of the identity question is therefore already answered correctly. Folded
in from a standalone capture on 2026-09-05, which was deleted in the same move: it changed no files and
was a fact rather than work, and it belongs where the reasoning that uses it lives. **What it does not
establish, stated so it is not read as more than it is:** none of the registration, verification, $25
fee, D-U-N-S number, incorporation documents, address proof or authorised representative below is done
by having an organisation payments profile. The profile ID itself is deliberately not recorded — this
repository is public, and the ID is an account identifier of no use to anyone reading these documents.

The consequence is that this item now waits on the business registration rather than on a choice. Held
against [business-registration-for-play-account], which stands for the Taskflow-side dependency; the
registration work itself belongs to the flintcraft.tech project and is not duplicated here. Note the
**D-U-N-S number can take up to 30 days** and is free — it is the long pole, and requesting it early is
what stops this step waiting on paperwork once everything else is ready.

Walkthrough:

1. Request a D-U-N-S number for the business, using Dun & Bradstreet's free route for Google developer
   accounts, unless you already hold one. Look for: a confirmation that the request is lodged, and
   later the nine-digit number itself. Expect up to 30 days.
2. Gather the organization documents Google asks for: government-issued business registration or
   incorporation papers, proof of the business's physical address (a registered agent's address is not
   accepted), and the identity document of the authorised representative, who must be named on the
   registration. Look for: all three to hand before starting signup.
3. Register the Play Console developer account as an **organization**, paying the one-off $25 fee, with
   2-Step Verification on the Google account. Look for: the Play Console dashboard opening on your
   account.
4. Complete verification, supplying the D-U-N-S number, the organization phone number and the
   organization website. Look for: the account status showing verification complete rather than
   pending.
5. Set up the payments profile with tax information and a bank account. Look for: the profile showing a
   verified payment method.
6. Create the Taskflow app entry and, under it, a subscription product with a 30-day free trial. Look
   for: the subscription listed as active in the Play Console.
7. Tell a planning session the product exists, and give its product ID. That is what
   [0017-tier-model-and-subscription-handling] needs in order to be built.

Observable: nothing in this repository changes, so completion is not checkable from here — the
subscription's existence lives in your Play Console. The item waits until you mention it. Stated
plainly rather than left implied, so a later session asks rather than pretending to check.

Rests on, read 2026-09-03 and recorded in
`workshop/resources/research/google-play-organization-account-requirements.md`: that both account types
can monetize; that an organization account requires a D-U-N-S number, incorporation documents, physical
address proof, an authorised representative, a public phone number and a website; that the D-U-N-S
number is free and can take up to 30 days; and that the payments profile's legal name and address appear
on customer receipts. Registration requirements are amended on a cycle, so re-read before driving this.

#### Cloud sync, paid tier [0018-cloud-sync-paid-tier]
Blocked by: [supabase-rls-policies], [supabase-apply-cloud-migrations]

Held below the line on 2026-09-03, with your agreement: the backend is now named, but the Supabase project it syncs to does not exist yet and creating it is your step.

**Re-held on 2026-09-05, against different work.** [supabase-project-setup] shipped on 2026-09-04, so the original hold was spent and its slug no longer resolved. What replaces it is the cloud schema and its Row Level Security policies, plus applying them: this item writes sync against tables that do not exist, and the shape of the policy protecting those tables is the whole security model. Building sync first would mean designing the security around code already written, which is the reasoning [supabase-rls-policies] was widened on. It also inherits the key decision made there — cloud rows carry a UUID primary key rather than the local autoincrementing integer, because two devices on one account both generate id 1, and mapping a local id to that UUID is this item's work.

Push-pull sync between the device Room DB and a cloud backend. Full original spec: `archive/backlog-specs/0018-cloud-sync-paid-tier.md`.

**The cloud backend is Supabase, settled by you on 2026-09-03** — Postgres plus Supabase Auth, on the free plan while this is built and Pro ($25/month) once anyone is paying. It was chosen over Cloudflare Workers because it answers this item and [0020-remote-mcp-server] with one product: the same project that holds the synced data also issues the OAuth credentials the MCP server authenticates against, so user accounts are configured rather than written. Reasoning and prices in `workshop/resources/research/paid-tier-backend-and-mcp-hosting.md`. Note the free plan pauses a project after a week of inactivity, which is why Pro is named as the launch condition rather than an upgrade to consider later.

--- Build block ---
Changes: push-pull sync between the device's Room database and the cloud backend, with conflict handling for cross-device edits, since a paid user may have several devices. **Resume after a paused subscription is the same problem with a longer gap**, folded in from [subscription-pause-resume-merge] in planning on 2026-08-25, which was deleted at that point: while paused, Taskflow is local-only on every device, so two devices diverge with no cloud arbiter between them. On resume the cloud takes the **union** of tasks from every device rather than letting one device's state win; where the same task was edited on two devices the most recent edit wins field by field; and a task deleted on one device during the pause is **not** deleted on resume if another device still has it. The reasoning: a deletion made while offline is a weak signal, and Taskflow's instinct everywhere else is that nothing is lost to a delete — deleting a Project reassigns its tasks rather than destroying them. The accepted cost is that a task deleted on one device may reappear, which is one gesture to fix, where a task destroyed by a merge is gone. This precedes [0020-remote-mcp-server], which reads the cloud-side store rather than a device. Full original spec: `archive/backlog-specs/0018-cloud-sync-paid-tier.md`.
Acceptance: with two devices on one account — a task created on one appears on the other; the same task edited on both converges to a single state with nothing silently lost. After a pause during which both devices were edited separately, resuming leaves every task from both devices present, a task edited on both carries the later edit, and a task deleted on one but still held on the other is still there.
Refused: letting the last device to sync win on resume — it would silently discard everything done on the other device during a pause that may have run for months.
--- End build block ---

#### Remote MCP server [0020-remote-mcp-server]
Blocked by: [supabase-project-setup], [0018-cloud-sync-paid-tier]

Held below the line on 2026-09-03, with your agreement: the server runs as a Supabase Edge Function against the same project cloud sync writes to, so both the project and the synced data have to exist before this can be built.

Hosted MCP server with tool surface, authentication, and system-prompt delivery. Full original spec: `archive/backlog-specs/0020-remote-mcp-server.md`. [project-lifecycle-paid] is held against this one.

**The registration mechanism was revisited on 2026-09-03 and has a known expiry.** This item's auth design, settled 2026-08-25, names OAuth 2.1 with **dynamic client registration**. That is no longer the preferred shape: Client ID Metadata Documents were made the preferred default for MCP client registration by the MCP specification of 2025-11-25, with dynamic registration demoted to a fallback, and Cloudflare's documentation describes dynamic registration as slated for removal after summer 2027. The OAuth decision itself is untouched — the reasoning that ruled out URL-borne and pasted static keys still holds.

Dynamic registration stays the mechanism because it is what the platform actually offers: Supabase's OAuth server supports it as a setting, and its CIMD support was in implementation rather than shipped when this was checked on 2026-09-03. **Before writing the auth, the build checks whether Supabase advertises CIMD support and prefers it if so** — that is a lookup against the authorization server's own metadata, not a design decision left open.

**What accepting dynamic registration accepts.** It means an open registration endpoint: any MCP client can register itself with the project, which is the attack surface CIMD exists to remove. It grants nobody the data — a user still signs in and consents — but on the one part of Taskflow facing the open internet it is a deliberate acceptance rather than a default, and it is why the approval and monitoring controls Supabase recommends are part of this item's work rather than a later hardening pass.

**Where it runs was settled by you on 2026-09-03: Supabase**, the same project [0018-cloud-sync-paid-tier] syncs to. The server itself is a Supabase Edge Function; the authorization server is Supabase's own OAuth 2.1 Server, which is documented for authenticating a project's existing users to an MCP server the project owner builds, publishing `.well-known/oauth-authorization-server` for Claude to discover. That is what turns this item's auth design from a server to write into a server to configure. Cloudflare Workers was the alternative and lost: `workers-oauth-provider` is more purpose-built for the MCP half, but the sync store and Taskflow's own user accounts would then both have to be assembled separately. Reasoning and prices in `workshop/resources/research/paid-tier-backend-and-mcp-hosting.md`. Carry into the build the caution Supabase's own docs give: self-registration lets any MCP client register with the project, so approval and monitoring are part of the work rather than an afterthought.

**The auth model was settled in planning on 2026-08-25** and folded in here from the capture [mcp-server-auth-model], which was deleted at that point — this item builds the server, and how the server knows whose data it is serving is the same piece of work rather than a separate one. It carried an uncleared red flag: this is the one part of Taskflow that faces the open internet, and the data behind it — the Strategy doc and life-area profile — describes the shape of someone's life. The flag is cleared by the design below rather than by accepting the risk.

Why OAuth and not a shared key, per `workshop/resources/research/claude-custom-connector-auth-options.md`: a per-user secret in the connector URL is ruled out by Anthropic's own guidance, since a URL carrying a token leaks through server logs, proxy logs, browser history, analytics and screenshots. A pasted static key has no consumer-facing field to paste into — the add-connector flow asks only for a URL — and the `static_headers` type is framed for a fixed organisation-level credential rather than a per-person one. Dynamic client registration needs no registration with Anthropic, so it is the route that works out of the box. The cost was named to the user and accepted: this is a real authorisation server to build, more work than a shared secret would have been.

--- Build block ---
Changes: the MCP server itself — hosted, reachable on the public internet, serving one specific user's cloud-synced data. Tool surface: read tasks, create tasks, set and clear a date, refile to a Project, get and update the user's life-area profile, read and edit Strategy doc descriptions, and mark complete. Authentication is OAuth 2.1 with dynamic client registration, so the user pastes only the server URL: the server publishes its authorization-server metadata for Claude to discover, hosts the sign-in and consent page where the user logs in to their own Taskflow account and sees what Claude is asking to reach, and issues an access credential scoped to that one account, read from the request rather than from the URL. Every tool call resolves the account from that credential and touches no other account's data. A request carrying no credential, an expired one, or one that does not match the data being asked for is refused with an authorization error rather than served or guessed at. Access is revocable server-side without the user redoing connector setup, and revoking is what happens when paid access ends, per [0017-tier-model-and-subscription-handling]. Where identity lives in the cloud store is fixed by [0018-cloud-sync-paid-tier], which this authenticates against. The server-instructions field serves `SYSTEM-PROMPT.md` as the connection-time system prompt to Claude. Serves SYSTEM-PROMPT.md. Full original spec: `archive/backlog-specs/0020-remote-mcp-server.md`.
Acceptance: connect Claude to the server as a custom connector by URL alone — the sign-in and consent page appears, and after approval each tool runs against that account's data and no other's; a call with a missing, expired or mismatched credential is refused; revoking access server-side stops the tools working without touching the connector; and the connection delivers `SYSTEM-PROMPT.md` as the system prompt.
Red flag: cleared
Rests on, checked 2026-09-03: that dynamic client registration is a fallback rather than the preferred mechanism under the MCP specification of 2025-11-25, and is documented as slated for removal after summer 2027; and that Supabase's OAuth server offers dynamic registration but not yet CIMD. Both are amended on a cycle — re-read before writing the auth.
Refused: a per-user secret pasted into the connector URL or a header — the URL form leaks through logs, history and screenshots on Anthropic's own guidance, and there is no consumer-facing field for pasting a key at all.
--- End build block ---

#### AI-choice flow and MCP setup [0019-ai-choice-flow-and-mcp-setup]
Blocked by: [0020-remote-mcp-server]

Held below the line on 2026-09-03, with your agreement. The screens themselves are buildable today, but this item's acceptance is that its verification screen reports the connector reachable, and there is no connector to reach until the MCP server exists. Splitting the screens out from the verification was considered and left alone: a setup path that cannot be checked end to end is what makes the verification screen worth having.

Claude setup path with connector deep-link and verification. Full original spec: `archive/backlog-specs/0019-ai-choice-flow-and-mcp-setup.md`.

--- Build block ---
Changes: the "How do I set up Claude?" path — an explanation screen, a deep link into Anthropic's add-custom-connector modal, and instructions written to work whether or not that URL accepts pre-filled values; plus an in-app verification screen confirming the connector is reachable. Full original spec: `archive/backlog-specs/0019-ai-choice-flow-and-mcp-setup.md`.
Acceptance: on a device — follow the path end to end and the verification screen reports the connector reachable; remove the connector and it reports it unreachable rather than passing silently.
--- End build block ---

#### Strategy doc reconciliation, paid tier [0021-strategy-doc-reconciliation-paid-tier]
Blocked by: [0020-remote-mcp-server]

Held below the line on 2026-09-03, with your agreement: every part of this runs through Claude reaching Taskflow, which is what the MCP server builds.

Initial and ongoing Strategy doc reconciliation via Claude. Full original spec: `archive/backlog-specs/0021-strategy-doc-reconciliation-paid-tier.md`. [project-lifecycle-paid] is held against this one.

--- Build block ---
Changes: initial reconciliation on the first AI-mode open of the Strategy doc area — Claude reads the existing content, identifies tasks that contradict it or are missing from it, presents them in groups and asks, never silently editing. Ongoing reconciliation on every submitted Strategy doc edit — Claude diffs it and surfaces the downstream task impact, with a new edit superseding any prior pass's unanswered suggestions per [sysprompt-reconciliation-supersession]. Plus the conflict-resolution UX. Serves SYSTEM-PROMPT.md. Full original spec: `archive/backlog-specs/0021-strategy-doc-reconciliation-paid-tier.md`.
Acceptance: with Claude connected — a first open produces grouped suggestions and makes no edit until answered; a later Strategy edit surfaces the tasks it affects and folds in any suggestions still outstanding.
--- End build block ---

#### Tier model and subscription handling [0017-tier-model-and-subscription-handling]
Blocked by: [play-console-subscription-product], [0020-remote-mcp-server]

Held below the line on 2026-09-03, with your agreement, having been cleared to run since 2026-08-19 without being buildable. Splitting the local half out was considered and refused: "free disables cloud sync and Claude, paid enables them" builds a flag with nothing to gate while neither cloud sync nor the MCP server exists, and the item's own acceptance is that starting a trial unlocks the paid surfaces on a test account, which needs a Play subscription product that does not exist. So it waits on both — the product, and something for the tier to switch on.

Google Play subscription, trial, local tier enforcement. Full original spec: `archive/backlog-specs/0017-tier-model-and-subscription-handling.md`. [subscription-pause-play-billing] is held against this one.

--- Build block ---
Changes: Google Play subscription wiring for the paid tier, a 30-day paid-tier trial handled through Play, subscription-pause behaviour, and local enforcement — the free tier disables cloud sync and MCP, the paid tier enables both. [subscription-pause-play-billing] confirms what Play actually exposes for pause at the moment this builds. Full original spec: `archive/backlog-specs/0017-tier-model-and-subscription-handling.md`.
Acceptance: on a test account — starting the trial unlocks the paid surfaces; cancelling or letting it lapse returns the app to free behaviour with cloud sync and MCP disabled.
--- End build block ---

#### Help, Thanks and Report-a-bug screen content [0022-help-thanks-report-a-bug-content]
Blocked by: [help-thanks-report-content]

Held below the line on 2026-09-03, with your agreement, for the reason the item's own text already gave: the words for all three screens come from [help-thanks-report-content], which is still an unprocessed capture. Nothing else about it is unready.

Bottom-of-drawer screen content including help and custom instructions. Full original spec: `archive/backlog-specs/0022-help-thanks-report-a-bug-content.md`. The words that go in these screens are still being worked out — see [help-thanks-report-content].

--- Build block ---
Changes: fill the three remaining bottom-of-drawer screens — Help, Thanks and Report a bug — with real content. Help covers MCP setup, the production version of the suggested custom-instruction text, and the "tasks dated before today stay on Today" behaviour described without naming the category. The words themselves come from [help-thanks-report-content], which waits on [custom-instruction-production-text] and the MCP setup design. Full original spec: `archive/backlog-specs/0022-help-thanks-report-a-bug-content.md`.
Acceptance: on a device — each of the three screens opens with real content rather than a placeholder, and Help covers all three topics.
--- End build block ---

#### [user] Verify the Schedule date-matrix rendering on a device [verify-schedule-date-matrix]
Blocked by: [0006-side-scrolling-date-picker]

Confirms that dated tasks render with their DD/MM label in the right slot, for the cases the FAB can't seed yet: a 2–7-day date in Soon, an 8+-day date in Later, and a past date staying on Today (DD/MM, no overdue label). The slot maths is already unit-tested from 0002, and the non-Today DD/MM render was exercised by the Tomorrow check in [device-verify-core-screens]; what's untested is the render across the other slots. This also serves as the regression check for [tomorrow-no-date-label] — the same Soon/Later DD/MM and past-Today verification confirms the Tomorrow-no-label change didn't break the other slots' labels (the Tomorrow-shows-no-label half was confirmed on-device 2026-06-25). Held because setting those dates needs a date picker, which is what [0006-side-scrolling-date-picker] builds.

Walkthrough, once the date picker ships:
1. Create three tasks and open each one's edit dialogue.
2. Give the first a date 2–7 days from today; give the second a date 8 or more days out; give the third a date in the past.
3. Swipe to **Soon** — the first task should be there, showing its date as DD/MM.
4. Swipe to **Later** — the second task should be there, showing DD/MM.
5. Swipe to **Today** — the past-dated task should be sitting there, showing DD/MM and *no* overdue marking.

#### [user] Verify a far-future dated task under a user Project card [verify-far-future-project-card]
Blocked by: [0006-side-scrolling-date-picker]

Confirms a user Project's card on Later holds its far-future (8+ day) dated tasks with the DD/MM label. Held for the same reason as the item above: an 8+-day date can't be set without date-editing, and the FAB on Later only creates undated tasks. Came out of [later-by-project-screen]; the multi-user-Project ordering half and the move-between-Projects check rolled into [project-create-picker-ui] during planning on 2026-06-22.

Walkthrough, once the date picker ships:
1. On **Later**, make sure you have a Project of your own (not just Unassigned).
2. Create a task in that Project and open its edit dialogue.
3. Set a date 8 or more days from today, and save.
4. Go back to **Later** and open that Project's card. The task should be inside it, showing its date as DD/MM.

#### Confirm what Play Billing exposes for subscription pause [subscription-pause-play-billing]
Blocked by: [0017-tier-model-and-subscription-handling]

Verify what Google Play Billing actually exposes for subscription pause — it's largely Play-Store-controlled rather than app-controlled, so the app's options may be narrower than they look. Deliberately not checked now: this is staleness-prone information, so it's worth confirming at the moment the subscription handling is actually built rather than carrying an answer that may have changed by then.

#### Free-tier delete a Project on Later [project-delete-later]
Blocked by: [task-reorder-within-list]

Free tier — long-press a Later Project card and drag it to a delete target in the upper-right, the same gesture used to delete a task. Deleting a Project does not delete its tasks: they reassign to the system Unassigned Project (reassign logic shipped in [unassigned-project-model]). SPEC §Create or delete a Project describes this. Carved from the retired [project-lifecycle-later] during planning on 2026-06-22 — its create half was promoted to [project-create-picker-ui], its three remaining pieces split out by dependency.

What actually holds it is the general drag + bin/delete drag-target gesture (SPEC §Drag-target icons), which doesn't exist anywhere in the app yet (confirmed 2026-06-22). Any of several items could deliver it — [task-reorder-within-list] builds the first drag primitive, and [0008-drag-task-between-schedule-screens], [0010-outliner-typing-drag-target-icons] or [0011-cut-and-paste-os-clipboard] would each bring the bin target. It is held against [task-reorder-within-list] because that is the one sitting nearest the top; if the bin gesture arrives by another route first, this lifts then instead. Build order doesn't matter — only that the bin gesture exists.

#### Verify Project-deletion data behaviour end-to-end [verify-project-delete-data]
Blocked by: [project-delete-later]

[unassigned-project-model] built the repository logic: deleting a real Project reassigns its tasks to the system Unassigned Project (`TaskDao.reassignTasksToProject`, called by `ProjectRepository.delete`), and the Unassigned Project is undeletable (`ProjectRepository.delete` no-ops when `isSystem`). The reassign query was verified live on-device that session; the two guards were verified by code inspection. There is no automated test of `ProjectRepository.delete` itself and no delete gesture yet to exercise it end-to-end. When [project-delete-later] builds the long-press-drag delete, its test pass should confirm: deleting a real Project moves its tasks onto Unassigned with none orphaned, and the Unassigned card cannot be deleted. Rides that item's test pass rather than needing a run of its own.

#### Free-tier reorder Projects in the Strategy-doc editor [project-reorder-strategy]
Blocked by: [0015-strategy-doc-and-life-area-context]

Free tier — drag Project headings in the Strategy-doc editor to set Project order; the Strategy doc owns Project order app-wide, including the Later card order. SPEC §Strategy doc describes this. Carved from the retired [project-lifecycle-later] during planning on 2026-06-22. Held because the Strategy-doc editor must exist before headings can be dragged in it.

#### Paid-tier Project reorder and delete via Claude, plus a SYSTEM-PROMPT.md edit [project-lifecycle-paid]
Blocked by: [0020-remote-mcp-server], [0021-strategy-doc-reconciliation-paid-tier]

Paid tier — reordering and deleting a Project go through discussion with Claude, not a direct gesture; long-pressing a Later card shows the toast "Discuss high-level strategy with Claude," and Claude applies the change and reflects it into the Strategy doc and Later. Why the tier split: reordering and deleting a Project are decisions about the shape of the user's life, so on paid they earn a check-in with Claude rather than a quick gesture; free has no Claude, so it gets direct manual gestures. That was Alex's call, 2026-06-22. Needs a SYSTEM-PROMPT.md edit — how Claude handles a Project reorder/delete discussion and reflects it back — which the spec-edit work left untouched because SYSTEM-PROMPT.md was locked there. SPEC §Create or delete a Project and §Strategy doc describe the paid behaviour. Carved from the retired [project-lifecycle-later] during planning on 2026-06-22. Held until both the MCP server and reconciliation are in place.

#### [user] Live-test the proactive-use custom-instruction text [custom-instruction-production-text]
Blocked by: [0019-ai-choice-flow-and-mcp-setup], [0020-remote-mcp-server]

The suggested proactive-use custom-instruction text is the wording a user would paste into their own Claude preferences so Claude reaches for Taskflow without being asked each time. It cannot be published untested, and the only test that means anything is living with it: whether Claude offers Taskflow when it should, and whether it offers when it wasn't wanted. That judgment is the user's and nobody else's, which is what makes this a `[user]` line rather than a build.

Split in planning on 2026-08-25, doing what the previous session's own note asked for. The drafting half went to [help-thanks-report-content], which already owns the words for the Help screen and has to cover this text as one of its three topics — writing it there keeps one item owning the Help wording rather than two. What stays here is only the test.

Held because the test needs Claude actually connected to Taskflow, which is what [0019-ai-choice-flow-and-mcp-setup] and [0020-remote-mcp-server] build. The result feeds back into [help-thanks-report-content] for publishing, and into `SYSTEM-PROMPT.md` if the testing shows the server-side prompt is the better home for any of it.

Walkthrough, once Claude is connected to Taskflow and [help-thanks-report-content] has produced a candidate text:
1. In your Claude client, open your account settings and find the personal-preferences box — the one whose text is applied to every conversation.
2. Paste the candidate text in and save it. The box should show the text when you reopen the settings; if it doesn't, it didn't save.
3. Use Claude normally for about two weeks. Don't go looking for Taskflow moments — the point is what happens unprompted.
4. Note each time Claude reached for Taskflow: was it a moment you wanted it, or an interruption?
5. Note any moment you expected Claude to put something into Taskflow and it didn't.
6. Say which wording changes follow from those notes — that is what gets published.

#### [user] Produce the onboarding video [onboarding-video-content]
Blocked by: [onboarding-video-script], [0019-ai-choice-flow-and-mcp-setup], [0020-remote-mcp-server], [0021-strategy-doc-reconciliation-paid-tier]

The multi-page onboarding video must demonstrate Claude/MCP value concretely, since it's what the free-vs-paid choice turns on. Sequencing note: [0016-onboarding-flow] is queued ahead of the MCP work, so it may need to ship with a placeholder video and have the real one added once the blockers above land.

Split in planning on 2026-08-25. The design half — what each page shows and claims — went to [onboarding-video-script], which can be written now from SPEC and `SYSTEM-PROMPT.md`. This item is the production job, and it is held because the Claude side has to exist before it can be filmed.

**Not all of this is user work, and the walkthrough says so.** The capability check on 2026-08-25 found that screen recordings can be captured with `adb screenrecord` against a connected device or emulator and assembled with `ffmpeg`, both of which Claude can run. What is irreducibly yours is driving a real paid-tier Claude conversation to film, and judging whether the finished thing actually sells the paid tier. Expect the Claude-doable portion to be split off into its own build item once the script exists and the shot list is known.

Walkthrough, once the script and the Claude integration exist:
1. Open `ONBOARDING-VIDEO-SCRIPT.md` and read the page list. It marks which pages need a live Claude conversation and which are plain screen capture.
2. For the plain-capture pages, ask Claude in a session to record them — it can drive `adb screenrecord` against a device or emulator. You should get one video file per shot.
3. For the live-Claude pages, run the conversation yourself with Taskflow connected, recording the screen as you go. Watch that no real personal data from your own Strategy doc appears on screen — use a test account's data.
4. Hand the clips back to a session to assemble with `ffmpeg` in script order.
5. Watch the result once through as a first-time user would. The question to answer is whether someone who has never used Claude would understand what they are being offered.
6. Say whether it ships or what needs re-shooting.

#### [audit] Post-first-test polish review [post-first-test-polish-review]
Blocked by: [first-end-to-end-test]

After the first end-to-end test, walk the test notes and decide which polish issues warrant their own SPEC.md entry and which fold into existing ones — polish that doesn't trace to SPEC.md is a capture, not a build item.

It waits on a real-world event rather than on a build, so in planning on 2026-08-25 that event was filed as its own line, [first-end-to-end-test], and this item held against it — which is what the previous session's note asked for. It now surfaces on its own once the test has been done, instead of being offered every session in the meantime.

Flavored `[audit]` because it reads and reports rather than editing: it takes the test notes as input and files what it finds as captures, which a later planning run turns into work. Its own output is that set of captures, plus a statement of which notes traced to SPEC and which did not.

## Unprocessed

> Captured ideas and tasks not yet fully processed. The next /plan session goes
> through these with you and decides each one's fate — keep it (move it up to
> Processed) or drop it. Each is filed as its own `#### ` heading, so the list shows
> up in an editor's outline.

#### Last session advises processing [supabase-apply-cloud-migrations] next [forward-advisory]

Advice from the /plan session that closed on 2026-09-05, for whoever opens the next planning session.
Read it, act on it or don't, and clear it — it is orientation rather than work.

**Read the queue's shape first.** Unprocessed holds only three entries and none of them can be taken up:
[help-thanks-report-content] is held against open MCP work, and [personal-strategy-preview] and
[business-registration-for-play-account] carry dates ahead of today. So a planning session opening on
this queue has nothing to process, and that is the intended state rather than a fault — ten items are
cleared to run and the work now is building them.

**Why [supabase-apply-cloud-migrations] is the one to look at.** It is held against
[supabase-rls-policies], which is cleared, so it becomes live the moment that build ships — and it is the
step that turns the Row Level Security policies from text in this repository into rules on a database.
Nothing about the cloud tables is actually protected until its cross-account read test has been run by a
person. If the RLS build has shipped by the time you read this, that walkthrough is the first thing worth
driving.

**What lifts when the cleared work ships.** [subtask-affordance-in-edit-dialogue] waits on
[verify-run-2026-08-31], which is now cleared. [0018-cloud-sync-paid-tier] waits on both the RLS build
and the applying step. Several older held items — [verify-schedule-date-matrix],
[verify-far-future-project-card], [project-delete-later], [project-reorder-strategy] — name blockers that
shipped in the 2026-08-31 run but have never been verified on a device, which is exactly what
[verify-run-2026-08-31] does; expect a batch of them to become liftable once that audit has run.

**Condition on the build route.** The cleared region opens with four build items and the device audit,
then three `[user]` items — the end-to-end test the user is running through their own day, the bug-report
address waiting on Google provisioning a Workspace account, and the instrumentation test run that needs
Android Studio. A /next run will get through the builds and the audit and then stop at work only the user
can do.

**Two reports are queued outward rather than inward.** [digest-next-recomputes-medians] drafts a message
to the No code method project; a second report on the same project's over-asking rule was already sent
during this session and is recorded in `INBOX/sent.md`.

#### Help, Thanks and Report-a-bug content [help-thanks-report-content]
Blocked by: [0019-ai-choice-flow-and-mcp-setup], [0020-remote-mcp-server]

**Held from further offers on 2026-09-03, with your agreement, after a third session reached it and found nothing had changed.** It had been skipped on 2026-08-25 and again on 2026-08-31 for the same reason both times: two of Help's three topics describe a setup path and a Claude behaviour that do not exist. Rather than re-read that explanation every session, the capture now names what it waits on and returns by itself once both have shipped. Splitting the writable third out was refused twice, on the reasoning recorded below, and was not re-proposed.

The user-only step this item had buried in its prose — creating the bug-report email address — was lifted out on 2026-09-03 into [bug-report-email-address], which is cleared to run. The reasoning for choosing an email address stays below; what left is the doing of it.

The words for the three bottom-of-drawer screens that [0022-help-thanks-report-a-bug-content] builds. Help should cover MCP setup, the production custom-instruction text, and the "tasks dated before today" behaviour described without naming the category — the SPEC §Tasks dated before today wording is ready. **This item also drafts the custom-instruction text itself**, folded in on 2026-08-25 from [custom-instruction-production-text]: that text is one of Help's three topics, so the words belong with the rest of Help's words rather than in an item of their own. What stayed behind there is only the live test, which reads the draft this item produces and reports back the wording changes that follow. Two of its inputs aren't ready: the live-tested wording of that custom-instruction text ([custom-instruction-production-text]) and the MCP setup design ([0019-ai-choice-flow-and-mcp-setup], [0020-remote-mcp-server]). Write the content when those have landed.

**Skipped in planning on 2026-08-25**, with the design progress made there recorded here. The item is mixed and mostly not writable yet: Help's MCP-setup instructions would have to be invented before [0019-ai-choice-flow-and-mcp-setup] and [0020-remote-mcp-server] define the path, and the custom-instruction text this item now drafts is in the same position. Thanks is writable today but is a single paragraph [0022-help-thanks-report-a-bug-content] will write when it builds, so splitting it out would produce a fragment rather than a piece of work.

**Where a bug report goes — settled 2026-08-31, the user's call: a dedicated email address.** A web form (buildable later on flintcraft.tech, replaceable without redesign) and a GitHub issue (requires an account most of Taskflow's non-technical users won't have) were the alternatives and lost on audience fit and cost to stand up. The address itself is not yet named; it gets named before [0022-help-thanks-report-a-bug-content] ships the screen, and creating it is a step only the user can do. The Report-a-bug words stay in this item rather than splitting off — they are one sentence from writable and a split would produce a fragment. Skipped again 2026-08-31: the other two inputs ([0019-ai-choice-flow-and-mcp-setup]/[0020-remote-mcp-server] setup design, and the custom-instruction text) still haven't landed.

#### Personal strategy and memory dogfood — an early preview of Taskflow's Strategy-doc experience [personal-strategy-preview]
Not before: 2026-11-25

Alex's idea, raised in planning on 2026-06-24: use this workspace as an early, live preview of Taskflow's personal Strategy-doc experience, before Taskflow has the feature built. She'd keep a real personal Strategy doc and have the strategy conversations here with Claude directly, instead of through Taskflow plus a remote MCP server — neither of which exists yet. In her words: "We're just here, so we don't need the MCP."

Three threads bundled in it: (1) a real personal Strategy doc for Alex, maintained here in conversation with Claude — the experience a paid Taskflow user would eventually get via MCP, doubling as genuine design research for Taskflow's Strategy-doc feature; (2) Alex's real tasks, handled carefully so they don't go missing when Taskflow test builds wipe data — her live task data must not depend on the test app; (3) Claude-memory sync across her Claude surfaces. The insight she reached, and Claude strongly seconded: rather than pushing each strategy update out into many Claude memory stores, point all her Claudes at one canonical strategy doc and have them read from it. Pull-from-one beats push-to-many — one source of truth, nothing to hand-sync.

Why it was shelved, decided 2026-06-24: the method is one-spec-per-project. Two of the three threads (personal strategy, memory sync) aren't Taskflow app features, so they don't fit SPEC.md's contract that every entry describes something existing in the build. Governing three concerns in one workspace would need either multi-spec handling in the method or a deliberate re-framing of what this SPEC is about. Alex chose to wait for multi-spec support rather than bend SPEC now. That's a change to the method itself rather than a queue item here, which is why nothing in this queue holds it.

Privacy note to carry into any revival: if the personal strategy and real tasks get committed into this product repo and it's ever shared or made public, that's the user's private life data exposed. Decide the home with that in mind when this revives — it is the first question when this comes back, not an afterthought.

**Dated in planning on 2026-08-25, with the user's approval.** It waits on multi-spec support in the method, which no item in this queue can deliver and which belongs to the No code method project. It cannot be held below the readiness line either, because held work has to be specific enough to build and this is not. Left as a plain capture it returned to the top every session and was set aside again, which is what had been happening. Three months was chosen as long enough not to re-read it every session and short enough that it comes back while still fresh if multi-spec support lands sooner. It is not offered again before that date.

#### [user] Business registered far enough to open an organization Play account [business-registration-for-play-account]
Not before: 2026-09-10

**Dated 2026-09-03 with your approval, a week out, on your word that the registration is actively being
worked on.** It waits on something no item in this queue can deliver — the business existing — and it
cannot be driven before then either, because its first real step is requesting the D-U-N-S number and
Dun & Bradstreet issue those to registered businesses. Left as a plain capture it would return to the
top of every planning session and be set aside again; kept below the readiness line it would need to be
specific enough to build, which it is not. A week was your figure rather than a guess of Claude's, and
it is short deliberately: you expect movement, so the next ask should come while that is still true.
Nothing is held up by the wait — [play-console-subscription-product] is parked behind this either way.

Taskflow's Google Play developer account will be an **organization** account, settled by you on
2026-09-03, which means it cannot be opened until your business registration produces the things
Google asks for. This line exists so that dependency has something real to wait on inside this queue:
[play-console-subscription-product] is held against it, and through that item the whole paid tier.

**The work itself is not this project's.** Registering the business is tracked in the flintcraft.tech
project, and nothing here duplicates it. What this item holds is only the Taskflow-side consequence —
the Play Console step cannot start until the registration is far enough along, and it releases when
you say so.

What "far enough along" means, per
`workshop/resources/research/google-play-organization-account-requirements.md` (read 2026-09-03): a
**D-U-N-S number** for the business, government-issued business registration or incorporation
documents, proof of a physical address that is not a registered agent's, an authorised representative
named on that registration, and an organization phone number and website. The D-U-N-S number is free
but can take **up to 30 days**, so it is the long pole and worth requesting as soon as the business
registration itself supports it — well before anyone wants to open the Play account.

Observable: nothing in this repository changes when this is done, so completion is not checkable from
here — a later session asks rather than checks. Stated plainly so nobody builds a check that cannot
work.

Filed 2026-09-03 during planning, at the moment the account-type choice was settled. The capture that
held that choice was deleted in the same move, its facts folded into
[play-console-subscription-product], which is where they are used.

