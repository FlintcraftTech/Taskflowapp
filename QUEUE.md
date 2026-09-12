# QUEUE

## Processed

> Vetted work, ready to build — worked top to bottom. Each piece of work is one
> item: a `#### ` heading naming it, a `[slug]` at the end of that heading line, and
> a short rationale beneath. A leading flavor tag names how it runs — none for a
> build (Claude edits files), `[audit]` for a review pass, `[user]` for a step only
> you can do. A security or privacy risk Claude surfaces lives here too, as a work
> item carrying a `Red flag · State: cleared/uncleared` marker. The line below marks
> how far down is cleared to build; anything below it is decided but not ready yet.

#### CLAUDE.md rules: handing a compile over mid-run, and ordering the queue for daily use before publishing [runs-in-android-studio-decision]

Adds a rule to this project's CLAUDE.md saying what a build does when its work needs compiling: it stops,
hands Alex one command to paste into Android Studio's integrated terminal, and waits for the result
before ticking the item.

**The decision behind it, settled with Alex on 2026-09-07: sessions stay in the desktop app.** She raised
the underlying question on 2026-09-05, during the decision step on [gradle-from-ide-terminal], seeing
what that item's payoff implied before it was written down — compiling mid-run is only possible in a
Claude session started in Android Studio's terminal, so having the capability would mean moving whole
runs there. Against that: a terminal session has none of the file viewer and side panel she reads the
work through, and she is a no-code developer for whom that surface is not a convenience but how the work
is legible at all. For it: a compile inside the run is what stops code items shipping ticked UNCONFIRMED.

**Refused: moving whole `/next` runs into that terminal**, on the legibility ground above. **Refused: a
rule naming which items require it** — the middle option the capture proposed — because handing the
command over works for every item, so nothing has to be classified. What is taken instead is a fourth
route: stay in the desktop app and hand the compile over as a paste-in step, which is exactly what
happened on 2026-09-06 and worked.

**It also disposes of an unverified assumption rather than betting on it.** The capture named a second
thing nobody had established — whether a Claude session started in that terminal loads the plugin, the
skills and the hooks at all. Under this decision no session runs there, so the question never has to be
answered. It is recorded here rather than dropped, in case the decision is ever revisited.

The cost, stated: Alex has to be at the machine when a run wants a compile, so an unattended run still
cannot confirm a build. That limitation is unchanged by this — it is simply not paid for with her
working surface.

**A third CLAUDE.md rule was folded in on 2026-09-12, from the look-back over this session.** While Alex
is using Taskflow for real, no connected instrumentation test run happens on her phone without a JSON
export taken from Settings first and moved off the device — and any queue item whose observation needs
such a run says so in its own text, so an unattended session meets the condition before it drives the
check rather than improvising at the moment it matters. The hazard is recorded in TOOLS.md: a connected
test run installs the app, runs the tests and then uninstalls both, taking the Room database with it,
and on AGP 9.2.1 no build setting prevents it. Alex accepted that on 2026-09-06 — but she accepted it
when the phone held test data, and her month of daily use changes what is at stake rather than what is
true. [rotating-roster-recurrence], cleared to run in this same session, names a 5 → 6 migration test as
part of its proof, which is exactly the collision. [emulator-for-instrumented-tests] is the durable fix;
this rule is what holds until it lands.

**A second CLAUDE.md rule was folded in on 2026-09-12, rather than filed as its own item**, because it
writes the same file and a build touching one paragraph may as well write both. Alex said she intends to
use Taskflow daily for about a month, testing it and changing it as she goes, before considering
publishing — and that the publishing blockers live in another project waiting on her financial and tax
position. The rule records that as the standing ordering instruction: until she has had that month of
real use, the queue is worked for what makes daily use good rather than for what gets the app to the
Play Store, and the publishing chain is parked rather than abandoned. It belongs in CLAUDE.md rather
than SPEC because it directs how sessions work on the project, not what the product is.

Files:
- `CLAUDE.md` — a new rule under Project rules, plus the ordering instruction above written as a second
  one. The first says that Claude's own shell cannot run `gradlew`
  (the loopback failure recorded in TOOLS.md), that the working route is Android Studio's integrated
  terminal, and that a build needing a compile stops and hands the command over rather than ticking the
  item unconfirmed. The handover carries, as typed lines, the `cd` to the project folder, then
  `$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"`, then
  `.\gradlew.bat :app:assembleDebug --no-watch-fs --no-daemon` — with `BUILD SUCCESSFUL` named as the
  thing to look for. The JAVA_HOME line is stated because leaving it out is what failed the first
  attempt on 2026-09-06, and its absence produces a confusing error that looks like the loopback
  failure rather than a missing setting.

Observation: CLAUDE.md's Project rules section contains the rule, and a reader following its three typed
lines from a fresh Android Studio terminal reaches `BUILD SUCCESSFUL`. The check reaches the one file
named above.

Rests on, read 2026-09-07 from `LOG/2026-09-06-gradle-from-ide-terminal-result.md`: that
`.\gradlew.bat :app:assembleDebug --no-watch-fs --no-daemon` reached `BUILD SUCCESSFUL` in Android
Studio's integrated terminal on 2026-09-06, and that it failed first on an unset `JAVA_HOME` until that
line was supplied.

Filed 2026-09-05 during planning, at the moment the implication was raised.

#### Cloud schema grants no privileges to the authenticated role, so the app itself would be denied every table [cloud-schema-missing-grants]

Found on 2026-09-06 while driving [supabase-apply-cloud-migrations] against the real Supabase project, not
yet reviewed.

`0001_initial_schema.sql` creates the four tables and enables Row Level Security on each. It issues no
`grant` statements. `0002_rls_policies.sql` creates sixteen policies scoped `to authenticated`. Neither
file grants the `authenticated` role any privilege on any table.

**Row Level Security and table privileges are two separate gates, and Postgres checks the privilege first.**
A policy says which rows a role may touch; it does not give the role permission to touch the table at all.
So with the migrations exactly as they stand, the first real query from a signed-in Taskflow user fails
before any policy is consulted.

Observed rather than reasoned. Impersonating a test user in the SQL editor —
`set local role authenticated`, with the user's id set as the JWT claim — and selecting from `public.tasks`
returned:

```
ERROR: 42501: permission denied for table tasks
HINT: Grant the required privileges to the current role with:
      GRANT SELECT ON public.tasks TO authenticated;
```

The database's own hint names the fix. Every one of the four tables needs it, for select, insert, update
and delete, and the sequence-free UUID keys mean no sequence grants are needed alongside.

**This fails closed, so it is a functionality defect and not a data-exposure risk.** Nothing is readable
that should not be; the tables are simply unreachable. Recording that explicitly because the words
"missing grants" read like a hole, and here the hole is in the other direction.

Why it was not caught before: the SQL editor connects as `postgres`, which holds `bypassrls` and owns the
tables, so every check run there succeeds regardless. Nothing had queried these tables as the role the app
will actually use until this drive. The impersonation technique that exposed it is written up in
`workshop/resources/research/supabase-rls-and-edge-function-identity.md`, so any later check of these
policies starts from a method that can tell a pass from a fail.

**The three open questions were settled on 2026-09-07, both migration files read that day.**

- *A new `0003_grants.sql`, not an edit to `0001` or `0002`.* Both have already been applied to the live
  project. Editing an applied migration makes the file stop describing what actually ran, and a fresh
  project would then be built by a sequence nobody has exercised.
- *Per-table grants, written out; refused: `alter default privileges` on the schema.* Default privileges
  reach only tables created after they are set, so they would do nothing for four tables that already
  exist and the explicit grants would be needed anyway — and they would silently open future tables,
  which is the opposite of the closed-by-default posture `0001` deliberately takes.
- *Re-runs: `grant` is idempotent in Postgres, so `0003` is safe to run repeatedly.* That is also what
  makes it the repair for the live database rather than a change to it — the grants are already there by
  hand, applied during the 2026-09-06 drive, so applying `0003` proves the file and the project agree.
- *Refused: adding `if not exists` guards to `0001` and `0002`.* A migration series is applied once in
  order, so re-running an applied file is not something to make safe — doing so invites it. On an empty
  project both run cleanly, which is the case that matters. `0003`'s header says so, so this is not
  re-opened.

This closes the one gap between the repository and the working Supabase project: the grants exist on the
live database and in no file.

Files:
- new `supabase/migrations/0003_grants.sql` — `grant select, insert, update, delete` on
  `public.projects`, `public.tasks`, `public.strategy_entries` and `public.life_areas` to
  `authenticated`, and nothing to `anon`, matching `0002`'s stance that a request with no session
  matches no policy at all. No sequence grants: the keys are UUIDs. A header comment carrying the four
  decisions above.

Reads but does not change: `supabase/migrations/0001_initial_schema.sql` and
`supabase/migrations/0002_rls_policies.sql`.

Observation: applying `0003` to the Supabase project completes without error, and completes again on a
second run; then, in the SQL editor, `set local role authenticated` with a test user's id as the JWT
claim selects from each of the four tables and returns that user's rows rather than `42501`. Touching
the live database needs Alex's go-ahead in the moment, as the 2026-09-06 drive did.

Rests on, read 2026-09-07 in the files: that `0001` issues no `grant` statement and enables Row Level
Security on all four tables; that `0002` scopes all sixteen policies `to authenticated` and grants
nothing; and that every primary key is a UUID default, so no sequence exists to grant on. The testing
method it will be verified with is in
`workshop/resources/research/supabase-rls-and-edge-function-identity.md`, read the same day.

Filed 2026-09-06, 12:22, mid-run, read from the device clock.

#### Free-tier reorder Projects in the Strategy-doc editor [project-reorder-strategy]

**Lifted on 2026-09-05.** The editor it waited on exists and was seen working on a phone:
[0015-strategy-doc-and-life-area-context] shipped on 2026-09-02, and the 2026-09-05 device audit
confirmed the doc renders one heading per Project with Unassigned excluded (TEST-LOG row 047). What that
row did not exercise is edit persistence across a relaunch or the share sheet — neither of which this
item touches, since it adds heading drag rather than paragraph editing.

Free tier — drag Project headings in the Strategy-doc editor to set Project order; the Strategy doc owns Project order app-wide, including the Later card order. SPEC §Strategy doc describes this. Carved from the retired [project-lifecycle-later] during planning on 2026-06-22. Held until 2026-09-05 because the Strategy-doc editor had to exist before headings could be dragged in it.

**Designed on 2026-09-06**, because it had been cleared to run since 2026-09-05 while naming no files —
a /next run reached it that day, could not scope it, and stopped. The design is wiring rather than
invention: everything it needs already exists and nothing in the app calls it yet.

**The drag handle is the heading alone, settled with Alex on 2026-09-06.** Refused: making the whole
section draggable, paragraph included — a bigger target, but a long-press meant to select a word in the
paragraph would start a drag instead, and heading-only is what SPEC §Strategy doc's wording implies.

Files:
- `app/src/main/java/com/example/taskflow/ui/strategy/StrategyScreen.kt` — the sections list becomes a
  `ReorderableColumn` (the existing primitive in `ui/common/Reorderable.kt`, already used by the
  Schedule pages and the Later cards), with the heading `Text` as the drag handle and the paragraph's
  `OutlinedTextField` left alone.
- `app/src/main/java/com/example/taskflow/ui/strategy/StrategyViewModel.kt` — a `reorderProjects`
  taking the new ordered list of Project ids and writing each one's position through
  `ProjectRepository.updateSortOrder`, rewriting the whole sequence to 0..n-1 rather than nudging the
  moved row, which is the pattern `ScheduleViewModel.reorderSlot` and `reorderCard` already use because
  positions drift as Projects come and go.

Reads but does not change: `data/repository/ProjectRepository.kt` and `data/local/ProjectDao.kt`, which
already carry `updateSortOrder` and `getAllOrdered`; and `ui/schedule/LaterPage.kt`, which needs no edit
because the Later cards already read Project order from `getAllOrdered`, so setting the order here moves
the cards for free — which is what SPEC means by the Strategy doc owning Project order app-wide.

Observation: on a device, dragging a Project heading in the Strategy doc moves it, the new order
survives a relaunch, and swiping back to Later shows the cards in that same order with Unassigned still
pinned to the bottom. Long-pressing inside a paragraph selects text as before rather than starting a
drag. The check reaches the two files named above.

Rests on, read 2026-09-06 in the source: that `Project` carries a `sortOrder` column and
`ProjectRepository.updateSortOrder` writes it; that no UI calls it today; and that `ReorderableColumn`
exists in `ui/common/Reorderable.kt` and takes an ordered-ids callback.

#### Drag targets are unreachable because the page turns first — the target row's area stops turning it [drag-eaten-by-page-swipe]

The row of bin and cut targets that appears when a task is picked up cannot be reached: moving the finger
toward it turns the page instead. So the bin, the cut and the promote targets are all unreachable by the
gesture SPEC gives for reaching them (SPEC §Drag-target icons).

Found on 2026-09-06 by Alex, on a real phone with her own thumb, while being walked through
[bin-drag-target-check]: a long-press on Today lifted the task and raised the targets, and the next
movement navigated to Tomorrow.

**The mechanism was misdiagnosed when this was captured, and the correction is the whole design.** The
capture suspected Compose's horizontal pager was swallowing the drag before the task's own handler saw
it. Reading `ScheduleScreen.kt` on 2026-09-06 shows otherwise: Taskflow turns the page itself, on
purpose. `onTaskDragHorizontal` accumulates the sideways distance of a held task and calls
`animateScrollToPage` once it passes `DRAG_PAGE_THRESHOLD` — 140 pixels. That is
[0008-drag-task-between-schedule-screens] working as built. So this is not a library defect but two
wanted features sharing one axis: reaching a target and turning a page are the same motion, and the page
turn fires first.

**The fix, settled with Alex on 2026-09-06: the target row's own area does not turn the page.** While the
drag position sits within the bounds the row has already laid itself out into, the page-turn accumulator
is skipped and the drag belongs to the targets; below the row, sideways still reschedules exactly as it
does now. No new affordance and no new tuned distance — the row already measures its icons for hover
detection, so all that is added is reporting where it ended up.

Refused: moving the targets to the bottom of the screen so reaching them is a downward motion — it
separates the axes cleanly but fights the "drag it away to get rid of it" instinct and contradicts SPEC's
"upper-right corner". Refused: raising `DRAG_PAGE_THRESHOLD` or making the page turn wait — it degrades
rescheduling, which works, to fix something else. Refused too is Alex's own first reading of the
recommendation, that a band be reserved at the top: her correction is that the row already occupies one,
so the band is its own footprint rather than something new.

**The second symptom is not a fault.** `DragTargetRow` is aligned to the top-end of the content area
below the header, and `BIN` is the first of the two icons, so it renders at the upper-right of the task
area with the bin on its left — which is what Alex saw and what SPEC §Drag-target icons and §Create or
delete a Project describe. It is [bin-drag-target-check]'s walkthrough that loosely says "at the top of
the screen"; nothing needs changing in the app for it.

**Its filing also corrected the record.** Three adb attempts across two sessions had each registered as a
page swipe and been written up as a limit of driving a device over adb — a line since removed from
TOOLS.md. They were reproducing a broken gesture faithfully, not failing to reproduce a working one.

Consequence for the queue: [bin-drag-target-check] cannot pass while this stands, and neither can the
promote target, the cut target from a Schedule screen, or free-tier Project deletion
([project-delete-later]), which was re-held against this item on 2026-09-06 for that reason.

Files:
- `app/src/main/java/com/example/taskflow/ui/common/DragTargets.kt` — `DragTargetRow` reports its own
  laid-out bounds upward through a new callback, alongside the hover it already reports.
- `app/src/main/java/com/example/taskflow/ui/schedule/ScheduleScreen.kt` — `onTaskDragHorizontal` skips
  the accumulate-and-turn step while `dragPosition` falls inside those bounds.

Observation: on a device, long-pressing a task on Today and dragging up into the icon row leaves the page
on Today and highlights the icon under the finger; dragging sideways below the row still turns the page
and still reschedules the task. The check reaches the two files named above.

Rests on, read 2026-09-06 in the source: that `ScheduleScreen.kt` turns the page from
`onTaskDragHorizontal` at a 140-pixel threshold, and that `DragTargetRow` already records each icon's
`boundsInWindow` for hover.

Filed 2026-09-06, 12:58, mid-run, read from the device clock.

#### Day-detail card layer, opened from a search result [nav-day-card-layer]

**Lifted on 2026-09-06.** The Search page it waited on exists and has been seen working on a phone:
[nav-search-completed-history] shipped on 2026-09-05, and the [verify-run-2026-08-31-remainder] audit on
2026-09-06 confirmed search narrowing the completed history in both directions. That build also recorded
that tapping a completed result is deliberately inert until this item ships, so the tap target this item
adds is the piece that was left out rather than a change to what is there.

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

#### Rotating-roster repeats — a recurring task that cycles through a list rather than repeating identically [rotating-roster-recurrence]

Raised by you on 2026-09-06, describing how you actually want to use a Project for keeping up with your
immediate family.

A recurring task whose *subject* advances through an ordered list each time it comes round, rather than
repeating unchanged. Taskflow cannot express it: `Recurrence` holds an interval and every generated
instance is identical, with no per-instance content and no ordered list. The failure that fixes is the
one the app exists for — keeping in touch with four people evenly means holding the rotation in your
head, which is the executive-function load Taskflow is meant to absorb. The workaround, four staggered
recurring tasks, drifts the moment one is completed late.

**The four open questions were settled with Alex on 2026-09-12.**

- **The roster lives on the task** — an ordered list of labels the repeat rule steps through, stored
  alongside the rule itself. **Refused: putting it on the Project.** That would make a Project a thing
  with members, where a Project is an area of the user's life (SPEC §Strategy doc), and it is a much
  larger change to what a Project is than this one task feature warrants.
- **The free-choice position is split off** into [rotating-roster-free-choice] and is not part of this
  item. A rotation is deterministic — the app knows what comes next — while a position offering two
  names and waiting for the user to pick has to interrupt them and needs an answer for what happens if
  they never reply. Bundling them would make the straightforward half wait on the awkward one.
- **The free tier gets it.** The rotation is data, not intelligence: an ordered list and a position in
  it. **Refused: making it paid-only.** Claude arranging a roster conversationally is a separate thing
  built on top — Claude *setting* the list — rather than a substitute for the list existing.

**The roster advances by COMPLETION, settled with Alex on 2026-09-12.** Names are handed out in order
to the instances still to come, starting from however many the user has already completed: the nearest
upcoming instance takes `roster[completions mod size]`, the one after it the next name. An occurrence
nobody ticked adds no completion, so the same name is still up.

**Refused: advancing by date** — the third occurrence always the third name, whether or not the earlier
two were done. `instancesOf` never re-shows a missed instance, so that would lose the occasion *and*
skip the person's turn, against SPEC UX principle 4. **Accepted cost:** a rotation nobody does stops
advancing and keeps showing one name; advancing by date never stalls.

Names of the people in the roster are deliberately not recorded here — third parties who have published
nothing, described by relationship per this project's scrub checklist. The concrete roster is Alex's.

Files:
- `app/src/main/java/com/example/taskflow/data/model/Task.kt` — a `roster` column, `TEXT NOT NULL
  DEFAULT ''`, holding one label per line, empty for a task with no rotation; plus a `rosterLabels`
  accessor splitting it, alongside the existing `completedInstanceDates`.
- `app/src/main/java/com/example/taskflow/data/local/TaskflowDatabase.kt` — `version = 6` and a
  `Migration(5, 6)` adding that column. The existing destructive-migration range (1–4) is untouched:
  [durable-local-data] made 5 a floor, so 5 → 6 is a real migration.
- `app/schemas/com.example.taskflow.data.local.TaskflowDatabase/6.json` — the schema the Room compiler
  exports for version 6, committed beside the existing `5.json`.
- `app/src/main/java/com/example/taskflow/ui/schedule/ScheduleViewModel.kt` — `instancesOf` carries a
  label on each `Instance`: over the list it already builds (completed dates filtered out, window
  starting at the current day), the Nth entry takes `roster[(completedCount + N) mod roster.size]`, N
  counted from zero. A task with an empty roster is unchanged. The row renders `<title> — <label>`.
- `app/src/main/java/com/example/taskflow/ui/edit/EditTaskScreen.kt` — a roster field in the edit
  dialogue, one name per line, shown only when the task carries a repeat rule.
- `app/src/main/java/com/example/taskflow/ui/edit/EditTaskViewModel.kt` — reads and writes that field.
- `app/src/main/java/com/example/taskflow/data/transfer/TaskflowJson.kt` — `roster` carried through
  export and import, since SPEC §JSON export and import promises the full database.
- `app/src/androidTest/java/com/example/taskflow/data/local/MigrationTest.kt` — a 5 → 6 case, existing
  rows arriving with an empty roster.

Reads but does not change: `app/src/main/java/com/example/taskflow/domain/Recurrence.kt`, which supplies
the instance dates and needs no change — the roster is a property of the task, not of the repeat rule,
so `serialize()` and its stored format stay exactly as they are.

Observation: on a device, a weekly task carrying four names shows the first name on its nearest
upcoming instance and the second on the one after, in Later; completing the nearest one moves the list
on; letting an occurrence pass without completing it leaves the same name up rather than moving on.
`MigrationTest` passes 5 → 6 with an existing task arriving with an empty roster and its repeat rule
intact. A JSON export and re-import round-trips the roster. The check reaches the eight files named
above.

**The migration half of that observation needs a connected instrumentation run, which destroys the
database it runs against.** Run it on the emulator from [emulator-for-instrumented-tests] where that
exists; otherwise take a JSON export from Settings and move it off the device before starting, per the
CLAUDE.md rule in [runs-in-android-studio-decision]. Written here on 2026-09-12 because an unattended
run reads the item, not the rule, and this item was cleared before the rule existed.

Rests on, read 2026-09-12 in the source: that the schema is at version 5 with `5.json` the only
exported schema; that `Recurrence` derives instance dates and stores none, so the roster needs no change
to the rule format; that `ScheduleViewModel.instancesOf` filters completed instances out and starts its
window at the current day, so a missed instance is never re-shown; and that `Task.completedInstances`
already stores completed dates as a joined string.

Filed on 2026-09-06 at 12:40, mid-run, read from the device clock.

#### TEST-LOG.md owes rows for the 2026-09-06 run, which produced more test outcomes than any session so far [test-log-owes-2026-09-06-rows]

This project's CLAUDE.md carries a standing rule: TEST-LOG.md is the running test history, and material
test outcomes are reflected there as well as in the session's LOG entry. The /next run of 2026-09-06
produced a large number and wrote none of them; the file's last rows predate the session entirely.

What is owed, from that run's records:

- The device audit's passes — search narrowing the completed history in both directions, a manually-dated
  one-off at five months out appearing in Later, the side menu matching SPEC's spine order, a past-dated
  task sitting on Today with no overdue marking.
- Its failures and blocks — the Strategy Share button doing nothing on an empty doc, the day boundary
  offering whole hours only, the rollover check unrun, Strategy edit persistence unreachable, the Yesterday
  page untested with content.
- The date matrix, all three slots: 2–7 days to Soon, exactly eight days to Later, a past date staying on
  Today, each with its DD/MM label.
- The far-future task inside a real user Project's card.
- The three Supabase checks — cross-account read denied, no-session denied at the grant, null identity
  denied by the policy — and the `42501` failure that preceded them, which is the row that matters most
  because it found [cloud-schema-missing-grants].
- The compile: BUILD SUCCESSFUL from Android Studio's terminal, covering four changed files.

**Why this was missed, and what was settled about it on 2026-09-12.** The rule's trigger is a build
running tests; that run's testing happened inside an `[audit]` item and inside `[user]` walk-throughs,
so the trigger and the run's shape never met. **Settled with Alex: the table records a test outcome
whoever produced it** — a build, a review pass, or a step the user performed on the device. The value
of a running history is being the one place that says what has actually been exercised, and one that
silently omits the checks a person ran is worse than none, because it reads as complete.
**Refused: leaving the rule narrow and backfilling the rows anyway** — that moves the ambiguity into
the file, where the next session guesses again.

Everything needed is already recorded in the session's LOG entries, so nothing has to be re-run.

Files:
- `CLAUDE.md` — the TEST-LOG paragraph's last sentence, currently "reflect material test outcomes
  (pass/fail/skip) in TEST-LOG.md too when a build runs tests", widened to cover an outcome from any
  source: a build's tests, an `[audit]` run's device checks, and a `[user]` walkthrough's steps alike.
- `TEST-LOG.md` — the rows listed above, appended in the table's existing eleven-column shape,
  numbered on from its last row, with Verifier naming who produced each outcome (Claude for the
  adb-driven checks, Alex for anything she performed) and Confirmed Explicitly left No until she
  reviews them.

Reads but does not change: the `LOG/2026-09-06-*.md` entries, which hold every outcome the rows are
written from.

Observation: `CLAUDE.md`'s TEST-LOG paragraph no longer conditions recording on a build, and a grep of
`TEST-LOG.md` for `2026-09-06` returns rows covering each outcome listed above. The check reaches the
two files named above.

Rests on, read 2026-09-12: `TEST-LOG.md`'s eleven-column table shape and its numbering running from
001; and `CLAUDE.md`'s TEST-LOG paragraph as quoted.

Filed on 2026-09-06 by the look-back over the /next run's own conversation, and stamped by the queue
tool the same evening.

#### Say why the Strategy doc cannot be shared while it is empty [strategy-share-silent-when-empty]

The Strategy page's empty state gains a sentence telling the user the doc can be shared once it has
something in it. The Share button stays disabled.

Came out of the [verify-run-2026-08-31-remainder] audit on 2026-09-06, which tapped Share twice on a
device with no Projects in the database: no share sheet opened, the focused window stayed on Taskflow's
own MainActivity, and a cleared logcat caught nothing at all. The audit read that as a visible control
silently doing nothing, and left two dispositions open — open the sheet anyway, or disable the button and
say why.

**The premise was corrected on 2026-09-12 by reading `ScheduleScreen.kt`.** The button already carries
`enabled = strategySections.isNotEmpty()`, so with no Projects it is genuinely disabled and the tap fired
nothing — which is why the logcat was empty. The second of the audit's two dispositions is therefore
already the implemented behaviour, and a build following the capture as written would add a guard that
exists. What is missing is only the visible half: a disabled `TextButton` in a top bar is muted rather
than obviously unavailable, and the empty state says nothing about sharing.

**Refused: enabling the button so it shares an empty document.** Handing someone a blank share sheet is
a worse answer than a control that is visibly not available yet.

Files:
- `app/src/main/java/com/example/taskflow/ui/strategy/StrategyScreen.kt` — the empty-state text, which
  currently reads that Projects appear here as headings with room to write about each one, gains a
  closing sentence saying the doc can be shared once it holds something.

Reads but does not change: `app/src/main/java/com/example/taskflow/ui/schedule/ScheduleScreen.kt`, which
owns the Share button and its `enabled` guard — the guard is correct and stays as it is.

Observation: on a device with no Projects, the Strategy page's empty state names sharing, and the Share
action in the header is visibly unavailable rather than looking ordinary. The check reaches the one file
named above.

Rests on, read 2026-09-12 in the source: that `ScheduleScreen.kt` renders the Strategy header's Share
action with `enabled = strategySections.isNotEmpty()`, and that `StrategyScreen.kt` owns the empty-state
sentence.

**The audit's other half rides elsewhere.** Whether the share sheet opens at all was unverifiable while
this button was the only route to it. A Project named Family now exists, so the doc has content and the
button is live; that check belongs with [strategy-edit-persistence-blocked], which already drives this
page on a device.

Filed on 2026-09-06 at 11:35, mid-run, read from the device clock.

#### [audit] Verify on the phone that Enter creates a subtask that survives a save [verify-edit-outliner-fix]

Confirms that the fix shipped by [edit-outliner-missing] works on a device. Pressing Enter at the end of
a task's title in the edit dialogue is meant to open an indented subtask line beneath it (SPEC §Edit
dialogue: outliner-style typing for subtasks). On 2026-09-06 the [verify-run-2026-08-31-remainder] audit
found no subtask could be created anywhere in the app: `onNext` appended an empty child line and
`Outline.parse` discarded it immediately. [edit-outliner-missing] moved the blank-dropping rule to the
save path and compiles, and its own record says the on-device observation is still unrun.

**Filed on 2026-09-12, raised by Claude during planning and taken up on Alex's word.** Nothing in the
queue was tracking this verification, while two items waited behind it —
[subtask-affordance-in-edit-dialogue], which advertises the feature, and [first-end-to-end-test], which
Alex deferred on her own condition that subtasks work first. Shipped-but-unverified work with nothing
naming the check is what this closes.

**Claude drives it, not the user.** The capability check on 2026-09-12 found adb sufficient: a session
drove the date-behaviour checks the same way on 2026-09-06 after finding a `[user]` tag wrong on
similar work. What is needed from Alex is the phone unlocked and connected, and the phone re-locks
after a few minutes (TOOLS.md), so possibly more than once.

Steps:

1. Establish what is on the phone before anything else. Read the install time with
   `adb shell dumpsys package com.example.taskflow | findstr lastUpdateTime` and compare it against
   2026-09-06, when the fix was written. Look for: an install time at or after that date.
   **If it is older, stop and hand the build back to Alex** — Claude cannot compile from its own shell
   (TOOLS.md), so she runs, in Android Studio's integrated terminal, `cd` to the project folder, then
   `$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"`, then
   `.\gradlew.bat :app:assembleDebug --no-watch-fs --no-daemon`, looking for `BUILD SUCCESSFUL`. Claude
   then installs it with `adb install -r -t` on the path in TOOLS.md.
2. Open any existing task's edit dialogue on Today. Screenshot before typing, since `adb shell input
   text` can silently fail into a dialogue field (TOOLS.md). Look for: the outliner with the title on
   its first line.
3. Press Enter at the end of the title line, then type a short child line. Screenshot before committing.
   Look for: an indented line beneath the title carrying that text.
4. Save, leave the dialogue, and reopen the same task. Look for: the child line still there, indented
   under its parent.
5. On Today, look at the parent row. Look for: an expand control where its checkbox was, and the child
   nested beneath it when expanded (SPEC §Parent tasks expand/collapse instead of having a checkbox).
6. Leave the database as it was found: tick the subtask off, which completes the parent and sends it to
   the Completed tray. Look for: the parent gone from the list. **Do not attempt to delete anything** —
   deleting is a drag onto the bin target, which does not work at all until [drag-eaten-by-page-swipe]
   ships.

Report: whether a subtask can be created, whether it survives a save and reopen, and whether the parent
renders as a parent. A failure at step 3 or 4 is a finding and goes back to the queue as a capture
rather than being fixed here.

Observation: on the device, a task that had no subtasks has one after step 4, still present after the
dialogue is closed and reopened. The check reaches no files — it reads the running app.

Rests on, read 2026-09-12: `LOG/2026-09-06-edit-outliner-missing-build.md`, which records the fix as
compiling with the on-device observation unrun; and TOOLS.md's adb, install and locking facts, each
carrying its own date there.

Filed on 2026-09-12 at 11:25, read from the clock.
Filed 2026-09-12 11:28, stamped by the queue tool.

#### [audit] Check a Strategy doc paragraph survives a relaunch [strategy-edit-persistence-blocked]

Confirms that text typed under a Project's heading in the Strategy doc is still there after the app is
closed and reopened. SPEC §Strategy doc says the user writes the paragraphs while the headings are
generated from their Projects; nothing has ever checked that what they write is kept.

**Its blocker resolved itself between the filing and the planning, and the item is rewritten for that.**
It was captured on 2026-09-06 by the [verify-run-2026-08-31-remainder] audit, which could not run the
check: the database held no Projects, so the doc showed its empty state and there was no paragraph to
type into. Creating one would have left a Project behind that no session could remove, since deleting a
Project is a drag onto a target and that gesture does not work ([drag-eaten-by-page-swipe]) — a worse
outcome than the check was worth. Later the same day a run created **Family**, a Project Alex named
because she wanted it rather than accept a throwaway, and its creation wrote a Strategy heading. So a
heading with an empty paragraph now exists and the check is simply available.

**Refused: the two ways out the capture named** — making this a `[user]` item run alongside a Project the
user wants anyway, and waiting until deleting a Project is reachable. The first has already happened by
accident of sequence, and the second is no longer the price of running the check.

This also closes the capture's closing note: the same blockage was said to sit under
[verify-far-future-project-card], and that check passed against Family on 2026-09-06.

Steps:

1. On the phone, swipe to the Strategy page and find the **Family** heading. Look for: the heading with
   a text box beneath it.
2. Type a sentence into that box. Read it back off the screen rather than assuming it arrived — a run on
   2026-09-06 recorded that `adb input text` can silently fail into a field. Look for: the exact
   sentence rendered in the box.
3. Force-stop Taskflow and reopen it on the Strategy page. Look for: the same sentence still under
   Family.
4. Tap **Share** in the page header, while the doc still has content. Look for: Android's own share
   sheet opening over Taskflow, listing apps to send to.
5. Back out of the share sheet without choosing anything. Look for: the Strategy page again, and
   nothing sent. **Do not pick a target** — this checks that the control works, not that sharing works
   end to end, and nothing should leave the phone.
6. Clear the box, leaving the doc as it was found, and read the empty box back.

**Steps 4 and 5 were added on 2026-09-12**, carrying the other half of the 2026-09-06 audit that
[strategy-share-silent-when-empty] hands over. That audit could not tell whether the share sheet opens
at all, because the only route to it was a button that is disabled while the doc is empty — and a
Project named Family now makes the doc shareable. Written into this item as well as into the one that
hands it over, because a relationship recorded on one side only is a relationship a driving session
never sees.

Report: whether the sentence survived, whether the share sheet opened, and whether the field was left
empty. If step 2 cannot get text into the box at all, that is the finding and the steps after it do not
run.

Rests on, read 2026-09-07 in `LOG/2026-09-06-verify-far-future-project-card.md`: that a Project named
Family exists in the database and that creating it wrote a Strategy heading.

Filed 2026-09-06, 11:38, mid-run, read from the device clock.

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

#### [user] Create an emulator so instrumented tests never run on Alex's phone [emulator-for-instrumented-tests]

Creates a virtual Android device on this PC, so the app's instrumentation tests have somewhere to run
that is not the phone holding Alex's real tasks.

**Why it is worth an errand.** A connected instrumentation test run installs the app, runs the tests and
then uninstalls both, taking the Room database with it, and on AGP 9.2.1 no build setting prevents that
— recorded in TOOLS.md and in
`workshop/resources/research/instrumented-test-uninstall-after-run.md`. Alex intends to use Taskflow
daily for about a month, changing it as she goes, so tests will keep being run while her own tasks are
on the phone. [rotating-roster-recurrence] already names a 5 → 6 migration test as part of its proof.
The CLAUDE.md rule in [runs-in-android-studio-decision] holds the line meanwhile by requiring an export
first; this removes the hazard instead of managing it.

**It is `[user]` work because nothing here can do it.** The capability check on 2026-09-12 established
what is actually on the machine: `emulator.exe` is present under the SDK, but `emulator -list-avds`
returns nothing, no `avd` folder exists under either user profile, there is no `system-images` directory,
and `cmdline-tools` is not installed. So there is no image to build a device from and no command-line
route to fetch one — it is Android Studio's Device Manager, and the download is Alex's to start and
her disk it lands on. Claude can drive the emulator once it exists, the same way it drives the phone.

Walkthrough:

1. In Android Studio, open the Device Manager — the phone icon in the right-hand toolbar, or
   View → Tool Windows → Device Manager. Look for: a panel listing devices, with none under Virtual.
2. Click the **+** (Create Virtual Device). Look for: a hardware picker listing phone models.
3. Pick any recent phone (a Pixel is the obvious choice) and continue to the system-image step. Look
   for: a list of Android versions, most with a download arrow beside them.
4. Choose an image whose API level is at or above the app's minimum and start its download. Look for:
   the download completing, and the arrow turning into a selectable entry. Expect this to be large and
   to take a while.
5. Finish the wizard, then press the play button beside the new device. Look for: a phone window
   opening on your screen and reaching the Android home screen.
6. Tell a planning session the emulator exists and what it is called.

Observable: `emulator -list-avds` from the SDK's `emulator` folder prints the device's name, and
`adb devices` lists it while it is running. A later session checks that rather than asking.

Rests on, read 2026-09-12 on this machine: that `emulator.exe` exists at
`…/Android/Sdk/emulator/emulator.exe` and lists no AVDs; that neither `…/Android/Sdk/system-images/` nor
`…/Android/Sdk/cmdline-tools/` exists; and that no `.android/avd` folder exists under either user
profile. Android Studio's Device Manager wording is amended between releases, so read the step against
what is on screen rather than expecting an exact match.

Filed on 2026-09-12 during planning, from the look-back over this session.
Filed 2026-09-12 11:48, stamped by the queue tool.

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

#### Subtasks in the edit dialogue have no affordance, so nobody finds them [subtask-affordance-in-edit-dialogue]
Blocked by: [verify-edit-outliner-fix]

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

Help's other two topics — Claude setup, and the suggested custom-instruction text — and the Thanks
paragraph are still to come from [help-thanks-report-content].

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
Blocked by: [verify-edit-outliner-fix]

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

#### Last session advises processing [verify-edit-outliner-fix] next [forward-advisory]

**Open on [verify-edit-outliner-fix], and treat the queue as ordered for daily use
rather than for publishing.** Alex decided on 2026-09-12 that she wants about a
month of using Taskflow for real, changing it as she goes, before considering
publishing at all — and that the publishing blockers sit in another project waiting
on her financial and tax position. The whole paid-tier chain is parked behind that.

The condition, stated as a condition: nothing verifies that subtasks can be created
on a device. The fix shipped on 2026-09-06 and compiles; nobody has opened the app
and tried it. Two items wait on that check — the hint advertising the feature, and
[first-end-to-end-test], which is Alex's first real day of use and the thing her
month starts with. So this one check is what releases the run of work she actually
wants.

**Two things a run should reach early for the same reason.**
[drag-eaten-by-page-swipe] is the fix that makes deleting a task possible at all —
for a month of daily use with her own tasks, that is not cosmetic, and its absence
has already forced three separate checks in this session to tick things off instead
of removing them. [emulator-for-instrumented-tests] is her own errand, and until it
lands, any run whose work needs an instrumented test has to take a JSON export off
the phone first or it will delete her tasks.

**The overlap scan found nothing waiting that touches this.** Unprocessed holds four
entries and every one is set aside: two on dates Alex approved (2026-10-12 and
2026-11-25), and two on other entries — the Help wording, and the free-choice roster
slot filed in that session. So nothing unprocessed contradicts or would benefit the
cleared work, and there is no reason to plan before building.

One thing to carry rather than rediscover: five of the sixteen entries processed on
2026-09-12 had premises that did not survive contact with the code or the record —
a guard that already existed, a blocker that had resolved itself, work already done,
a defect that never happened, and a mechanism blamed on the wrong thing. Reading the
mechanism before describing the build is what caught each one.

Filed on 2026-09-12 at the close of that planning session.
Filed 2026-09-12 12:02, stamped by the queue tool.

#### Help's Claude topics, and what the Thanks screen says [help-thanks-report-content]
Blocked by: [0019-ai-choice-flow-and-mcp-setup], [0020-remote-mcp-server]

What is left of the words for the bottom-of-drawer screens that
[0022-help-thanks-report-a-bug-content] builds, after the writable parts were folded into that item on
2026-09-12. Three things remain, and they are stuck for two different reasons.

**Two wait on the Claude work.** Help must explain how to set Claude up, and must carry the production
version of the suggested custom-instruction text — the wording a user pastes into their own Claude
preferences. Neither can be written before [0019-ai-choice-flow-and-mcp-setup] and
[0020-remote-mcp-server] define the path. The custom-instruction drafting was folded in here on
2026-08-25 from [custom-instruction-production-text], which kept only the live test; that test reads
the draft this item produces and reports back what to change.

**One waits on a decision, not a dependency: what the Thanks screen actually says.** Nobody has settled
who or what the app is thanking. Recorded as a distinct blocker on 2026-09-12, because it had been
travelling with the two above as though it were waiting on the same thing — it is one question for
Alex, answerable at any time.

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

Observable: nothing in this repository changes when this is done, so completion is not checkable from
here — a later session asks rather than checks. Stated plainly so nobody builds a check that cannot
work.

Filed 2026-09-03 during planning, at the moment the account-type choice was settled. The capture that
held that choice was deleted in the same move, its facts folded into
[play-console-subscription-product], which is where they are used.

#### Free-choice roster slot — a repeating task that offers two names and waits for the user to pick [rotating-roster-free-choice]
Blocked by: [rotating-roster-recurrence]

Split out of [rotating-roster-recurrence] on 2026-09-12, with Alex's agreement, because it is a
different feature wearing the same coat.

What it is: one position in a repeating task's rotation that does not name a single subject. Instead it
offers two — "either of these two, whichever suits" — and the user resolves it when the instance lands.
Alex's own rotation has exactly one such position among its six.

Why it is not part of the rotation itself. A rotation is deterministic: the app knows what comes next
and can render it without asking anybody. A free-choice position cannot be rendered that way. It has to
interrupt the user, which Taskflow has no surface for — there are no notifications in v1 (SPEC §No
notifications in v1) — and it needs a settled answer for the case the user never picks, which the
rotation has no equivalent of. Bundling the two would make the straightforward half wait on the
awkward one.

Held against [rotating-roster-recurrence] because there is no rotation for a position to sit inside
until that ships, and because the shape of a position is decided there.

What a design would have to settle, none of it decided here: where the choice is presented, given the
app never interrupts; what the task shows while the choice is unresolved; and whether an unresolved
instance blocks the rotation from advancing or is simply passed.

Filed 2026-09-12 during planning, at the moment the split was agreed.
Filed 2026-09-12 11:12, stamped by the queue tool.

