# 3defa43 — build [0007-recurring-tasks]: repeat rules with instances derived from the rule rather than stored as rows

Recurring tasks generate an indefinite tail of future instances, and SPEC §Recurring tasks resolves that by showing every instance inside a 30-day window and none beyond it — a yearly birthday reminder stays invisible until you are within a month of it, while a hand-dated one-off is never capped.

**The design decision, and the alternative that lost.** Instances could be materialised — a real row per occurrence, written ahead. That was rejected for deriving them from the rule at render time. A stored tail is unbounded, has to be regenerated on every edit to the rule, and only ever a month of it is shown, so the writing is work that is thrown away. Deriving them means a recurring task is one row carrying a rule and an anchor date, and the dates it appears on are computed each time the Schedule is built.

The part that genuinely has to be stored is the opposite one: which instances the user has *completed*. That is the only piece a repeat's rhythm cannot produce, because the user's own action creates it. It lives as a delimited set of dates on the task, and it is what lets ticking one Monday leave every other Monday alone.

Two consequences followed. A recurring task renders as several rows, so a row's identity became the task id plus its instance date — list keys and the completion callback both had to carry it, or the rows would collide on one key and completing "this Monday" would have no way to say which. And the instance window opens at today rather than at the anchor: a month of uncompleted past Mondays piled onto Today is exactly the nagging UX principle 4 refuses.

Changing a rule clears the completed-instance set, since those dates were produced by a rhythm that no longer applies and keeping them would silently hide days the new rule lands on.

**Files touched:** app/src/main/java/com/example/taskflow/domain/Recurrence.kt (new — rule model, serialize/parse, describe, instancesBetween, the 30-day horizon), data/model/Task.kt (recurrence and completed_instances columns plus instance-completion helpers), data/local/TaskDao.kt (updateRecurrence, updateCompletedInstances, getRecurringTasks), data/repository/TaskRepository.kt (pass-throughs plus setInstanceCompleted), data/local/TaskflowDatabase.kt (schema 2 → 3), ui/schedule/ScheduleViewModel.kt (instance expansion and bucketing, per-instance completion, recurring rows in the Today tray, row keys), ui/schedule/SlotPage.kt and LaterPage.kt (instance-aware callback and keys), ui/edit/EditTaskScreen.kt (the Repeats field — presets, custom interval stepper, weekday chips), ui/edit/EditTaskViewModel.kt (recurrence in the form and on save; clearing the date clears the repeat), app/src/test/java/com/example/taskflow/domain/RecurrenceTest.kt (new — 11 tests over expansion, month-end clamping and round-tripping).

**Routed to Captures:** none.

**Tick:** done, UNCONFIRMED: neither the new unit tests nor a compile could be run from this shell (see TOOLS.md); needs an Android Studio build, `RecurrenceTest` run, and the item's device check (set a task to repeat daily; instances appear across Today/Tomorrow/Soon and stop at 30 days; complete today's and tomorrow's survives; a one-off dated six months ahead still shows in Later).
