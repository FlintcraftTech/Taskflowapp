# QUEUE

## Processed

> Vetted work, ready to build — worked top to bottom. Each piece of work is one
> item: a `#### ` heading naming it, a `[slug]` at the end of that heading line, and
> a short rationale beneath. A leading flavor tag names how it runs — none for a
> build (Claude edits files), `[audit]` for a review pass, `[user]` for a step only
> you can do. A security or privacy risk Claude surfaces lives here too, as a work
> item carrying a `Red flag · State: cleared/uncleared` marker. The line below marks
> how far down is cleared to build; anything below it is decided but not ready yet.

#### Open the side menu by ☰ only — disable drawer swipe-to-open [disable-drawer-swipe-open]

The navigation drawer's default left-edge swipe-to-open collides with Taskflow's signature gesture: horizontal swipe is how the whole spine moves (Today ↔ Tomorrow ↔ Soon ↔ Later and onward). On Today — the leftmost, default page — a right-swipe has no previous spine page, so the drawer quietly claims it, and the same horizontal gesture means "open menu" near the edge but "change day" in the content area. That region-dependent meaning is the confusion. Resolution: the menu opens only by tapping the ☰ button; swipe-to-open is disabled. The Android edge-swipe-to-open convention was weighed and set aside — it carries less weight in an app that repurposes horizontal swipe as its core navigation, and the ☰ remains a standard, discoverable opener, so no affordance is truly lost. Verified in AppRoot.kt: the ☰ opens the drawer programmatically (`drawerState.open()`), unaffected by the gesture flag, and the spine's `HorizontalPager` is a separate gesture, also unaffected. One known side effect: disabling drawer gestures also removes swipe-to-close, but tapping the scrim or any menu item still closes it. Noticed on device 2026-06-20.

Change `app/src/main/java/com/example/taskflow/ui/navigation/AppRoot.kt` — add `gesturesEnabled = false` to the `ModalNavigationDrawer`. The ☰ (`onMenuClick` → `drawerState.open()`) and scrim/item taps to close are unaffected; the `HorizontalPager` day-swipe is unaffected.

Verify on a device: a left-edge right-swipe no longer opens the menu; the ☰ button still opens it; horizontal swipe in the content area still changes the day and the chevrons still work; tapping the scrim or a menu item still closes the drawer.

#### Side-scrolling date picker [0006-side-scrolling-date-picker]

Horizontal date strip replacing the read-only date display in the edit dialogue. Full original spec: `archive/backlog-specs/0006-side-scrolling-date-picker.md`. Several held items wait on this one, because it is the only path to setting a date at all — see [verify-schedule-date-matrix] and [verify-far-future-project-card].

#### Recurring tasks [0007-recurring-tasks]

Recurrence rules and 30-day-capped instance rendering. Full original spec: `archive/backlog-specs/0007-recurring-tasks.md`.

#### Later cards open showing the first ~3 tasks [later-card-peek]

Builds the peek behaviour [later-peek-spec-edit] wrote into SPEC: each Later Project card opens with its first ~3 tasks visible instead of collapsed to its header, the rest behind the expand/collapse control. Shares LaterPage.kt with [task-reorder-within-list] (drag-reorder), so whichever builds second integrates with the first's card-task-list rendering.

Change `app/src/main/java/com/example/taskflow/ui/schedule/LaterPage.kt` — render each Project card with its first ~3 tasks shown by default; the expand/collapse control reveals/hides the remainder.

Verify on a device: a Later card with more than 3 tasks opens showing its first 3, the rest appearing on expand; a card with 3 or fewer shows all of them; collapse/expand still works.

#### Within-list task reorder by drag — Schedule slots + Later cards [task-reorder-within-list]

SPEC §Reorder within a Schedule slot specifies within-list drag-reorder of top-level tasks across two surfaces: the flat Schedule slots (Today/Tomorrow/Soon) and, on Later, within each Project card. Nothing builds either yet; the data layer persists both orders already (`updateSlotSortOrder` for slots, `updateProjectSortOrder` for Later cards — both surfaced on `TaskRepository`, DAO-tested in 0001). What's missing is the drag UI — and there's no drag-reorder pattern anywhere in the app to copy and no library, so it's a from-scratch reorderable list with real design uncertainty (nested scrolling, variable row heights, persistence), sharpest on the nested Later case. The two surfaces share one drag primitive, so this item builds it once across both, designed with the nested card case in hand from the start so flat-list assumptions don't get baked in and force rework. No spec-edit — SPEC §Schedule view and §Reorder within a Schedule slot already describe both. (Merged 2026-06-23: the within-card Later half — split out of [later-by-project-screen] mid-build and set aside as a capture — folds in here so the primitive is written once. Supersedes the earlier rescope note that wrongly assumed [later-by-project-screen] would build Later reorder.) This is also the first drag primitive in the app, which is why [project-delete-later] is held against it.

Change `app/src/main/java/com/example/taskflow/ui/schedule/SlotPage.kt` + `app/src/main/java/com/example/taskflow/ui/schedule/ScheduleViewModel.kt` — drag-to-reorder on the flat slot task lists (Today/Tomorrow/Soon), persisted via `updateSlotSortOrder`. Change `app/src/main/java/com/example/taskflow/ui/schedule/LaterPage.kt` + the same view-model — within-card drag-to-reorder of a Project card's tasks, persisted via `updateProjectSortOrder`; handle the nested-scroll / variable-row-height case inside the expandable card. `TaskRepository` reorder calls already exist, so no data-layer change is expected.

Verify on a device: drag a task within a Schedule slot (Today/Tomorrow/Soon) to a new position and confirm the order persists across navigation and relaunch; then the same within a Later Project card.

#### Drag a task between Schedule screens [0008-drag-task-between-schedule-screens]

Long-press drag to reschedule across Schedule slots. Full original spec: `archive/backlog-specs/0008-drag-task-between-schedule-screens.md`.

#### Subtasks under a parent, with expand/collapse [0009-subtasks-under-parent-expand-collapse]

Nested subtasks with parent expand/collapse and completion roll-up. Full original spec: `archive/backlog-specs/0009-subtasks-under-parent-expand-collapse.md`.

#### Outliner typing and drag-target icons [0010-outliner-typing-drag-target-icons]

Outliner editor for subtasks plus bin and promote drag targets. Full original spec: `archive/backlog-specs/0010-outliner-typing-drag-target-icons.md`.

#### Cut and paste via the OS clipboard [0011-cut-and-paste-os-clipboard]

Cut drag target and OS-clipboard paste in the edit dialogue. Full original spec: `archive/backlog-specs/0011-cut-and-paste-os-clipboard.md`.

#### Settings — day begins at [0012-settings-day-begins-at]

Day-boundary time picker wired into rollover and the date anchor. Full original spec: `archive/backlog-specs/0012-settings-day-begins-at.md`. [schedule-day-boundary-tick] is held against this one and should fold into its wiring.

#### Settings — date format [0013-settings-date-format]

DD/MM or MM/DD setting applied app-wide. Full original spec: `archive/backlog-specs/0013-settings-date-format.md`.

#### JSON export and import [0014-json-export-and-import]

Full database export/import via JSON files. Full original spec: `archive/backlog-specs/0014-json-export-and-import.md`.

#### Strategy doc and life-area context [0015-strategy-doc-and-life-area-context]

Strategy doc editor, mechanical structure, life-area Room schema. Full original spec: `archive/backlog-specs/0015-strategy-doc-and-life-area-context.md`. [project-reorder-strategy] is held against this one.

#### Onboarding flow [0016-onboarding-flow]

First-run cards, AI-value video, free/paid choice. Full original spec: `archive/backlog-specs/0016-onboarding-flow.md`. Sequenced ahead of the Claude/MCP work, so it may ship with a placeholder video — see [onboarding-video-content].

#### Tier model and subscription handling [0017-tier-model-and-subscription-handling]

Google Play subscription, trial, local tier enforcement. Full original spec: `archive/backlog-specs/0017-tier-model-and-subscription-handling.md`. [subscription-pause-play-billing] is held against this one.

#### Cloud sync, paid tier [0018-cloud-sync-paid-tier]

Push-pull sync between the device Room DB and a cloud backend. Full original spec: `archive/backlog-specs/0018-cloud-sync-paid-tier.md`.

#### AI-choice flow and MCP setup [0019-ai-choice-flow-and-mcp-setup]

Claude setup path with connector deep-link and verification. Full original spec: `archive/backlog-specs/0019-ai-choice-flow-and-mcp-setup.md`.

#### Remote MCP server [0020-remote-mcp-server]

Hosted MCP server with tool surface and system-prompt delivery. Full original spec: `archive/backlog-specs/0020-remote-mcp-server.md`. [project-lifecycle-paid] is held against this one.

#### SYSTEM-PROMPT.md — pending-suggestion supersession [sysprompt-reconciliation-supersession]

Serves SYSTEM-PROMPT.md.

`SYSTEM-PROMPT.md` describes Strategy-doc reconciliation but doesn't say what happens when the user submits a new Strategy edit while a prior reconciliation's suggestions are still unanswered. Decided in planning 2026-06-16: the new edit wins. This writes that rule into the doc so the reconciliation feature ([0021-strategy-doc-reconciliation-paid-tier]) is built against a complete description.

Edit `SYSTEM-PROMPT.md` → §Strategy doc reconciliation → *Ongoing reconciliation*: add that a new Strategy doc edit submitted while prior suggestions are still pending triggers fresh reconciliation against the latest version, superseding (folding in) the prior pass's unanswered suggestions rather than stacking them. Keep one line of rationale in the doc: stale suggestions against a superseded version confuse; newest text is the source of truth.

#### Strategy doc reconciliation, paid tier [0021-strategy-doc-reconciliation-paid-tier]

Initial and ongoing Strategy doc reconciliation via Claude. Full original spec: `archive/backlog-specs/0021-strategy-doc-reconciliation-paid-tier.md`. [project-lifecycle-paid] is held against this one.

#### Help, Thanks and Report-a-bug screen content [0022-help-thanks-report-a-bug-content]

Bottom-of-drawer screen content including help and custom instructions. Full original spec: `archive/backlog-specs/0022-help-thanks-report-a-bug-content.md`. The words that go in these screens are still being worked out — see [help-thanks-report-content].

#### [user] Verify the blank New-task form fix on a device [verify-blank-new-task-form]

The [add-flow-create-path-fixes] build fixed the stale New-task title by giving the add dialogue a fresh view-model store per open. This is the device check that confirms it on the installed build — it couldn't run in the build's own session, so it waited.

Walkthrough, on a device with the current build installed:
1. Open Taskflow and go to any Schedule slot (Today is fine).
2. Tap the **+** button (the round FAB, bottom-right). The New-task form opens.
3. Type a title and save it. The task appears in the list.
4. Tap the **+** button again on the same slot.
5. Look at the Title field. It should be **empty**. If it still shows the title you just saved, the fix hasn't landed.

--- Cleared to run above this line ---

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

#### Recompute Schedule bucketing across the day boundary [schedule-day-boundary-tick]
Blocked by: [0012-settings-day-begins-at]

The Schedule view computes "now" each time the task flow re-emits — a DB change, or re-subscription when the app returns to foreground. If the app sits continuously in the foreground across the day-begins-at boundary (e.g. 4 AM) with no edits, a task won't move from Tomorrow into Today until the next emission or recomposition. It's an edge case: the app is foreground-only, and returning to it re-subscribes and recomputes. Best handled when [0012-settings-day-begins-at] wires day-begins-at — add a boundary/lifecycle-resume tick that recomputes placement, rather than building a separate mechanism now. Discovered building 0002.

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

## Unprocessed

> Captured ideas and tasks not yet fully processed. The next /plan session goes
> through these with you and decides each one's fate — keep it (move it up to
> Processed) or drop it. Each is filed as its own `#### ` heading, so the list shows
> up in an editor's outline.

#### Last session advises processing [claude-md-stale-vocabulary] next [forward-advisory]

The setup migration left CLAUDE.md describing a queue shape the project no longer has. CLAUDE.md is read at the start of every session, so while that stands, every session begins from a wrong picture of how this project's work is organised — a cost that repeats each session rather than waiting on a build. It is also small and self-contained: the only open question is what wording the user wants, not what to build, so it can be settled in the planning conversation itself. Nothing in the cleared region depends on it, so processing it first holds no build back.

#### Multi-device resume merge after a paused subscription [subscription-pause-resume-merge]

The policy is decided: paused → revert to local-only. What's open is what happens when a paused multi-device user resumes — which device's local state wins, and how the two are merged. Entangled with the cloud-sync design, so it can't be settled independently of [0018-cloud-sync-paid-tier]; that's a design dependency rather than a build order, which is why this sits here rather than being held against it.

#### Production version of the custom-instruction text [custom-instruction-production-text]

The suggested proactive-use custom-instruction text — the wording a user would paste into their own Claude preferences — needs testing in Alex's own real use before publishing. Once tested, it gets documented in the Help screen ([0022-help-thanks-report-a-bug-content]) and folded into SYSTEM-PROMPT.md if relevant. The real-world testing is the user's own work and needs the app usable first, so the next planning session should split that half out as its own step.

#### Empty state copy and visuals [empty-state-copy-and-visuals]

Write copy and visuals for each empty state in the Later-by-Project world: a Schedule slot (Today / Tomorrow / Soon) with zero tasks; an empty **Later card** — a Project the user has but with nothing in it, since cards always render even when empty; and **Later before the user has made any Project of their own**, where only the pinned Unassigned card shows. The old "empty Project" state is now the empty Later card; the old "Projects list before any Project exists" is gone with the side-menu Projects list — Later is the Projects surface now.

A fourth empty state, folded in from a capture during planning on 2026-06-24: a Later card that reads empty not because the Project has no tasks, but because all its tasks are near-term dated (Today/Tomorrow/Soon) and live on the schedule rather than the card — so "nothing in this project yet" misleads, since the Project does have a task. Noticed on device 2026-06-24 verifying [project-create-picker-ui]: a Project created from a Tomorrow-dated task shows an empty Later card while the task sits correctly on Tomorrow. Two candidate fixes to weigh at the design pass: copy that distinguishes "no tasks" from "no Later tasks — N on the schedule"; or surfacing a Project's near-term task count on its card. The second is broader than empty-state copy — it changes what every card shows, empty or not, so treat it as a card-design question, not a free copy tweak.

Best written in front of the real screens rather than against screens that don't exist yet, which is why it isn't designed yet. The zero-task Schedule-slot copy could be written now against shipped 0002, but writing all the empty states together against the real Later screen is cleaner.

#### Help, Thanks and Report-a-bug content [help-thanks-report-content]

The words for the three bottom-of-drawer screens that [0022-help-thanks-report-a-bug-content] builds. Help should cover MCP setup, the production custom-instruction text, and the "tasks dated before today" behaviour described without naming the category — the SPEC §Tasks dated before today wording is ready. Two of its inputs aren't ready: the production custom-instruction text ([custom-instruction-production-text]) and the MCP setup design ([0019-ai-choice-flow-and-mcp-setup], [0020-remote-mcp-server]). Write the content when those have landed.

#### Search [search-feature]

A confirmed feature, deferred. There IS a search box — finding tasks gets overwhelming at volume. Design decided: search results display like the in-Project/category listing — task details are visible but a task CANNOT be marked complete from the results list, so the results are read-only. Tapping a result navigates to where the task actually lives, so the user can complete or edit it there. Still to pin down at build time: search scope — current screen, current Project, or the whole database including tasks, Projects and the Strategy doc. Keeping it here rather than in the ready list because it needs a SPEC edit (adding §Search) before there's anything to build. Reconcile with [nav-completed-history], which proposes a search page of its own.

#### Completed-history sub-system — the left half of the navigation spine [nav-completed-history]

The pages left of Today on the spine: a **Search / completed-history page** (leftmost), a **Yesterday page**, a **day-detail card layer**, and **share-a-day**. The right half (spine backbone, Projects/Strategy pages, side-menu mirror) was promoted to [nav-spine-spec-edit] during planning on 2026-06-17; this is the remainder. It was [nav-zoom-spine-and-completed-history] before that split, and grew out of [completed-task-post-tray-fate] during the same session (evidence citation, not a dependency).

**Completed-history (the Search page).** The leftmost page lists completed tasks in completion order, most recent at top, with date headers between days. Typing a search narrows what shows below; the relevant date headers still display above each day's results.

**Yesterday page.** A spine page, not a card. Entangled with completed-history: since past-due tasks stay on Today (principle 4), Yesterday's content is essentially what was completed yesterday — so its design belongs with this sub-system, not the schedule screens.

**Day-detail card layer (a separate axis).** Tapping a result, a group, or a date header opens that day on a **card in the foreground** — deliberately a *different* left-right axis from the spine, signalled by the card visual. Swipe right = previous (older) day slides in from the left; swipe left = next (newer) day slides in from the right. Swiping left past the newest card (day-before-yesterday) slides the whole card layer *and* the Search page off in one smooth motion, returning the user to the main spine, landing on **Yesterday**. Only from a day card can the user tap a single task to edit or uncomplete it.

**Share-a-day.** A share button on a day screen shares that single day's completed tasks. Format open — leaning PNG or PDF, with PNG seen as most universally readable and still printable; plain-text and app-sensitive output also considered. Reconcile with the existing Strategy-doc share button, which uses the Android share sheet.

**SPEC consequences when developed.** Adds new sections for the completed-history page, the Yesterday page, the day-card layer, and share-a-day, and extends the spine described by [nav-spine-spec-edit] leftward (Search · Yesterday, left of Today). The right-half SPEC rewrite is handled by [nav-spine-spec-edit], which also absorbs the held spec-trim findings F3/F4/F18 — they live in the Schedule-view and side-menu sections it rewrites. This left-half item adds only the new completed-history sections; it does not touch those findings.

**Open sub-questions, to resolve before this can move up:** (1) reconcile with [search-feature] — find *active* tasks, read-only results, tap to jump to where the task lives; is the leftmost page completed-history only, with active-task search separate and later, or one unified search covering both? (2) share format — PNG / PDF / plain text / app-sensitive — worth researching before deciding. (3) swipe and card-layer interaction detail, best finalised against a real screen. No concrete trigger; revisit when taking up post-core work.

#### Onboarding video content [onboarding-video-content]

The multi-page onboarding video must demonstrate Claude/MCP value concretely, since it's what the free-vs-paid choice turns on. It can't be produced until the Claude/MCP integration exists to film. Sequencing note: [0016-onboarding-flow] is queued ahead of the MCP work, so it may need to ship with a placeholder video and have the real one added once [0019-ai-choice-flow-and-mcp-setup], [0020-remote-mcp-server] and [0021-strategy-doc-reconciliation-paid-tier] land. Producing a video is Alex's own work rather than something Claude can build, so the next planning session should file that half as its own step.

#### Post-first-test polish review [post-first-test-polish-review]

After the first end-to-end test, walk the test notes and decide which polish issues warrant their own SPEC.md entry and which fold into existing ones — polish that doesn't trace to SPEC.md is a capture, not a build item. Waits on the first end-to-end test having happened, which is a real-world event rather than a queued build, so the next planning session should file that test as its own step and hold this against it.

#### Execute by task area across the spine — focus on one area's tasks temporarily [execute-by-task-area]

Raised by Alex during the [project-create] device test on 2026-06-21, sharpened in planning on 2026-06-22, and looked at again against the real Project-grouped Later on 2026-06-23. The need: a way to *temporarily* focus on a single area (Project) and see all its tasks across the spine — including Today, Tomorrow and Soon — for when motivation is only there for one area.

The tension: the near-term slots are deliberately flat, horizon-sliced lists (UX principle 3 — execution structured by time, not category), so serving area-focus there cuts against the one view built to refuse category-slicing. No solution decided. A transient focus/filter-mode approach was floated and set aside because of UI concerns Alex couldn't accept. Alex's own read, 2026-06-22: there may be no good answer now, and leaving it open is acceptable. No concrete trigger — revisit when taking up post-core focus/area-execution work.

#### Personal strategy and memory dogfood — an early preview of Taskflow's Strategy-doc experience [personal-strategy-preview]

Alex's idea, raised in planning on 2026-06-24: use this workspace as an early, live preview of Taskflow's personal Strategy-doc experience, before Taskflow has the feature built. She'd keep a real personal Strategy doc and have the strategy conversations here with Claude directly, instead of through Taskflow plus a remote MCP server — neither of which exists yet. In her words: "We're just here, so we don't need the MCP."

Three threads bundled in it: (1) a real personal Strategy doc for Alex, maintained here in conversation with Claude — the experience a paid Taskflow user would eventually get via MCP, doubling as genuine design research for Taskflow's Strategy-doc feature; (2) Alex's real tasks, handled carefully so they don't go missing when Taskflow test builds wipe data — her live task data must not depend on the test app; (3) Claude-memory sync across her Claude surfaces. The insight she reached, and Claude strongly seconded: rather than pushing each strategy update out into many Claude memory stores, point all her Claudes at one canonical strategy doc and have them read from it. Pull-from-one beats push-to-many — one source of truth, nothing to hand-sync.

Why it was shelved, decided 2026-06-24: the method is one-spec-per-project. Two of the three threads (personal strategy, memory sync) aren't Taskflow app features, so they don't fit SPEC.md's contract that every entry describes something existing in the build. Governing three concerns in one workspace would need either multi-spec handling in the method or a deliberate re-framing of what this SPEC is about. Alex chose to wait for multi-spec support rather than bend SPEC now. That's a change to the method itself rather than a queue item here, which is why nothing in this queue holds it.

Privacy note to carry into any revival: if the personal strategy and real tasks get committed into this product repo and it's ever shared or made public, that's Alex's private life data exposed. Decide the home with that in mind when this revives.

#### CLAUDE.md and SPEC.md still describe the retired workflow vocabulary [claude-md-stale-vocabulary]

`CLAUDE.md` describes the queue as Red flags / Batches with Build-Test-Audit subheadings / Deferred tests / Captures, mentions `Parked:` headers and a `--- Plan session here ---` marker, and describes /next as executing "the top batch". None of that exists after this session's conversion — work items are now single entries with a flavor tag, deferred checks are `[user]` items, and holding is done by `Blocked by:` or a date. The same file also still lists `REGISTRY.md` as a project doc, which was deleted this session. SPEC.md's opening paragraph carries one instance too, pointing implementation detail at "each batch's spec".

This matters because `CLAUDE.md` is read at the start of every session, so a stale description is read as current — one earlier session went looking for a section that no longer existed. Left unfixed during the setup run deliberately: the file is the user's own wording, and reconciling it against the current template would clobber what they wrote, so the new wording needs their agreement rather than a silent rewrite.

#### Hand a multi-part [user] work item off to Taskflow as tasks [method-user-item-to-taskflow-handoff]

Raised by the user through a consumer project running this method, which reported the case and asked that Taskflow take the design up with the method project directly. A method queue item tagged `[user]` carries a walkthrough and is walked through live. That holds for a handful of steps in one sitting; it broke on a real item whose step 1 turned out to contain an extraction, a re-sort and a per-pile filing decision of unknown length, spread over days, partly belonging elsewhere. Written as one queue line such work hides its size; split into many it floods a queue meant to track a venture rather than a person's errands. The user's conclusion: those parts belong on their to-do list — which is this app. So Taskflow needs to say what a handed-off task looks like, whether items travel one way or round-trip, and what the queue keeps once every part is done. The consumer project's one further observation: the handoff most likely fires mid-walkthrough, exactly when the item's true size becomes visible and the user is least able to stop and reorganise a queue. Several planning sessions' worth of work, on the user's estimate.

