# QUEUE

## Processed

> Vetted work, ready to build — worked top to bottom. Each piece of work is one
> item: a `#### ` heading naming it, a `[slug]` at the end of that heading line, and
> a short rationale beneath. A leading flavor tag names how it runs — none for a
> build (Claude edits files), `[audit]` for a review pass, `[user]` for a step only
> you can do. A security or privacy risk Claude surfaces lives here too, as a work
> item carrying a `Red flag · State: cleared/uncleared` marker. The line below marks
> how far down is cleared to build; anything below it is decided but not ready yet.

#### Let the compile rule cover a batch, and say what happens when it fails [compile-rule-vs-multi-item-runs]

Rewrites the compile-handover rule in this project's CLAUDE.md so it describes what a multi-item run
actually does, and so it covers the case it currently says nothing about.

**The mismatch.** The rule written on 2026-09-12 by [runs-in-android-studio-decision] says that when a
build's work needs compiling, the build stops, hands Alex the command to paste into Android Studio's
terminal, and waits for the result before ticking the item. Read literally that is one hand-over per item.
The same session's run then built four Kotlin items back to back and handed over **one** compile covering
all four, ticking them only once it returned `BUILD SUCCESSFUL`. It recorded why in its build working file
and left every one of them in the queue until then, so nothing was ticked on an unverified build and
nothing could strand. The rule's purpose was met and its wording was not — and the next session reads the
wording.

**Settled with Alex on 2026-09-17, in favour of what the run did.** A run may cover consecutive code items
with a single compile, provided not one of them is ticked or removed from the queue until it passes. Four
identical hand-overs of the same command interrupt her four times to learn the same thing once. What the
rule must still forbid is the reading it was written against: ticking code items unconfirmed and leaving
the compile for later.

**And the half the rule does not cover at all: what happens when the compile fails.** On a failure the run
stops and nothing is ticked. It reads the error to find which item's work caused it; where that is clear
it fixes it and hands the command over again; where it is not, the run halts and says so plainly —
including that the working tree holds the changes from every item in the batch, because a failed compile
does not undo them.

**The accepted cost, recorded rather than left to be discovered.** Batching means a failure does not say
which item broke it. That is the price of not interrupting Alex once per item, and the compiler's error
names the file in most cases anyway.

**Refused: a cap on how many items one compile may cover.** Any figure would be invented rather than
derived, and the natural bound already exists — consecutive code items within one run.

**Why this is queued rather than done at planning.** A planning session may write the queue, the spec, the
log and the research folder, and not this project's CLAUDE.md. The precedent for changing it as a build is
[runs-in-android-studio-decision], which is the item that wrote the rule in the first place.

Files:
- `CLAUDE.md` — the compile-handover paragraph under Project rules gains two things: that a run may cover
  consecutive code items with one compile so long as none is ticked or removed from the queue until it
  passes, and the failure path above. The three typed command lines it already carries are unchanged, and
  so is the `JAVA_HOME` line and the note saying why that line is stated.

Observation: `CLAUDE.md`'s compile rule states the batching permission with its no-tick condition, and
states what happens on a failed compile including that the working tree still holds the batch's changes; a
read of the paragraph finds the three typed command lines still present and unaltered. The check reaches
the one file named above.

Rests on: that a planning session's scope-lock excludes `CLAUDE.md`, read in the procedure on 2026-09-17;
and on the 2026-09-12 run's own record of handing over one compile for four items, in LOG.

Filed at the close of the 2026-09-12 /next run, at 13:14, read from the clock; settled with Alex on
2026-09-17.
Filed 2026-09-17 15:47, stamped by the queue tool.

#### Record two environment facts in TOOLS.md — the local plugin marketplace, and the Gmail connector's reach [tools-md-environment-facts]

Writes into TOOLS.md two things this project established on 2026-09-16 and 2026-09-17, so a later session
reads them rather than rediscovering them at cost.

**The plugin is installed from a local folder, not a public channel.** `known_marketplaces.json` on this
machine records the `flintcraft` marketplace with source type `directory`, pointing at the `throughliner/`
folder inside the No code method project. So the only version this install can reach is whatever that
folder holds. A session-start notice announcing a newer version on a stable channel is comparing against
something the install route does not read from, and the update commands correctly answer that there is
nothing to do. The cost of not knowing this was two app restarts on 2026-09-16, taken on the expectation
the notice set up. A session that has read this can say so before offering the update.

**The Gmail connector reaches the everyday account only.** Established on 2026-09-16 by searching it for
the D-U-N-S correspondence and finding no such thread: the connector is authenticated to Alex's ordinary
account, not to the separate business account. That is why forwarding, rather than reconfiguring the
connector, was the route taken in [duns-application-outcome]. Whether a second Google account can be added
to a Claude connector was not looked up and remains unknown rather than ruled out.

**Why this is a build rather than something written at planning.** A planning session may write the queue,
the spec, the log and the research folder; TOOLS.md is not among them.

Both facts are already reported to the method's own project, on 2026-09-16 and 2026-09-17 — this item is
about Taskflow keeping them where its own sessions will read them, which is a different job from the
plugin fixing anything.

Files:
- `TOOLS.md` — two entries: the local-marketplace fact with its consequence for update notices, and the
  Gmail connector's reach with the date it was established.

Observation: a read of `TOOLS.md` finds both facts, each naming the date it was established. The check
reaches the one file named above. No compile.

Rests on, read 2026-09-16 on this machine: `known_marketplaces.json`'s `flintcraft` entry with source type
`directory`, and `claude plugin update throughliner@flintcraft` answering that the installed version is
already the latest. And, the same day: a search of the connected Gmail account returning no D-U-N-S
correspondence. Both are facts about this machine's setup and stop being true if it is reconfigured.

Filed 2026-09-17 by the rescan, from two things this session learned rather than was told.
Filed 2026-09-17 15:53, stamped by the queue tool.

#### Remove the Thanks row from the side menu [remove-thanks-screen]

Deletes the "Thanks" entry from the bottom of Taskflow's side menu, and the placeholder screen it opens.

Why it exists, recovered by Alex on 2026-09-12 when nobody could say what the screen was for: the Thanks
screen dates from an earlier plan to fund Taskflow through "buy me a coffee" style donations, which was
replaced by the paid subscription tier. Its premise went with that change, and no record anywhere had
kept the connection — SPEC listed the row and the archived original spec named it only in its own title.
So the row survived as a menu entry with no stated purpose.

SPEC §Side menu and §Settings were corrected in the same planning run on 2026-09-12 and no longer name
Thanks. This item is what makes the app agree with them. Never announced to anyone — `INBOX/sent.md`
carries no mention of it — so nothing said publicly needs correcting.

**Refused: leaving the row in place.** It opens a placeholder, and SPEC no longer describes it, so it
would be a menu entry with no product truth behind it — the shape of thing the next person to read the
drawer would file as a defect.

Files:
- `app/src/main/java/com/example/taskflow/ui/navigation/AppDrawer.kt` — delete the `DrawerRow(text = "Thanks")`
  from the app-actions block.
- `app/src/main/java/com/example/taskflow/ui/navigation/Destination.kt` — delete the
  `data object Thanks : Overlay` declaration, and drop Thanks from the KDoc comment listing which
  destinations are still placeholders.
- `app/src/main/java/com/example/taskflow/ui/common/PlaceholderScreen.kt` — its KDoc comment reads
  "Help/Thanks/Report-a-bug 0022"; rewrite as "Help/Report-a-bug 0022".

Observation: on a device, opening the side menu shows Settings, Help and Report a bug pinned at the
bottom with no Thanks row, and nothing else in the drawer changes. The check reaches the three files
named above. Needs a compile, so it carries the Android Studio hand-over the project rules describe.

Rests on, read 2026-09-12 in the source: that `AppDrawer.kt` renders a drawer row labelled "Thanks"
wired to `Overlay.Thanks`, that `Destination.kt` declares that `Overlay`, and that a grep of `app/src`
for "Thanks" finds those two plus one comment in `PlaceholderScreen.kt` and nothing else.

Filed 2026-09-12 during planning, at the moment the screen was dropped from SPEC.
Filed 2026-09-12 21:06, stamped by the queue tool.

#### Every dated task reads a day early when Day begins at is after midday [tomorrow-task-lands-on-today-under-custom-boundary]

Stops the day-boundary adjustment being applied to stored task dates, which are already days rather than
moments in time.

**The mechanism, established by reading the source on 2026-09-17 — no device needed.** A task's date is
stored as **noon** on the chosen day: `EditTaskViewModel.noonEpoch` and `ScheduleViewModel.noonEpoch` both
write `date.atTime(12, 0)`. `SlotDeriver.logicalDate` then takes a stored moment and subtracts the
day-begins-at hour before reading off its day. Applied to noon with a boundary of 13, that subtraction
lands on the previous calendar day — so the stored date reads one day early. Applied to the shipped
default of 4, noon minus four hours is still the same day and nothing looks wrong.

**So the fault is general, and the reported symptom is one instance of it.** At any boundary later than
midday, every dated task shifts a day early on every Schedule surface: Tomorrow's onto Today, Soon's onto
Tomorrow, Later's onto Soon. Recurring tasks shift too, because `ScheduleViewModel.instancesOf` passes the
task's stored date through the same adjustment as its anchor, so every generated instance moves with it.

**The capture's diagnosis was the opposite of the truth, and the correction is recorded because it changes
what to fix.** It supposed the Schedule pages were deriving their slot from the calendar date while the
edit dialogue's date strip used the logical one, and named reading `SlotDeriver` as the next step. That
read shows both surfaces behaving consistently: the strip and the pages use the same function, and the
damage happens in the round trip through a noon anchor that is not far enough from an afternoon boundary
to survive the subtraction.

**What was unestablished in the capture and is now settled:** the fault is in neither the slot derivation
nor the add path alone but in the interpretation of a stored date; and yes, the same disagreement reaches
Soon and Later, and recurrence, for the same reason.

**Refused: storing dates anchored at the day-begins-at hour instead of noon.** It removes the problem too,
and it was rejected because every stored date would then have to be rewritten whenever the setting
changed — turning a display question into a data migration, on the database holding Alex's real tasks.

**Refused: widening the noon anchor to some later hour.** It would buy room rather than correctness, and
would fail again at whatever boundary someone eventually picks.

Files:
- `app/src/main/java/com/example/taskflow/domain/SlotDeriver.kt` — a function giving the day a **stored
  task date** represents, reading its calendar day with no boundary subtraction, since a stored date is
  already a day; `slotForDate` and `isBeforeToday` use it for the task side while continuing to use
  `logicalDate` for `nowMillis`. `logicalDate` itself is unchanged, because its other callers pass real
  moments — the clock, and `completedAt` — where the subtraction is correct.
- `app/src/main/java/com/example/taskflow/ui/schedule/ScheduleViewModel.kt` — `instancesOf` derives its
  recurrence anchor from the task's stored date with the new function rather than `logicalDate`.
- `app/src/test/java/com/example/taskflow/domain/SlotDeriverTest.kt` — cases at a boundary after midday:
  a task dated tomorrow lands on Tomorrow rather than Today, one dated today lands on Today, and one dated
  two days out lands on Soon; plus the existing default-boundary cases still passing.

Reads but does not change: `app/src/main/java/com/example/taskflow/ui/edit/EditTaskViewModel.kt`, whose
`noonEpoch` is the anchor being reasoned about and which is deliberately left as it is, per the refusal
above.

Nothing stored changes, so there is no migration and nothing on the phone is rewritten: existing dates are
already noon-anchored, and reading them as their own calendar day is what makes them correct.

Observation: the unit tests pass, including the new after-midday cases. On a device, with Day begins at
set to an afternoon hour, a task added from the Tomorrow page appears on Tomorrow and not on Today, and
the edit dialogue's date strip and the Schedule pages agree about which day is which. The check reaches
the three files named above, `SlotDeriverTest.kt` among them.

**It unblocks a cleared item.** [day-begins-at-rollover-still-unrun] cannot be driven until this ships:
its setup puts a task on Tomorrow with an afternoon boundary, and that task currently appears on Today
before the boundary arrives, leaving nothing to watch. This item is placed ahead of it in the cleared work
for that reason — an ordering, written here and in that item, rather than a `Blocked by:` line that would
hide the watch from the run.

Rests on, all read in the source on 2026-09-17: that both `noonEpoch` functions store `atTime(12, 0)`;
that `SlotDeriver.logicalDate` subtracts `dayStartHour` before taking the date; that `slotForDate` and
`isBeforeToday` pass a stored task date through it; that `instancesOf` does the same with its anchor; and
that the remaining callers pass the clock or `completedAt`, which are real moments.

Found on 2026-09-12 at 12:37 while setting up the [day-begins-at-rollover-still-unrun] watch. Filed
mid-run that day at 12:38, read from the clock; diagnosed and designed with Alex on 2026-09-17.
Filed 2026-09-17 14:26, stamped by the queue tool.

#### Subtasks in the edit dialogue have no affordance, so nobody finds them [subtask-affordance-in-edit-dialogue]

**Lifted above the line on 2026-09-12.** [verify-edit-outliner-fix] ran on a device that day and came back
clean on every claim: Enter opens an indented subtask, it survives a save and reopen, the parent renders
with an expand control, and completing the child completes the parent. So the behaviour this item's hint
would advertise is now behaviour somebody has watched work, which is the whole of what held it back.

**Its blocker was repointed on 2026-09-12, from [edit-outliner-missing] to the check that verifies it.**
That fix shipped on 2026-09-06 but compiles only — nobody has opened the app and tried it — so the
concern that holds this item back is untouched: a hint advertising a feature that does not work is worse
than the silence it replaces. The item left the queue on shipping, so this hold was pointing at nothing
and would never have lifted. [verify-edit-outliner-fix] is the on-device check, and this lifts when it
passes.

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

#### Single-pick subtasks on a repeating task, replacing the rotating roster [roster-as-subtasks]

Gives a repeating task an option to turn its subtask list into a single-pick list: instead of ticking
every subtask, you pick one, and that one completes that occasion of the parent. The app does no
rotation of its own.

**The design is Alex's, settled with her on 2026-09-16.** Her reasoning: the thing that rotates under a
repeating task is a subtask, not a name — "call family" with a person under it is one example, not the
shape — and the user should handle the rotation as they see fit rather than having the app decide whose
turn it is. Evenness is something she judges by reading her own history, not something the app maintains.

**This replaces the rotating roster that shipped on 2026-09-12** as [rotating-roster-recurrence], where
the task carries a free-text list of labels and the app advances through it by counting completions.
Under this design nothing advances, so that machinery has no job. **Refused: keeping both.** Two ways to
express the same intention, one of which quietly overrides the user's own choice, is worse than either
alone.

**What comes out, and what deliberately stays.** The roster text box goes from the edit dialogue and the
instance labelling that shows the next name goes with it — a field you meet every time you open a task is
the cost that matters, and Alex is about to use this app daily for a month. The `roster` **column stays**
on the task, unused, with its comment rewritten to say it is retired and why. **Refused: dropping the
column now** — dropping a column under Room means rebuilding the table and copying every row, and that
would run on the phone holding the only live copy of her real tasks. It can ride along with a later schema
change that has its own reason to exist.

**Where the pick is recorded.** Each repeating task already stores a plain comma-separated list of the
dates its occasions were completed. That encoding widens so an entry may optionally carry the id of the
subtask picked with it — `2026-09-11:47` beside a bare `2026-09-15`. A bare date keeps meaning exactly
what is true of every occasion completed before this ships: done, with no pick recorded. **Refused: a
separate table of completed occasions.** It is the tidier answer and the one to reach for if this were
queried heavily, but it costs a schema version, a migration copying existing completions across, and a
rewrite of every read site, to serve a query nothing performs yet. It can be promoted later with the data
already captured.

**The migration this adds is proved by [run-migration-test-on-emulator]**, which sits after this item and
the two that follow it, and runs the whole instrumented suite on the emulator once — covering the 6 → 7
case this item adds as well as the 5 → 6 case that has never been run. That is an ordering written in both
items rather than a `Blocked by:` line.

**There is still one schema version bump, and it is the safe kind.** The single-pick mode is a new flag on
the task, which is an added column — a one-line `ALTER TABLE ADD COLUMN`, the same additive shape as the
5 → 6 migration. That is a different operation from the column *drop* refused above, which is why one is
taken and the other is not.

**Reading the picks back is NOT this item.** Completed occasions of a repeating task surface only in the
Completed tray at the bottom of Today, on the day they are completed, and the tray clears at the next
day-begins-at rollover — read in the source on 2026-09-16. So nothing lists past occasions anywhere. This
item makes the pick recordable; [recurring-completions-in-history] is what makes it readable back, and
that is where Alex's searchable history lives. **That item sits immediately after this one and must be
built after it**, since it displays picks this item records; both are Kotlin, so one run and one compile
can take them in order. Written as an ordering rather than a `Blocked by:` line, which would hide it from
the run that should build it.

Files:
- `app/src/main/java/com/example/taskflow/data/model/Task.kt` — a `pickOne` column defaulting to false;
  `completedInstances` parsing widened to accept `date` or `date:childId`; `withInstanceCompletion`
  extended to carry the picked subtask; an accessor giving the picked subtask for a completed date; the
  `roster` comment rewritten as retired.
- `app/src/main/java/com/example/taskflow/data/local/TaskflowDatabase.kt` — schema 6 → 7 with
  `MIGRATION_6_7` adding the new column.
- `app/src/main/java/com/example/taskflow/data/repository/TaskRepository.kt` — `setInstanceCompleted`
  carries the picked subtask through.
- `app/src/main/java/com/example/taskflow/ui/edit/EditTaskScreen.kt` — the roster text field removed; a
  single-pick option added, shown only alongside a repeat rule.
- `app/src/main/java/com/example/taskflow/ui/edit/EditTaskViewModel.kt` — roster form state dropped, the
  single-pick flag added, and dropping the repeat rule drops single-pick with it as it already drops the
  roster.
- `app/src/main/java/com/example/taskflow/ui/schedule/ScheduleViewModel.kt` — the roster labelling removed
  from `instancesOf`; a single-pick parent's children rendered as single-select, and picking one completing
  that occasion rather than waiting for all of them.
- `app/src/main/java/com/example/taskflow/ui/schedule/SlotPage.kt` and
  `app/src/main/java/com/example/taskflow/ui/schedule/LaterPage.kt` — the single-select affordance in place
  of a checkbox on those children.
- `app/src/main/java/com/example/taskflow/data/transfer/TaskflowJson.kt` — the new flag carried in export
  and import; `roster` left exactly as it is.
- `app/src/androidTest/java/com/example/taskflow/MigrationTest.kt` — a 6 → 7 case.

Observation: on a device, opening a repeating task that has subtasks shows a single-pick option, and a
task with no repeat rule shows none; turning it on makes that task's subtasks render as single-select on
Today; picking one sends that occasion to the Completed tray and leaves the others alone; the roster text
box is gone from the dialogue and no instance shows a roster name; and the migration suite's 6 → 7 case
passes. The check reaches the nine files named above, `MigrationTest.kt` among them. Needs a compile, so
it carries the Android Studio hand-over the project rules describe.

Refused: the app choosing whose turn it is. That was the shipped roster's whole behaviour, and Alex's
decision on 2026-09-16 is that the user handles the rotation as they see fit.

Rests on, all read in the source on 2026-09-16: that `Task.completedInstances` is a comma-separated list
of dates parsed in `Task.kt`; that `TaskRepository.setInstanceCompleted` is the one writer of it; that
`ScheduleViewModel.instancesOf` performs the roster labelling; that `EditTaskScreen.kt` renders the roster
as a text field shown alongside a repeat rule; and that completed recurring occasions appear only in
today's Completed tray. And, on the shipped roster itself: none of [rotating-roster-recurrence] has been
watched running on a device, so this reworks behaviour nobody has yet seen — which is also why the roster
limb of [verify-2026-09-12-code-on-device] has nothing left to prove.

Filed 2026-09-16 during planning, designed with Alex in the same session.
Filed 2026-09-16 10:32, stamped by the queue tool.

#### Completed occasions of a repeating task appear in history as their own rows [recurring-completions-in-history]

Makes a completed occasion of a repeating task show up on the surfaces that already list completed work,
on the day it was completed, so past occasions can be read back instead of disappearing overnight.

**The gap, read in the source on 2026-09-16.** A completed occasion appears in the Completed tray at the
bottom of Today, and only on the day it was completed; the tray clears at the next day-begins-at rollover
(SPEC §Completed task tray on Today). After that no screen shows the occasion happened. Search, the
day-detail card and the Yesterday page all miss it, and all three for the same structural reason:
`SearchViewModel`, `DayCardViewModel` and `YesterdayViewModel` each select tasks where `isCompleted` is
true and `completedAt` is not null, then group by the logical date of `completedAt`. A recurring occasion
is neither — the parent task is not complete, because it repeats, and the occasion is a date recorded in
the parent's `completedInstances`. So none of the three is behaving wrongly; the shape of the data simply
does not reach them.

**Why it matters, in Alex's own terms.** Her design for single-pick subtasks deliberately puts no rotation
logic in the app: she picks whichever subtask suits each occasion, and judges evenness by reading her own
history back — searching the parent task's name and seeing each past occasion with the subtask she picked.
[roster-as-subtasks] makes that pick recordable. Without this, the picks are written and never seen.

**Refused: a surface of its own for repeating tasks.** It was the open question when this was split out,
and reading the three surfaces settled it. SPEC §Search and completed history puts active and completed
work in one box on the stated ground that two boxes make the user guess which to open; a third list for
occasions would reintroduce exactly that guess for the one kind of task whose history Alex most wants to
read.

**Refused: fixing each of the three view models separately.** They ask the same question in three places,
so three fixes would drift apart the first time one was touched.

**Ordering, not a hold.** It sits immediately after [roster-as-subtasks] in the cleared work rather than
being held against it: both are Kotlin, one compile covers both, so a single run can do them in order —
and this item is useless on its own, since until that one ships there are no picks to show. Written as
placement and this sentence rather than a `Blocked by:` line, which would hide the entry from the run
that should build it.

Files:
- new `app/src/main/java/com/example/taskflow/ui/history/CompletedOccasions.kt` — the one shared
  derivation: given the tasks, produce a history row per completed occasion, carrying the parent's title,
  the occasion's date as its completion day, and the picked subtask's label where the task is single-pick
  and a pick was recorded.
- `app/src/main/java/com/example/taskflow/ui/history/SearchViewModel.kt` — fold those rows into the
  completed list before grouping by day, so one row appears per occasion rather than one per task; and
  extend the row model it defines to carry the picked subtask's label.
- `app/src/main/java/com/example/taskflow/ui/history/DayCardViewModel.kt` — the same rows folded into the
  day's list, so a day card shows the occasions completed that day.
- `app/src/main/java/com/example/taskflow/ui/history/YesterdayViewModel.kt` — the same, for the Yesterday
  page.
- `app/src/main/java/com/example/taskflow/ui/history/SearchScreen.kt` and
  `app/src/main/java/com/example/taskflow/ui/history/DayCardLayer.kt` — render the picked subtask beneath
  the task's title where a row carries one.

Reads but does not change: `app/src/main/java/com/example/taskflow/data/model/Task.kt`, for the recorded
occasions and their picks, and `SlotDeriver.logicalDate`, which is how all three surfaces already decide
which day a completion belongs to.

Observation: on a device, complete an occasion of a repeating task, pass the day-begins-at rollover, then
search that task's name — the occasion is listed under its own day rather than absent, with the picked
subtask shown beneath the title; opening that day as a card shows it there too, and un-completing it from
the card removes it. A second occasion on a different day produces a second row rather than replacing the
first. The check reaches the six files named above.

Rests on, read in the source on 2026-09-16: that `SearchViewModel`, `DayCardViewModel` and
`YesterdayViewModel` each filter on `isCompleted` and a non-null `completedAt` and group by
`SlotDeriver.logicalDate`; and that a recurring occasion is held as a date in the parent's
`completedInstances` rather than as a row. And on [roster-as-subtasks] for the picked-subtask half, which
records the pick this item displays.

Filed 2026-09-16 during planning, split out of [roster-as-subtasks] at the moment the gap was found, and
designed out with Alex on 2026-09-17.
Filed 2026-09-17 13:04, stamped by the queue tool.

#### Strategy paragraphs are typed through the database, so fast input loses characters [strategy-paragraph-input-loses-characters]

Makes the Strategy page's paragraph box hold its own text while being typed into, instead of showing
whatever the database last handed back.

**The mechanism, read in the source on 2026-09-17.** `StrategyScreen.kt` renders each paragraph as an
`OutlinedTextField` whose `value` is `section.description` — a value fed from the flow the database emits
— and whose `onValueChange` calls `StrategyViewModel.setDescription`. That function launches a coroutine
which reads the existing row and upserts it. So the field displays a value that is one asynchronous round
trip behind the typing, and every keystroke starts an independent read-modify-write with no ordering
between them. Two consequences follow directly: a keystroke arriving before the previous round trip
returns is appended to the stale value, losing the character before it; and two overlapping coroutines can
interleave so the later write carries the earlier read. That is a lost-update race, and it is the only
text field in the app built this way — the edit dialogue's outliner holds its text locally and writes once
on Save, which is why it cannot do this.

**The capture's own reasoning was partly wrong, and the correction is recorded because it changes what the
evidence proves.** It treated the *substituted* character — `l` arriving as `m` while typing `Stayclose`
at a 0.35-second spacing — as the strong evidence, on the ground that silent dropping is a known
`adb shell input text` behaviour recorded in TOOLS.md but substitution is not. That is backwards. A
lost-update race drops and duplicates text; it does not transform one letter into a different letter. A
keycode delivered one off from the intended one does exactly that, and the two letters are believed to sit
on adjacent keycodes — unverified, and it does not bear on the fix. So the substitution is most likely an
adb artifact, and what the code review establishes is the dropping.

**Not established, and it stays unestablished after this build:** whether a person typing at human speed on
this device would hit the race. Nobody has tried, and a 0.4-second spacing was observed to work, which is
slower than most people type. The fix is taken anyway, on two grounds the item already carried: the race
is real in the code whatever anyone has reproduced by thumb, and the field performs a database read and a
write on every keypress for no benefit.

**Refused: saving only when the box loses focus.** It is the simplest way to remove the race, and it would
quietly undo something already proved — the [strategy-edit-persistence-blocked] audit on 2026-09-12
confirmed that text in this box survives a force-stop and relaunch exactly as written, and it survives
because of the per-keystroke write this item is removing. Saving on a pause as well as on focus loss keeps
that durability within a second or so rather than trading it away.

**The pause length is a tunable constant, not a design decision left open.** Take **500 ms** of no typing
as the trigger. It was not derived from measurement — it is the common debounce figure for text input, and
it is chosen so a force-stop loses at most that much. What would settle it properly is Alex using the box
for real prose during [first-end-to-end-test] and saying whether saving feels late; it is revisable once
seen.

Files:
- `app/src/main/java/com/example/taskflow/ui/strategy/StrategyScreen.kt` — the paragraph field holds its
  text in local state, seeded from `section.description` and re-seeded when that changes for a reason
  other than this field's own typing, so what the user typed is what the box shows immediately.
- `app/src/main/java/com/example/taskflow/ui/strategy/StrategyViewModel.kt` — `setDescription` is called
  after 500 ms of no typing and when the field loses focus, rather than on every keystroke; the pending
  save for a paragraph replaces any earlier pending save for that same paragraph, so the writes cannot
  overlap.

Observation: on a device, typing a full sentence into a Project's paragraph at speed leaves exactly that
sentence in the box, with no characters missing; leaving the box and returning shows the same sentence;
and force-stopping the app more than a second after the last keystroke, then relaunching, still shows it.
The check reaches the two files named above.

Rests on, read 2026-09-17 in the source: that `StrategyScreen.kt` binds the field's `value` to
`section.description` and its `onValueChange` to `setDescription`, and that `setDescription` launches a
coroutine doing a repository read followed by an upsert. And, from the 2026-09-12 audit recorded in LOG:
that text already in the box survives a force-stop and relaunch.

From the [strategy-edit-persistence-blocked] audit on 2026-09-12. Filed mid-run that day at 12:33, read
from the clock; reviewed and designed with Alex on 2026-09-17.
Filed 2026-09-17 13:59, stamped by the queue tool.

#### [audit] Run the whole instrumented suite on the emulator, covering both schema upgrades [run-migration-test-on-emulator]

Runs Taskflow's instrumented tests against the emulator and reports what they say. Its point is the
migration cases: the proof that a database carrying real tasks survives a schema upgrade, which is what
the version-5 floor exists for.

**Why it runs on the emulator and not the phone.** A connected instrumentation run installs the app, runs
the tests, then uninstalls both — taking the Room database with it. The phone holds Alex's real tasks, and
on AGP 9.2.1 no build setting prevents the uninstall; the CLAUDE.md rule requiring a JSON export first was
the holding measure. The AVD `Pixel_6` was created on 2026-09-12 by [emulator-for-instrumented-tests] and
boots, which removes the reason to hold back — running the suite there destroys nothing anyone wants.
TOOLS.md records the AVD, and records that with both devices attached the target has to be named
explicitly, since `adb` refuses an ambiguous command when two are connected.

**Placed after [roster-as-subtasks], deliberately, and the reasoning is what changed this item on
2026-09-17.** As filed it was about the 5 → 6 case that shipped with [rotating-roster-recurrence] on
2026-09-12. But that upgrade has already happened to Alex's own data: the build installed on her phone
that day carries schema 6, so her real database went through it untested, and it evidently survived —
she has used the app since. Running the suite now would confirm that after the fact. Meanwhile
[roster-as-subtasks] adds a schema 7 with its own upgrade step and its own test case, and that is the
upgrade still ahead of her phone. One run after that lands proves both, and proves the one that can still
cost her something. Written as placement plus this paragraph rather than a `Blocked by:` line, which would
hide the item from the run that should reach it.

**Re-flavoured `[audit]` on 2026-09-17.** It was filed as a build and changes no files: it runs a command
and reports what came back, which is a review pass. Its output is a statement of which tests passed, plus
a capture for any that did not.

**It needs a Gradle run, which Claude's own shell cannot perform on this machine** — the loopback failure
recorded in TOOLS.md. So this follows the project's standing compile rule: the run stops, hands Alex the
three typed lines to paste into Android Studio's integrated terminal, and waits for the result before the
item is ticked. The command is the instrumented suite rather than `assembleDebug`, and it names the
emulator as its target.

Worth running the whole `androidTest` suite rather than only the migration cases: it was last run in full
on 2026-09-05 at schema version 5, and the roster column touches `Task`, which most of the DAO tests
exercise. Two schema versions have landed since that last full run.

Report: which tests passed and which failed, with both migration cases named individually — 5 → 6 and
6 → 7. A failure is a finding and goes back as a capture rather than being fixed here. Record the outcomes
in TEST-LOG.md as the project rules require, whichever way they go.

Rests on: that the AVD `Pixel_6` exists and boots, recorded in TOOLS.md on 2026-09-12; that Claude's shell
cannot run `gradlew` here, recorded in TOOLS.md; that a connected instrumented run uninstalls the app and
its database on AGP 9.2.1, recorded in TOOLS.md; and that [roster-as-subtasks] will add the 6 → 7 case
this item expects to find — if that item ships without it, this one reports the absence rather than
inventing it.

Filed at the close of the 2026-09-12 /next run, at 13:14, read from the clock; reshaped with Alex on
2026-09-17.
Filed 2026-09-17 15:34, stamped by the queue tool.

#### [audit] Watch three of the 2026-09-12 code changes actually run on a device [verify-2026-09-12-code-on-device]

Drives Alex's phone over adb and checks three changes that were built on 2026-09-12 and compiled, and
that nobody has yet seen working: dragging a Project heading to reorder it in the Strategy doc
[project-reorder-strategy], reaching the drag targets without the page turning underneath
[drag-eaten-by-page-swipe], and the day-detail card opened from a search result [nav-day-card-layer].
Each item's own record ticks it `UNCONFIRMED` and names what is unrun.

Filed because nothing else was tracking those checks — the same shape of problem [verify-edit-outliner-fix]
existed to fix, where work shipped, compiled, and then sat unverified with no item naming the
verification. Three held items are waiting behind two of these three: [project-delete-later] and
[bin-drag-target-check] behind the drag-target fix, [share-a-day] behind the card layer. None can honestly
be released on a compile alone, which is what makes this the item that unblocks the most.

**Cut from four checks to three on 2026-09-16, with Alex.** The fourth was the rotating roster advancing
on completion, shipped as [rotating-roster-recurrence]. She replaced that design in the same session:
[roster-as-subtasks] removes the rotation entirely in favour of single-pick subtasks the user chooses
herself, and SPEC §Recurring tasks was rewritten to match. **What that overturns** is this item's own
reasoning that all four deserved one sitting — proving that a roster advances correctly is phone time
spent on behaviour already on its way out. The other three are untouched by that decision.

Claude drives every step over adb, as it drove the two audits on 2026-09-12. What is needed from Alex is
the phone unlocked and connected; it re-locks after a few minutes (TOOLS.md), so possibly more than once.
The build installed on the phone on 2026-09-12 already carries all three changes, so no fresh install is
needed unless the code moves on first.

Splitting this into separate items was considered and refused: they are one sitting with one phone, and
separate items would mean separate connect-and-unlock cycles for checks that take minutes each.

Steps:

1. Open the Strategy doc and drag a Project heading to a different position. Look for: the heading moving
   under the drag, and the sections resettling in the new order.
2. Close Taskflow completely, reopen it and return to the Strategy doc. Look for: the new order still
   there.
3. Swipe to Later. Look for: the Project cards in the Strategy doc's order, with Unassigned still pinned
   at the bottom.
4. On Today, press and hold a task and drag it upward into the row of target icons. Look for: the page
   staying exactly where it is, and the icon under the finger highlighting.
5. Still holding, move the task sideways below the icon row. Look for: the page turning, the way it does
   for an ordinary drag-to-reschedule.
6. Open Search and tap a completed task in the results. Look for: that day opening as a card in front of
   the page.
7. Swipe left and right on the card. Look for: moving between days that hold completions, and days with
   nothing completed being skipped rather than shown empty.
8. Tap a task on the card, edit it, then use the back gesture. Look for: the edit taking, and back closing
   the card and returning to Search.

Report: which of the three changes behaved as its own item's Observation line describes, and which did
not. A mismatch is a finding and goes back as a capture rather than being fixed here. Record the outcomes
in TEST-LOG.md as the project rules require, whichever way each goes.

Rests on, read 2026-09-12 in each item's own record: that all three changes are in the build installed on
the phone, and that each is ticked `UNCONFIRMED`. And, on the dropped fourth check, the decision recorded
in [roster-as-subtasks] on 2026-09-16.

Filed at the close of the 2026-09-12 /next run, at 13:14, read from the clock.
Filed 2026-09-16 10:35, stamped by the queue tool.

#### [user] Watch a Tomorrow task roll into Today at the day boundary [day-begins-at-rollover-still-unrun]

Watching a Tomorrow task cross the day boundary onto Today, and checking it arrives unlabelled and
unmoved. Came out of the [verify-run-2026-08-31-remainder] audit on 2026-09-06 and was weighed on
2026-09-07.

The one check that audit could not run. SPEC §Schedule view says that at the day-begins-at boundary
Tomorrow's tasks roll into Today with no label, no reordering and no shame, and that Today's uncompleted
tasks stay where the user put them. The picker was confirmed present at its 4:00 AM default on 2026-09-05
and again on 2026-09-06; what nobody has watched is the rollover itself.

Why it did not run this time. The check needs the boundary set a short way ahead so the clock crosses it
while somebody watches. The setting takes whole hours only — settled as deliberate on 2026-09-07 and
written into SPEC §Settings → Day begins at, so this is a fact to plan around rather than something that
may change — and the shortest wait available is therefore up to sixty minutes — a wait in the middle of a run, on the user's phone,
holding the only session there is. It is `[user]` work for that reason rather than because Claude cannot
perform the steps: a session can drive every one of them, but not the waiting — and not the judging
either, since whether the task arrived unlabelled and unmoved needs eyes on the screen.

**A session can do the setting-up half on your say-so.** Steps 2 to 5 are ordinary adb work — changing
the setting, adding the task, reading Today's order back. If a session is open when you start, ask it to
do those and keep only the looking for yourself. Not split into its own item because the setup and the
watch are one sitting: separating them would mean two sessions coordinated minutes apart for the sake of
five taps.

**Do not drive this until [tomorrow-task-lands-on-today-under-custom-boundary] has shipped, and it sits
immediately ahead of this item for that reason.** A drive on 2026-09-12 halted at step 4: with the
boundary set to an afternoon hour, the test task added from the Tomorrow page appeared on the Today page
straight away, so there was nothing left to cross the boundary. That was diagnosed on 2026-09-17 as a
general fault — a stored task date is noon-anchored and the day-boundary adjustment is applied to it, so
at any boundary after midday every dated task reads a day early. Steps 4 to 7 below cannot mean anything
until that is fixed. Written as an ordering rather than a `Blocked by:` line so this item stays visible to
the run that will reach both.

Walkthrough:

1. Note the time on your phone, then work out the next whole hour that is at least ten minutes away —
   if it is 2:15, that is 3:00. Look for: one specific hour you are going to wait for.
2. Open the side menu with the ☰ button, tap Settings, and tap the row under Day begins at showing
   4:00 AM. Look for: a dropdown list of whole hours.
3. Choose the hour from step 1, then go back. Look for: the Day begins at row now showing that hour.
4. Swipe to Tomorrow and add a task there called "rollover-test". Look for: it sitting on Tomorrow.
5. Swipe to Today and note what is already there and in what order. Look for: your existing tasks, in
   an order you could recognise again.
6. Wait until the phone's clock has passed the hour you chose, then open Taskflow on Today.
   Look for: "rollover-test" now on Today.
7. Check three things about it: that it carries no label of any kind marking it as moved or late, that
   it did not jump to the top, and that the tasks noted in step 5 are still in the order you saw them.
   Look for: all three true. Any one of them false is the finding.
8. Tick "rollover-test" off. Look for: it gone from the list and sitting in the greyed Completed tray at
   the bottom of Today.
9. Set Day begins at back to 4:00 AM. Look for: the row reading 4:00 AM again.
10. Tell a planning session what you saw at step 7.

Observable: "rollover-test" present on Today after the boundary, unlabelled and not reordered. A session
can read the task's presence off the device; the no-label and no-reorder halves are the user's eyes.

**Completing the test task is how it is cleaned up, decided 2026-09-07.** Deleting a task is a drag onto
the bin target, and that gesture does not work at all until [drag-eaten-by-page-swipe] ships — so no hand
can delete it today. Completing needs no drag, and the Completed tray empties itself at the next
day-begins-at rollover (SPEC §Completed task tray on Today), so the test disposes of its own litter and
this item does not have to wait on the drag fix. **Refused: the earlier step 9**, which told the user to
report the task as needing deletion and blamed [bin-drag-target-check] — that reasoning rested on the
retired belief that the failure was a limit of driving a device over adb.

Rests on, read 2026-09-06 on the phone: that Day begins at is a whole-hour dropdown defaulting to 4:00 AM,
and that a task added from Tomorrow is dated tomorrow (SPEC §Add a new task). And, read 2026-09-07 in
SPEC: that the Completed tray clears at the day-begins-at rollover.

Filed 2026-09-06, 11:37, mid-run, read from the device clock.

#### [user] First end-to-end test of Taskflow on a device [first-end-to-end-test]

**Lifted above the line on 2026-09-12.** [verify-edit-outliner-fix] ran on a device that day and came back
clean: Enter opens an indented subtask, it survives a save and reopen, and completing the child completes
the parent. Alex's condition was that subtasks *work*, and that is now a watched fact rather than a
compile, so the condition is met.

**Its blocker was repointed on 2026-09-12, from [edit-outliner-missing] to the check that verifies it.**
Alex's condition was that subtasks *work*, and the fix shipped on 2026-09-06 compiles without anyone
having opened the app and tried it — so shipping alone does not meet the condition she set. The item
also left the queue when it shipped, leaving this hold pointing at nothing. [verify-edit-outliner-fix]
is the on-device check, and this lifts when it passes. Raised on 2026-09-12 by Alex asking whether the
app is yet safe to use for real, which is the question this item exists to answer.

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

#### [user] Apply 0003_grants.sql to the live Supabase project and prove a signed-in user can read their own rows [supabase-apply-0003-grants]

Runs one SQL file against Taskflow's live cloud database, then checks that a signed-in user can actually
read their own rows — which is the thing the whole paid tier rests on and which nothing has yet
demonstrated.

`supabase/migrations/0003_grants.sql` was written on 2026-09-12 by the build of
[cloud-schema-missing-grants]. The file exists; nothing has run it against the live project. The grants it
contains are already on the live database, applied by hand during the 2026-09-06 drive, so running the
file proves the repository and the project agree rather than changing anything. `grant` is idempotent, so
running it twice is safe and a clean second run is itself the evidence.

**It is `[user]` work, and the capability check on 2026-09-17 found no way round that.** There is no
Supabase command-line tool installed on this machine, no `psql`, no link between this folder and the live
project, and no Supabase access recorded in `TOOLS.md`. Every route would need Alex's credentials, which
Claude does not handle. This is genuine incapability rather than a rule, so there is no say-so that would
move it.

**Two facts from `workshop/resources/research/supabase-rls-and-edge-function-identity.md` are written into
the steps below rather than left to be fetched**, because a walkthrough that sends the reader off to
assemble SQL is three jobs wearing one step's clothes. First: the SQL editor connects as an administrator
that bypasses row protection entirely, so an ordinary query there returns every row whatever the policies
say — it can tell a working policy from a broken one no better than a coin. Second: `42501: permission
denied` means the **grants** are missing, not that a policy denied the read; Postgres checks the privilege
gate before it consults any policy, and reading a `42501` as a policy working records a pass that was
never earned. That is not hypothetical — it is how the missing grants were found on 2026-09-06.

**The SQL below is the form that finding verified on a real run**, rather than a tidier one nobody has
executed.

Walkthrough:

1. Open the Taskflow project in the Supabase dashboard and go to the SQL editor. Look for: a blank query
   pane with the project's name in the header.
2. Open `supabase/migrations/0003_grants.sql`, paste its whole contents into the pane and run it. Look
   for: success, and no error.
3. Run exactly the same thing a second time without changing anything. Look for: success again — that is
   the step's whole purpose, since a second clean run is what proves the file is safe to re-apply.
4. In a new query, get a real account's identifier by running `select id from auth.users limit 1;`. Look
   for: one identifier. **If it returns nothing, the project has no accounts yet** — stop here, say so,
   and the rest waits until someone has signed up.
5. In a new query, paste the block below, replacing the placeholder with the identifier from step 4, and
   run all four lines together. Look for: rows belonging to that account, and specifically **not**
   `ERROR: 42501: permission denied for table tasks`.

   ```
   set local role authenticated;
   set local request.jwt.claim.sub = 'PASTE-THE-IDENTIFIER-HERE';
   select * from public.tasks;
   ```

6. Run the same block three more times, changing only the last line to select from `public.projects`, then
   `public.strategy_entries`, then `public.life_areas`. Look for: rows rather than a `42501` on each.
7. Tell a planning session what each step returned, including any `42501` and which table it named.

Observable: a select as the `authenticated` role against each of the four tables returns rows rather than
`42501`. Where step 4 found no accounts, the observable is instead that finding, reported — which is a
real outcome and not a failure to complete the item.

Rests on, read 2026-09-17: that no Supabase CLI or `psql` is installed and nothing links this folder to
the live project. And, recorded in `workshop/resources/research/supabase-rls-and-edge-function-identity.md`
from a real run on 2026-09-06: that the SQL editor's role bypasses row protection, that the impersonation
block above is what makes policies evaluate as they would for a real request, and that `42501` is a
privilege failure rather than a policy denial. That file's own frame assessment marks this subject as
amended on a cycle.

Filed mid-run on 2026-09-12 at 12:09, read from the clock, by the build of [cloud-schema-missing-grants] —
that item's own observation names a live-database check, and no item was tracking it. Walkthrough rewritten
on 2026-09-17 to carry the SQL rather than point at it.
Filed 2026-09-17 13:07, stamped by the queue tool.

--- Cleared to run above this line ---

#### Free-tier delete a Project on Later [project-delete-later]
Blocked by: [drag-eaten-by-page-swipe]

**Re-held on 2026-09-06, because the sentence that cleared it was wrong.** It was lifted on 2026-09-05
on the argument that the drag gesture it needs "exists and has been driven on a real phone", citing
[task-reorder-within-list] and two TEST-LOG rows from the 2026-09-05 device audit. That argument does
not survive: what those rows proved is a *vertical* drag reordering within a list, while this item needs
delivery of a card *onto a target* — a different motion, and the one that fails. On 2026-09-06 Alex
long-pressed a task on her own phone and the page navigated instead of the task dragging. Row 043,
cited above as evidence of a drag to the target row, is in doubt for the same reason.

The gesture question is settled rather than open: it is a known defect with a designed fix,
[drag-eaten-by-page-swipe], which this now waits on. The design of this item is untouched by that —
long-press-and-drag-to-a-target is SPEC's deletion gesture throughout, and what failed was the target
row sharing an axis with the page turn, not the route to deletion. Reconsidering how a Project is
deleted was weighed on 2026-09-06 and refused on that ground: it would be redesigning around a bug that
is already being fixed.

**It needs designing before it can be built, separately from the hold.** It carries no file list and
does not say what changes inside any file, so lifting it once the drag fix ships is not enough on its
own — the design work comes first. That was deliberately left until then on 2026-09-06: where the
drag-target row ends up affects what this item's own file list has to name, and its sibling
[project-reorder-strategy] was designed the same day because nothing about it waits on the drag.

Free tier — long-press a Later Project card and drag it to a delete target in the upper-right, the same gesture used to delete a task. Deleting a Project does not delete its tasks: they reassign to the system Unassigned Project (reassign logic shipped in [unassigned-project-model]). SPEC §Create or delete a Project describes this. Carved from the retired [project-lifecycle-later] during planning on 2026-06-22 — its create half was promoted to [project-create-picker-ui], its three remaining pieces split out by dependency.

What actually holds it is the general drag + bin/delete drag-target gesture (SPEC §Drag-target icons), which doesn't exist anywhere in the app yet (confirmed 2026-06-22). Any of several items could deliver it — [task-reorder-within-list] builds the first drag primitive, and [0008-drag-task-between-schedule-screens], [0010-outliner-typing-drag-target-icons] or [0011-cut-and-paste-os-clipboard] would each bring the bin target. It is held against [task-reorder-within-list] because that is the one sitting nearest the top; if the bin gesture arrives by another route first, this lifts then instead. Build order doesn't matter — only that the bin gesture exists.

#### [user] Check the bin drag target deletes a task [bin-drag-target-check]
Blocked by: [drag-eaten-by-page-swipe]

**Held below the line on 2026-09-06.** Its drive on 2026-09-06 halted at step 2: the targets appeared but
the page turned instead of the task dragging, so no target was reachable. That is
[drag-eaten-by-page-swipe], and until it ships this walkthrough cannot get past its second step — a /next
run reaching it would stop in the same place a second time. Step 2's look-for should say the page must
stay put as well as that the targets appear, when this is next driven.

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
registration work itself belongs to the flintcraft.tech project and is not duplicated here. **The D-U-N-S number is no longer the long pole, established on 2026-09-16 by reading the provider's
reply.** One was issued on 2026-05-17 and the provider closed the case, and the further wait before the
number reaches Google has also passed. So step 1 below is already done — but not usefully, and the reason
is the standing condition on this whole item.

**Standing condition, and it governs every route this item could take: the Play account is not opened on
any identity whose published address is Alex's home, or the former address her existing registration
carries.** The payments profile's legal name and address are printed on every subscriber's receipt, which
is the entire reason an organization account was chosen over a personal one on 2026-09-03. The D-U-N-S
already issued was obtained as a **sole trader**, and a sole trader is not a separate legal identity: its
registered name is her own name and its registered address is whatever the registration carries. That
address is a former one she no longer occupies and is not free to publish, so it fails on its own terms —
before Google's policy on sole traders is even reached, and proof of physical address could not be
supplied for it either.

**What that means for this item.** The existing D-U-N-S number is not usable as it stands. Opening the
account waits on [business-registration-for-play-account] producing an identity whose published name and
address Alex is content to have on every receipt — a registered company, or a genuine business address
that is not a mail-forwarding service, which Google does not accept. Whether a fresh D-U-N-S would then be
needed against that identity is a read to do at the time, not now.

**Refused, and these are the two that look reasonable: using the sole-trader D-U-N-S because it already
exists**, which publishes exactly what the account type was chosen to avoid; **and treating "does Google
accept a sole trader as an organization?" as the open question**, which it is not — a yes to it would not
change what gets printed.

Red flag · State: cleared — designed out on 2026-09-16 by writing the condition above into this item,
rather than accepted. It is cleared here rather than carried on a separate entry because this item's own
walkthrough is what would breach it.

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

#### Help and Report-a-bug screen content [0022-help-thanks-report-a-bug-content]
Blocked by: [help-thanks-report-content]

**The Thanks screen was dropped from this item on 2026-09-12**, overturning the three-screen scope this
item has carried since it was filed. Alex recovered what nobody had written down: the Thanks screen came
from an earlier plan to fund Taskflow through "buy me a coffee" style donations, replaced since by the
paid subscription tier, so its premise went with that change. SPEC §Side menu and §Settings were
corrected in the same planning run and no longer name it, and [remove-thanks-screen] takes the row out
of the app. What that overturns: the reasoning that had the Thanks paragraph's wording as one of this
item's three owed pieces, and the assumption — never examined until then — that a screen named in SPEC
must have a purpose behind it. The slug keeps the word "thanks" because slugs are stable through a
rename.

Held below the line on 2026-09-03, with your agreement, for the reason the item's own text already gave: the words for the screens come from [help-thanks-report-content], which is still an unprocessed capture. Nothing else about it is unready.

Bottom-of-drawer screen content including help and custom instructions. Full original spec: `archive/backlog-specs/0022-help-thanks-report-a-bug-content.md`.

**Two of the screens' words were settled on 2026-09-12 and are written below**, folded in from
[help-thanks-report-content] rather than left to be fetched from it. That capture had been set aside
three times over for topics that cannot be written yet, and the writable parts were going round with
them; it now carries only what is genuinely undecided. Alex can change any of this wording.

*Report a bug — the whole screen:*

> Found something wrong? Email **bugs@flintcraft.tech** and tell us what happened — what you were
> doing, what you expected, and what the app did instead. A screenshot helps if you have one.
> There's no form to fill in and no account to make.

The address was settled on 2026-08-31 as a dedicated email rather than a web form or a GitHub issue,
and created and tested on 2026-09-05. **Refused: pointing users at flintcraft.tech/report** — that
page exists and lists TaskFlow, but it carries a note telling non-automated visitors not to complete
it, and Taskflow's users are mostly non-technical people with no Claude in the loop.

*Help — the "tasks dated before today" topic:*

> Tasks dated before today stay on Today, in the order you placed them, until you do them or move
> them. The date on the right is the only thing that changes — nothing is highlighted, counted, or
> moved to the top.

Written without naming the behaviour as a category, which SPEC §Tasks dated before today requires:
the app has no "overdue" label anywhere, and the help text must not invent one.

Help's other two topics — Claude setup, and the suggested custom-instruction text — are still to come
from [help-thanks-report-content].

--- Build block ---
Changes: fill the two remaining bottom-of-drawer screens — Help and Report a bug — with real content. Help covers MCP setup, the production version of the suggested custom-instruction text, and the "tasks dated before today stay on Today" behaviour described without naming the category. The words themselves come from [help-thanks-report-content], which waits on [custom-instruction-production-text] and the MCP setup design. Full original spec: `archive/backlog-specs/0022-help-thanks-report-a-bug-content.md`.
Acceptance: on a device — each of the two screens opens with real content rather than a placeholder, Help covers all three topics, and there is no Thanks row in the drawer.
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

#### [audit] Check the Yesterday page draws a real list, not just its empty state [yesterday-page-with-content-untested]
Blocked by: [first-end-to-end-test]

Confirms that the Yesterday page renders what the user completed the day before. Came out of the
[verify-run-2026-08-31-remainder] audit on 2026-09-06 and was weighed on 2026-09-12.

That audit read the page against SPEC §Yesterday page and three of its four claims held on the device:
one swipe left of Today, a full page rather than a card, and an empty state reading "Nothing completed
yesterday." The fourth went untested because nothing had been completed the day before — and a page that
correctly draws its empty state says nothing about how it draws a list, which is this page's entire
content.

**Held against [first-end-to-end-test] on 2026-09-12, so the check rides real data.** That item has Alex
using the app for a normal day and completing at least one task, so the day after it runs, this page has
genuine content and the check costs a swipe. It is deliberately not folded into that item's own
walkthrough: a walkthrough ends at its own observable rather than growing a step that fires a day later.

**Refused: driving it from a crafted import file.** The route works — `TransferRepository.importAdding`
copies each incoming task through unchanged apart from its identifiers, so completion state and
completion date survive, confirmed by reading it on 2026-09-12 — and it would not need anyone to wait.
It is refused because it would put a fabricated completed task permanently into Alex's real history, and
nothing can remove it while deleting a task is the drag gesture [drag-eaten-by-page-swipe] fixes. Kept
here as the fallback if her first real day somehow produces no completion.

Steps:

1. On the phone, swipe left from Today to Yesterday. Look for: the page's own header, and a list rather
   than the "Nothing completed yesterday" empty state.
2. Read the list against what Alex completed during [first-end-to-end-test]. Look for: each of those
   tasks present, and nothing else.
3. Check how the list is presented — completion order, and whatever dating the page carries. Look for:
   agreement with SPEC §Yesterday page, and anything it does that SPEC does not describe.

Report: whether the page listed the right completions, and whether its presentation matches what SPEC
says. A mismatch is a finding and goes back as a capture rather than being fixed here.

Rests on, read 2026-09-12 in the source: that `TransferRepository.importAdding` preserves `isCompleted`
and `completedAt` on an added task, which is what makes the refused route a real alternative rather than
a guess.

Filed 2026-09-06, 11:39, mid-run, read from the device clock.

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

#### Help's two Claude topics — setting Claude up, and the custom-instruction text [help-thanks-report-content]
Blocked by: [0019-ai-choice-flow-and-mcp-setup], [0020-remote-mcp-server] until built

**The hold was changed on 2026-09-16 to wait for the Claude work to ship rather than to be agreed**, at
the fourth time of setting this entry aside for the same reason. Until then a hold naming an entry that
had reached Processed counted as satisfied, so this one was released and re-offered every planning
session however often it was deferred — a defect this project reported to the method's own project on
2026-09-12. The installed version answers it: a hold may end `until built`, which passes the entry over
until every named entry has a build record. This is the first item here to use it. Nothing else about the
entry changes, and it returns by itself the day the MCP server ships.

What is left of the words for the bottom-of-drawer screens that
[0022-help-thanks-report-a-bug-content] builds, after the writable parts were folded into that item on
2026-09-12. Two things remain, and both are stuck for the same reason.

**Both wait on the Claude work.** Help must explain how to set Claude up, and must carry the production
version of the suggested custom-instruction text — the wording a user pastes into their own Claude
preferences. Neither can be written before [0019-ai-choice-flow-and-mcp-setup] and
[0020-remote-mcp-server] define the path. The custom-instruction drafting was folded in here on
2026-08-25 from [custom-instruction-production-text], which kept only the live test; that test reads
the draft this item produces and reports back what to change.

**The third thing this entry owed is gone: there is no Thanks screen.** It had been recorded as a
distinct blocker — nobody had ever settled what that screen says — and when it was put to Alex on
2026-09-12 she recovered the reason nobody could answer it: the screen came from an earlier plan to
fund Taskflow through "buy me a coffee" style donations, replaced since by the paid subscription tier.
The screen's premise went with that change. SPEC §Side menu and §Settings were corrected in the same
run, [remove-thanks-screen] takes the row out of the app, and the wording this entry owed for it is no
longer owed by anybody. **What that overturns:** the assumption that the question was merely
undecided, when it was in fact a leftover of a superseded payment model — and separating it out as its
own blocker, which was right on the evidence then available and stopped it being deferred a fourth
time with the Claude topics.

**Why this was set aside three times** — 2026-08-25, 2026-08-31, 2026-09-03 — and why that is no longer
the whole story. The reason each time was the two Claude topics, and it still holds. What also happened
each time is that the writable parts went round with them: the Report-a-bug words and the
tasks-dated-before-today explanation were re-read and re-deferred at every pass. Those are now written
into [0022-help-thanks-report-a-bug-content], with the reasoning that chose an email address over a web
form or a GitHub issue, and the note that flintcraft.tech/report exists but tells non-automated visitors
not to use it. **Refused: splitting the writable parts into an item of their own** — they produce no
file of their own, so they are design belonging inside the build that consumes them, not work.

**One limit carried forward rather than filed:** `bugs@flintcraft.tech` is a Google Workspace alias, so
it receives but cannot send. Replying to a bug report from that address would need a send-as configured
in Gmail, which nobody has done. Not needed for a screen to print an address; if replying-as-bugs is
ever wanted, that becomes its own item.

The user-only step this item once had buried in its prose — creating the address — was lifted out on
2026-09-03 into [bug-report-email-address] and has since been done.

#### Personal strategy and memory dogfood — an early preview of Taskflow's Strategy-doc experience [personal-strategy-preview]
Not before: 2026-11-25

Alex's idea, raised in planning on 2026-06-24: use this workspace as an early, live preview of Taskflow's personal Strategy-doc experience, before Taskflow has the feature built. She'd keep a real personal Strategy doc and have the strategy conversations here with Claude directly, instead of through Taskflow plus a remote MCP server — neither of which exists yet. In her words: "We're just here, so we don't need the MCP."

Three threads bundled in it: (1) a real personal Strategy doc for Alex, maintained here in conversation with Claude — the experience a paid Taskflow user would eventually get via MCP, doubling as genuine design research for Taskflow's Strategy-doc feature; (2) Alex's real tasks, handled carefully so they don't go missing when Taskflow test builds wipe data — her live task data must not depend on the test app; (3) Claude-memory sync across her Claude surfaces. The insight she reached, and Claude strongly seconded: rather than pushing each strategy update out into many Claude memory stores, point all her Claudes at one canonical strategy doc and have them read from it. Pull-from-one beats push-to-many — one source of truth, nothing to hand-sync.

Why it was shelved, decided 2026-06-24: the method is one-spec-per-project. Two of the three threads (personal strategy, memory sync) aren't Taskflow app features, so they don't fit SPEC.md's contract that every entry describes something existing in the build. Governing three concerns in one workspace would need either multi-spec handling in the method or a deliberate re-framing of what this SPEC is about. Alex chose to wait for multi-spec support rather than bend SPEC now. That's a change to the method itself rather than a queue item here, which is why nothing in this queue holds it.

Privacy note to carry into any revival: if the personal strategy and real tasks get committed into this product repo and it's ever shared or made public, that's the user's private life data exposed. Decide the home with that in mind when this revives — it is the first question when this comes back, not an afterthought.

**Dated in planning on 2026-08-25, with the user's approval.** It waits on multi-spec support in the method, which no item in this queue can deliver and which belongs to the No code method project. It cannot be held below the readiness line either, because held work has to be specific enough to build and this is not. Left as a plain capture it returned to the top every session and was set aside again, which is what had been happening. Three months was chosen as long enough not to re-read it every session and short enough that it comes back while still fresh if multi-spec support lands sooner. It is not offered again before that date.

#### [user] Business registered far enough to open an organization Play account [business-registration-for-play-account]
Not before: 2026-10-12

**Dated a month out on 2026-09-12, with Alex's approval, and the week she first proposed the same day
was withdrawn as too short.** She had said the registration was not yet far enough along to lodge the
D-U-N-S request, and a week was written on the earlier pattern of expecting movement. She then said
what it actually waits on: her financial and tax position, tracked in another project, and her own
intention to use Taskflow daily for about a month before considering publishing at all. Against that, a
week would simply re-ask the same question every Saturday. A month is her figure, and the item is not
offered again before the date — she can pull it back sooner by saying so if her tax position resolves
first. Nothing is held up by the wait; [play-console-subscription-product] is parked behind this either
way, and the whole publishing chain behind that.

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

**The D-U-N-S wait described above is already served, established on 2026-09-16.** A number was issued on
2026-05-17 and the provider closed the case; the further 14-to-30-day wait its reply describes has also
long passed. So the paragraph above is right about the requirement and wrong about the timing: nothing is
waiting on a D-U-N-S number.

**What this item has to produce is therefore sharper than "far enough along".** The existing number was
issued to a **sole trader**, whose registered name is Alex's own and whose registered address is a former
one she no longer occupies and is not free to publish — so it cannot be used, for reasons that do not
depend on Google's policy at all. [play-console-subscription-product] now carries the standing condition
in full: the Play account is not opened on any identity whose published address is her home or that former
one. What this item must deliver is an identity whose published name and address she is content to have
printed on every subscriber's receipt — a registered company, or a genuine business address that is not a
mail-forwarding service, which Google does not accept.

**The date was re-raised on 2026-09-17 and deliberately kept.** With the dependency chain below now
understood, three months was proposed instead — long enough that the entry would return when there was
plausibly something to say. Alex kept mid-October. Recorded so a later session does not put the same
question to her a third time.

**And it is blocked further back than this queue can see.** The registration waits on changing its
registered address, which waits in turn on an identity-verification step being handled in the
flintcraft.tech project — with no date this project controls, and none this project should invent. Alex's
own condition, given on 2026-09-16: she will not register until she has confirmation that no mail reaches
the former address.

The D-U-N-S number itself, and the details on the application, are deliberately not written into this
queue, which is public.

Observable: nothing in this repository changes when this is done, so completion is not checkable from
here — a later session asks rather than checks. Stated plainly so nobody builds a check that cannot
work.

Filed 2026-09-03 during planning, at the moment the account-type choice was settled. The capture that
held that choice was deleted in the same move, its facts folded into
[play-console-subscription-product], which is where they are used.

#### Revisit how Taskflow is paid for, before anything is published [payment-model-revisit]
Blocked by: [business-registration-for-play-account]

captured by you, 2026-09-12, at the moment the Thanks screen was dropped.

Taskflow has had at least two payment models. The Thanks screen was a leftover of the first — funding
through "buy me a coffee" style donations — which was replaced by the paid subscription tier now
described in SPEC §Tier model, sold as a Google Play subscription with a 30-day trial. Nothing in the
record shows that swap ever being weighed as a decision; the donation model simply stopped being
mentioned, and the only trace it left was a screen nobody could explain. Alex's point: the current model
deserves an actual discussion rather than inheriting the same silence.

What the discussion would cover, none of it decided here: whether a subscription is the right shape for
an app whose free tier is deliberately complete, what the price is, whether a one-off purchase or a
donation route sits alongside it, and what any of that does to the free/paid line the onboarding flow
asks the user to choose between.

**Held against [business-registration-for-play-account]**, which is the item the whole publishing chain
waits on and is itself dated to 2026-10-12. That is deliberate: the payment model only has to be settled
before Taskflow is sold to anyone, and Alex's standing instruction is that the queue is worked for daily
use until she has had her month of real use. Two nearer-looking candidates were considered and rejected —
[0017-tier-model-and-subscription-handling] and [play-console-subscription-product] both sit in
Processed, and a `Blocked by:` naming an entry already in Processed counts as resolved, so either would
release this capture on the next planning run and hold nothing at all.

Filed 2026-09-12 during planning, on Alex's instruction.
Filed 2026-09-12 21:06, stamped by the queue tool.

