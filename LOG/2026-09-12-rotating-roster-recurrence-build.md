# [HASH] — /next [rotating-roster-recurrence]: a recurring task can carry an ordered roster, and it advances on completion rather than on the calendar

Written 2026-09-12 at 13:05, read from the clock.

Alex raised this on 2026-09-06, describing how she actually wants to use a Project for keeping up with her immediate family. A recurring task whose subject advances through an ordered list each time it comes round is exactly the executive-function load Taskflow exists to absorb — a rotation held in someone's head is the thing the app is meant to hold instead. The workaround, several staggered recurring tasks, drifts the moment one is completed late.

The four open questions were settled with Alex on 2026-09-12, and the build implements those settlements without re-opening them. **The roster lives on the task**, an ordered list of labels stored alongside the repeat rule. **Refused: putting it on the Project** — that would make a Project a thing with members, where a Project is an area of the user's life (SPEC §Strategy doc), a much larger change to what a Project is than one task feature warrants. **The free tier gets it**, because the rotation is data rather than intelligence: an ordered list and a position in it. **Refused: making it paid-only** — Claude arranging a roster conversationally is a separate thing built on top, not a substitute for the list existing. The free-choice position was split off into [rotating-roster-free-choice] so the straightforward half need not wait on the awkward one.

**The roster advances by completion, and that is the load-bearing decision.** Names are handed to the instances still to come, starting from however many the user has already completed: the nearest upcoming instance takes `roster[completions mod size]`, the next takes the one after. An occasion nobody ticked adds no completion, so the same name is still up. **Refused: advancing by date** — the third occurrence always the third name, whether or not the earlier two happened. `instancesOf` never re-shows a missed instance, so advancing by date would lose the occasion *and* skip that person's turn, against UX principle 4. **The accepted cost is stated rather than hidden:** a rotation nobody does stops advancing and keeps showing one name, where advancing by date never stalls.

Implementation followed the item's file list. `Recurrence` needed no change at all — the roster is a property of the task rather than of the repeat rule, so the serialised rule format is untouched, which is what keeps the migration to a single added column.

Version 5 is the floor below which no data may be destroyed, so 5 → 6 is a real migration rather than a destructive one, and existing rows arrive with an empty roster — which is exactly a task with no rotation, so nothing already on a phone changes behaviour.

Names of the people in Alex's roster are deliberately not recorded here: third parties who have published nothing, described by relationship per this project's scrub checklist.

**Files touched:** `data/model/Task.kt` (the `roster` column and a `rosterLabels` accessor), `data/local/TaskflowDatabase.kt` (version 6 and `MIGRATION_5_6`), `app/schemas/…/6.json` (exported by the Room compiler during this session's compile), `ui/schedule/ScheduleViewModel.kt` (`instancesOf` labels each instance; the row renders "title — label"), `ui/edit/EditTaskScreen.kt` and `ui/edit/EditTaskViewModel.kt` (the roster field, shown only alongside a repeat rule and cleared when the repeat is dropped), `data/transfer/TaskflowJson.kt` (carried through export and import), and `androidTest/…/MigrationTest.kt` (a 5 → 6 case).

**Routed to Captures:** none.

**Verification:** done, UNCONFIRMED — it compiles and `6.json` was exported with the roster column, but `MigrationTest`'s 5 → 6 case has not been run and nobody has seen a roster advance on a device. The migration test needs a connected instrumentation run, which destroys the database it runs against; the emulator created later in this same session, [emulator-for-instrumented-tests], is where it should now run rather than on Alex's phone.
