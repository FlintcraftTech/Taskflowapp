# QUEUE

## Red flags

Security, privacy, and data-exposure risks Claude has surfaced — kept at the top so they're the first thing seen each session. Each carries a state: open, resolved, or accepted. Empty until a risk comes up.

(none surfaced yet)

## Batches

Worked top to bottom. Each batch is one /next session. Subheadings name the kind of work (Build, Test, Audit).

The detailed original spec for each build batch is archived at `archive/backlog-specs/` — the filename matches the batch number/slug. These came from the old folder-based backlog; the entry below is the summary, the archived file is the full spec.

### Build

**Drop the date label from Tomorrow** **[tomorrow-no-date-label]**

Tomorrow rows currently show their DD/MM date (SPEC §Schedule view), same as Soon and Later. But a Tomorrow task always carries exactly tomorrow's date — the slot is derived from the date, so nothing else can land there — which makes the label fully redundant with the page title. Tomorrow also has no past-date case the way Today does: a past-dated task falls onto Today, never Tomorrow, so dropping the label loses no stale-date signal. The date earns its place only on Soon (2–7 days) and Later (8+ days), where the page name doesn't tell you the actual day. Today keeps its existing rule unchanged — no label except when the date has slipped into the past. This is a design change, not a bug; the current Tomorrow labels follow the spec as written. Raised during the [add-flow-create-path-fixes] device check on 2026-06-19.

Build:
- `app/src/main/java/com/example/taskflow/ui/schedule/ScheduleViewModel.kt` — in `dateLabelFor`, return no label for the Tomorrow slot (Tomorrow always holds tomorrow's date, so there's nothing to show). Update the KDoc above the function that currently reads "Tomorrow/Soon/Later rows show their date."

Test:
- On a device: a task on Tomorrow shows no date label; Soon and Later tasks still show their DD/MM date; a past-dated task on Today still shows its date. User-run.

**Open the side menu by ☰ only — disable drawer swipe-to-open** **[disable-drawer-swipe-open]**

The navigation drawer's default left-edge swipe-to-open collides with Taskflow's signature gesture: horizontal swipe is how the whole spine moves (Today ↔ Tomorrow ↔ Soon ↔ Later and onward). On Today — the leftmost, default page — a right-swipe has no previous spine page, so the drawer quietly claims it, and the same horizontal gesture means "open menu" near the edge but "change day" in the content area. That region-dependent meaning is the confusion. Resolution: the menu opens only by tapping the ☰ button; swipe-to-open is disabled. The Android edge-swipe-to-open convention was weighed and set aside — it carries less weight in an app that repurposes horizontal swipe as its core navigation, and the ☰ remains a standard, discoverable opener, so no affordance is truly lost. Verified in AppRoot.kt: the ☰ opens the drawer programmatically (`drawerState.open()`), unaffected by the gesture flag, and the spine's `HorizontalPager` is a separate gesture, also unaffected. One known side effect: disabling drawer gestures also removes swipe-to-close, but tapping the scrim or any menu item still closes it. Noticed on device 2026-06-20.

Build:
- `app/src/main/java/com/example/taskflow/ui/navigation/AppRoot.kt` — add `gesturesEnabled = false` to the `ModalNavigationDrawer`. The ☰ (`onMenuClick` → `drawerState.open()`) and scrim/item taps to close are unaffected; the `HorizontalPager` day-swipe is unaffected.

Test:
- On a device: a left-edge right-swipe no longer opens the menu; the ☰ button still opens it; horizontal swipe in the content area still changes the day and the chevrons still work; tapping the scrim or a menu item still closes the drawer. User-run.

**Verify the blank New-task form fix** **[verify-blank-new-task-form]**

Rolled from Deferred tests. The [add-flow-create-path-fixes] build fixed the stale New-task title by giving the add dialogue a fresh view-model store per open. This is the device check that confirms it on the installed build — it couldn't run in the build's own session, so it was deferred.

Test:
- On a device with the current build installed: add and save a task on a Schedule slot, then tap the FAB again on the same slot; confirm the New-task form opens with an empty Title field. Verifies the stale-title fix from [add-flow-create-path-fixes]. User-run.

- **0006 — side-scrolling-date-picker** — Horizontal date strip replacing read-only date display in edit dialogue.
- **0007 — recurring-tasks** — Recurrence rules and 30-day-capped instance rendering.
**Later cards open showing the first ~3 tasks — build** **[later-card-peek]**

Builds the peek behaviour [later-peek-spec-edit] writes into SPEC: each Later Project card opens with its first ~3 tasks visible instead of collapsed to its header, the rest behind the expand/collapse control. Shares LaterPage.kt with [task-reorder-within-list] (drag-reorder), so whichever builds second integrates with the first's card-task-list rendering.

Build:
- `app/src/main/java/com/example/taskflow/ui/schedule/LaterPage.kt` — render each Project card with its first ~3 tasks shown by default; the expand/collapse control reveals/hides the remainder.

Test:
- On a device: a Later card with more than 3 tasks opens showing its first 3, the rest appearing on expand; a card with 3 or fewer shows all of them; collapse/expand still works. User-run.

**Within-list task reorder by drag — Schedule slots + Later cards** **[task-reorder-within-list]**

SPEC §Reorder within a Schedule slot specifies within-list drag-reorder of top-level tasks across two surfaces: the flat Schedule slots (Today/Tomorrow/Soon) and, on Later, within each Project card. No batch builds either yet; the data layer persists both orders already (`updateSlotSortOrder` for slots, `updateProjectSortOrder` for Later cards — both surfaced on `TaskRepository`, DAO-tested in 0001). What's missing is the drag UI — and there's no drag-reorder pattern anywhere in the app to copy and no library, so it's a from-scratch reorderable list with real design uncertainty (nested scrolling, variable row heights, persistence), sharpest on the nested Later case. The two surfaces share one drag primitive, so this batch builds it once across both, designed with the nested card case in hand from the start so flat-list assumptions don't get baked in and force rework. No spec-edit — SPEC §Schedule view and §Reorder within a Schedule slot already describe both. (Merged 2026-06-23: the within-card Later half — split out of [later-by-project-screen] mid-build and parked as a capture — folds in here so the primitive is written once. Supersedes the earlier rescope note that wrongly assumed [later-by-project-screen] would build Later reorder.)

Build:
- `app/src/main/java/com/example/taskflow/ui/schedule/SlotPage.kt` + `app/src/main/java/com/example/taskflow/ui/schedule/ScheduleViewModel.kt` — drag-to-reorder on the flat slot task lists (Today/Tomorrow/Soon), persisted via `updateSlotSortOrder`.
- `app/src/main/java/com/example/taskflow/ui/schedule/LaterPage.kt` + `app/src/main/java/com/example/taskflow/ui/schedule/ScheduleViewModel.kt` — within-card drag-to-reorder of a Project card's tasks, persisted via `updateProjectSortOrder`; handle the nested-scroll / variable-row-height case inside the expandable card.
- `TaskRepository` reorder calls already exist (`updateSlotSortOrder`, `updateProjectSortOrder`) — no data-layer change expected.

Test:
- On a device: drag a task within a Schedule slot (Today/Tomorrow/Soon) to a new position and confirm the order persists across navigation/relaunch. User-run.
- On a device: drag a task within a Later Project card to a new position and confirm the order persists across navigation/relaunch. User-run.

- **0008 — drag-task-between-schedule-screens** — Long-press drag to reschedule across Schedule slots.
- **0009 — subtasks-under-parent-expand-collapse** — Nested subtasks with parent expand/collapse and completion roll-up.
- **0010 — outliner-typing-drag-target-icons** — Outliner editor for subtasks plus bin and promote drag targets.
- **0011 — cut-and-paste-os-clipboard** — Cut drag target and OS-clipboard paste in edit dialogue.
- **0012 — settings-day-begins-at** — Day-boundary time picker wired into rollover and date anchor.
- **0013 — settings-date-format** — DD/MM or MM/DD setting applied app-wide.
- **0014 — json-export-and-import** — Full database export/import via JSON files.
- **0015 — strategy-doc-and-life-area-context** — Strategy doc editor, mechanical structure, life-area Room schema.
- **0016 — onboarding-flow** — First-run cards, AI-value video, free/paid choice.
- **0017 — tier-model-and-subscription-handling** — Google Play subscription, trial, local tier enforcement.
- **0018 — cloud-sync-paid-tier** — Push-pull sync between device Room DB and cloud backend.
- **0019 — ai-choice-flow-and-mcp-setup** — Claude setup path with connector deep-link and verification.
- **0020 — remote-mcp-server** — Hosted MCP server with tool surface and system-prompt delivery.
**SYSTEM-PROMPT.md — reconciliation: pending-suggestion supersession** **[sysprompt-reconciliation-supersession]**
Serves SYSTEM-PROMPT.md.

`SYSTEM-PROMPT.md` describes Strategy-doc reconciliation but doesn't say what happens when the user submits a new Strategy edit while a prior reconciliation's suggestions are still unanswered. Decided in planning 2026-06-16: the new edit wins. This batch writes that rule into the doc so the reconciliation feature (batch 0021) is built against a complete description.

Build:
- Edit `SYSTEM-PROMPT.md` → §Strategy doc reconciliation → *Ongoing reconciliation*: add that a new Strategy doc edit submitted while prior suggestions are still pending triggers fresh reconciliation against the latest version, superseding (folding in) the prior pass's unanswered suggestions rather than stacking them. Keep one line of rationale in the doc: stale suggestions against a superseded version confuse; newest text is the source of truth.

- **0021 — strategy-doc-reconciliation-paid-tier** — Initial and ongoing Strategy doc reconciliation via Claude.
- **0022 — help-thanks-report-a-bug-content** — Bottom-of-drawer screen content including help and custom instructions.

### Parked

## Deferred tests

Planned tests that couldn't run in their own session. /plan rolls the runnable ones into a test batch.

- **[device-verify-core-screens → 0002] Schedule date-matrix rendering.** On a device, verify dated tasks render with their DD/MM label in the right slot for the cases the FAB can't seed yet: a 2–7-day date in Soon, an 8+-day date in Later, and a past date staying on Today (DD/MM, no overdue label). Confirmed by: user-run on device once 0006 ships date-editing — the only path to those dates. (Slot math already unit-tested in 0002; the non-Today DD/MM render is exercised by the Tomorrow check in [device-verify-core-screens].)

- **[later-by-project-screen] Far-future dated task under a user Project card.** On a device, confirm a user Project's card on Later holds its far-future (8+ day) dated tasks with the DD/MM label. Confirmed by: user-run on device. Deferral: blocked on 0006 (side-scrolling-date-picker) — an 8+-day date can't be set without date-editing, and the FAB on Later only creates undated tasks. Runnability: user-run. (The multi-user-Project ordering half and the move-between-Projects check rolled into [project-create-picker-ui] in /plan 2026-06-22, runnable once a second Project can be created there.)

## Captures

Captured outside /plan. Picked up and routed during the next /plan session.

---

(Raw captures collect below this line, then get processed and moved above it during /plan.)

### Parked

- **Schedule bucketing doesn't re-evaluate across the day boundary while the app stays open.** The Schedule view computes "now" each time the task flow re-emits — a DB change, or re-subscription when the app returns to foreground. If the app sits continuously in the foreground across the day-begins-at boundary (e.g. 4 AM) with no edits, a task won't move from Tomorrow into Today until the next emission or recomposition. It's an edge case (foreground-only app, and returning to the app re-subscribes and recomputes). Best handled when 0012 wires day-begins-at: add a boundary/lifecycle-resume tick that recomputes placement. Discovered building 0002.
  Blocked by: 0012 (settings-day-begins-at) — landing; fold the refresh tick into the day-begins-at wiring.
- **Subscription pause — Play Billing pause mechanics.** Verify what Google Play Billing actually exposes for subscription pause (largely Play-Store-controlled, not app-controlled). Staleness-prone, so check at build time, not now.
  Blocked by: the subscription-handling build (queue entry 0017) — behavioural trigger, no slug yet (0017 is still a rough placeholder); confirm the Play Billing pause API when that batch is authored.
- **Subscription pause — multi-device resume merge.** Policy is decided (paused → revert to local-only). Open: when a paused multi-device user resumes, which device's local state wins / how to merge. Entangled with cloud-sync design.
  Blocked by: the cloud-sync build (queue entry 0018) — behavioural trigger, no slug yet; decide the resume-merge strategy when that batch is designed.
- **Custom instruction text — production version.** The suggested proactive-use custom-instruction text (for users' Claude preferences) needs testing in Alex's own real use before publishing. Once tested, document in Help (0022) and fold into SYSTEM-PROMPT.md if relevant.
  Blocked by: Alex testing the proactive custom-instruction in her own Claude use — behavioural trigger, no slug; needs the app usable first.
- **Empty state copy and visuals.** Write copy + visuals for each empty state in the Later-by-Project world: a Schedule slot (Today / Tomorrow / Soon) with zero tasks; an empty **Later card** — a Project the user has but with nothing in it (cards always render, even empty); and **Later before the user has made any Project of their own**, where only the pinned Unassigned card shows. The old "empty Project" state is now the empty Later card; the old "Projects list before any Project exists" is gone with the side-menu Projects list — Later is the Projects surface now. A fourth empty state, folded in from a capture (/plan 2026-06-24): a Later card that reads empty not because the Project has no tasks, but because all its tasks are near-term dated (Today/Tomorrow/Soon) and live on the schedule rather than the card — so "nothing in this project yet" misleads, since the Project does have a task. Noticed on device 2026-06-24 verifying [project-create-picker-ui]: a Project created from a Tomorrow-dated task shows an empty Later card while the task sits correctly on Tomorrow. Two candidate fixes to weigh at the design pass: copy that distinguishes "no tasks" from "no Later tasks — N on the schedule"; or surfacing a Project's near-term task count on its card. The second is broader than empty-state copy — it changes what every card shows, empty or not, so treat it as a card-design question, not a free copy tweak. Best written in front of the real screens, not against screens that don't exist yet.
  Blocked by: [later-by-project-screen] reaching its polish pass — behavioural; the Later cards must exist before their empty states can be written. (The zero-task Schedule-slot copy could be written now against shipped 0002, but writing all the empty states together against the real Later screen is cleaner.)
- **Help, Thanks, Report a bug content.** Content for the three bottom-of-drawer screens (batch 0022). Help should cover MCP setup, the production custom-instruction text, and the "tasks dated before today" behaviour described without naming the category (the SPEC §Tasks dated before today wording is ready).
  Blocked by: the production custom-instruction text (itself parked, behavioural) and the MCP setup design (0019/0020) — write Help content when 0022 is built and those inputs are ready.
- **Search.** Confirmed feature, deferred. There IS a search box — finding tasks gets overwhelming at volume. Design decided: search results display like the in-Project/category listing — task details are visible but a task CANNOT be marked complete from the results list (read-only display). Tapping a result navigates to where the task actually lives, so the user can complete or edit it there. Still to pin at build time: search scope (current screen / current Project / whole database — tasks + Projects + Strategy doc).
  Parked: confirmed for a later build, no batch yet; revisit when scoping post-core work. When unparked it needs a spec-edit (add §Search to SPEC) plus a feature batch.
- **Completed-history sub-system (left half of the navigation spine)** **[nav-completed-history]**
  Parked: post-core design thread — the left end of the navigation spine. The right half (spine backbone, Projects/Strategy pages, side-menu mirror) was promoted to [nav-spine-spec-edit] in /plan 2026-06-17; this is the remainder. Trigger-less — revisit when taking up post-core work. Was [nav-zoom-spine-and-completed-history] before that split. Grew out of [completed-task-post-tray-fate] during /plan 2026-06-17 (evidence citation, not a dependency).

  **What it covers.** The pages left of Today on the spine: a **Search / completed-history page** (leftmost), a **Yesterday page**, a **day-detail card layer**, and **share-a-day**.

  **Completed-history (the Search page).** The leftmost page lists completed tasks in completion order, most recent at top, with date headers between days. Typing a search narrows what shows below; the relevant date headers still display above each day's results.

  **Yesterday page.** A spine page, not a card. Entangled with completed-history: since past-due tasks stay on Today (principle 4), Yesterday's content is essentially what was completed yesterday — so its design belongs with this sub-system, not the schedule screens.

  **Day-detail card layer (a separate axis).** Tapping a result, a group, or a date header opens that day on a **card in the foreground** — deliberately a *different* left-right axis from the spine, signalled by the card visual. Swipe right = previous (older) day slides in from the left; swipe left = next (newer) day slides in from the right. Swiping left past the newest card (day-before-yesterday) slides the whole card layer *and* the Search page off in one smooth motion, returning the user to the main spine, landing on **Yesterday**. Only from a day card can the user tap a single task to edit or uncomplete it.

  **Share-a-day.** A share button on a day screen shares that single day's completed tasks. Format open — leaning PNG or PDF (PNG seen as most universally readable and still printable); plain-text and app-sensitive output also considered. Reconcile with the existing Strategy-doc share button, which uses the Android share sheet.

  **SPEC consequences when developed.** Adds new sections for the completed-history page, the Yesterday page, the day-card layer, and share-a-day, and extends the spine described by [nav-spine-spec-edit] leftward (Search · Yesterday, left of Today). The right-half SPEC rewrite is handled by [nav-spine-spec-edit], which also absorbs the held spec-trim findings F3/F4/F18 (they live in the Schedule-view and side-menu sections it rewrites). This left-half item adds only the new completed-history sections; it does not touch those findings.

  **Open sub-questions (resolve before this can promote):**
  1. Reconcile with the parked Search feature (find *active* tasks, read-only results, tap to jump to where the task lives). Is the leftmost page completed-history only, with active-task search separate/later — or one unified search covering both?
  2. Share format (PNG / PDF / plain text / app-sensitive) — research-worthy before deciding.
  3. Swipe and card-layer interaction detail — best finalized against a real screen.
- **Onboarding video content.** The multi-page onboarding video must demonstrate Claude/MCP value concretely (for the free-vs-paid choice). Can't be produced until the Claude/MCP integration exists to film. Sequencing note: onboarding (0016) is queued ahead of the MCP work, so 0016 may need to ship with a placeholder video and have the real one added once the integration lands.
  Blocked by: the Claude/MCP integration builds (0019 AI-choice / MCP setup, 0020 remote MCP server, 0021 reconciliation) — behavioural, no slug; produce the video once there's a working integration to demonstrate.
- **Post-first-test polish review.** After the first end-to-end test, walk the test notes and decide which polish issues warrant their own SPEC.md entry and which fold into existing ones — polish that doesn't trace to SPEC.md is a capture, not a build item.
  Blocked by: the first end-to-end test having happened — behavioural, no slug; when it lands, run a /plan polish-review pass over the test notes.
- **Execute-by-task-area across the spine — focus on one area's tasks temporarily.** Raised by Alex during the [project-create] device test (2026-06-21), sharpened in /plan 2026-06-22, looked at against the real Project-grouped Later in /plan 2026-06-23. The need: a way to *temporarily* focus on a single area (Project) and see all its tasks across the spine — including Today, Tomorrow, and Soon — for when motivation is only there for one area. The tension: the near-term slots are deliberately flat, horizon-sliced lists (UX principle 3 — execution structured by time, not category), so serving area-focus there cuts against the one view built to refuse category-slicing. No solution decided; a transient focus/filter-mode approach was floated and set aside (UI concerns Alex couldn't accept). Alex (2026-06-22): there may be no good answer now, and leaving it open is acceptable.
  Parked: open design question, no concrete trigger — revisit when taking up post-core focus/area-execution work. (The [later-by-project-screen] trigger has fired, which is why this is now Parked: rather than Blocked by.)
- **Free-tier delete a Project on Later** **[project-delete-later]**
  Blocked by: the general drag + bin/delete drag-target gesture (SPEC §Drag-target icons) — deleting a Project reuses the same bin gesture used to delete a task, which doesn't exist yet (no drag pattern anywhere in the app, confirmed 2026-06-22). Behavioural, no firm slug: the bin gesture is general, so it likely arrives with the task drag-target work (queue entries 0010/0011) or with the first drag primitive ([task-reorder-within-list] / 0008), whichever lands first — build order doesn't matter, only that the bin gesture exists.

  Free tier — long-press a Later Project card and drag it to a delete target in the upper-right (the same gesture used for tasks). Deleting a Project does not delete its tasks: they reassign to the system Unassigned Project (reassign logic shipped in [unassigned-project-model]). SPEC §Create or delete a Project describes this. Carved from the retired [project-lifecycle-later] in /plan 2026-06-22 — its create half was promoted to [project-create-picker-ui], its three remaining pieces split out by dependency. The end-to-end Project-deletion data check ([verify-project-delete-data]) rides this batch's test pass.

- **Verify Project-deletion data behaviour end-to-end once the delete UI exists** **[verify-project-delete-data]**
  Blocked by: [project-delete-later] — behavioural; the Project-delete bin gesture must exist to exercise this end-to-end. Rides that batch's test pass.

  [unassigned-project-model] built the repository logic: deleting a real Project reassigns its tasks to the system Unassigned Project (`TaskDao.reassignTasksToProject`, called by `ProjectRepository.delete`), and the Unassigned Project is undeletable (`ProjectRepository.delete` no-ops when `isSystem`). The reassign query was verified live on-device that session; the two guards were verified by code inspection. There is no automated test of `ProjectRepository.delete` itself and no delete gesture yet to exercise it end-to-end. When [project-delete-later] builds the long-press-drag delete, its test pass should confirm: deleting a real Project moves its tasks onto Unassigned with none orphaned, and the Unassigned card cannot be deleted.

- **Free-tier reorder Projects in the Strategy-doc editor** **[project-reorder-strategy]**
  Blocked by: 0015 (strategy-doc-and-life-area-context) — landing; the Strategy-doc editor must exist before Project headings can be drag-reordered in it.

  Free tier — drag Project headings in the Strategy-doc editor to set Project order; the Strategy doc owns Project order app-wide (including the Later card order). SPEC §Strategy doc describes this. Carved from the retired [project-lifecycle-later] in /plan 2026-06-22.

- **Paid-tier Project reorder/delete via Claude (+ SYSTEM-PROMPT.md)** **[project-lifecycle-paid]**
  Blocked by: 0020 (remote-mcp-server) + 0021 (strategy-doc-reconciliation-paid-tier) — landing; Claude-mediated reorder/delete needs the MCP server and reconciliation in place.

  Paid tier — reordering and deleting a Project go through discussion with Claude, not a direct gesture; long-pressing a Later card shows the toast "Discuss high-level strategy with Claude," and Claude applies the change and reflects it into the Strategy doc + Later. Why tier-split: reordering and deleting a Project are decisions about the shape of the user's life, so on paid they earn a check-in with Claude rather than a quick gesture; free has no Claude, so it gets direct manual gestures (Alex's call, 2026-06-22). Needs a SYSTEM-PROMPT.md edit — how Claude handles a Project reorder/delete discussion and reflects it back — which the spec-edit batch left untouched because SYSTEM-PROMPT.md was locked there. SPEC §Create or delete a Project and §Strategy doc describe the paid behaviour. Carved from the retired [project-lifecycle-later] in /plan 2026-06-22.

- **Personal strategy + memory dogfood — early preview of Taskflow's Strategy-doc experience** **[personal-strategy-preview]**
  Blocked by: SI gaining multi-spec / multi-project handling — behavioural, no slug; revisit the whole direction when the method can cleanly govern more than one spec in a workspace.

  Alex's idea (raised in /plan 2026-06-24): use this workspace as an early, live preview of Taskflow's personal Strategy-doc experience, before Taskflow has the feature built. She'd keep a real personal Strategy doc and have the strategy conversations here with Claude directly, instead of through Taskflow plus a remote MCP server (neither exists yet). "We're just here, so we don't need the MCP."

  Three threads bundled in it:
  1. A real personal Strategy doc for Alex, maintained here in conversation with Claude — the experience a paid Taskflow user would eventually get via MCP. Doubles as genuine design research for Taskflow's Strategy-doc feature.
  2. Alex's real tasks, handled carefully so they don't go missing when Taskflow test builds wipe data. Her live task data must not depend on the test app.
  3. Claude-memory sync across her Claude surfaces. The insight she reached, and Claude strongly seconded: rather than pushing each strategy update out into many Claude memory stores, point all her Claudes at one canonical strategy doc and have them read from it. Pull-from-one beats push-to-many — one source of truth, nothing to hand-sync.

  Why shelved (decided 2026-06-24): SI is one-spec-per-project. Two of the three threads (personal strategy, memory sync) aren't Taskflow app features, so they don't fit SPEC.md's contract that every entry describes something existing in the build. Governing three concerns in one workspace would need either multi-spec handling in SI or a deliberate re-framing of what this SPEC is about. Alex chose to wait for SI multi-spec support rather than bend SPEC now.

  Privacy note to carry into the unpark: if the personal strategy and real tasks get committed into this product repo and it's ever shared or made public, that's Alex's private life data exposed. Decide the home with that in mind when this revives.
