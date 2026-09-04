# QUEUE

## Processed

> Vetted work, ready to build — worked top to bottom. Each piece of work is one
> item: a `#### ` heading naming it, a `[slug]` at the end of that heading line, and
> a short rationale beneath. A leading flavor tag names how it runs — none for a
> build (Claude edits files), `[audit]` for a review pass, `[user]` for a step only
> you can do. A security or privacy risk Claude surfaces lives here too, as a work
> item carrying a `Red flag · State: cleared/uncleared` marker. The line below marks
> how far down is cleared to build; anything below it is decided but not ready yet.

#### Move Gradle's build output to a short path outside Drive [project-out-of-drive]

Points Gradle's build directory at `C:\builds\taskflow` instead of `app\build` inside the project. The
project itself does not move. This is aimed at the recurring "Unable to delete directory …\app\build\…"
failure that has now hit twice.

**The slug predates the decision.** It was filed as "Google Drive is the likely cause of the recurring
build lock", proposing the project be moved out of Drive. That is not what this item does. The slug is
kept because [install-current-build-on-device] and [verify-run-2026-08-31] already cite it, and a slug
is stable through a rename.

**Why the original hypothesis was demoted, 2026-09-03.** You said this never happens in your other
Android Studio projects, which Drive alone does not explain. Measuring the paths found something that
does: **sixteen files under `app/build` exceed Windows' 260-character path limit**, the longest at 279.
The project's folder path is 99 characters before the project name begins. "Unable to delete directory"
is what a deletion looks like when it walks a tree and meets files it cannot address, so path length now
leads and Drive is a co-suspect rather than the explanation.

**Stated as an open question rather than settled:** `LongPathsEnabled` is `1` on this machine, which
should lift that limit. Whether Gradle's Java processes actually honour it was not established and is
not guessed at here. Neither cause is proven, and this item does not need to prove one — the fix
addresses both. The longest path drops from 279 characters to roughly 190, and the folder Gradle
rewrites constantly leaves Drive's sync entirely, taking about 59 MB of repeated uploads with it.

Files:
- `app/build.gradle.kts` — set the module's build directory from an optional `buildDir` entry read out
  of `local.properties`, via `layout.buildDirectory`. Where the entry is absent, behaviour is exactly
  as it is today, so a clone on any other machine builds normally.
- `local.properties` — add `buildDir=C:\\builds\\taskflow\\app`. This file is already git-ignored and
  already carries the SDK path, so the machine-specific value stays out of the repository.

Reads but does not change: `gradle/libs.versions.toml` and `settings.gradle.kts`, to confirm the Gradle
version supports `layout.buildDirectory` and that no setting there fixes the build path.

Observation: after a build, `C:\builds\taskflow\app` holds the compiled output, `app\build` is gone or
empty, no path under the new location exceeds 260 characters, and the build completes without the
delete failure. The check reaches both files named above.

**Who can observe it.** Claude makes the change and can inspect the filesystem afterwards, but cannot
compile — see [tools-md-device-capability]. The build is one press of Run in Android Studio, which is
yours, and the same press produces the APK [install-current-build-on-device] needs — that item was
moved to the end of the cleared region at the 2026-09-04 close, so one press covers this change and
every other build in the run.

**If it goes wrong, it is one line to undo.** Deleting the `buildDir` entry from `local.properties`
restores today's behaviour with no other change. The risk of putting it first is that a broken build
also blocks the install item behind it; that is visible immediately and reversible in seconds, which is
why it was accepted rather than sequenced around.

Refused: moving the whole project out of Drive — your call on 2026-09-03. Every one of your projects
lives under the same parent folder, so moving this one alone would put it somewhere different from all
the others, a cost paid every time you go looking for it, for a benefit this change already delivers.
Refused: excluding `app/build` from Drive's sync — Drive for desktop's folder selection decides what
exists on the machine, not what is uploaded, so excluding the folder Gradle must write is incoherent
rather than merely unsupported. Refused: hardcoding the path into `app/build.gradle.kts` — it is a
committed file in a public repository, and a machine-specific absolute path there breaks every other
checkout.

Rests on, each read 2026-09-03: that sixteen files under `app/build` exceed 260 characters with the
longest at 279; that the project root path is 99 characters; that `LongPathsEnabled` is `1` on this
machine; that the wrapper is on **Gradle 9.4.1**, where `layout.buildDirectory` is the live API and the
old `buildDir` property is gone, so the modern form is required rather than preferred; and that
`.gitignore` excludes `local.properties` on two lines, with the file untracked. Not established,
deliberately: whether Gradle's JVM honours the long-path setting.

#### Stop wiping the device on every schema change [durable-local-data]

Taskflow currently destroys every task on the device whenever its database shape changes. This item
puts a floor under that: version 5 becomes the oldest schema whose data must survive, and every change
after it has to carry a real migration.

captured by you, 2026-09-01, at the moment an install prompt warned it would delete the app's data.
Your point: the goal is a state where your tasks live in Taskflow stably enough that you can start
actually using it, and that is a requirement rather than a nice-to-have. The justification written
into the code — that a wipe is "acceptable while there are no real users" — is retired by you becoming
one, so the decision needed remaking rather than merely honouring.

Read in the code on 2026-09-03, and all of it checks out. `TaskflowDatabase` is at `version = 5` with
`exportSchema = false`, built with `fallbackToDestructiveMigration(dropAllTables = true)`, and there is
no `app/schemas` folder. The last fact is the one that bites: Room writes and tests migrations against
a *recorded* schema, so today no migration could be written even if someone wanted one. The version
comments record four bumps — v2 non-null project IDs, v3 recurrence, v4 completion timestamps, v5 life
areas — three of which landed in the single run that closed on 2026-09-02, each emptying the device.

**Scope, settled with you on 2026-09-03: surviving app upgrades.** Surviving a reinstall or a lost
phone is a different mechanism and is left where it already sits — Android Auto Backup, on by default
per SPEC §JSON export and import, and the JSON export shipped in the same run. Neither has been tested,
which is part of [verify-run-2026-08-31]. The full answer to a lost phone is cloud sync, which is paid
tier and now waits on [supabase-project-setup].

Nothing needs migrating today, because v5 is current. The work is building the floor, so that the next
schema change cannot quietly fall back to a wipe.

Files:
- `app/build.gradle.kts` — pass Room's schema-location argument to ksp so schemas are exported to
  `app/schemas`, and point the androidTest source set's assets at that folder so `MigrationTestHelper`
  can read them.
- `app/src/main/java/com/example/taskflow/data/local/TaskflowDatabase.kt` — set `exportSchema = true`,
  and replace the blanket `fallbackToDestructiveMigration(dropAllTables = true)` with
  `fallbackToDestructiveMigrationFrom(dropAllTables = true, 1, 2, 3, 4)`, which is Room 2.7's overload
  (2.7.1 is the version in `gradle/libs.versions.toml`, and the existing call already uses the 2.7
  signature). A v5 database is then no longer allowed to be thrown away. Rewrite the comment beside it
  so it states the new rule rather than the retired justification.
- new `app/schemas/com.example.taskflow.data.local.TaskflowDatabase/5.json` — generated by the build,
  and committed, because it is what every later migration is written against.
- new `app/src/androidTest/java/com/example/taskflow/data/local/MigrationTest.kt` — a
  `MigrationTestHelper` test that creates a version 5 database, writes a task into it, closes and
  reopens it, and asserts the task is still there. It proves the recorded schema is usable and gives
  the next schema change somewhere to add its migration case.

Reads but does not change: `gradle/libs.versions.toml`, for the Room version.

Observation: after a build, `app/schemas/com.example.taskflow.data.local.TaskflowDatabase/5.json`
exists; a grep for `fallbackToDestructiveMigration(` finds no remaining bare call; and the migration
test passes. The check reaches the four files named above.

**Who can observe it, stated plainly.** Claude cannot run Gradle on this machine — the failure is
recorded in TOOLS.md and was what stopped the 2026-09-02 run compiling anything — so the schema file
is produced, and the test run, by a build in Android Studio, which is yours. The code and config
changes are Claude's. [project-out-of-drive] is the capture about the underlying build problem.

Refused: writing migrations for versions 1 to 4. No data exists at those versions that anyone wants,
and the effort would buy nothing — hence a floor at 5 rather than at 1.

Rests on, each read 2026-09-03: that the database is at version 5 with `exportSchema = false` and a
blanket destructive fallback; that no `app/schemas` folder exists; that Room 2.7.1 is in use; and that
Auto Backup is on by default per SPEC.

#### Taskflow follows the phone's light/dark setting [compose-dark-theme]

Taskflow has no colour scheme of its own, so it renders light on every phone whatever the phone is set
to. This item gives it a light and a dark palette and makes it follow the system setting.

Found on 2026-09-02 while driving [verify-blank-new-task-form] on the device, when the onboarding
screens rendered dark-on-dark. That instance was fixed in the same run by wrapping the screen in a
Surface; the condition underneath was left alone. Confirmed against your own device on 2026-09-03,
captured by you: your phone is in dark mode and the app is always white.

The mechanism, read in the code on 2026-09-03. `MainActivity` calls `MaterialTheme { AppRoot() }` with
no colour scheme argument, so Compose falls back to its built-in **light** palette regardless of the
phone. The Android window underneath does follow the system — `res/values/themes.xml` uses
`android:Theme.Material.Light.NoActionBar` and `res/values-night/themes.xml` overrides it to the dark
parent — so on a dark phone the window is dark while everything Compose paints on it is light. Where a
screen paints its own background the result is a light screen in a dark frame; where a screen paints
none, the light palette's near-black text lands on the dark window and is unreadable, which is what the
onboarding screens did.

Why this is cheap, and it is the fact the decision turned on: a grep on 2026-09-03 found **zero**
hardcoded colours across the app's 40 Kotlin files — no `Color(0x…)`, no `Color.White`, `Color.Black`
or the greys. Every colour already resolves through `MaterialTheme.colorScheme`, so defining the
schemes is the whole job and no screen needs touching.

Whether Taskflow has a dark theme at all was a product question rather than a defect call, and it was
settled by you on 2026-09-03: follow the system. The reason given in that discussion — a phone in dark
mode at 1 AM is exactly the person SPEC §Settings → Day begins at exists for, and a task app that
answers them with a full white screen fails them. SPEC gains §Light and dark in the same planning
session.

Files:
- new `app/src/main/java/com/example/taskflow/ui/theme/Theme.kt` — a `TaskflowTheme` composable
  holding a `lightColorScheme()` and a `darkColorScheme()`, choosing between them with
  `isSystemInDarkTheme()`, and wrapping its content in `MaterialTheme` with the chosen scheme. Palette
  values may sit in a sibling `Color.kt` if they run long enough to be worth splitting out.
- `app/src/main/java/com/example/taskflow/MainActivity.kt` — `MaterialTheme { AppRoot() }` becomes
  `TaskflowTheme { AppRoot() }`.

Reads but does not change: `app/src/main/res/values/themes.xml` and
`app/src/main/res/values-night/themes.xml`. The window themes already follow the system and stay as
they are; this is what makes the content agree with them rather than the other way round.

Observation: on a device with the phone in dark mode, every screen renders dark with readable text,
onboarding included — it paints no background of its own, so it is the screen that fails first. Switch
the phone to light mode and every screen renders light. The check reaches both files named above.

Refused: light only, deleting `values-night` — the cheapest fix and it would end the disagreement, but
it hands a full white screen to the 1 AM user the app is designed around. Refused: an in-app
appearance setting — the phone already holds that preference, so it would be a Settings row to build
and maintain for a choice Android has already made.

Rests on, each read 2026-09-03: that `MaterialTheme` with no `colorScheme` argument uses Compose's
light palette; that no hardcoded colours exist anywhere under `app/src/main`; and that the two window
theme files are light and night as described.

#### Narrow TOOLS.md's claim that Claude cannot test this app [tools-md-device-capability]

TOOLS.md's closing line reads "Claude cannot compile, test or install this app." Only the first of those
three is true, and the sentence has been read as all three. This item replaces it with two accurate
lines.

Found on 2026-09-03 while processing [verify-run-2026-08-31]. The capability check that item's decision
step required turned up what nobody had tried: `adb` is present, a phone was connected, and Taskflow was
installed on it. The blanket sentence is why nobody had tried — nineteen items shipped on 2026-09-02
with UNCONFIRMED ticks against them and the device sat untouched for two days, because the environment
file said device work was impossible. Not a wrong fact; a true fact (Gradle is broken from Claude's
shell) stated far wider than it holds.

Files:
- `TOOLS.md` — replace the final bullet, the one beginning "Consequence: Claude cannot compile, test or
  install this app", with three bullets:
  1. Claude cannot **compile** this app. Producing a build is Android Studio's job and the user's, so
    code items tick UNCONFIRMED until a build has run, naming the build as the check nobody has done.
    (Keep the existing date, 2026-08-31, on this one — the fact is unchanged, only its scope.)
  2. Claude **can** drive the app on a connected device. `adb.exe` is at
    `C:\Users\Alex\AppData\Local\Android\Sdk\platform-tools\` — not on PATH, which is why it reads as
    absent to `which adb`. On 2026-09-03 a phone answered `adb devices` over wireless debugging with
    `com.example.taskflow` installed. Device verification is therefore Claude's work rather than the
    user's. (2026-09-03)
  3. The app's database sits almost entirely in a write-ahead log — the main file is one page while the
    WAL holds the rest — so its schema version cannot be read from the file header on the device. Doing
    it properly means copying the database off the phone, which carries the user's real task content.
    (2026-09-03)

Observation: a grep of `TOOLS.md` for "cannot compile, test or install" returns nothing, and the file
names the `platform-tools` path. The check reaches the one file named above.

Refused: deleting the old line outright. The Gradle limit is real and load-bearing — it is why builds
are the user's — so the correction narrows the claim rather than removing it.

Rests on, each read 2026-09-03: that `adb.exe` sits in that SDK path; that a device answered
`adb devices`; that `com.example.taskflow` was installed on it; and that the on-device database file
carries a 4 KB main file against a much larger `-wal`.

#### Back gesture closes Taskflow from the spine, unhandled [left-edge-swipe-collision]

**Rescoped on 2026-09-03, on your reasoning.** This was filed as a three-way collision over the left
edge and read as something that had to be settled before the pages left of Today could be built. It is
not. Swiping between pages is one mechanism, not two: adding Yesterday and Search to the page list
makes right-swipes work exactly as left-swipes already do, and there is no new gesture to build. And
Android's back gesture lives on **both** edges, so the outermost-strip contest has been present all
along in the direction that works fine. A collision the app already lives with, in the direction you
use daily, is not a gate on the direction that does not exist yet. The slug is kept because
[nav-completed-history] cites it.

**What is actually left, and it is real but small.** Read in the code on 2026-09-03: Taskflow is a
single activity, and `AppRoot.kt` enables its `BackHandler` only while an edit dialogue or a menu
overlay is open. On the bare spine, back is unhandled — so an edge back-swipe on Today, Tomorrow, Soon
or Later closes the app. Nobody chose that; it is what falls out of never having decided.

**Settled by you on 2026-09-03: back returns to Today, and closes the app only from Today.** That is
the standard Android pattern for an app whose top level is a row of pages. SPEC §Schedule view gained
the sentence in the same planning session.

Files:
- `app/src/main/java/com/example/taskflow/ui/navigation/AppRoot.kt` — the existing `BackHandler`, today
  enabled only when `editTarget != null || overlay != null`, also becomes enabled when neither is open
  and the pager is off Today; in that case it animates the pager to `SpinePage.TODAY`. On Today with
  nothing open it stays disabled, so the system closes the app as it does now. The dialogue and overlay
  branches are untouched.

Reads but does not change: `ui/navigation/Destination.kt`, for `SpinePage` and its ordering.

**One future case is named here so it is not missed.** [nav-day-card-layer] adds a day-detail card that
opens in front of the spine, and back closes that card rather than returning to Today — settled with
you on 2026-09-03, the same "up one level" meaning the dialogue and overlay branches already carry.
That item does the wiring when it builds; this item only has to leave the handler's structure able to
take a third case, rather than hard-coding two.

Observation: on a device, back from Later, Soon or Tomorrow lands on Today; back again closes the app;
back with an edit dialogue open still closes the dialogue and back with a menu overlay open still
closes the overlay. The check reaches the one file named above.

Refused: back stepping one page left. It reads well until someone swipes right three times, at which
point back walks them through pages they never visited. Refused: leaving it as it is — the app closing
from a mid-spine page is a surprise nobody chose.

Not owed an edit after all: SPEC §Side menu explains the disabled drawer swipe by naming the collision
with the spine's own swiping, which is still accurate. The original capture expected that sentence to
need rewriting, on the larger reading of this item that was withdrawn.

Rests on, each read 2026-09-03: that `AppRoot.kt` holds the pager state and gates its `BackHandler` on
`editTarget` and `overlay`; that `SpinePage` has four entries with `TODAY` first; and that Android caps
system-gesture exclusions at 200dp per edge, which is why no exclusion is attempted here.

Not established: whether the phone in use is on gesture navigation or three-button navigation. The
device dropped off wireless debugging mid-check on 2026-09-03. On three-button navigation there is no
edge-back at all and none of this is reachable, which would explain the original report that nothing
happened.

Also refused, 2026-09-03: claiming a gesture-exclusion zone for the left edge. Android caps exclusions
at **200dp per edge** — roughly four touch targets on a screen many times taller — so the app can only
ever take a band, leaving paging to work in part of the edge and back in the rest. Worse than either
clean answer, and it was ruled out on that.

The original capture is kept below, since it is the reasoning that produced this one.

---

captured by you, 2026-09-02, while being asked to swipe right from the left edge and expect nothing
to happen. Your objection: that gesture contradicts the environment — everywhere else a swipe right
means the screen to the left comes in.

The objection lands, and it exposes a collision nobody has named. Three things want the left edge:

- **Android's system back gesture**, which is an inward swipe from the left edge and belongs to the
  OS rather than to Taskflow.
- **The spine's own navigation**, which SPEC §Schedule view now runs as Search · Yesterday · Today ·
  Tomorrow · Soon · Later · Strategy — so a swipe right on Today is supposed to bring Yesterday in
  from the left, exactly as you expect.
- **The drawer's swipe-to-open**, which SPEC §Side menu already disables for precisely this reason,
  naming the collision with the spine but not the one with system back.

Today the conflict is invisible because Today is the leftmost page built, so a swipe right has
nowhere to go and does nothing. It becomes real the moment [nav-completed-history] builds Yesterday
and Search: from then on the user's edge swipe is contested between paging left and going back, and
which one wins is decided by the OS's gesture-exclusion behaviour rather than by anything Taskflow
has chosen.

What this needs is a decision before that work is built, not after: whether the spine's leftward
navigation is reachable from the edge at all, or only from further in, and whether Taskflow claims
any gesture-exclusion zone. SPEC §Side menu currently explains the disabled drawer swipe by naming
only the spine, so whichever way this goes, that sentence is owed an update.

Also noted from the same moment: this made the walkthrough step in [verify-drawer-swipe-off-on-device]
weaker than it reads. "Swipe from the left edge and the menu does not open" is satisfied by the
drawer being off *and* by Today simply having nowhere to go, so it does not isolate what it means to
test. Its look-for was sharpened while driving it.

#### Yesterday page, and the spine plumbing that lets it exist [nav-yesterday-page]

Adds **Yesterday** to the row of pages, immediately left of Today, showing what the user completed
yesterday. It also carries the shared plumbing the whole left half needs, which is why it goes first.

Split out of [nav-completed-history] on 2026-09-03 with your agreement, after that item's own blocker
([nav-left-spine-spec-edit]) shipped and SPEC gained §Yesterday page as decided product truth. It is
first of the four because it is the simplest real page and because the enum change below is a
precondition for the other three.

Foundations confirmed in the code on 2026-09-03: `Task` carries `completedAt: Long?` alongside
`is_completed`, and `TaskDao.getCompletedTasks(): Flow<List<Task>>` already exists. Nothing new is
needed in the data layer.

Files:
- `app/src/main/java/com/example/taskflow/ui/navigation/Destination.kt` — `SpinePage` currently pairs
  each entry with a non-null `ScheduleSlot`. Yesterday has no slot, so `slot` becomes `ScheduleSlot?`
  and a `YESTERDAY` entry is added **before** `TODAY`. This is the shared change the other three
  left-half items build on.
- `app/src/main/java/com/example/taskflow/ui/schedule/ScheduleScreen.kt` — the pager's page content
  branches on a null `slot`, rendering the Yesterday screen instead of a slot list.
- `app/src/main/java/com/example/taskflow/ui/navigation/AppRoot.kt` — confirm the pager still opens on
  Today. `initialPage = SpinePage.TODAY.ordinal` already reads the ordinal, so it follows the enum, but
  it is named here because a wrong opening page is the most likely regression.
- new `app/src/main/java/com/example/taskflow/ui/history/YesterdayScreen.kt` and its ViewModel — the
  list of tasks whose `completedAt` falls inside yesterday, newest first. Yesterday's bounds are
  computed from the day-begins-at setting, not from midnight, per SPEC §Settings → Day begins at.
- `app/src/main/java/com/example/taskflow/ui/navigation/AppDrawer.kt` — a **Yesterday** row in the
  menu's navigation list, directly above Today. Folded in from [side-menu-spine-mismatch] on
  2026-09-03, which was deleted at that point: SPEC §Side menu now says the menu mirrors the whole
  spine, and a row navigating to a page that does not exist yet is the only thing a separate item could
  have delivered, so each page's row ships with that page.

Reads but does not change: `app/src/main/java/com/example/taskflow/data/local/TaskDao.kt`, for the
existing completed-tasks flow; and `app/src/main/java/com/example/taskflow/data/settings/` for
day-begins-at.

Observation: on a device, swiping right from Today opens a page headed Yesterday listing what was
completed yesterday; a task completed today does not appear on it; swiping left returns to Today; and
the app still opens on Today after a relaunch. The check reaches the four files named above.

Refused: giving Yesterday its own `ScheduleSlot`. It is a history view, not a place tasks can be put,
and a slot would make it a capture target — which SPEC §Add a new task rules out by naming the four
add surfaces.

Rests on, each read 2026-09-03: that `SpinePage` has four entries carrying a non-null `ScheduleSlot`;
that `Task.completedAt` exists; and that `TaskDao.getCompletedTasks()` returns a flow of completed
tasks.

#### Search and completed-history page, the new leftmost page [nav-search-completed-history]

Adds the **Search** page at the far left of the row: a search field above a dated list of completed
tasks, newest first, with a date header between each day's results.

Split out of [nav-completed-history] on 2026-09-03. **Build it after [nav-yesterday-page]** — both edit
the same two files, and that item makes `SpinePage.slot` nullable, which this one depends on. An
ordering preference written here rather than a blocker, so the item stays visible.

Files:
- `app/src/main/java/com/example/taskflow/ui/navigation/Destination.kt` — a `SEARCH` entry added before
  `YESTERDAY`, with a null slot.
- `app/src/main/java/com/example/taskflow/ui/schedule/ScheduleScreen.kt` — the null-slot branch routes
  to the search screen for this page.
- new `app/src/main/java/com/example/taskflow/ui/history/SearchScreen.kt` and its ViewModel — the search
  field, and beneath it completed tasks in completion order with per-day headers. Typing narrows the
  list; the date headers for days still holding results stay above them.
- `app/src/main/java/com/example/taskflow/data/local/TaskDao.kt` — a query matching a search term
  against task titles across active and completed tasks, and against Project names. The Strategy doc is
  excluded, per SPEC §Search and completed history.
- `app/src/main/java/com/example/taskflow/ui/navigation/AppDrawer.kt` — a **Search** row at the top of
  the menu's navigation list, above Yesterday. Folded in from [side-menu-spine-mismatch] on 2026-09-03,
  which was deleted at that point. SPEC §Side menu now says the menu mirrors the whole spine, and names
  Search as the row that earns its place most: it is the page the user reaches for when they have lost
  something, which is exactly when swiping around hunting for it is the wrong answer.

Observation: on a device, swiping right from Yesterday opens the Search page; it lists completed tasks
newest first under date headers; typing part of a task's title narrows the list and leaves the headers
above the days that still match; typing a Project's name matches that Project; a result cannot be
completed or edited from the list, and tapping one navigates to where the task lives. The check reaches
the four files named above.

Refused: scoping search to the current page or Project — SPEC settles this, on the reasoning that a
task someone is searching for is one they have lost, so the app's own organising principles are exactly
what they cannot use at that moment.

Rests on, read 2026-09-03: that `TaskDao` holds no title-search query today; and SPEC §Search and
completed history for the scope and the read-only rule.

#### Strategy becomes the spine's rightmost page, not a drawer overlay [strategy-on-spine]

Moves the Strategy doc from a "deep destination" overlay shown over the spine to being the spine's
rightmost page, reached by swiping right from Later. Its menu row stays, scrolling the pager to it the
way the four slot rows already do.

**No SPEC edit: SPEC already says this, and has twice.** Found on 2026-09-04 while looking at the
navigation code, and settled by reading the record rather than by re-deciding it — you asked for that
retrieve, and it changed the answer:

- `LOG/nav-spine-spec-edit.md` (June) records the spine as Today · Tomorrow · Soon · Later · Projects ·
  Strategy, "with Strategy the page to its right", and says §Strategy doc's row sentence was rewritten
  "keeping the reachable-but-never-foregrounded intent" — which is about where the menu row sits and
  Today remaining the default page, not about excluding Strategy from the spine.
- `LOG/later-by-project-spec-edit.md` (2026-06-22) restates it after the Projects page was folded into
  Later: the right end of the spine becomes "one 'zoom by time' flow — Today, Tomorrow, Soon, Later,
  Strategy".
- SPEC §Schedule view and UX principle 3 both carry it today: the spine "extends rightward into
  Strategy".

**Why the code differs, and why that reason has expired.**
`LOG/0003-side-menu-schedule-projects-app-actions.md` records the divergence as a deliberate build
choice: "'deep' destinations — a Project, Strategy, the app actions — are shown as a single placeholder
overlay over the spine", chosen over Jetpack Navigation because "every deep destination is a
placeholder in this batch, so a navigation-library dependency and its boilerplate buy nothing now —
the overlay can be swapped for NavHost later if destinations multiply". Strategy's real screen then
shipped in [0015-strategy-doc-and-life-area-context] and the routing was left alone. So the overlay
was right when every deep destination was a placeholder, and Strategy stopped being one.

**Corrected in the same session:** this was first described as SPEC being ambiguous about whether
Strategy is swipeable or menu-only. It is not ambiguous. That reading is recorded here so it is not
reached for again.

Files:
- `app/src/main/java/com/example/taskflow/ui/navigation/Destination.kt` — a `STRATEGY` entry added to
  `SpinePage` after `LATER`, with a null slot, and `Overlay.Strategy` removed.
- `app/src/main/java/com/example/taskflow/ui/schedule/ScheduleScreen.kt` — the null-slot branch renders
  `StrategyScreen` for that page.
- `app/src/main/java/com/example/taskflow/ui/navigation/AppRoot.kt` — the `Overlay.Strategy` branch that
  renders `StrategyScreen` over the spine is removed, and the drawer's Strategy tap scrolls the pager
  to that page instead of setting an overlay, matching what the slot rows already do.
- `app/src/main/java/com/example/taskflow/ui/navigation/AppDrawer.kt` — the Strategy row's callback
  moves from the app-action/overlay path to the page-navigation path. Its position in the list is
  unchanged.

**Build it after [nav-yesterday-page]**, which makes `SpinePage.slot` nullable — this item needs that
change and does not repeat it. An ordering preference written here rather than a blocker, so the item
stays visible. Nothing else depends on this one.

Reads but does not change: `app/src/main/java/com/example/taskflow/ui/strategy/StrategyScreen.kt`,
which is the real screen already and is rendered from a different place rather than altered.

Observation: on a device, swiping right from Later opens the Strategy doc as a page with the spine
header naming it; the drawer's Strategy row jumps to that page rather than opening something over the
spine; the back gesture from Strategy returns to Today, per SPEC §Schedule view's back rule; and the
Strategy doc's editor and share button still work. The check reaches the four files named above.

Refused: keeping the overlay and editing SPEC to match it. SPEC's account is the older and twice-stated
one, and UX principle 3 rests on it — the spine's whole story is that navigation glides from arranging
time into arranging areas of life, which needs Strategy to be somewhere you can swipe to. Refused:
introducing Jetpack Navigation Compose to do it. 0003 weighed and rejected that, and putting Strategy on
the pager removes a deep destination rather than adding one, so its reasoning holds even more now.

Rests on, each read 2026-09-04: that `SpinePage` holds four entries, Today through Later; that
`Overlay.Strategy` exists in `Destination.kt` and is rendered by `AppRoot.kt`; that `StrategyScreen` is
the real screen rather than a placeholder; and the three LOG entries cited above.

#### Take the Notes field out of the edit dialogue [notes-out-of-edit-dialogue]

Removes the free-text Notes box from the task edit dialogue, leaving the outliner, Project, date and
repeat. The database column stays where it is.

captured by you, 2026-09-02, looking at the dialogue on the device: you did not understand why there
was a Notes field you never asked for. Checked on 2026-09-03 and your not recognising it is evidence
rather than forgetfulness — the earliest trace in the record is the 0005 build entry listing "title,
notes, editable Project incl. unassigned, read-only date" as what it built, with no decision recorded
anywhere about wanting notes. It arrived as part of a minimum-viable editor and was later written into
SPEC as though it had been chosen.

**Settled by you on 2026-09-03: Notes comes out.** The reason: the app exists to reduce the weight of a
task list, and a free-text box invites the user to put work into describing work. The counter-argument
in your capture — that a task sometimes carries a real detail like an address or a phone number — was
weighed and lost, because a subtask line holds "123 Fake Street" perfectly well and needs no field of
its own. SPEC §Edit a task and §Move between Schedule and Project were edited in the same planning
session.

Files:
- `app/src/main/java/com/example/taskflow/ui/edit/EditTaskScreen.kt` — remove the notes text field from
  the dialogue's layout.
- `app/src/main/java/com/example/taskflow/ui/edit/EditTaskViewModel.kt` — remove `notes` from the
  rendered edit state and from the save path, so the dialogue neither reads nor writes it.

**Two exclusions, each stated apart from the files above because getting either wrong is expensive.**
`Task.notes` in `data/model/Task.kt` is **not** dropped, and no schema change is made — see the refusal
below. And `LifeArea.notes` in `data/model/LifeArea.kt` is a **different field**, holding Claude's
working understanding of an area of life; it is untouched, and a change that catches it has gone wrong.

Reads but does not change: `app/src/main/java/com/example/taskflow/data/model/Task.kt` and
`app/src/main/java/com/example/taskflow/data/transfer/TaskflowJson.kt`, which keeps writing and reading
the `notes` key so existing exports stay valid.

Observation: on a device, the edit dialogue shows the outliner, Project, date and repeat and no notes
box; a JSON export still contains a `notes` key for every task; and a task that already had notes still
carries them in that export. The check reaches the two files that change.

Refused: dropping the `notes` column. That is a schema change, and [durable-local-data] — cleared to
run — makes version 5 the floor below which no data may be destroyed, so from that point a removal
needs a real migration written and tested. Taking the field out of the UI costs nothing, breaks no
export, and is reversible by putting the field back if you miss it. The column can be dropped later as
its own item if it is still dead.

Rests on, each read 2026-09-03: that `Task.notes` exists as a column; that `TaskflowJson` both writes
and reads it; that `EditTaskScreen.kt` renders it; that `LifeArea` carries an unrelated field of the
same name; and that SPEC's three mentions of task notes were edited out in this planning session.

#### Date strip — week jump, day-over-month tiles, and no clipped tile [date-strip-legibility]

Three changes to the date picker's side-scrolling strip, all in one file: the jump control moves by a
week as well as a month, each tile shows its day number above the month name, and tiles stop being cut
off at the screen edge.

captured by you, 2026-09-02, from the strip on the device. Two of the three were your proposals; the
clipped tile is what the screenshot showed on its own. Settled with you on 2026-09-03.

**1. The jump control gains a week step.** Read in the code on 2026-09-03: `DateStrip.kt` renders a row
of `‹ month`, the month currently in view, and `month ›`, driven by a `jumpMonths(delta)` function. So
it is a labelled navigator rather than a bare button, and the label is worth keeping. It becomes
`‹‹ ‹ Sep 2026 › ››` — single chevrons move a **week**, double chevrons a **month**. Your reasoning,
and it is the stronger argument: the control's unit should match the view's unit, and the strip shows
about a week, so a month jump lands the user somewhere they have to re-read. **Refused: swapping month
for week outright** — your own capture named the problem with it, that crossing to next April by week
is many presses, so the coarse step is kept rather than dropped.

**2. Tiles show the day number over the month name.** Today they read "24/08 25/08 26/08" in a row,
which runs together into a continuous line of digits with nothing for the eye to catch on. Each tile
becomes 24 with Aug beneath it: one large glanceable number, and the month no longer repeated seven
times in a form that looks like part of the number.

This one reached SPEC and was settled deliberately rather than patched. §Date picker said each tile
showed its date "in DD/MM format (or MM/DD per the user's setting)", while §Settings → Date format said
that setting applied "everywhere a date is shown" — and a tile reading 24 above Aug has no day/month
order left to obey. **The resolution, written into SPEC in this planning session: the date-format
setting governs dates rendered as numbers, and does not reach the tiles**, because the setting exists to
disambiguate digits and a month name leaves nothing ambiguous. **Refused: making the strip a silent
exception to "everywhere"** — the word was doing real work, and quietly breaking it would leave the two
sections contradicting each other for the next reader.

**3. No tile is clipped.** `TILE_WIDTH` is a fixed value, with a comment recording that it was sized so
five to seven tiles sit on a typical phone and a 360dp screen fits six. On your device seven nearly fit
and the seventh renders as "Su" over "30/" with the rest cut off at the edge. Tiles are sized instead so
a whole number of them fills the width available. A tile showing half a date is worse than one fewer
tile. No SPEC change — SPEC never named a tile count, and the count was a build decision from 0006.

Files:
- `app/src/main/java/com/example/taskflow/ui/edit/DateStrip.kt` — the jump row gains a week step
  alongside the month step; the tile composables render the day number above the short month name
  instead of DD/MM; and `TILE_WIDTH` is replaced by a width derived from the available space so a whole
  number of tiles fits.

Reads but does not change: `app/src/main/java/com/example/taskflow/data/settings/SettingsRepository.kt`,
for the date-format setting the tiles now stop consulting.

Observation: on a device, opening the edit dialogue shows tiles reading a day number above a month name
with no tile clipped at the right edge; the single chevrons move the strip seven days and the double
chevrons a month; the month label still names the month in view; and switching the date format between
DD/MM and MM/DD changes task rows while leaving the tiles unchanged. The check reaches the one file
named above.

Rests on, each read 2026-09-03: that `DateStrip.kt` holds a month-jump row driven by `jumpMonths`, a
fixed `TILE_WIDTH` commented as fitting five to seven tiles, and tiles rendering DD/MM; and that SPEC's
two sentences about tile format and setting reach were edited in this planning session.

#### Shorten the AI row and size the drawer to its contents [drawer-ai-row-copy]

The side menu's bottom row becomes **"Turn on AI"**, and the drawer sheet is sized to its own content
instead of taking Material3's default width.

captured by you, 2026-09-02, on seeing the drawer on the device: the menu was too fat, and the bottom
row should read something like "Turn on AI".

**The capture's mechanism was wrong, and the correction is why both halves are in one item.** It read
as though the long row set the drawer's width. It does not: `AppDrawer.kt` calls
`ModalDrawerSheet(modifier = modifier)` with no width, so the width comes from Material3's own
`DrawerDefaults.MaximumDrawerWidth`, which the app never passes and the text cannot influence. You
confirmed on 2026-09-03 that the drawer was too wide and the row sat on **one** line filling that
width — so the row was making an already-wide sheet visible rather than causing it. Shortening the copy
alone would therefore have made it worse: a wide sheet with nothing in it reaching the edge.

**Sized to content rather than to a number.** The sheet's width wraps its longest row plus padding,
bounded by the Material default as a maximum. That follows the copy instead of hard-coding a figure
picked out of the air, and it stays correct if the rows change again. **Refused: choosing a fixed dp
value** — it would be an invented number with no derivation, and it would rot the next time a row is
reworded.

**The copy change is product truth and SPEC was edited in this planning session**, in §Side menu and
§Tier model, both of which carried the old string. Your capture's own point is recorded there: the
phrase was doing two jobs, naming the row and selling the tier, and the screen the row opens is the AI
choice flow, which exists to make that case properly.

Files:
- `app/src/main/java/com/example/taskflow/ui/navigation/Destination.kt` — `Overlay.TurnOnAi`'s `label`
  becomes "Turn on AI".
- `app/src/main/java/com/example/taskflow/ui/navigation/AppDrawer.kt` — the row's text becomes "Turn on
  AI", and `ModalDrawerSheet` gains a width that wraps the sheet's content, bounded by the Material
  default.
- `app/src/main/java/com/example/taskflow/ui/navigation/AppRoot.kt` — the comment quoting the old string
  is updated so it still names what it describes.

**One thing to watch:** `Overlay.TurnOnAi.label` is both the drawer row's text and the title of the
placeholder screen the row opens, so the two move together. That is intended, not a side effect.

Observation: on a device, the drawer's bottom row reads "Turn on AI"; the sheet's right edge sits just
past its longest row rather than at the Material default width; every row still opens what it opened
before; and the screen that row opens is titled "Turn on AI". The check reaches the three files named
above.

Rests on, each read 2026-09-03: that `AppDrawer.kt` passes `ModalDrawerSheet` no width; that
`DrawerDefaults.MaximumDrawerWidth` is Material3's default and maximum drawer width; that the string
lives in both `AppDrawer.kt` and `Destination.kt` with a third mention in an `AppRoot.kt` comment; and
that SPEC's two occurrences were edited in this planning session.

#### Remove the orphaned worktree folder carrying retired instructions [orphan-worktree-cleanup]

Deletes `.claude/worktrees/optimistic-ellis-3ec0fa/`, a dead directory inside this project holding a
July snapshot of Taskflow's pre-migration layout, an early copy of your Claude instructions as
`claude.md.md`, and a copy of the no-code-method project. The check that its contents are superseded
runs first; the deletion follows.

Found at the end of the 2026-09-03 planning session, when you asked whether a `UX.md` still existed —
a retired doc type. It does not exist as a live doc in this project: the principles are a section
inside SPEC.md, and CLAUDE.md's migration note records `UX.md → SPEC.md`. But the search turned up a
real `UX.md` at `.claude/worktrees/optimistic-ellis-3ec0fa/no-code-method/UX.md`, alongside a
`claude.md.md` instructing a reader to search `UX.md` first, to compare `UX.md` against `MANIFEST.md`,
and to file things in `BACKLOG.md`. All three doc types are retired.

**What makes it worth removing rather than ignoring.** It is under `.claude/`, which `.gitignore`
excludes, so none of it is published — that part is fine. The risk is a future session grepping this
folder for guidance, finding those instructions, and following them. That is the same failure the
TOOLS.md correction addressed in the same session: stale text nobody deleted, read as current.

**It is inert, established by three reads.** Its `.git` is not a repository but a one-line pointer to
`C:\Users\Alex\Desktop\Taskflowapp\.git` — a different user profile, at a path that no longer exists,
so git cannot read the directory at all. `git worktree list` in this repository does not include it.
And it is 344K, so the Google Drive sync-weight argument that was first reached for does not apply and
was withdrawn.

**The one thing that makes this a real deletion: git holds nothing.** The repository this folder
belonged to is gone from the path it names, so "delete it, history keeps it" is false here. Whatever
exists only in that folder exists only there. Hence the check before the removal rather than a
straight delete, and hence the deletion being your call, made knowing that.

Files:
- `.claude/worktrees/optimistic-ellis-3ec0fa/` — removed entirely, after the check below. Plain
  directory removal: it is not a registered worktree here, so `git worktree remove` does not apply.

Steps, in order, because the second is irreversible:
1. List the folder's contents and compare each part against its live original — this project for
   Taskflow's files, the no-code-method project folder one level up for that project's, and the live
   root `CLAUDE.md` for the instructions in `claude.md.md`.
2. Where every part is covered, delete the folder. Where anything is not covered, stop, leave the
   folder alone, and file a capture saying what is unique to it.

Observation: `.claude/worktrees/` no longer contains `optimistic-ellis-3ec0fa`, and a grep for `UX.md`
across the project returns nothing outside this item's own text. The check reaches the path named
above.

Refused: leaving it in place because it is gitignored and small. Being unpublished answers the privacy
question and not the misreading one, which is the actual risk. Refused: moving it into this project's
`archive/` folder instead — that folder is tracked, so it would commit a stale copy of an entire
project into a public repository, which is worse than either alternative.

Rests on, each read at the end of the 2026-09-03 planning session: that the folder's `.git` is a
pointer file naming a path with no repository at it; that `git worktree list` here lists only the main
checkout; that the folder is 344K; that the no-code-method project exists as its own live folder; and
that `.gitignore` excludes `.claude/`.

#### [user] Install the current Taskflow build on the device [install-current-build-on-device]

Puts a build of the current code on your phone, so the twelve unrun checks in [verify-run-2026-08-31]
test the code that actually shipped. It is one press of Run in Android Studio; everything after it is
Claude's.

It is `[user]` work for one reason only, recorded in TOOLS.md: Gradle cannot run from Claude's shell on
this machine — every invocation dies on a loopback-socket error — so compiling is Android Studio's job
and Android Studio is yours. Claude can drive the device over adb and can install an APK; what it
cannot do is produce one.

Why it is needed rather than reusing what is there, read 2026-09-03: the phone is running a build
installed **2026-09-01 at 16:41**, while the newest APK on disk was built **2026-09-02 at 16:26** —
after the run's last commit. Installing that existing APK was considered and refused: nothing here can
confirm it is the run's final state, and verifying against the wrong binary produces confident wrong
answers. A fresh build removes the question.

**Run this after the cleared code builds, not before them** — revised at the 2026-09-04 close, when
the close's own step batches the human stops at the end of the cleared region. The first placement put
it second, so the audit would measure the 2026-08-31 run before two new changes landed on top of it.
That reasoning was overtaken: eleven builds are now cleared ahead of it, so installing early would
leave the device stale again by the time anything was checked. Running it last means one press of Run
covers every build in the run, and the audit then measures the app as it actually stands. That is an
ordering preference and not a blocker.

Walkthrough:

1. Open the Taskflow project in Android Studio. Look for: the project tree loaded and the status bar
   reporting Gradle sync finished rather than an error.
2. Connect your phone and pick it in the device dropdown at the top of the window. It was reachable
   over wireless debugging on 2026-09-03, so it may already be listed. Look for: your device's name
   showing in that dropdown.
3. Press Run. Look for: Taskflow launching on the phone by itself.

**Expect the app's data to be wiped, and do not read it as a new fault.** Taskflow currently falls back
to destroying the database whenever the schema changes, which is precisely the behaviour
[durable-local-data] is queued to end. If your tasks vanish on this install, that is the known bug
rather than a fresh one. Nothing here depends on the old data surviving.

If the build fails with "Unable to delete directory …\app\build", that is the Google Drive file lock
CLAUDE.md's Project rules already describe — ask a session to delete `app\build` and press Run again.
[project-out-of-drive] is the capture about the underlying cause.

Observable: Claude reads the install time off the device over adb and sees a timestamp later than
2026-09-02 16:26. This is checkable without asking you, so nothing waits on you remembering to report
it.

#### [user] Create the Taskflow Supabase project [supabase-project-setup]
Red flag · State: cleared

Supabase is where Taskflow's paid tier runs — the cloud store the app syncs to and the host of the
MCP server Claude connects to, settled by you on 2026-09-03. Nothing on the paid tier can be built
until that project exists, so this is the step everything else waits behind.

It is `[user]` work because it starts with creating an account, which needs your own identity and
eventually your card, and ends with credentials only you can read out of your own dashboard. The
capability check on 2026-09-03: no tool available here can sign up for a service on your behalf, and
account creation is barred to Claude in any case.

**The risk, and how it is designed out.** This repository is public. A Supabase database password
committed into it would be readable by anyone, permanently, and removing it later would not untrack
what was already pushed. So the walkthrough sends the database password to your password manager and
never into the project folder, and sends the app's connection values to `local.properties`, which
`.gitignore` excludes on two separate lines (checked 2026-09-03). Nothing this step produces lands in
a tracked file. Red flag cleared by that design rather than by accepting the risk.

Stay on the **Free** plan for now. Free is enough to build against; the reason it cannot ship is that
Supabase pauses a free project after a week of inactivity, so the move to Pro at $25/month belongs
with launch, not with this step. That is recorded in [0018-cloud-sync-paid-tier].

Walkthrough:

1. Go to `supabase.com` and sign up, or sign in if you already have an account. Look for: a dashboard
   page listing your organizations.
2. Open `https://supabase.com/dashboard/new/_` to start a new project. Give it the name **Taskflow**,
   let the page generate the database password rather than typing one, and pick the region closest to
   you. Look for: the new project's page. It may say it is still setting up for a minute or two.
3. Copy the generated database password into your password manager and save it there now, before
   leaving the page — Supabase does not show it again. Do not put it anywhere in the Taskflow project
   folder. Look for: an entry in your password manager you can find again by searching "Supabase".
4. Confirm the project is on the Free plan. Look for: the plan named as Free on the project's billing
   or settings page.
5. Open `https://supabase.com/dashboard/project/_?showConnect=true&framework=androidkotlin&connectTab=mobiles`
   — this is the project's Connect panel, already set to Android Kotlin. Look for: a panel showing a
   project URL and a publishable (anon) key.
6. Open `local.properties` in the Taskflow project folder. It already exists and is git-ignored, so
   you are adding to it rather than creating it. Add two lines, pasting your own values after the
   equals signs: `supabaseUrl=` and `supabaseAnonKey=`. Look for: the file saved with both lines
   present and nothing else changed.
7. Tell a planning session the Supabase project exists. That is what releases the four items held
   against this one.

Observable: `local.properties` in the project root contains a `supabaseUrl` line and a
`supabaseAnonKey` line, both with values after the equals sign. A later session checks the file rather
than asking whether the step was done. The database password is deliberately not observable from here
— it lives only in your password manager, which is the point.

Held against this one: [0018-cloud-sync-paid-tier], and through it [0020-remote-mcp-server],
[0019-ai-choice-flow-and-mcp-setup] and [0021-strategy-doc-reconciliation-paid-tier].

Rests on, read 2026-09-03: that a project is created from `supabase.com/dashboard/new/_` and its URL
and publishable key are read from the project's Connect panel, both per Supabase's own Android Kotlin
quickstart; and that `.gitignore` here excludes `local.properties`.

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

#### [audit] Verify the rest of the 2026-08-31 run on a device [verify-run-2026-08-31]
Blocked by: [install-current-build-on-device]
Red flag · State: cleared

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
Blocked by: [supabase-project-setup]

Held below the line on 2026-09-03, with your agreement: the backend is now named, but the Supabase project it syncs to does not exist yet and creating it is your step.

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

#### LOG/index.md is overdue for its month split [log-index-month-split]

`LOG/index.md` still holds every line this project has ever written, back to May. The close's own rule
says a month that has ended moves into `LOG/index-YYYY-MM.md`, leaving the current month's lines in the
main file, so that a planning session's opening read stays short as the archive grows. Nothing has been
moved yet, and the file now runs to dozens of entries.

Not attempted at the 2026-09-04 close, deliberately. Index lines begin with a commit hash rather than a
date, and the older entry filenames carry no date prefix at all, so working out which month each line
belongs to means opening the entries it points at. That is a bulk restructure of the one file every
session's opening depends on, and doing it unasked at commit time — after a close that had already had
to repair a mis-placed readiness marker — was the wrong moment for it.

What it would take: derive each line's month from the entry it names (its own date field, or the
commit), split the ended months into `LOG/index-YYYY-MM.md` files newest-first, and leave September's
lines in `LOG/index.md`. Retrieval searches `LOG/index*.md`, so nothing is lost to the move.

Worth weighing at the same time: whether this should be a small script rather than a hand edit, given it
will be due again every month, and whether the method's own close should have caught it sooner than it
did.

Filed at the 2026-09-04 close, 12:27, by Claude.

