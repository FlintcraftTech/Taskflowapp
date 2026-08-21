# BUILD VIEW — generated, do not edit

Regenerated from QUEUE.md. Editing this file changes nothing: the next run overwrites it. The queue itself is where work is changed.

**This carries instructions and no decision history, deliberately.** Each item below is what the keep-step agreed would change, how to tell it worked, and any option already refused. Why the work is worth doing lives in QUEUE.md and is read back at the close, one entry at a time.

## Cleared to run — 24 item(s)

### [claude-md-stale-vocabulary] CLAUDE.md's plugin-managed block still describes the retired workflow vocabulary

Flavor: build

Changes: `CLAUDE.md` — replace everything between the two `PLUGIN-MANAGED` marker comments with the corresponding block from the installed plugin's `templates/CLAUDE-TEMPLATE.md`, verbatim, keeping `Language: English`. Then, in the user-owned Project rules below the markers, fix the archived-backlog-specs paragraph pointing at "QUEUE.md's Batches" and the migration note still naming `REGISTRY.md` as a live doc.
Acceptance: reading `CLAUDE.md` back shows no occurrence of "Batches", "Deferred tests", "Parked:", "Plan session here" or "REGISTRY.md", and the Project rules section is otherwise unchanged.

### [disable-drawer-swipe-open] Open the side menu by ☰ only — disable drawer swipe-to-open

Flavor: build

Changes: `app/src/main/java/com/example/taskflow/ui/navigation/AppRoot.kt` — add `gesturesEnabled = false` to the `ModalNavigationDrawer`. The ☰ (`onMenuClick` → `drawerState.open()`), scrim/item taps to close, and the spine's `HorizontalPager` are all unaffected.
Acceptance: on a device — a left-edge right-swipe no longer opens the menu; the ☰ still opens it; horizontal swipe in the content area still changes the day and the chevrons still work; tapping the scrim or a menu item still closes the drawer.
Refused: Android's edge-swipe-to-open convention — it carries less weight in an app that repurposes horizontal swipe as its core navigation, and the ☰ remains a standard, discoverable opener.

### [0006-side-scrolling-date-picker] Side-scrolling date picker

Flavor: build

Changes: `app/src/main/java/com/example/taskflow/ui/edit/EditTaskScreen.kt` and `EditTaskViewModel.kt` — replace the read-only date display with a horizontal date strip. Tiles labelled DD/MM (or MM/DD per setting), today as the visual anchor, tiles fading linearly with distance from today and capped so far-off tiles stay readable; tap a tile to select it, with a highlight; a visually distinct "no date" tile at the left edge, labelled rather than merely greyed, which clears the date. The strip spans roughly one month back to twelve months forward with a month-jump affordance, shows roughly five to seven tiles at once, and opens centred on the task's own date or on today if undated. Setting a date on a previously undated task moves it into Schedule, and into its Project's card if it has one. Full original spec: `archive/backlog-specs/0006-side-scrolling-date-picker.md`.
Acceptance: on a device — open a task's edit dialogue and the strip is centred on today; tap a date three days out and save, and the task appears in Soon with that DD/MM label; reopen it, tap "no date" and save, and the task leaves Schedule.

### [0007-recurring-tasks] Recurring tasks

Flavor: build

Changes: recurrence-rule editing (daily / weekly / monthly / custom — shapes confirmed at build time) in `app/src/main/java/com/example/taskflow/ui/edit/EditTaskScreen.kt`, backed by a recurrence field on `data/model/Task.kt` and its DAO and repository. The Schedule pages under `ui/schedule/` render every instance of a recurring task in the visible window, capped at 30 days from today; manually dated one-off tasks are not capped and still appear in Later however far out they are. Completing one instance marks that instance complete and leaves future instances alone. Full original spec: `archive/backlog-specs/0007-recurring-tasks.md`.
Acceptance: on a device — set a task to repeat daily; its instances appear across Today, Tomorrow and Soon and stop at 30 days out; complete today's instance and tomorrow's is still there; a one-off task dated six months ahead still shows in Later.

### [later-card-peek] Later cards open showing the first ~3 tasks

Flavor: build

Changes: `app/src/main/java/com/example/taskflow/ui/schedule/LaterPage.kt` — render each Project card with its first ~3 tasks shown by default; the expand/collapse control reveals and hides the remainder.
Acceptance: on a device — a Later card with more than 3 tasks opens showing its first 3, the rest appearing on expand; a card with 3 or fewer shows all of them; collapse and expand still work.

### [task-reorder-within-list] Within-list task reorder by drag — Schedule slots + Later cards

Flavor: build

Changes: `app/src/main/java/com/example/taskflow/ui/schedule/SlotPage.kt` and `ScheduleViewModel.kt` — drag-to-reorder on the flat slot task lists (Today/Tomorrow/Soon), persisted via `updateSlotSortOrder`. `app/src/main/java/com/example/taskflow/ui/schedule/LaterPage.kt` and the same view-model — within-card drag-to-reorder of a Project card's tasks, persisted via `updateProjectSortOrder`, handling nested scrolling and variable row heights inside the expandable card. One drag primitive written across both surfaces, designed with the nested case in hand. `TaskRepository`'s reorder calls already exist, so no data-layer change is expected.
Acceptance: on a device — drag a task within a Schedule slot to a new position and the order persists across navigation and relaunch; the same holds within a Later Project card.

### [0008-drag-task-between-schedule-screens] Drag a task between Schedule screens

Flavor: build

Changes: `app/src/main/java/com/example/taskflow/ui/schedule/SlotPage.kt`, `ScheduleScreen.kt` and `ScheduleViewModel.kt` — long-press to pick a task up, then drag to the left or right edge to page through to the adjacent slot. Dropping on Today or Tomorrow sets the date to today or tomorrow; dropping on Soon sets today + 2 and on Later today + 8, except that a task that was undated stays undated and parks in that slot. Reordering within a slot uses the per-slot sort order. Dragging a parent carries its children as a single unit. Full original spec: `archive/backlog-specs/0008-drag-task-between-schedule-screens.md`.
Acceptance: on a device — drag a dated task from Today to Soon and its date becomes today + 2; drag an undated task to Later and it parks there still undated; drag a parent and its children travel with it.

### [0009-subtasks-under-parent-expand-collapse] Subtasks under a parent, with expand/collapse

Flavor: build

Changes: a parent/child relation on `app/src/main/java/com/example/taskflow/data/model/Task.kt` with its DAO and repository support, and rendering across `ui/schedule/SlotPage.kt` and `LaterPage.kt` — subtasks nested under their parent on the parent's Schedule page and in its Project card. Parent tasks show an expand/collapse control instead of a checkbox, revealing and hiding their children inline. Completion rolls up: completing all subtasks completes the parent, and un-completing a subtask un-completes the parent and brings it back out of the Completed tray. A parent dragged between Schedule slots or refiled to another Project carries its children as one unit. Full original spec: `archive/backlog-specs/0009-subtasks-under-parent-expand-collapse.md`.
Acceptance: on a device — add two subtasks under a task; the parent shows an expand/collapse control rather than a checkbox; complete both subtasks and the parent completes; un-complete one and the parent returns to the list with its children.

### [0010-outliner-typing-drag-target-icons] Outliner typing and drag-target icons

Flavor: build

Changes: `app/src/main/java/com/example/taskflow/ui/edit/EditTaskScreen.kt` — the text area renders parent and children as an indented outline; Enter at the end of any line creates a new child line below it; Backspace at the start of a line deletes it and merges the remaining text into the line above; child lines carry drag handles and the parent line does not. Across the Schedule pages, the Later page and the dialogue, any task drag reveals a row of drag-target icons fixed at the top right with hover feedback, carrying two targets in this item: **bin**, which deletes, and **promote**, on dialogue subtask drags only, which makes a child a top-level task at the bottom of the parent's slot or Project with the parent's date. A parent left with no children reverts from expand/collapse to a checkbox. Full original spec: `archive/backlog-specs/0010-outliner-typing-drag-target-icons.md`.
Acceptance: on a device — typing in the dialogue behaves as an outline, with Enter making a child line and Backspace merging one away; dragging a task reveals the icon row; dropping on bin deletes the task; promoting a child makes it a top-level task carrying the parent's date, and the emptied parent shows a checkbox again.

### [0011-cut-and-paste-os-clipboard] Cut and paste via the OS clipboard

Flavor: build

Changes: add a **cut** icon to the drag-target icon row, on both screen drags and dialogue drags. Dropping on cut removes the task from Taskflow and writes its content to the device's clipboard as plain text; cutting a parent writes the whole set as indented multi-line text. The outliner editor in `ui/edit/EditTaskScreen.kt` handles pasted multi-line indented text — line breaks become new lines and indentation becomes hierarchy. Pasting itself uses the device's own long-press menu, only inside an edit dialogue text field. Full original spec: `archive/backlog-specs/0011-cut-and-paste-os-clipboard.md`.
Acceptance: on a device — cut a parent with children, paste into a notes app, and the indented text is all there; paste that text back into an edit dialogue and the hierarchy rebuilds.
Refused: a recently-cut buffer inside Taskflow — the clipboard-loss risk was consciously accepted in SPEC instead.

### [0012-settings-day-begins-at] Settings — day begins at

Flavor: build

Changes: a Settings screen reachable from the side menu's bottom section (`app/src/main/java/com/example/taskflow/ui/navigation/AppDrawer.kt` plus a new settings screen under `ui/`), holding a single time picker labelled "Day begins at", stored locally on the device. Wire that time into the Tomorrow → Today rollover (`domain/SlotDeriver.kt`, `ui/schedule/ScheduleViewModel.kt`) and into the side-scrolling date picker's "today" anchor. Fold in [schedule-day-boundary-tick]: recompute slot placement at that boundary and on lifecycle resume, so a task moves without waiting for an edit. Full original spec: `archive/backlog-specs/0012-settings-day-begins-at.md`.
Acceptance: on a device — set day-begins-at a few minutes ahead, leave the app in the foreground across it, and a Tomorrow task moves to Today with no edit; the date strip's "today" anchor follows the same boundary; the setting survives relaunch.

### [0013-settings-date-format] Settings — date format

Flavor: build

Changes: a two-option setting on the Settings screen — DD/MM (default) or MM/DD — applied everywhere a date is shown: Schedule task rows, date-picker tiles, the Strategy doc, and anywhere else a date renders. Full original spec: `archive/backlog-specs/0013-settings-date-format.md`.
Acceptance: on a device — switch to MM/DD and every date on screen flips format; relaunch and the choice persists.

### [0014-json-export-and-import] JSON export and import

Flavor: build

Changes: a Settings entry "Export to JSON" producing a file containing tasks, Projects, Strategy doc descriptions, ordering metadata and `projectSuggestionDeclined` flags; and a Settings entry "Import from JSON" accepting a previously exported file and restoring the database from it, warning the user first that it replaces existing data. Full original spec: `archive/backlog-specs/0014-json-export-and-import.md`. [bridge-asks-from-method-project] asks whether an *additive* import is possible alongside this replacing one, and whether completions can be read out of an export — settle both with this item's design rather than after it.
Acceptance: on a device — export, then add and delete some tasks, then import the file back, and the database matches the export; the replace warning appears before the import proceeds.

### [0015-strategy-doc-and-life-area-context] Strategy doc and life-area context

Flavor: build

Changes: a Strategy doc reachable from the side menu, with an in-app markdown editor, building on `data/model/StrategyEntry.kt` and `data/repository/StrategyRepository.kt`. Mechanical structure: Project headings are generated from Project names in side-menu order and the user edits only the description paragraphs beneath them, so reordering Projects reorders the heading-and-paragraph pairs. A share button surfaces Android's standard share sheet for the doc or a portion of it. Plus a Room schema for Claude's life-area picture, with no user-facing surface — reached only by Claude through MCP tools. This is the free-tier surface: editor and structure, no AI reconciliation, which is [0021-strategy-doc-reconciliation-paid-tier]. Full original spec: `archive/backlog-specs/0015-strategy-doc-and-life-area-context.md`.
Acceptance: on a device — edit a description paragraph and it persists across relaunch; rename or reorder a Project and the headings follow; headings cannot be edited directly; the share button opens Android's share sheet.

### [0016-onboarding-flow] Onboarding flow

Flavor: build

Changes: a first-run flow — two cards explaining Schedule versus Projects, then the multi-page video, then the AI choice ("Skip AI for now" / "How do I set up Claude?"), with the X-in-corner escape hatch wired in throughout. Plus a side-menu entry "Turn on AI for the full experience" that re-triggers the AI choice later. The video content itself comes from [onboarding-video-content] and may be a placeholder at ship. Full original spec: `archive/backlog-specs/0016-onboarding-flow.md`.
Acceptance: on a device — a fresh install walks the two cards, the video and the AI choice in order; the X exits from any point and does not re-trigger on next launch; the side-menu entry brings the AI choice back.

### [0017-tier-model-and-subscription-handling] Tier model and subscription handling

Flavor: build

Changes: Google Play subscription wiring for the paid tier, a 30-day paid-tier trial handled through Play, subscription-pause behaviour, and local enforcement — the free tier disables cloud sync and MCP, the paid tier enables both. [subscription-pause-play-billing] confirms what Play actually exposes for pause at the moment this builds. Full original spec: `archive/backlog-specs/0017-tier-model-and-subscription-handling.md`.
Acceptance: on a test account — starting the trial unlocks the paid surfaces; cancelling or letting it lapse returns the app to free behaviour with cloud sync and MCP disabled.

### [0018-cloud-sync-paid-tier] Cloud sync, paid tier

Flavor: build

Changes: push-pull sync between the device's Room database and the cloud backend, with conflict handling for cross-device edits, since a paid user may have several devices. This precedes [0020-remote-mcp-server], which reads the cloud-side store rather than a device. Full original spec: `archive/backlog-specs/0018-cloud-sync-paid-tier.md`.
Acceptance: with two devices on one account — a task created on one appears on the other; the same task edited on both converges to a single state with nothing silently lost.

### [0019-ai-choice-flow-and-mcp-setup] AI-choice flow and MCP setup

Flavor: build

Changes: the "How do I set up Claude?" path — an explanation screen, a deep link into Anthropic's add-custom-connector modal, and instructions written to work whether or not that URL accepts pre-filled values; plus an in-app verification screen confirming the connector is reachable. Full original spec: `archive/backlog-specs/0019-ai-choice-flow-and-mcp-setup.md`.
Acceptance: on a device — follow the path end to end and the verification screen reports the connector reachable; remove the connector and it reports it unreachable rather than passing silently.

### [0020-remote-mcp-server] Remote MCP server

Flavor: build

Changes: the MCP server itself — hosted, reachable on the public internet, authenticating to one specific user's cloud-synced data. Tool surface: read tasks, create tasks, set and clear a date, refile to a Project, get and update the user's life-area profile, read and edit Strategy doc descriptions, and mark complete. The server-instructions field serves `SYSTEM-PROMPT.md` as the connection-time system prompt to Claude. Serves SYSTEM-PROMPT.md. Full original spec: `archive/backlog-specs/0020-remote-mcp-server.md`.
Acceptance: connect Claude to the server as a custom connector — each tool runs against the right user's data and no other's, and the connection delivers `SYSTEM-PROMPT.md` as the system prompt.

### [sysprompt-reconciliation-supersession] SYSTEM-PROMPT.md — pending-suggestion supersession

Flavor: build

Changes: `SYSTEM-PROMPT.md` → §Strategy doc reconciliation → *Ongoing reconciliation* — add that a new Strategy doc edit submitted while prior suggestions are still pending triggers fresh reconciliation against the latest version, folding in the prior pass's unanswered suggestions rather than stacking them, with one line of rationale kept in the doc: stale suggestions against a superseded version confuse, and the newest text is the source of truth.
Acceptance: `SYSTEM-PROMPT.md`'s Ongoing reconciliation passage states the supersession rule and the reason for it.

### [0021-strategy-doc-reconciliation-paid-tier] Strategy doc reconciliation, paid tier

Flavor: build

Changes: initial reconciliation on the first AI-mode open of the Strategy doc area — Claude reads the existing content, identifies tasks that contradict it or are missing from it, presents them in groups and asks, never silently editing. Ongoing reconciliation on every submitted Strategy doc edit — Claude diffs it and surfaces the downstream task impact, with a new edit superseding any prior pass's unanswered suggestions per [sysprompt-reconciliation-supersession]. Plus the conflict-resolution UX. Serves SYSTEM-PROMPT.md. Full original spec: `archive/backlog-specs/0021-strategy-doc-reconciliation-paid-tier.md`.
Acceptance: with Claude connected — a first open produces grouped suggestions and makes no edit until answered; a later Strategy edit surfaces the tasks it affects and folds in any suggestions still outstanding.

### [0022-help-thanks-report-a-bug-content] Help, Thanks and Report-a-bug screen content

Flavor: build

Changes: fill the three remaining bottom-of-drawer screens — Help, Thanks and Report a bug — with real content. Help covers MCP setup, the production version of the suggested custom-instruction text, and the "tasks dated before today stay on Today" behaviour described without naming the category. The words themselves come from [help-thanks-report-content], which waits on [custom-instruction-production-text] and the MCP setup design. Full original spec: `archive/backlog-specs/0022-help-thanks-report-a-bug-content.md`.
Acceptance: on a device — each of the three screens opens with real content rather than a placeholder, and Help covers all three topics.

### [nav-left-spine-spec-edit] SPEC sections for the spine's left half — completed history, Yesterday, day cards, share-a-day

Flavor: build

Changes: `SPEC.md` — add four sections after §Completed task tray on Today: §Search and completed history (the leftmost spine page, one unified search over active and completed tasks, completed listed most-recent-first with date headers between days, typing narrowing what shows below); §Yesterday page (a spine page immediately left of Today, its content essentially what was completed yesterday, since past-due tasks stay on Today); §Day-detail card layer (a foreground card on a deliberately different left-right axis from the spine, older days entering from the left and newer from the right, swiping left past the newest carrying the card layer and the search page off together and landing on Yesterday, with editing or un-completing a single task happening only from a day card); and §Share a day (a share button offering PNG and Markdown, citing `resources/research/android-share-format-png-vs-pdf.md`, Markdown carried as `text/plain`, reconciled with the existing Strategy-doc share button). Also extend §Schedule view's spine sentence leftward to read Search · Yesterday · Today · Tomorrow · Soon · Later · Strategy. Fold in [search-feature]'s surviving decisions — results are read-only and tapping one navigates to where the task lives, plus its still-open scope question — and delete that item at that point. Not in scope: building any of these screens.
Acceptance: `SPEC.md` carries the four new sections and the extended spine sentence, [search-feature]'s surviving decisions appear inside them, and no app code changes.
Refused: a separate completed-history page with an active-task search added later — the user settled on one unified search surface on 2026-08-21, since someone hunting a task usually doesn't know or care whether they already finished it, and two boxes means guessing which to open.

### [verify-blank-new-task-form] [user] Verify the blank New-task form fix on a device

Flavor: user

**No build block.** This item has not been given one at a keep-step, so the view carries no instructions for it. The run halts on it as underspecified rather than reading the queue for the missing detail.

## Everything in the queue, by name only

Headings and slugs, no reasoning. Enough to tell whether something is already filed before capturing it again — and not enough to tell what other work depends on it.

**Processed — 32 entry(s)**

- [claude-md-stale-vocabulary] (cleared) CLAUDE.md's plugin-managed block still describes the retired workflow vocabulary
- [disable-drawer-swipe-open] (cleared) Open the side menu by ☰ only — disable drawer swipe-to-open
- [0006-side-scrolling-date-picker] (cleared) Side-scrolling date picker
- [0007-recurring-tasks] (cleared) Recurring tasks
- [later-card-peek] (cleared) Later cards open showing the first ~3 tasks
- [task-reorder-within-list] (cleared) Within-list task reorder by drag — Schedule slots + Later cards
- [0008-drag-task-between-schedule-screens] (cleared) Drag a task between Schedule screens
- [0009-subtasks-under-parent-expand-collapse] (cleared) Subtasks under a parent, with expand/collapse
- [0010-outliner-typing-drag-target-icons] (cleared) Outliner typing and drag-target icons
- [0011-cut-and-paste-os-clipboard] (cleared) Cut and paste via the OS clipboard
- [0012-settings-day-begins-at] (cleared) Settings — day begins at
- [0013-settings-date-format] (cleared) Settings — date format
- [0014-json-export-and-import] (cleared) JSON export and import
- [0015-strategy-doc-and-life-area-context] (cleared) Strategy doc and life-area context
- [0016-onboarding-flow] (cleared) Onboarding flow
- [0017-tier-model-and-subscription-handling] (cleared) Tier model and subscription handling
- [0018-cloud-sync-paid-tier] (cleared) Cloud sync, paid tier
- [0019-ai-choice-flow-and-mcp-setup] (cleared) AI-choice flow and MCP setup
- [0020-remote-mcp-server] (cleared) Remote MCP server
- [sysprompt-reconciliation-supersession] (cleared) SYSTEM-PROMPT.md — pending-suggestion supersession
- [0021-strategy-doc-reconciliation-paid-tier] (cleared) Strategy doc reconciliation, paid tier
- [0022-help-thanks-report-a-bug-content] (cleared) Help, Thanks and Report-a-bug screen content
- [nav-left-spine-spec-edit] (cleared) SPEC sections for the spine's left half — completed history, Yesterday, day cards, share-a-day
- [verify-blank-new-task-form] (cleared) [user] Verify the blank New-task form fix on a device
- [verify-schedule-date-matrix] (held) [user] Verify the Schedule date-matrix rendering on a device
- [verify-far-future-project-card] (held) [user] Verify a far-future dated task under a user Project card
- [schedule-day-boundary-tick] (held) Recompute Schedule bucketing across the day boundary
- [subscription-pause-play-billing] (held) Confirm what Play Billing exposes for subscription pause
- [project-delete-later] (held) Free-tier delete a Project on Later
- [verify-project-delete-data] (held) Verify Project-deletion data behaviour end-to-end
- [project-reorder-strategy] (held) Free-tier reorder Projects in the Strategy-doc editor
- [project-lifecycle-paid] (held) Paid-tier Project reorder and delete via Claude, plus a SYSTEM-PROMPT.md edit

**Unprocessed — 14 entry(s)**

- [forward-advisory] Last session advises processing mcp-server-auth-model next
- [subscription-pause-resume-merge] Multi-device resume merge after a paused subscription
- [custom-instruction-production-text] Production version of the custom-instruction text
- [empty-state-copy-and-visuals] Empty state copy and visuals
- [help-thanks-report-content] Help, Thanks and Report-a-bug content
- [search-feature] Search
- [nav-completed-history] Completed-history sub-system — the left half of the navigation spine
- [onboarding-video-content] Onboarding video content
- [post-first-test-polish-review] Post-first-test polish review
- [execute-by-task-area] Execute by task area across the spine — focus on one area's tasks temporarily
- [personal-strategy-preview] Personal strategy and memory dogfood — an early preview of Taskflow's Strategy-doc experience
- [method-user-item-to-taskflow-handoff] Hand a multi-part [user] work item off to Taskflow as tasks
- [bridge-asks-from-method-project] Answer three asks about a Claude-to-Taskflow work bridge
- [mcp-server-auth-model] Decide how the remote MCP server proves a request belongs to the right user
