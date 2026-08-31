# QUEUE

## Processed

> Vetted work, ready to build — worked top to bottom. Each piece of work is one
> item: a `#### ` heading naming it, a `[slug]` at the end of that heading line, and
> a short rationale beneath. A leading flavor tag names how it runs — none for a
> build (Claude edits files), `[audit]` for a review pass, `[user]` for a step only
> you can do. A security or privacy risk Claude surfaces lives here too, as a work
> item carrying a `Red flag · State: cleared/uncleared` marker. The line below marks
> how far down is cleared to build; anything below it is decided but not ready yet.

#### Side-scrolling date picker [0006-side-scrolling-date-picker]

Horizontal date strip replacing the read-only date display in the edit dialogue. Full original spec: `archive/backlog-specs/0006-side-scrolling-date-picker.md`. Several held items wait on this one, because it is the only path to setting a date at all — see [verify-schedule-date-matrix] and [verify-far-future-project-card].

--- Build block ---
Changes: `app/src/main/java/com/example/taskflow/ui/edit/EditTaskScreen.kt` and `EditTaskViewModel.kt` — replace the read-only date display with a horizontal date strip. Tiles labelled DD/MM (or MM/DD per setting), today as the visual anchor, tiles fading linearly with distance from today and capped so far-off tiles stay readable; tap a tile to select it, with a highlight; a visually distinct "no date" tile at the left edge, labelled rather than merely greyed, which clears the date. The strip spans roughly one month back to twelve months forward with a month-jump affordance, shows roughly five to seven tiles at once, and opens centred on the task's own date or on today if undated. Setting a date on a previously undated task moves it into Schedule, and into its Project's card if it has one. Full original spec: `archive/backlog-specs/0006-side-scrolling-date-picker.md`.
Acceptance: on a device — open a task's edit dialogue and the strip is centred on today; tap a date three days out and save, and the task appears in Soon with that DD/MM label; reopen it, tap "no date" and save, and the task leaves Schedule.
--- End build block ---

#### Recurring tasks [0007-recurring-tasks]

Recurrence rules and 30-day-capped instance rendering. Full original spec: `archive/backlog-specs/0007-recurring-tasks.md`.

--- Build block ---
Changes: recurrence-rule editing (daily / weekly / monthly / custom — shapes confirmed at build time) in `app/src/main/java/com/example/taskflow/ui/edit/EditTaskScreen.kt`, backed by a recurrence field on `data/model/Task.kt` and its DAO and repository. The Schedule pages under `ui/schedule/` render every instance of a recurring task in the visible window, capped at 30 days from today; manually dated one-off tasks are not capped and still appear in Later however far out they are. Completing one instance marks that instance complete and leaves future instances alone. Full original spec: `archive/backlog-specs/0007-recurring-tasks.md`.
Acceptance: on a device — set a task to repeat daily; its instances appear across Today, Tomorrow and Soon and stop at 30 days out; complete today's instance and tomorrow's is still there; a one-off task dated six months ahead still shows in Later.
--- End build block ---

#### Later cards open showing the first ~3 tasks [later-card-peek]

Builds the peek behaviour [later-peek-spec-edit] wrote into SPEC: each Later Project card opens with its first ~3 tasks visible instead of collapsed to its header, the rest behind the expand/collapse control. Shares LaterPage.kt with [task-reorder-within-list] (drag-reorder), so whichever builds second integrates with the first's card-task-list rendering.

Change `app/src/main/java/com/example/taskflow/ui/schedule/LaterPage.kt` — render each Project card with its first ~3 tasks shown by default; the expand/collapse control reveals/hides the remainder.

Verify on a device: a Later card with more than 3 tasks opens showing its first 3, the rest appearing on expand; a card with 3 or fewer shows all of them; collapse/expand still works.

--- Build block ---
Changes: `app/src/main/java/com/example/taskflow/ui/schedule/LaterPage.kt` — render each Project card with its first ~3 tasks shown by default; the expand/collapse control reveals and hides the remainder.
Acceptance: on a device — a Later card with more than 3 tasks opens showing its first 3, the rest appearing on expand; a card with 3 or fewer shows all of them; collapse and expand still work.
--- End build block ---

#### Within-list task reorder by drag — Schedule slots + Later cards [task-reorder-within-list]

SPEC §Reorder within a Schedule slot specifies within-list drag-reorder of top-level tasks across two surfaces: the flat Schedule slots (Today/Tomorrow/Soon) and, on Later, within each Project card. Nothing builds either yet; the data layer persists both orders already (`updateSlotSortOrder` for slots, `updateProjectSortOrder` for Later cards — both surfaced on `TaskRepository`, DAO-tested in 0001). What's missing is the drag UI — and there's no drag-reorder pattern anywhere in the app to copy and no library, so it's a from-scratch reorderable list with real design uncertainty (nested scrolling, variable row heights, persistence), sharpest on the nested Later case. The two surfaces share one drag primitive, so this item builds it once across both, designed with the nested card case in hand from the start so flat-list assumptions don't get baked in and force rework. No spec-edit — SPEC §Schedule view and §Reorder within a Schedule slot already describe both. (Merged 2026-06-23: the within-card Later half — split out of [later-by-project-screen] mid-build and set aside as a capture — folds in here so the primitive is written once. Supersedes the earlier rescope note that wrongly assumed [later-by-project-screen] would build Later reorder.) This is also the first drag primitive in the app, which is why [project-delete-later] is held against it.

Change `app/src/main/java/com/example/taskflow/ui/schedule/SlotPage.kt` + `app/src/main/java/com/example/taskflow/ui/schedule/ScheduleViewModel.kt` — drag-to-reorder on the flat slot task lists (Today/Tomorrow/Soon), persisted via `updateSlotSortOrder`. Change `app/src/main/java/com/example/taskflow/ui/schedule/LaterPage.kt` + the same view-model — within-card drag-to-reorder of a Project card's tasks, persisted via `updateProjectSortOrder`; handle the nested-scroll / variable-row-height case inside the expandable card. `TaskRepository` reorder calls already exist, so no data-layer change is expected.

Verify on a device: drag a task within a Schedule slot (Today/Tomorrow/Soon) to a new position and confirm the order persists across navigation and relaunch; then the same within a Later Project card.

--- Build block ---
Changes: `app/src/main/java/com/example/taskflow/ui/schedule/SlotPage.kt` and `ScheduleViewModel.kt` — drag-to-reorder on the flat slot task lists (Today/Tomorrow/Soon), persisted via `updateSlotSortOrder`. `app/src/main/java/com/example/taskflow/ui/schedule/LaterPage.kt` and the same view-model — within-card drag-to-reorder of a Project card's tasks, persisted via `updateProjectSortOrder`, handling nested scrolling and variable row heights inside the expandable card. One drag primitive written across both surfaces, designed with the nested case in hand. `TaskRepository`'s reorder calls already exist, so no data-layer change is expected.
Acceptance: on a device — drag a task within a Schedule slot to a new position and the order persists across navigation and relaunch; the same holds within a Later Project card.
--- End build block ---

#### Drag a task between Schedule screens [0008-drag-task-between-schedule-screens]

Long-press drag to reschedule across Schedule slots. Full original spec: `archive/backlog-specs/0008-drag-task-between-schedule-screens.md`.

--- Build block ---
Changes: `app/src/main/java/com/example/taskflow/ui/schedule/SlotPage.kt`, `ScheduleScreen.kt` and `ScheduleViewModel.kt` — long-press to pick a task up, then drag to the left or right edge to page through to the adjacent slot. Dropping on Today or Tomorrow sets the date to today or tomorrow; dropping on Soon sets today + 2 and on Later today + 8, except that a task that was undated stays undated and parks in that slot. Reordering within a slot uses the per-slot sort order. Dragging a parent carries its children as a single unit. Full original spec: `archive/backlog-specs/0008-drag-task-between-schedule-screens.md`.
Acceptance: on a device — drag a dated task from Today to Soon and its date becomes today + 2; drag an undated task to Later and it parks there still undated; drag a parent and its children travel with it.
--- End build block ---

#### Subtasks under a parent, with expand/collapse [0009-subtasks-under-parent-expand-collapse]

Nested subtasks with parent expand/collapse and completion roll-up. Full original spec: `archive/backlog-specs/0009-subtasks-under-parent-expand-collapse.md`.

--- Build block ---
Changes: a parent/child relation on `app/src/main/java/com/example/taskflow/data/model/Task.kt` with its DAO and repository support, and rendering across `ui/schedule/SlotPage.kt` and `LaterPage.kt` — subtasks nested under their parent on the parent's Schedule page and in its Project card. Parent tasks show an expand/collapse control instead of a checkbox, revealing and hiding their children inline. Completion rolls up: completing all subtasks completes the parent, and un-completing a subtask un-completes the parent and brings it back out of the Completed tray. A parent dragged between Schedule slots or refiled to another Project carries its children as one unit. Full original spec: `archive/backlog-specs/0009-subtasks-under-parent-expand-collapse.md`.
Acceptance: on a device — add two subtasks under a task; the parent shows an expand/collapse control rather than a checkbox; complete both subtasks and the parent completes; un-complete one and the parent returns to the list with its children.
--- End build block ---

#### Outliner typing and drag-target icons [0010-outliner-typing-drag-target-icons]

Outliner editor for subtasks plus bin and promote drag targets. Full original spec: `archive/backlog-specs/0010-outliner-typing-drag-target-icons.md`.

--- Build block ---
Changes: `app/src/main/java/com/example/taskflow/ui/edit/EditTaskScreen.kt` — the text area renders parent and children as an indented outline; Enter at the end of any line creates a new child line below it; Backspace at the start of a line deletes it and merges the remaining text into the line above; child lines carry drag handles and the parent line does not. Across the Schedule pages, the Later page and the dialogue, any task drag reveals a row of drag-target icons fixed at the top right with hover feedback, carrying two targets in this item: **bin**, which deletes, and **promote**, on dialogue subtask drags only, which makes a child a top-level task at the bottom of the parent's slot or Project with the parent's date. A parent left with no children reverts from expand/collapse to a checkbox. Full original spec: `archive/backlog-specs/0010-outliner-typing-drag-target-icons.md`.
Acceptance: on a device — typing in the dialogue behaves as an outline, with Enter making a child line and Backspace merging one away; dragging a task reveals the icon row; dropping on bin deletes the task; promoting a child makes it a top-level task carrying the parent's date, and the emptied parent shows a checkbox again.
--- End build block ---

#### Cut and paste via the OS clipboard [0011-cut-and-paste-os-clipboard]

Cut drag target and OS-clipboard paste in the edit dialogue. Full original spec: `archive/backlog-specs/0011-cut-and-paste-os-clipboard.md`.

--- Build block ---
Changes: add a **cut** icon to the drag-target icon row, on both screen drags and dialogue drags. Dropping on cut removes the task from Taskflow and writes its content to the device's clipboard as plain text; cutting a parent writes the whole set as indented multi-line text. The outliner editor in `ui/edit/EditTaskScreen.kt` handles pasted multi-line indented text — line breaks become new lines and indentation becomes hierarchy. Pasting itself uses the device's own long-press menu, only inside an edit dialogue text field. Full original spec: `archive/backlog-specs/0011-cut-and-paste-os-clipboard.md`.
Acceptance: on a device — cut a parent with children, paste into a notes app, and the indented text is all there; paste that text back into an edit dialogue and the hierarchy rebuilds.
Refused: a recently-cut buffer inside Taskflow — the clipboard-loss risk was consciously accepted in SPEC instead.
--- End build block ---

#### Settings — day begins at [0012-settings-day-begins-at]

Day-boundary time picker wired into rollover and the date anchor. Full original spec: `archive/backlog-specs/0012-settings-day-begins-at.md`. [schedule-day-boundary-tick] is held against this one and should fold into its wiring.

--- Build block ---
Changes: a Settings screen reachable from the side menu's bottom section (`app/src/main/java/com/example/taskflow/ui/navigation/AppDrawer.kt` plus a new settings screen under `ui/`), holding a single time picker labelled "Day begins at", stored locally on the device. Wire that time into the Tomorrow → Today rollover (`domain/SlotDeriver.kt`, `ui/schedule/ScheduleViewModel.kt`) and into the side-scrolling date picker's "today" anchor. Fold in [schedule-day-boundary-tick]: recompute slot placement at that boundary and on lifecycle resume, so a task moves without waiting for an edit. Full original spec: `archive/backlog-specs/0012-settings-day-begins-at.md`.
Acceptance: on a device — set day-begins-at a few minutes ahead, leave the app in the foreground across it, and a Tomorrow task moves to Today with no edit; the date strip's "today" anchor follows the same boundary; the setting survives relaunch.
--- End build block ---

#### Settings — date format [0013-settings-date-format]

DD/MM or MM/DD setting applied app-wide. Full original spec: `archive/backlog-specs/0013-settings-date-format.md`.

--- Build block ---
Changes: a two-option setting on the Settings screen — DD/MM (default) or MM/DD — applied everywhere a date is shown: Schedule task rows, date-picker tiles, the Strategy doc, and anywhere else a date renders. Full original spec: `archive/backlog-specs/0013-settings-date-format.md`.
Acceptance: on a device — switch to MM/DD and every date on screen flips format; relaunch and the choice persists.
--- End build block ---

#### JSON export and import [0014-json-export-and-import]

Full database export/import via JSON files. Full original spec: `archive/backlog-specs/0014-json-export-and-import.md`.

--- Build block ---
Changes: a Settings entry "Export to JSON" producing a file containing tasks, Projects, Strategy doc descriptions, ordering metadata and `projectSuggestionDeclined` flags; every exported task carries its completion state and the date it was completed, settled in planning on 2026-08-25 in answer to [bridge-asks-from-method-project] — a parent's state is written as the **derived** roll-up value rather than left for an outside reader to recompute from its children, with the children's own states exported alongside it, since without completion state anything reading an export can put work in but never learn what happened to it; and a Settings entry "Import from JSON" accepting a previously exported file and restoring the database from it, warning the user first that it replaces existing data. Full original spec: `archive/backlog-specs/0014-json-export-and-import.md`. Both questions [bridge-asks-from-method-project] raised — whether an *additive* import is possible alongside this replacing one, and whether completions can be read out of an export — were settled into this item's design in planning on 2026-08-25, answered to the sending project, and that item was deleted at that point.
Plus a second, separately named Settings entry "Add tasks from a file" — an **additive** import, settled in planning on 2026-08-25 in answer to [bridge-asks-from-method-project]. It reads the same export format but inserts rather than restores: each incoming task is added and filed into its named Project, that Project created if it does not exist, and everything already in the database is left untouched. No warning is needed on this path because nothing is lost. Where an incoming task resembles one already present it is inserted anyway rather than de-duplicated — a duplicate is an annoyance the user can delete, while a wrongly-skipped task is work that silently never arrived. Kept as its own entry rather than a mode on the replacing import: one of the two destroys data and the other does not, and a checkbox beside a destructive action is how people lose their task list.
Acceptance: on a device — export, then add and delete some tasks, then import the file back, and the database matches the export; the replace warning appears before the import proceeds. Separately, "Add tasks from a file" against a file holding a handful of tasks adds exactly those, creating any missing Project, with every pre-existing task still present and no warning shown.
Refused: additive import as a checkbox or mode on the replacing import — the two differ in whether they destroy data, so they stay separately named. Also refused: de-duplicating incoming tasks against existing ones, since a silently dropped task is worse than a visible duplicate.
--- End build block ---

#### Strategy doc and life-area context [0015-strategy-doc-and-life-area-context]

Strategy doc editor, mechanical structure, life-area Room schema. Full original spec: `archive/backlog-specs/0015-strategy-doc-and-life-area-context.md`. [project-reorder-strategy] is held against this one.

--- Build block ---
Changes: a Strategy doc reachable from the side menu, with an in-app markdown editor, building on `data/model/StrategyEntry.kt` and `data/repository/StrategyRepository.kt`. Mechanical structure: Project headings are generated from Project names in side-menu order and the user edits only the description paragraphs beneath them, so reordering Projects reorders the heading-and-paragraph pairs. A share button surfaces Android's standard share sheet for the doc or a portion of it. Plus a Room schema for Claude's life-area picture, with no user-facing surface — reached only by Claude through MCP tools. This is the free-tier surface: editor and structure, no AI reconciliation, which is [0021-strategy-doc-reconciliation-paid-tier]. Full original spec: `archive/backlog-specs/0015-strategy-doc-and-life-area-context.md`.
Acceptance: on a device — edit a description paragraph and it persists across relaunch; rename or reorder a Project and the headings follow; headings cannot be edited directly; the share button opens Android's share sheet.
--- End build block ---

#### Onboarding flow [0016-onboarding-flow]

First-run cards, AI-value video, free/paid choice. Full original spec: `archive/backlog-specs/0016-onboarding-flow.md`. Sequenced ahead of the Claude/MCP work, so it may ship with a placeholder video — see [onboarding-video-content].

--- Build block ---
Changes: a first-run flow — two cards explaining Schedule versus Projects, then the multi-page video, then the AI choice ("Skip AI for now" / "How do I set up Claude?"), with the X-in-corner escape hatch wired in throughout. Plus a side-menu entry "Turn on AI for the full experience" that re-triggers the AI choice later. The video content itself comes from [onboarding-video-content] and may be a placeholder at ship. Full original spec: `archive/backlog-specs/0016-onboarding-flow.md`.
Acceptance: on a device — a fresh install walks the two cards, the video and the AI choice in order; the X exits from any point and does not re-trigger on next launch; the side-menu entry brings the AI choice back.
--- End build block ---

#### Tier model and subscription handling [0017-tier-model-and-subscription-handling]

Google Play subscription, trial, local tier enforcement. Full original spec: `archive/backlog-specs/0017-tier-model-and-subscription-handling.md`. [subscription-pause-play-billing] is held against this one.

--- Build block ---
Changes: Google Play subscription wiring for the paid tier, a 30-day paid-tier trial handled through Play, subscription-pause behaviour, and local enforcement — the free tier disables cloud sync and MCP, the paid tier enables both. [subscription-pause-play-billing] confirms what Play actually exposes for pause at the moment this builds. Full original spec: `archive/backlog-specs/0017-tier-model-and-subscription-handling.md`.
Acceptance: on a test account — starting the trial unlocks the paid surfaces; cancelling or letting it lapse returns the app to free behaviour with cloud sync and MCP disabled.
--- End build block ---

#### Cloud sync, paid tier [0018-cloud-sync-paid-tier]

Push-pull sync between the device Room DB and a cloud backend. Full original spec: `archive/backlog-specs/0018-cloud-sync-paid-tier.md`.

--- Build block ---
Changes: push-pull sync between the device's Room database and the cloud backend, with conflict handling for cross-device edits, since a paid user may have several devices. **Resume after a paused subscription is the same problem with a longer gap**, folded in from [subscription-pause-resume-merge] in planning on 2026-08-25, which was deleted at that point: while paused, Taskflow is local-only on every device, so two devices diverge with no cloud arbiter between them. On resume the cloud takes the **union** of tasks from every device rather than letting one device's state win; where the same task was edited on two devices the most recent edit wins field by field; and a task deleted on one device during the pause is **not** deleted on resume if another device still has it. The reasoning: a deletion made while offline is a weak signal, and Taskflow's instinct everywhere else is that nothing is lost to a delete — deleting a Project reassigns its tasks rather than destroying them. The accepted cost is that a task deleted on one device may reappear, which is one gesture to fix, where a task destroyed by a merge is gone. This precedes [0020-remote-mcp-server], which reads the cloud-side store rather than a device. Full original spec: `archive/backlog-specs/0018-cloud-sync-paid-tier.md`.
Acceptance: with two devices on one account — a task created on one appears on the other; the same task edited on both converges to a single state with nothing silently lost. After a pause during which both devices were edited separately, resuming leaves every task from both devices present, a task edited on both carries the later edit, and a task deleted on one but still held on the other is still there.
Refused: letting the last device to sync win on resume — it would silently discard everything done on the other device during a pause that may have run for months.
--- End build block ---

#### AI-choice flow and MCP setup [0019-ai-choice-flow-and-mcp-setup]

Claude setup path with connector deep-link and verification. Full original spec: `archive/backlog-specs/0019-ai-choice-flow-and-mcp-setup.md`.

--- Build block ---
Changes: the "How do I set up Claude?" path — an explanation screen, a deep link into Anthropic's add-custom-connector modal, and instructions written to work whether or not that URL accepts pre-filled values; plus an in-app verification screen confirming the connector is reachable. Full original spec: `archive/backlog-specs/0019-ai-choice-flow-and-mcp-setup.md`.
Acceptance: on a device — follow the path end to end and the verification screen reports the connector reachable; remove the connector and it reports it unreachable rather than passing silently.
--- End build block ---

#### Remote MCP server [0020-remote-mcp-server]

Hosted MCP server with tool surface, authentication, and system-prompt delivery. Full original spec: `archive/backlog-specs/0020-remote-mcp-server.md`. [project-lifecycle-paid] is held against this one.

**The auth model was settled in planning on 2026-08-25** and folded in here from the capture [mcp-server-auth-model], which was deleted at that point — this item builds the server, and how the server knows whose data it is serving is the same piece of work rather than a separate one. It carried an uncleared red flag: this is the one part of Taskflow that faces the open internet, and the data behind it — the Strategy doc and life-area profile — describes the shape of someone's life. The flag is cleared by the design below rather than by accepting the risk.

Why OAuth and not a shared key, per `resources/research/claude-custom-connector-auth-options.md`: a per-user secret in the connector URL is ruled out by Anthropic's own guidance, since a URL carrying a token leaks through server logs, proxy logs, browser history, analytics and screenshots. A pasted static key has no consumer-facing field to paste into — the add-connector flow asks only for a URL — and the `static_headers` type is framed for a fixed organisation-level credential rather than a per-person one. Dynamic client registration needs no registration with Anthropic, so it is the route that works out of the box. The cost was named to the user and accepted: this is a real authorisation server to build, more work than a shared secret would have been.

--- Build block ---
Changes: the MCP server itself — hosted, reachable on the public internet, serving one specific user's cloud-synced data. Tool surface: read tasks, create tasks, set and clear a date, refile to a Project, get and update the user's life-area profile, read and edit Strategy doc descriptions, and mark complete. Authentication is OAuth 2.1 with dynamic client registration, so the user pastes only the server URL: the server publishes its authorization-server metadata for Claude to discover, hosts the sign-in and consent page where the user logs in to their own Taskflow account and sees what Claude is asking to reach, and issues an access credential scoped to that one account, read from the request rather than from the URL. Every tool call resolves the account from that credential and touches no other account's data. A request carrying no credential, an expired one, or one that does not match the data being asked for is refused with an authorization error rather than served or guessed at. Access is revocable server-side without the user redoing connector setup, and revoking is what happens when paid access ends, per [0017-tier-model-and-subscription-handling]. Where identity lives in the cloud store is fixed by [0018-cloud-sync-paid-tier], which this authenticates against. The server-instructions field serves `SYSTEM-PROMPT.md` as the connection-time system prompt to Claude. Serves SYSTEM-PROMPT.md. Full original spec: `archive/backlog-specs/0020-remote-mcp-server.md`.
Acceptance: connect Claude to the server as a custom connector by URL alone — the sign-in and consent page appears, and after approval each tool runs against that account's data and no other's; a call with a missing, expired or mismatched credential is refused; revoking access server-side stops the tools working without touching the connector; and the connection delivers `SYSTEM-PROMPT.md` as the system prompt.
Red flag: cleared
Refused: a per-user secret pasted into the connector URL or a header — the URL form leaks through logs, history and screenshots on Anthropic's own guidance, and there is no consumer-facing field for pasting a key at all.
--- End build block ---

#### SYSTEM-PROMPT.md — pending-suggestion supersession [sysprompt-reconciliation-supersession]

Serves SYSTEM-PROMPT.md.

`SYSTEM-PROMPT.md` describes Strategy-doc reconciliation but doesn't say what happens when the user submits a new Strategy edit while a prior reconciliation's suggestions are still unanswered. Decided in planning 2026-06-16: the new edit wins. This writes that rule into the doc so the reconciliation feature ([0021-strategy-doc-reconciliation-paid-tier]) is built against a complete description.

Edit `SYSTEM-PROMPT.md` → §Strategy doc reconciliation → *Ongoing reconciliation*: add that a new Strategy doc edit submitted while prior suggestions are still pending triggers fresh reconciliation against the latest version, superseding (folding in) the prior pass's unanswered suggestions rather than stacking them. Keep one line of rationale in the doc: stale suggestions against a superseded version confuse; newest text is the source of truth.

--- Build block ---
Changes: `SYSTEM-PROMPT.md` → §Strategy doc reconciliation → *Ongoing reconciliation* — add that a new Strategy doc edit submitted while prior suggestions are still pending triggers fresh reconciliation against the latest version, folding in the prior pass's unanswered suggestions rather than stacking them, with one line of rationale kept in the doc: stale suggestions against a superseded version confuse, and the newest text is the source of truth.
Acceptance: `SYSTEM-PROMPT.md`'s Ongoing reconciliation passage states the supersession rule and the reason for it.
--- End build block ---

#### Strategy doc reconciliation, paid tier [0021-strategy-doc-reconciliation-paid-tier]

Initial and ongoing Strategy doc reconciliation via Claude. Full original spec: `archive/backlog-specs/0021-strategy-doc-reconciliation-paid-tier.md`. [project-lifecycle-paid] is held against this one.

--- Build block ---
Changes: initial reconciliation on the first AI-mode open of the Strategy doc area — Claude reads the existing content, identifies tasks that contradict it or are missing from it, presents them in groups and asks, never silently editing. Ongoing reconciliation on every submitted Strategy doc edit — Claude diffs it and surfaces the downstream task impact, with a new edit superseding any prior pass's unanswered suggestions per [sysprompt-reconciliation-supersession]. Plus the conflict-resolution UX. Serves SYSTEM-PROMPT.md. Full original spec: `archive/backlog-specs/0021-strategy-doc-reconciliation-paid-tier.md`.
Acceptance: with Claude connected — a first open produces grouped suggestions and makes no edit until answered; a later Strategy edit surfaces the tasks it affects and folds in any suggestions still outstanding.
--- End build block ---

#### Help, Thanks and Report-a-bug screen content [0022-help-thanks-report-a-bug-content]

Bottom-of-drawer screen content including help and custom instructions. Full original spec: `archive/backlog-specs/0022-help-thanks-report-a-bug-content.md`. The words that go in these screens are still being worked out — see [help-thanks-report-content].

--- Build block ---
Changes: fill the three remaining bottom-of-drawer screens — Help, Thanks and Report a bug — with real content. Help covers MCP setup, the production version of the suggested custom-instruction text, and the "tasks dated before today stay on Today" behaviour described without naming the category. The words themselves come from [help-thanks-report-content], which waits on [custom-instruction-production-text] and the MCP setup design. Full original spec: `archive/backlog-specs/0022-help-thanks-report-a-bug-content.md`.
Acceptance: on a device — each of the three screens opens with real content rather than a placeholder, and Help covers all three topics.
--- End build block ---

#### SPEC sections for the spine's left half — completed history, Yesterday, day cards, share-a-day [nav-left-spine-spec-edit]

Writes the four SPEC sections the left-half navigation work needs before any of it can be built. Split out of [nav-completed-history] in planning on 2026-08-21, once two of that item's three open questions were settled with the user — which is what makes these sections writable now.

Change `SPEC.md` — add four sections after §Completed task tray on Today:

- **§Search and completed history.** The leftmost spine page, unified across active and completed tasks. Settled 2026-08-21, the user's call: one search surface rather than two, because someone hunting a task usually doesn't know or care whether they already finished it, and two boxes means guessing which to open. Completed tasks list in completion order, most recent first, with date headers between days; typing narrows what shows below, and the relevant date headers still display above each day's results. This supersedes [search-feature]'s framing of a separate search surface, so that item is reconciled here rather than built alongside. That item's two surviving decisions were folded in on 2026-08-25 and it was deleted at that point: **results are read-only** — a task cannot be completed from the results list, and tapping a result navigates to where the task lives, which is also what keeps the read-only rule sitting comfortably beside a tappable completed list, since editing and un-completing happen only from a day card; and **scope is all tasks, active and completed, across every slot and every Project, plus Project names, with the Strategy doc excluded**. Settled 2026-08-25, the user's call on the recommendation: scoping to the current screen or Project would reintroduce the "am I looking in the right place?" guess that unified search exists to remove, and Project names come along because typing a Project's name and getting the Project is the same gesture. The Strategy doc is out because it is prose rather than items, so its results cannot render as task rows, and it is a single document the user can simply open and read. The current-screen and current-Project scopes were the other two candidates and lost for that reason.
- **§Yesterday page.** A spine page immediately left of Today, not a card. Its content is essentially what was completed yesterday, since past-due tasks stay on Today (UX principle 4).
- **§Day-detail card layer.** A foreground card opening on a tapped result, group or date header, on a deliberately different left-right axis from the spine and signalled by the card visual. Swipe right brings the older day in from the left, swipe left the newer from the right; swiping left past the newest card carries the card layer and the search page off together in one motion, landing the user on Yesterday. Editing or un-completing a single task happens only from a day card.
- **§Share a day.** A share button on a day screen shares that day's completed tasks, offered in two formats: PNG and Markdown. Settled 2026-08-21 — the user asked for Markdown alongside PNG on the view that Markdown will only become more widely read. Cites `resources/research/android-share-format-png-vs-pdf.md` for both halves: PNG because it renders inline in a chat thread rather than arriving as an attachment to open, and Markdown carried under the `text/plain` MIME type rather than `text/markdown`, which almost no Android app declares and which would produce a near-empty share sheet. Reconcile the wording with the existing Strategy-doc share button, which already uses the Android share sheet.

Also extend §Schedule view's spine sentence leftward, so the spine reads Search · Yesterday · Today · Tomorrow · Soon · Later · Strategy.

Not in scope: building any of these screens, and the swipe and card-layer interaction detail [nav-completed-history] deliberately holds open until there is a real screen to finalise it against.

--- Build block ---
Changes: `SPEC.md` — add four sections after §Completed task tray on Today: §Search and completed history (the leftmost spine page, one unified search over active and completed tasks, completed listed most-recent-first with date headers between days, typing narrowing what shows below); §Yesterday page (a spine page immediately left of Today, its content essentially what was completed yesterday, since past-due tasks stay on Today); §Day-detail card layer (a foreground card on a deliberately different left-right axis from the spine, older days entering from the left and newer from the right, swiping left past the newest carrying the card layer and the search page off together and landing on Yesterday, with editing or un-completing a single task happening only from a day card); and §Share a day (a share button offering PNG and Markdown, citing `resources/research/android-share-format-png-vs-pdf.md`, Markdown carried as `text/plain`, reconciled with the existing Strategy-doc share button). Also extend §Schedule view's spine sentence leftward to read Search · Yesterday · Today · Tomorrow · Soon · Later · Strategy. §Search and completed history also states that results are read-only — a task cannot be completed from the results list, and tapping a result navigates to where the task lives — and that search covers all tasks, active and completed, across every slot and every Project, plus Project names, with the Strategy doc excluded. Both settled in planning on 2026-08-25, when [search-feature] was folded in here and deleted. Not in scope: building any of these screens.
Acceptance: `SPEC.md` carries the four new sections and the extended spine sentence, [search-feature]'s surviving decisions appear inside them, and no app code changes.
Refused: a separate completed-history page with an active-task search added later — the user settled on one unified search surface on 2026-08-21, since someone hunting a task usually doesn't know or care whether they already finished it, and two boxes means guessing which to open.
--- End build block ---

#### Write the onboarding video script and storyboard [onboarding-video-script]

Split out of [onboarding-video-content] in planning on 2026-08-25. That item bundled a design decision with a production job: what the video *says* is designable now, while filming it waits on the Claude integration existing. This is the design half.

The video carries the whole free-versus-paid choice in onboarding, so what it demonstrates is a product decision rather than a production detail. It can be written from what is already settled — SPEC's tier model, §Claude integration via remote MCP, §Strategy doc, and `SYSTEM-PROMPT.md`'s account of how Claude explores life areas, suggests projects and reconciles the Strategy doc. Nothing in it waits on the integration being built.

It is also what [0016-onboarding-flow] needs in order to know what its placeholder video stands in for, and what [onboarding-video-content] then films.

--- Build block ---
Changes: a new `ONBOARDING-VIDEO-SCRIPT.md` at the project root — the multi-page onboarding video, page by page. Each page carries what is on screen (which Taskflow surface or which Claude exchange), the claim that page makes about what Claude adds, and roughly how long it runs. The through-line is the paid tier's actual value as SPEC describes it: the user talks to Claude where they already talk to Claude, and Taskflow is reachable from there — life-area exploration, project suggestions, and Strategy-doc reconciliation, drawn from `SYSTEM-PROMPT.md`. The script states plainly which pages need a live Claude conversation to film and which are plain screen capture, since that is what decides how much of the filming can be automated. No app code changes.
Acceptance: `ONBOARDING-VIDEO-SCRIPT.md` exists and covers every page of the video, each with its on-screen content, its claim and a rough duration, and marks which pages need a live Claude conversation; someone could film it without asking further design questions.
--- End build block ---

#### Empty state copy and visuals [empty-state-copy-and-visuals]

Write copy and visuals for each empty state in the Later-by-Project world: a Schedule slot (Today / Tomorrow / Soon) with zero tasks; an empty **Later card** — a Project the user has but with nothing in it, since cards always render even when empty; and **Later before the user has made any Project of their own**, where only the pinned Unassigned card shows. The old "empty Project" state is now the empty Later card; the old "Projects list before any Project exists" is gone with the side-menu Projects list — Later is the Projects surface now.

A fourth empty state, folded in from a capture during planning on 2026-06-24: a Later card that reads empty not because the Project has no tasks, but because all its tasks are near-term dated (Today/Tomorrow/Soon) and live on the schedule rather than the card — so "nothing in this project yet" misleads, since the Project does have a task. Noticed on device 2026-06-24 verifying [project-create-picker-ui]: a Project created from a Tomorrow-dated task shows an empty Later card while the task sits correctly on Tomorrow. Two candidate fixes to weigh at the design pass: copy that distinguishes "no tasks" from "no Later tasks — N on the schedule"; or surfacing a Project's near-term task count on its card. The second is broader than empty-state copy — it changes what every card shows, empty or not, so treat it as a card-design question, not a free copy tweak.

Best written in front of the real screens rather than against screens that don't exist yet, which is why it wasn't designed sooner. The zero-task Schedule-slot copy could be written against shipped 0002, and writing all the empty states together against the real Later screen is cleaner.

**Kept in planning on 2026-08-25: that reason has expired.** Three of the four states now have real screens on the device — the zero-task Schedule slot, the empty Later card, and Later before the user has made a Project of their own. The fourth state's open question was settled at the same time, the user's call on the recommendation: **distinguishing copy, not counters on cards.** An empty card whose Project does have near-term tasks says so in its own sentence, naming how many are on the schedule; a card that is empty because the Project has nothing says the plain thing. The rejected alternative was surfacing a near-term task count on every card, empty or not: it changes what every card shows, and SPEC §Schedule view is explicit that the small peek is what keeps Later a calm overview rather than a wall of tasks. The copy fix costs nothing when a card is not in that state, because the sentence only appears when the card is empty.

No SPEC edit: SPEC already says every Project appears on Later even with nothing in it, and the wording of a message is not product truth.

--- Build block ---
Changes: `app/src/main/java/com/example/taskflow/ui/schedule/SlotPage.kt` — an empty-state message and visual for a Schedule slot (Today / Tomorrow / Soon) holding no tasks. `app/src/main/java/com/example/taskflow/ui/schedule/LaterPage.kt` — three more: an empty Project card whose Project has no tasks at all; an empty Project card whose Project does have tasks, all of them near-term dated and living on the schedule, which says so and names how many are there; and Later before the user has created any Project of their own, where only the pinned Unassigned card shows. Copy stays in the app's string resources rather than inline, so all four read as one voice. Tone follows UX principle 4 — an empty list is a normal state, not a failure, so no chiding and no exhortation to add something.
Acceptance: on a device — an empty Today shows the slot empty-state rather than a blank page; a Project with nothing in it shows the plain empty-card message; a Project whose only tasks are dated for today or tomorrow shows the card message naming how many are on the schedule rather than claiming the Project is empty; and a fresh install with no user Projects shows the Later message with only the Unassigned card present.
Refused: surfacing a near-term task count on every Later card, empty or not — it changes what every card shows, against SPEC §Schedule view's statement that the small peek keeps Later a calm overview rather than a wall of tasks.
--- End build block ---

#### Execute by task area across the spine — focus on one area's tasks temporarily [execute-by-task-area]

Raised by Alex during the [project-create] device test on 2026-06-21, sharpened in planning on 2026-06-22, and looked at again against the real Project-grouped Later on 2026-06-23. The need: a way to *temporarily* focus on a single area (Project) and see all its tasks across the spine — including Today, Tomorrow and Soon — for when motivation is only there for one area.

The tension: the near-term slots are deliberately flat, horizon-sliced lists (UX principle 3 — execution structured by time, not category), so serving area-focus there cuts against the one view built to refuse category-slicing. A transient focus/filter-mode approach was floated on 2026-06-22 and set aside because of UI concerns the user couldn't accept; their own read that day was that there might be no good answer yet and leaving it open was acceptable.

**Designed out in planning on 2026-08-25, and SPEC §Focus on one Project temporarily now describes it.** The item had no blocker and no trigger, so it returned to the top every session and was set aside again — the reason for taking it up rather than dating it. The design that broke the tension: focus is entered from a Later card's header, makes itself continuously visible in a recoloured top bar carrying the Project's name and an X, and does not survive closing the app. That last property is what answers the principle-3 objection — the flat time-horizon list stays the app's real shape, and a lens that cannot survive a relaunch cannot become how the user lives in the app. The rejected alternative remains a general, persistent filter over the slots: it restructures the slots into a category-sliced app, which is the thing principle 3 exists to refuse.

--- Build block ---
Changes: `app/src/main/java/com/example/taskflow/ui/schedule/LaterPage.kt` — tapping a Project card's header (distinct from its expand/collapse control, and distinct from the long-press used for card drag) enters focus on that Project. `app/src/main/java/com/example/taskflow/ui/schedule/ScheduleViewModel.kt` — a transient focused-Project state, held in memory only and never persisted, that filters the Today, Tomorrow and Soon task flows to that Project; Later is not filtered, since it is already organised by area. `app/src/main/java/com/example/taskflow/ui/schedule/ScheduleScreen.kt` and `SlotPage.kt` — while focused, the top bar takes a distinct colour and shows the Project's name with an X that exits focus; a focused slot with no tasks shows the ordinary slot empty state. Task capture while focused files the new task into the focused Project instead of the system Unassigned Project. The Completed tray on Today shows that Project's completions while focus is on. Focus is cleared on app start, so a fresh launch is always unfocused. Serves SPEC §Focus on one Project temporarily.
Acceptance: on a device — tap a Later card's header and Today, Tomorrow and Soon show only that Project's tasks, with the top bar recoloured and naming the Project; tap the X and every task returns; add a task from Today while focused and it belongs to the focused Project; kill and relaunch the app and it opens unfocused; tapping a card's expand control still expands the card rather than entering focus.
Refused: a general persistent filter over the Schedule slots — it restructures the slots into a category-sliced app, which UX principle 3 exists to refuse; the transience and the always-visible banner are what make this lens acceptable where a filter was not.
--- End build block ---

#### [user] Verify the blank New-task form fix on a device [verify-blank-new-task-form]

The [add-flow-create-path-fixes] build fixed the stale New-task title by giving the add dialogue a fresh view-model store per open. This is the device check that confirms it on the installed build — it couldn't run in the build's own session, so it waited.

Walkthrough, on a device with the current build installed:
1. Open Taskflow and go to any Schedule slot (Today is fine).
2. Tap the **+** button (the round FAB, bottom-right). The New-task form opens.
3. Type a title and save it. The task appears in the list.
4. Tap the **+** button again on the same slot.
5. Look at the Title field. It should be **empty**. If it still shows the title you just saved, the fix hasn't landed.

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

#### Last session advises processing 0006-side-scrolling-date-picker next [forward-advisory]

Advice, not work: the top cleared item, [0006-side-scrolling-date-picker], already has partial code in the tree from a build session that crashed before recording it — a new `DateStrip.kt` plus edits to `EditTaskScreen.kt` and `EditTaskViewModel.kt`, committed at this close as unfinished, unreviewed work. A /next run that builds the item without knowing this would build on top of (or duplicate) code nobody has checked. Look at that partial work first — keep it, finish it, or discard it — before the item is built. Note also that none of the crashed run's Kotlin compiled (Gradle's daemon connection fails on this machine for Claude), so the next compile in Android Studio is the first real check of all of it.

#### Help, Thanks and Report-a-bug content [help-thanks-report-content]

The words for the three bottom-of-drawer screens that [0022-help-thanks-report-a-bug-content] builds. Help should cover MCP setup, the production custom-instruction text, and the "tasks dated before today" behaviour described without naming the category — the SPEC §Tasks dated before today wording is ready. **This item also drafts the custom-instruction text itself**, folded in on 2026-08-25 from [custom-instruction-production-text]: that text is one of Help's three topics, so the words belong with the rest of Help's words rather than in an item of their own. What stayed behind there is only the live test, which reads the draft this item produces and reports back the wording changes that follow. Two of its inputs aren't ready: the live-tested wording of that custom-instruction text ([custom-instruction-production-text]) and the MCP setup design ([0019-ai-choice-flow-and-mcp-setup], [0020-remote-mcp-server]). Write the content when those have landed.

**Skipped in planning on 2026-08-25**, with the design progress made there recorded here. The item is mixed and mostly not writable yet: Help's MCP-setup instructions would have to be invented before [0019-ai-choice-flow-and-mcp-setup] and [0020-remote-mcp-server] define the path, and the custom-instruction text this item now drafts is in the same position. Thanks is writable today but is a single paragraph [0022-help-thanks-report-a-bug-content] will write when it builds, so splitting it out would produce a fragment rather than a piece of work.

**One decision is available now and it is the user's: where a bug report goes** — an email address, a web form, or a GitHub issue. The Report-a-bug screen's words follow from that answer and cannot be written before it. Offered on 2026-08-25 and set aside with the rest of the item; ask again next time this comes up, because settling it makes the Report-a-bug half splittable on its own.

#### Completed-history sub-system — the left half of the navigation spine [nav-completed-history]

The pages left of Today on the spine: a **Search / completed-history page** (leftmost), a **Yesterday page**, a **day-detail card layer**, and **share-a-day**. The right half (spine backbone, Projects/Strategy pages, side-menu mirror) was promoted to [nav-spine-spec-edit] during planning on 2026-06-17; this is the remainder. It was [nav-zoom-spine-and-completed-history] before that split, and grew out of [completed-task-post-tray-fate] during the same session (evidence citation, not a dependency).

**Completed-history (the Search page).** The leftmost page lists completed tasks in completion order, most recent at top, with date headers between days. Typing a search narrows what shows below; the relevant date headers still display above each day's results.

**Yesterday page.** A spine page, not a card. Entangled with completed-history: since past-due tasks stay on Today (principle 4), Yesterday's content is essentially what was completed yesterday — so its design belongs with this sub-system, not the schedule screens.

**Day-detail card layer (a separate axis).** Tapping a result, a group, or a date header opens that day on a **card in the foreground** — deliberately a *different* left-right axis from the spine, signalled by the card visual. Swipe right = previous (older) day slides in from the left; swipe left = next (newer) day slides in from the right. Swiping left past the newest card (day-before-yesterday) slides the whole card layer *and* the Search page off in one smooth motion, returning the user to the main spine, landing on **Yesterday**. Only from a day card can the user tap a single task to edit or uncomplete it.

**Share-a-day.** A share button on a day screen shares that single day's completed tasks. Format settled 2026-08-21: **PNG and Markdown, both offered**, per `resources/research/android-share-format-png-vs-pdf.md` — PNG because it renders inline in a chat thread rather than arriving as an attachment to open, and Markdown carried under the `text/plain` MIME type, since almost no Android app declares `text/markdown` and using it would produce a near-empty share sheet. PDF and plain-text-only were the alternatives and lost on that arrival-behaviour reading. Reconcile with the existing Strategy-doc share button, which uses the Android share sheet.

**SPEC consequences when developed.** Adds new sections for the completed-history page, the Yesterday page, the day-card layer, and share-a-day, and extends the spine described by [nav-spine-spec-edit] leftward (Search · Yesterday, left of Today). The right-half SPEC rewrite is handled by [nav-spine-spec-edit], which also absorbs the held spec-trim findings F3/F4/F18 — they live in the Schedule-view and side-menu sections it rewrites. This left-half item adds only the new completed-history sections; it does not touch those findings.

**Sub-questions, two settled in planning on 2026-08-21.** (1) *Settled — unified.* The leftmost page searches active and completed tasks together, the user's call: someone hunting a task usually doesn't know or care whether they already finished it. Accepted against it: the page does two jobs, and [search-feature]'s read-only rule sits oddly beside a completed list you tap into — the day-card layer resolves that, since editing happens only from a card. [search-feature] is reconciled by this rather than built separately, and on 2026-08-25 its two surviving decisions — read-only results, and scope covering all tasks plus Project names with the Strategy doc excluded — were folded into [nav-left-spine-spec-edit] and that item was deleted, so it is no longer in the queue to look up. (2) *Settled — PNG and Markdown, both offered*, per `resources/research/android-share-format-png-vs-pdf.md`. (3) *Still open — swipe and card-layer interaction detail*, deliberately left until there is a real screen to finalise it against.

**What this item still needs before it can be kept.** It bundles four sub-features and none of their screens exist, so what changes inside which files can't be stated — the bar for Processed. The SPEC half was split out on 2026-08-21 as [nav-left-spine-spec-edit], which carries the two settled decisions above and must land before any of this is buildable. This item is the build remainder and stays here until that spec edit ships and there are real screens to design the interaction against. Skipped for that reason in planning on 2026-08-25, with nothing else about it left open: once [nav-left-spine-spec-edit] has shipped, SPEC describes all four screens and this splits into buildable pieces, so it becomes keepable then rather than needing further design discussion first.

#### Personal strategy and memory dogfood — an early preview of Taskflow's Strategy-doc experience [personal-strategy-preview]
Not before: 2026-11-25

Alex's idea, raised in planning on 2026-06-24: use this workspace as an early, live preview of Taskflow's personal Strategy-doc experience, before Taskflow has the feature built. She'd keep a real personal Strategy doc and have the strategy conversations here with Claude directly, instead of through Taskflow plus a remote MCP server — neither of which exists yet. In her words: "We're just here, so we don't need the MCP."

Three threads bundled in it: (1) a real personal Strategy doc for Alex, maintained here in conversation with Claude — the experience a paid Taskflow user would eventually get via MCP, doubling as genuine design research for Taskflow's Strategy-doc feature; (2) Alex's real tasks, handled carefully so they don't go missing when Taskflow test builds wipe data — her live task data must not depend on the test app; (3) Claude-memory sync across her Claude surfaces. The insight she reached, and Claude strongly seconded: rather than pushing each strategy update out into many Claude memory stores, point all her Claudes at one canonical strategy doc and have them read from it. Pull-from-one beats push-to-many — one source of truth, nothing to hand-sync.

Why it was shelved, decided 2026-06-24: the method is one-spec-per-project. Two of the three threads (personal strategy, memory sync) aren't Taskflow app features, so they don't fit SPEC.md's contract that every entry describes something existing in the build. Governing three concerns in one workspace would need either multi-spec handling in the method or a deliberate re-framing of what this SPEC is about. Alex chose to wait for multi-spec support rather than bend SPEC now. That's a change to the method itself rather than a queue item here, which is why nothing in this queue holds it.

Privacy note to carry into any revival: if the personal strategy and real tasks get committed into this product repo and it's ever shared or made public, that's the user's private life data exposed. Decide the home with that in mind when this revives — it is the first question when this comes back, not an afterthought.

**Dated in planning on 2026-08-25, with the user's approval.** It waits on multi-spec support in the method, which no item in this queue can deliver and which belongs to the No code method project. It cannot be held below the readiness line either, because held work has to be specific enough to build and this is not. Left as a plain capture it returned to the top every session and was set aside again, which is what had been happening. Three months was chosen as long enough not to re-read it every session and short enough that it comes back while still fresh if multi-spec support lands sooner. It is not offered again before that date.

#### [user] Verify the drawer swipe-off on your device [verify-drawer-swipe-off-on-device]

The [disable-drawer-swipe-open] build shipped its code change (AppRoot.kt gates `gesturesEnabled` on `drawerState.isOpen`), but the session that built it could not compile or deploy — Gradle's daemon connection fails on this machine — so its acceptance checks were never run. They need your eyes on the device. Walkthrough: (1) Install or run the current app build on your phone or emulator — the next successful build from Android Studio covers compiling this change; open the app to Today. (2) From the left edge of the screen, swipe right — look for: the side menu does NOT open. (3) Tap the ☰ button top-left — look for: the menu opens. (4) With the menu open, tap the dimmed area to its right — look for: the menu closes. (5) In the middle of the screen, swipe left — look for: the day changes to Tomorrow (and the chevrons still work). This verifies the shipped item's acceptance criteria and nothing else; if any step fails, say so and it becomes a fix item.

