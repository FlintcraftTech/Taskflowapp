# QUEUE

## Processed

> Vetted work, ready to build — worked top to bottom. Each piece of work is one
> item: a `#### ` heading naming it, a `[slug]` at the end of that heading line, and
> a short rationale beneath. A leading flavor tag names how it runs — none for a
> build (Claude edits files), `[audit]` for a review pass, `[user]` for a step only
> you can do. A security or privacy risk Claude surfaces lives here too, as a work
> item carrying a `Red flag · State: cleared/uncleared` marker. The line below marks
> how far down is cleared to build; anything below it is decided but not ready yet.

#### Free-tier delete a Project on Later [project-delete-later]

**Lifted on 2026-09-05.** The drag gesture it waited on exists and has been driven on a real phone:
[task-reorder-within-list] shipped on 2026-09-02, and the 2026-09-05 device audit dragged a task above
another within Today and saw the order hold across a relaunch (TEST-LOG row 041), and dragged a task to
the target row and back (row 043). The bin target itself was not separately hit — the audit says
steering an adb drag between two adjacent targets is guesswork — so this build adds the Project-card
case to a gesture that is proven, rather than inventing it.

Free tier — long-press a Later Project card and drag it to a delete target in the upper-right, the same gesture used to delete a task. Deleting a Project does not delete its tasks: they reassign to the system Unassigned Project (reassign logic shipped in [unassigned-project-model]). SPEC §Create or delete a Project describes this. Carved from the retired [project-lifecycle-later] during planning on 2026-06-22 — its create half was promoted to [project-create-picker-ui], its three remaining pieces split out by dependency.

What actually holds it is the general drag + bin/delete drag-target gesture (SPEC §Drag-target icons), which doesn't exist anywhere in the app yet (confirmed 2026-06-22). Any of several items could deliver it — [task-reorder-within-list] builds the first drag primitive, and [0008-drag-task-between-schedule-screens], [0010-outliner-typing-drag-target-icons] or [0011-cut-and-paste-os-clipboard] would each bring the bin target. It is held against [task-reorder-within-list] because that is the one sitting nearest the top; if the bin gesture arrives by another route first, this lifts then instead. Build order doesn't matter — only that the bin gesture exists.

#### Free-tier reorder Projects in the Strategy-doc editor [project-reorder-strategy]

**Lifted on 2026-09-05.** The editor it waited on exists and was seen working on a phone:
[0015-strategy-doc-and-life-area-context] shipped on 2026-09-02, and the 2026-09-05 device audit
confirmed the doc renders one heading per Project with Unassigned excluded (TEST-LOG row 047). What that
row did not exercise is edit persistence across a relaunch or the share sheet — neither of which this
item touches, since it adds heading drag rather than paragraph editing.

Free tier — drag Project headings in the Strategy-doc editor to set Project order; the Strategy doc owns Project order app-wide, including the Later card order. SPEC §Strategy doc describes this. Carved from the retired [project-lifecycle-later] during planning on 2026-06-22. Held because the Strategy-doc editor must exist before headings can be dragged in it.

#### Record this session's device and test-run facts in TOOLS.md [instrumentation-tests-uninstall-the-app]
Red flag · State: cleared

Adds three fact lines to TOOLS.md. The first is why this item exists: no future session should run
Taskflow's instrumentation tests without knowing the run removes the app and its database from the phone,
and without taking a JSON export first. The other two were found on 2026-09-06 while driving the device
during a planning session, and are folded in here rather than filed separately because they are the same
work — one file, one line each, one build — and because a future session meets all three at the moment it
is about to touch the phone.

**The red flag is cleared by informed acceptance, not by a fix, and this is the consent trail.** On
2026-09-05 Alex was told plainly: a connected test run removes the app and its Room database; on this
project's AGP there is no setting that stops it; taking a JSON export before each run is the protection;
and Android Auto Backup is a second net that worked once, unplanned, and that nobody has deliberately
tested. She chose to go ahead on that basis rather than stop running the tests.

Noticed on 2026-09-05, when Alex said Taskflow no longer seemed to be on her phone, hours after the
instrumentation tests were run from Android Studio in the same session.

**Why this is the likely cause.** A Gradle connected-test run installs the app and the test package,
runs the tests, and removes both afterwards. Nothing else in the day's work uninstalls anything: the
device drive earlier only added and deleted tasks through the app's own UI, and the app was confirmed
present at 14:11.

**Stated as suspected rather than established.** The check that would settle it returned nothing useful:
`adb` reported no device attached, because the daemon had restarted and wireless debugging had dropped,
so `pm list packages` was an empty query rather than an answer. Reconnecting the phone and asking for
the package is what confirms or kills this.

**Why it is filed as a red flag.** Losing the app takes the Room database with it, and the database is
the single source of truth for everything the user has (SPEC §UX principle 2). There is no cloud sync
yet, so the only other copies are Android Auto Backup and whatever JSON export happens to exist. In this
instance nothing was lost — a full export had been taken at 14:09 for the export/import check, and it
was handed to Alex — but that was luck rather than design: the export existed because a different test
needed it.

The risk it names is the one Alex raised herself hours earlier, asking whether she can count on tasks
staying in the app. If running the project's own tests wipes the device, the answer on the current setup
is no, and that has to be said plainly rather than softened.

**Whether it can be prevented was researched on 2026-09-05, and it cannot.** Filed as
`workshop/resources/research/instrumented-test-uninstall-after-run.md`. Every published answer says to set
`uninstallAfterTest = false`; that property was added in AGP 3 and does not exist anywhere in the AGP
9.2.1 jar this project resolves. The behaviour now lives inside the Unified Test Platform runner as an
internal `uninstallApksAfterTest` parameter with no Gradle property and no DSL exposing it. So an
export-first habit is the answer rather than a workaround for a setting nobody found.

**Where the warning goes changed at the decision step, on 2026-09-05.** The capture proposed adding it to
[run-instrumentation-tests]'s walkthrough. That item was walked to done earlier the same day and leaves the
queue at the next close, so a warning written there would be deleted within the day. TOOLS.md is where
environment facts live and is read by every session before it touches the device, so the warning goes there
instead. The export-first step for [first-end-to-end-test] was a separate matter and was written straight
into that item's walkthrough in the same planning session, its capture deleted once the content had moved.

Files:
- `TOOLS.md` — three dated fact lines, each in the same one-line-per-fact shape as the rest of the file:
  1. That a connected instrumentation test run (Android Studio's Run 'Tests in …', and
     `connectedDebugAndroidTest` from the command line) installs the app and the test package, runs the
     tests and then **removes both**, taking the Room database with it; that on AGP 9.2.1 no build-file
     setting prevents this, citing the research file; that a JSON export from Settings should be taken
     before any such run; and that on 2026-09-05 Android Auto Backup restored the data by itself after a
     reinstall, which is a second net rather than a plan.
  2. That a long-press drag cannot be steered between two adjacent drag targets over adb. Three attempts
     across two sessions on 2026-09-05 and 2026-09-06 were each read as a horizontal page swipe and
     navigated instead of dragging, including one built from explicit `input motionevent DOWN / MOVE / UP`
     calls with a hold between them. So a check that depends on hitting one target rather than its
     neighbour is user work — see [bin-drag-target-check].
  3. That the phone re-locks itself after a few minutes of inactivity, so a session driving the device
     needs the user to unlock it, and may need them again partway through a longer pass. Observed twice on
     2026-09-06, each time costing a turn.

Observation: a grep of `TOOLS.md` for `androidTest-results` returns the existing results-path line with the
uninstall line adjacent to it; a grep for `motionevent` returns the drag line; and a grep for `re-locks`
returns the lock line. The check reaches the one file named above.

Refused: adding the warning to [run-instrumentation-tests]'s walkthrough — that item is finished and about
to leave the queue, so the warning would go with it.

Rests on, read 2026-09-05: that the AGP 9.2.1 jar contains no `uninstallAfterTest` and no Gradle property
for `uninstallApksAfterTest`, established by scanning the resolved artifact rather than from documentation;
and that `gradle/libs.versions.toml` line 2 pins AGP to 9.2.1. Both change on an AGP upgrade.

Filed 2026-09-05, 21:35, mid-run. Processed 2026-09-05.

#### Subtask lines are discarded the instant they are created, so subtasks cannot be made at all [edit-outliner-missing]

Came out of the [verify-run-2026-08-31] audit on 2026-09-05 and was rewritten at the decision step the
same day, after the defect was reproduced on the phone and traced in the code.

**What the audit saw.** SPEC §Edit dialogue: outliner-style typing for subtasks says a task and its
subtasks are rendered as a small outliner and that adding them happens through ordinary typing. Driving
the app over adb, typing a title, pressing Enter, and typing a second line produced one concatenated
title — "AUDIT-parentAUDIT-child-oneAUDIT-child-two" — with no second line and no indentation. The
audit concluded from this that the dialogue has no outliner.

**That conclusion was wrong, and correcting it is why this item is small.** `Outliner.kt` exists and
`EditTaskScreen.kt` calls `OutlinerField` as the dialogue's first field. The outliner is built, wired in
and rendering. A build handed the audit's version of this item would have set about writing a feature
that is already there.

**What is actually broken, reproduced on the phone on 2026-09-05 and then read in the code.** Tapping the
keyboard's own Next key produces nothing, and so does an injected Enter — so this is a real defect rather
than an artifact of driving the device over adb. The cause is a round-trip that eats its own output.
`OutlinerField` holds the task and its children as one string. `onNext` appends an empty child line and
calls `onTextChange` with the re-rendered text. That text comes straight back in through
`Outline.parse`, which trims every line and filters out the empty ones — so the newly created child,
which is empty by definition at the moment it is created, is discarded before it can be typed into. The
blank-dropping rule is deliberate and its comment says why: *"Blank lines are dropped, so pressing Enter
and thinking better of it leaves nothing."* It makes abandoning a half-typed line harmless, and it makes
starting one impossible.

Why this matters more than one missing control. There is no other way to make a subtask anywhere in the
app, so four SPEC behaviours are unreachable rather than merely untested: subtasks living under their
parent, the parent showing an expand/collapse control instead of a checkbox, completion rolling up from
children, and the promote drag target that lifts a child out. The audit recorded those as blocked rather
than failed, because nothing could exercise them.

It also blocks half of cut-and-paste: cutting a childless task and pasting it back works (TEST-LOG row
043), but the parent-with-children indented block that SPEC says should round-trip cannot be produced.

Files:
- `app/src/main/java/com/example/taskflow/ui/edit/Outliner.kt` — `Outline.parse` stops discarding blank
  lines, so a child line that is empty while it is being typed into survives the round trip. The parent
  is still the first line and later lines are still children; what changes is that an empty later line is
  kept rather than filtered out. `normalise` already leaves blank lines unindented and needs no change.
- `app/src/main/java/com/example/taskflow/ui/edit/EditTaskViewModel.kt` — the save path drops empty
  children instead, so pressing Enter and thinking better of it still leaves nothing behind. That is
  where the blank-dropping rule moves to; it is not repealed, it is moved to the moment it was written
  for.

Observation: on a device, opening a task and pressing the keyboard's Next key at the end of the title
produces an indented empty line with the cursor in it; typing into that line and saving gives the task a
subtask; pressing Next and saving without typing leaves the task with no subtask. The check reaches the
two files named above.

Refused: building an outliner. The audit's reading was that there is none; there is one, and writing a
second would leave this defect in place behind it.

Rests on, read 2026-09-05: that `Outliner.kt`'s `Outline.parse` trims and filters blank lines and that
`OutlinerField`'s `onNext` appends an empty line and re-emits through it; that `EditTaskScreen.kt` line
127 calls `OutlinerField`; and that the failure reproduces on the phone through the keyboard's own Next
key as well as an injected Enter, so it is not an adb artifact.

Filed 2026-09-05, 14:35. Rewritten and processed 2026-09-05.

#### Date-picker tiles clip the month name, and the focused Project name overlaps the header chevron [device-layout-clipping]

From the [verify-run-2026-08-31] audit on 2026-09-05, not yet reviewed. Two layout defects seen on the
phone, filed together because both are a box too small for what is inside it.

**The date strip's month name is cut in half.** SPEC §Date picker — side-scrolling date strip says each tile
shows its day number with the month name beneath it — 24 above Aug. On the device the month row is clipped
horizontally through its middle, so "Sept" renders as something closer to "Sent" on every tile. The tile is
readable enough to use, and wrong enough that the letters are not the letters.

**The focused Project's name sits on top of the spine header's right chevron.** SPEC §Focus on one Project
temporarily says the top bar carries the Project's name with an X. It does, in the same horizontal space the
next-page chevron occupies, and the two draw over each other — the header read "AU>DIT-PROJ" with a Project
name of ten characters. A longer name would bury the chevron entirely.

Neither is a judgement about how the app looks, which the audit put out of scope; both are text rendered on
top of something else.

**The clipping was confirmed again on 2026-09-05, on the build now on the phone**, so it survived the
2026-09-04 date-strip work — and confirming it located the cause. [date-strip-legibility] stopped clipping by
computing tile **width** from the available space, which its record states. `DateStrip.kt` still sizes each
tile with a fixed `TILE_HEIGHT`, and the month row is what overflows that: the letters are cut through
along their lower half rather than truncated at their end. The capture's instinct — a box too small for what
is inside it — is right, and the axis it implies is wrong, which is why the earlier fix could not have caught
this.

**The header overlap was not re-checked**, and stands on the audit's evidence rather than a fresh look: on
2026-09-05 the database held no Projects at all, so there was nothing to focus. The audit saw it and recorded
the exact rendered string, on the older build.

Files:
- `app/src/main/java/com/example/taskflow/ui/edit/DateStrip.kt` — the tile's height follows its content
  instead of the fixed `TILE_HEIGHT`, so the month row is never cut through.
- `app/src/main/java/com/example/taskflow/ui/navigation/AppRoot.kt` — the focused Project's name and the
  spine header's next-page chevron stop sharing horizontal space; the name yields to the chevron rather than
  drawing over it, however long the name is.

Observation: on a device, every date tile shows its month name whole with no letters cut; and with a Project
focused whose name is long enough to have caused the overlap, the header shows both the name and the right
chevron with neither drawn on top of the other. The check reaches the two files named above. It needs a
Project to exist, which is worth knowing because none did when this was written.

Refused: raising `TILE_HEIGHT` to a larger fixed value. A bare number has no derivation, which is the ground
an invented dp value was refused on during [drawer-ai-row-copy]; the height should follow what is in the tile.

Rests on, read 2026-09-05: that `DateStrip.kt` sizes tiles with a fixed `TILE_HEIGHT` while their width is
computed; that [date-strip-legibility]'s record claims only the width fix; and that the clipping is present on
the build installed on the phone that day.

Filed 2026-09-05, 14:35. Processed 2026-09-05.

#### [user] Apply the cloud migrations and prove a cross-account read is denied [supabase-apply-cloud-migrations]

**Lifted on 2026-09-05.** [supabase-rls-policies] shipped that day: both SQL files now exist in the
repository — `supabase/migrations/0001_initial_schema.sql` and `0002_rls_policies.sql` — with four
tables mirroring Room v5 and sixteen per-user policies. Its verification is this item and nothing else,
so waiting for the blocker to be verified before lifting would have held it forever.

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

#### [user] Verify the Schedule date-matrix rendering on a device [verify-schedule-date-matrix]

**Lifted on 2026-09-05.** [0006-side-scrolling-date-picker] was found already shipped on 2026-09-02, and
the strip has since been used on a real phone — the 2026-09-05 device audit read its tiles while checking
the date-format setting (TEST-LOG row 044), and [device-layout-clipping] describes their rendering. So a
date can now be set by hand, which is the only thing this check was waiting for.

Confirms that dated tasks render with their DD/MM label in the right slot, for the cases the FAB can't seed yet: a 2–7-day date in Soon, an 8+-day date in Later, and a past date staying on Today (DD/MM, no overdue label). The slot maths is already unit-tested from 0002, and the non-Today DD/MM render was exercised by the Tomorrow check in [device-verify-core-screens]; what's untested is the render across the other slots. This also serves as the regression check for [tomorrow-no-date-label] — the same Soon/Later DD/MM and past-Today verification confirms the Tomorrow-no-label change didn't break the other slots' labels (the Tomorrow-shows-no-label half was confirmed on-device 2026-06-25). Held because setting those dates needs a date picker, which is what [0006-side-scrolling-date-picker] builds.

Walkthrough, once the date picker ships:
1. Create three tasks and open each one's edit dialogue.
2. Give the first a date 2–7 days from today; give the second a date 8 or more days out; give the third a date in the past.
3. Swipe to **Soon** — the first task should be there, showing its date as DD/MM.
4. Swipe to **Later** — the second task should be there, showing DD/MM.
5. Swipe to **Today** — the past-dated task should be sitting there, showing DD/MM and *no* overdue marking.

#### [user] Verify a far-future dated task under a user Project card [verify-far-future-project-card]

**Lifted on 2026-09-05**, for the same reason as the item above: the date strip shipped and has been used
on a phone, so an 8-or-more-day date can now be set by hand.

Confirms a user Project's card on Later holds its far-future (8+ day) dated tasks with the DD/MM label. Held for the same reason as the item above: an 8+-day date can't be set without date-editing, and the FAB on Later only creates undated tasks. Came out of [later-by-project-screen]; the multi-user-Project ordering half and the move-between-Projects check rolled into [project-create-picker-ui] during planning on 2026-06-22.

Walkthrough, once the date picker ships:
1. On **Later**, make sure you have a Project of your own (not just Unassigned).
2. Create a task in that Project and open its edit dialogue.
3. Set a date 8 or more days from today, and save.
4. Go back to **Later** and open that Project's card. The task should be inside it, showing its date as DD/MM.

#### [user] Check the bin drag target deletes a task [bin-drag-target-check]

Confirms that dragging a task to the **bin** target deletes it. SPEC §Drag-target icons says picking up a task
on a Schedule screen raises a row of targets — a bin that deletes it and a cut that removes it and puts its
text on the device clipboard. The cut half is proven (TEST-LOG row 043). The bin has never been hit.

Split out of [verify-run-2026-08-31-remainder] on 2026-09-05, because it is the one check in that audit a
session cannot drive. The two targets sit side by side, and steering a long-press drag between them over adb
is guesswork: the 2026-09-05 audit declined the attempt for that reason, and a later session that day tried
twice while clearing up a test task — both attempts were read as a page swipe and navigated instead of
dragging. Two independent failures, so this is a capability limit rather than bad luck.

Walkthrough:

1. On Today, add a throwaway task — something obviously disposable, like "bin-test". Look for: the new task
   in the list. **If a task named `AUDIT-completed-check` is already sitting there, use that one instead of
   making a new one** — a session left it behind on 2026-09-06 while testing search, having failed to delete
   it over adb, which is the same failure this item exists for.
2. Press and hold that task until it lifts, and keep holding. Look for: a row of target icons appearing at
   the top of the screen, one of them a bin.
3. Drag it onto the **bin** icon specifically — not the one beside it — and let go. Look for: the task gone
   from Today.
4. Check your device clipboard by long-pressing in any text field and choosing paste. Look for: **not** the
   task's text. If the task's text pastes, you hit the cut target rather than the bin, and the check has not
   been done — try again from step 1.
5. Tell a planning session what happened, including whether the task's text turned up on the clipboard.

Observable: the task is absent from Today and its text is absent from the clipboard. Step 4 is what
distinguishes a real pass from hitting the neighbouring target, which is the exact confusion that has left
this unchecked twice.

Rests on, read 2026-09-05: that the target row appears on a long-press drag from a Schedule screen and
carries both a bin and a cut, per SPEC §Drag-target icons and TEST-LOG row 043, which exercised the cut
target on a device.

#### [user] Try gradlew from Android Studio's integrated terminal, which is a different shell [gradle-from-ide-terminal]

Raised by you on 2026-09-05, when you installed the Claude Code plugin for Android Studio and asked what it
lets us do.

The plugin itself does not help with this: it exposes no code-execution tool to the model, so Claude still
cannot press Run or start a Gradle task through the IDE. That is settled and recorded in
`workshop/resources/research/claude-code-jetbrains-plugin-capabilities.md`.

What the question surfaced is a different route nobody has tried. The plugin works by running the `claude`
command in Android Studio's **integrated terminal**. That is a different shell from the one this project's
sessions run in — and TOOLS.md records the Gradle failure narrowly, as a loopback socket the JVM cannot open
in the process Gradle forks from Claude's shell, while recording separately that Android Studio builds this
project fine on the same machine. Whether `gradlew` succeeds from the IDE's own terminal is unknown.

Worth trying because of what it would remove. If it works, Claude could compile and run the instrumentation
tests from a session started there, and the `[user]` flavor comes off [run-instrumentation-tests] and off the
compile step that makes every code item tick UNCONFIRMED. If it fails, the answer costs one command and
TOOLS.md gains a line narrowing the failure further.

**This item establishes the fact and nothing more, settled with you on 2026-09-05.** A success would not mean
these sessions can compile. It would mean a session **started from Android Studio's terminal** can — which is
a terminal session rather than the desktop app, with none of the file viewer and side panel you read the work
through. Acting on a yes would mean running whole `/next` runs inside Android Studio and a rule in CLAUDE.md
saying which work items require it, and that trade is yours to weigh rather than something this item may
presuppose. It is filed separately as [runs-in-android-studio-decision], held against this one, so it only
comes up if the answer is yes.

**A second unknown rides on the first, stated rather than assumed:** nobody has run a session in that
terminal, so whether the plugin, the skills and the hooks all load there is expected rather than established.
It is the same CLI reading the same configuration, which is a reason to expect it and not evidence.

It is `[user]` work because Claude's shell is not Android Studio's terminal and nothing here reaches it — the
capability check on 2026-09-05 confirmed the IDE plugin exposes only a read-only diagnostics tool, so there is
no route to that shell from a session running here.

Walkthrough:

1. Open the Taskflow project in Android Studio and open its built-in terminal — the one along the bottom of
   the window, not a separate terminal app. Look for: a command prompt showing the project's folder path.
2. Run `.\gradlew.bat :app:assembleDebug --no-watch-fs --no-daemon`. Look for: either `BUILD SUCCESSFUL`, or
   an error containing `Unable to establish loopback connection`.
3. Tell a planning session which of those two you got. Look for: nothing further on your side — if it built,
   the whole question of where runs happen opens up, and that is the other item's business.

Observable: the command's own last lines, which the user reports. Nothing in this repository changes until
TOOLS.md gains its line, so completion is not checkable from here — a later session asks rather than checks.

Rests on, read 2026-09-05 and recorded in
`workshop/resources/research/claude-code-jetbrains-plugin-capabilities.md`: that the Android Studio plugin
works by running the `claude` CLI in the IDE's integrated terminal, and exposes no code-execution tool to the
model; and, from TOOLS.md, that the loopback failure is recorded against the shell these sessions run in while
Android Studio itself builds this project successfully on the same machine.

Filed 2026-09-05, 15:20, mid-run. Processed 2026-09-05.

#### [audit] Finish the checks the 2026-09-05 device pass could not reach [verify-run-2026-08-31-remainder]

Came out of the [verify-run-2026-08-31] audit on 2026-09-05 and was reviewed at the decision step the same
day. That audit drove the app on the phone and recorded verdicts for the areas it could reach (TEST-LOG rows
038–050). The checks below were left, each for a stated reason rather than for want of time, and this
collects them so they are not lost with the session.

- **The day-begins-at rollover.** The picker was confirmed present at its 4:00 AM default, but watching a
  Tomorrow task move into Today needs the clock to cross a boundary set a few minutes ahead — a wait, in the
  middle of a run, on the user's own phone.
- **A one-off task dated six months out still showing in Later.** The 30-day cap was confirmed for recurring
  instances; the uncapped manual-date case was not exercised.
- **Strategy doc edit persistence, and the share sheet opening.** The doc's structure was confirmed — one
  heading per Project, Unassigned excluded — but nothing was typed into a paragraph and no share sheet was
  opened.
- **The Yesterday page.** Reached during the pass but never examined against what SPEC says it holds.
- **Whether typing a query narrows the completed history too.** Added on 2026-09-05 when
  [search-omits-completed] was deleted. The empty-query half of SPEC §Search and completed history was
  re-checked on the phone that day and holds — the dated completed history renders under a day header, with
  matching active tasks listed above it. What was not re-checked is the other half: that typing narrows the
  completed list as well as the active one, with the day headers for days that still have results staying
  above them.

**The bin drag target left this item on 2026-09-05** and is now [bin-drag-target-check], a `[user]` line.
Driving it needs steering a long-press drag between two adjacent targets, which failed twice more that day
when a session tried it over adb — every attempt registered as a page swipe. Two independent failures make it
a capability limit rather than bad luck, so the check belongs to a person's thumb.

Three further areas stay blocked rather than unfinished, and belong to [edit-outliner-missing] rather than
here: subtasks, the parent's expand/collapse rollup, and the promote target.

**Two things to know before this runs, both read off the phone on 2026-09-05.** The device now carries that
day's builds rather than the 2026-09-04 APK — confirmed by the Strategy page showing a single header with its
Share action in the spine header's trailing slot, which is what [strategy-page-double-header] built. The
earlier warning that the phone was a build behind is therefore spent, and is replaced by this. And the
database currently holds **no Projects at all** — the Strategy page shows its empty state — so any check
needing a Project of the user's own has to create one first rather than assuming one exists.

Filed 2026-09-05, 14:35. Processed 2026-09-05.

--- Cleared to run above this line ---

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
Blocked by: [edit-outliner-missing]

**Its blocker was swapped on 2026-09-05.** It had waited on the [verify-run-2026-08-31] audit, which has
now run — and which found that pressing Enter produces no subtask at all. So the concern that put this
item below the line is not resolved but sharpened: the hint would advertise behaviour that genuinely does
not work today. [edit-outliner-missing] is the item that makes it work, and this now waits on that
instead. Everything below is unchanged and still stands; what the audit did settle is that the outliner
itself exists, so the hint has something real to point at once the defect is fixed.

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

#### Confirm what Play Billing exposes for subscription pause [subscription-pause-play-billing]
Blocked by: [0017-tier-model-and-subscription-handling]

Verify what Google Play Billing actually exposes for subscription pause — it's largely Play-Store-controlled rather than app-controlled, so the app's options may be narrower than they look. Deliberately not checked now: this is staleness-prone information, so it's worth confirming at the moment the subscription handling is actually built rather than carrying an answer that may have changed by then.

#### Verify Project-deletion data behaviour end-to-end [verify-project-delete-data]
Blocked by: [project-delete-later]

[unassigned-project-model] built the repository logic: deleting a real Project reassigns its tasks to the system Unassigned Project (`TaskDao.reassignTasksToProject`, called by `ProjectRepository.delete`), and the Unassigned Project is undeletable (`ProjectRepository.delete` no-ops when `isSystem`). The reassign query was verified live on-device that session; the two guards were verified by code inspection. There is no automated test of `ProjectRepository.delete` itself and no delete gesture yet to exercise it end-to-end. When [project-delete-later] builds the long-press-drag delete, its test pass should confirm: deleting a real Project moves its tasks onto Unassigned with none orphaned, and the Unassigned card cannot be deleted. Rides that item's test pass rather than needing a run of its own.

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

#### [user] First end-to-end test of Taskflow on a device [first-end-to-end-test]
Blocked by: [edit-outliner-missing]

**Held below the line on 2026-09-05, on your own condition** — given when you deferred this item during a
/next run: not until subtasks exist. The 2026-09-05 device audit had just established that no subtask can
be created anywhere in the app, and this test exists to find out how the app feels in a normal day's use.
A day of real tasks with no way to break one into steps would fill the notes with the absence of a feature
already known and already filed, which is the outcome the "run it after the cleared builds ship" paragraph
below was written to avoid. [edit-outliner-missing] is the item that fixes it; this lifts when that ships.

That paragraph called the ordering a preference rather than a blocker. Your condition supersedes it: this
is now a blocker, on the same reasoning, hardened by what the audit found.

Filed in planning on 2026-08-25, doing what [post-first-test-polish-review] asked for: that item waits on a real-world event rather than on a build, so the event becomes its own line and the review is held against it.

This is the first time the app is used as an app rather than checked feature by feature. What it produces is a set of notes, and those notes are the input to [post-first-test-polish-review], which decides which of them earn a SPEC entry and which fold into existing work.

Claude can install a build and drive taps over adb, but noticing that something feels wrong is the whole point of this and needs the user's own eyes, which is why it is a `[user]` line rather than a build.

**Run it after the currently cleared builds ship.** Run today and the notes fill with "there is no date picker", which is queued work rather than polish; run it after the cleared run and the notes are about how the thing actually feels. That is an ordering preference, not a blocker — nothing stops it being run earlier if the user wants to.

**A restore point comes first, added on 2026-09-05 on your own question** — whether you can count on tasks
staying in the app. What is established: `MigrationTest` passed for the first time on 2026-09-05, so
[durable-local-data]'s version-5 floor is a demonstrated fact rather than a claim; JSON export and import
round-tripped on the device the same day, first execution ever; and when the app was uninstalled that
evening, Android Auto Backup restored the data by itself. What is not established, and should not be read
into that: nothing proves a future migration will be written correctly, reinstalls were never in the
scope that was tested, Auto Backup's success was unplanned rather than a designed recovery path, and
there is no cloud sync, so the phone holds the only live copy. The export is three taps and it is yours
rather than the platform's, which is why it leads.

Walkthrough:
1. Open Settings and take a JSON export, then move the file somewhere off the phone. Look for: the export
   file where you put it, not just a success message.
2. Install the current build on your device (ask a session to build and install it if it isn't already there).
3. Use the app for a normal day's worth of tasks — capture what you actually need to do, not test data.
4. Capture something from each of Today, Tomorrow, Soon and Later, so every add path gets used at least once.
5. File at least one task into a Project of your own, and complete at least one task so it reaches the Completed tray on Today.
6. Note anything that felt slow, confusing, ugly or surprising — one line each, no need to diagnose it. Roughness you would normally push past is exactly what to write down.
7. Bring the notes to a planning session and say the test has been done.

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

#### Last session advises processing [edit-outliner-missing] next [forward-advisory]

It is the item the most other work waits on. [subtask-affordance-in-edit-dialogue] is blocked by it, and
[first-end-to-end-test] is blocked by it too, on Alex's own condition given on 2026-09-05 — so the app's
first real end-to-end use cannot happen until this ships. Four SPEC behaviours are unreachable rather than
merely untested while it stands: subtasks under their parent, the parent's expand/collapse in place of a
checkbox, completion rolling up from children, and the promote drag target.

It is also small, which is the part that changed on 2026-09-06. The audit had it as a missing feature. It
is not: the outliner is built and wired in, and the defect is that `Outline.parse` discards the empty child
line `onNext` has just created. Two files, and the fix moves the blank-dropping rule to the save path
rather than repealing it.

**Overlap with the still-unprocessed work: none.** All four entries left in Unprocessed are held by rule —
two by a date, two behind open blockers — and none of them touches the edit dialogue or the outliner.
Nothing waiting to be sorted bears on this item.

Advice, not work. It is read and cleared at the next planning session's opening.

#### Help, Thanks and Report-a-bug content [help-thanks-report-content]
Blocked by: [0019-ai-choice-flow-and-mcp-setup], [0020-remote-mcp-server]

**Held from further offers on 2026-09-03, with your agreement, after a third session reached it and found nothing had changed.** It had been skipped on 2026-08-25 and again on 2026-08-31 for the same reason both times: two of Help's three topics describe a setup path and a Claude behaviour that do not exist. Rather than re-read that explanation every session, the capture now names what it waits on and returns by itself once both have shipped. Splitting the writable third out was refused twice, on the reasoning recorded below, and was not re-proposed.

The user-only step this item had buried in its prose — creating the bug-report email address — was lifted out on 2026-09-03 into [bug-report-email-address], which is cleared to run. The reasoning for choosing an email address stays below; what left is the doing of it.

The words for the three bottom-of-drawer screens that [0022-help-thanks-report-a-bug-content] builds. Help should cover MCP setup, the production custom-instruction text, and the "tasks dated before today" behaviour described without naming the category — the SPEC §Tasks dated before today wording is ready. **This item also drafts the custom-instruction text itself**, folded in on 2026-08-25 from [custom-instruction-production-text]: that text is one of Help's three topics, so the words belong with the rest of Help's words rather than in an item of their own. What stayed behind there is only the live test, which reads the draft this item produces and reports back the wording changes that follow. Two of its inputs aren't ready: the live-tested wording of that custom-instruction text ([custom-instruction-production-text]) and the MCP setup design ([0019-ai-choice-flow-and-mcp-setup], [0020-remote-mcp-server]). Write the content when those have landed.

**Skipped in planning on 2026-08-25**, with the design progress made there recorded here. The item is mixed and mostly not writable yet: Help's MCP-setup instructions would have to be invented before [0019-ai-choice-flow-and-mcp-setup] and [0020-remote-mcp-server] define the path, and the custom-instruction text this item now drafts is in the same position. Thanks is writable today but is a single paragraph [0022-help-thanks-report-a-bug-content] will write when it builds, so splitting it out would produce a fragment rather than a piece of work.

**Where a bug report goes — settled 2026-08-31, the user's call: a dedicated email address.** A web form (buildable later on flintcraft.tech, replaceable without redesign) and a GitHub issue (requires an account most of Taskflow's non-technical users won't have) were the alternatives and lost on audience fit and cost to stand up.

**The address is `bugs@flintcraft.tech`, recorded here on 2026-09-05.** Created and tested on 2026-09-05 during a /next run and working: a message sent from an outside account arrived in the Workspace inbox, labelled External. It is a Google Workspace **alias** on the flintcraft.tech domain rather than a separate mailbox — free, no extra licence, and mail to it lands in the inbox the user already reads. Both choices [bug-report-email-address] left open are therefore settled: the domain, and the alias-not-mailbox shape. One limit to carry: an alias **receives** but does not **send**, so replying to a bug report from that address rather than a personal one would need a send-as configured in Gmail, which nobody has done. Not needed for the screen to print an address, so it is noted rather than filed — if replying-as-bugs is ever wanted, it becomes its own item.

**One half of the 2026-08-31 rejection is now spent, checked on 2026-09-05.** A bug-report form does exist at flintcraft.tech/report, and its project dropdown already lists TaskFlow alongside Throughliner. So "it would cost something to stand up" is no longer true and must not be reached for again. What still decides it is the other half, and the page strengthens it: the form carries a note telling non-automated visitors not to complete it, because it is built for reports Claude has captured and handed over. Taskflow's users are mostly non-technical people with no Claude in the loop, so an in-app Report-a-bug screen pointing at that form would send them to a page that says it is not for them. The email address stands. The Report-a-bug words stay in this item rather than splitting off — they are one sentence from writable and a split would produce a fragment. Skipped again 2026-08-31: the other two inputs ([0019-ai-choice-flow-and-mcp-setup]/[0020-remote-mcp-server] setup design, and the custom-instruction text) still haven't landed.

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

#### Decide whether some runs move into Android Studio's terminal, and which [runs-in-android-studio-decision]
Blocked by: [gradle-from-ide-terminal]

Whether `/next` runs that need a compile should happen in a Claude session started from Android Studio's
integrated terminal instead of the desktop app — and, if so, what rule in CLAUDE.md says which work items
require it.

Raised by Alex on 2026-09-05, during the decision step on [gradle-from-ide-terminal]. She saw what that
item's payoff actually implies before it was written down: compiling mid-run would only be possible in a
session started in that terminal, so realising it means moving whole runs there, and that is a departure from
how she works rather than a free capability.

**The trade, as far as it is understood today.** Against: a terminal session has none of the file viewer and
side panel she reads the work through, and she is a no-code developer for whom that surface is not a
convenience but how the work is legible at all. For: a compile inside the run is what would stop code items
shipping ticked UNCONFIRMED, and would take the `[user]` flavor off the test runs. The middle option is that
only some items — the ones where an unverified build genuinely holds something up — carry the requirement,
which is what a CLAUDE.md rule would have to name.

Held against [gradle-from-ide-terminal] because there is nothing to decide unless `gradlew` actually works in
that terminal. If it fails, this entry is deleted unread.

A second thing to establish before deciding, and it is not established now: whether a Claude session started
in that terminal loads the plugin, the skills and the hooks at all. Same CLI, same configuration, so it is
expected — but nobody has run one, and the whole point of this entry is not to presuppose things nobody has
checked.

Filed 2026-09-05 during planning, at the moment the implication was raised.

