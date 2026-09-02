# QUEUE

## Processed

> Vetted work, ready to build — worked top to bottom. Each piece of work is one
> item: a `#### ` heading naming it, a `[slug]` at the end of that heading line, and
> a short rationale beneath. A leading flavor tag names how it runs — none for a
> build (Claude edits files), `[audit]` for a review pass, `[user]` for a step only
> you can do. A security or privacy risk Claude surfaces lives here too, as a work
> item carrying a `Red flag · State: cleared/uncleared` marker. The line below marks
> how far down is cleared to build; anything below it is decided but not ready yet.

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

Why OAuth and not a shared key, per `workshop/resources/research/claude-custom-connector-auth-options.md`: a per-user secret in the connector URL is ruled out by Anthropic's own guidance, since a URL carrying a token leaks through server logs, proxy logs, browser history, analytics and screenshots. A pasted static key has no consumer-facing field to paste into — the add-connector flow asks only for a URL — and the `static_headers` type is framed for a fixed organisation-level credential rather than a per-person one. Dynamic client registration needs no registration with Anthropic, so it is the route that works out of the box. The cost was named to the user and accepted: this is a real authorisation server to build, more work than a shared secret would have been.

--- Build block ---
Changes: the MCP server itself — hosted, reachable on the public internet, serving one specific user's cloud-synced data. Tool surface: read tasks, create tasks, set and clear a date, refile to a Project, get and update the user's life-area profile, read and edit Strategy doc descriptions, and mark complete. Authentication is OAuth 2.1 with dynamic client registration, so the user pastes only the server URL: the server publishes its authorization-server metadata for Claude to discover, hosts the sign-in and consent page where the user logs in to their own Taskflow account and sees what Claude is asking to reach, and issues an access credential scoped to that one account, read from the request rather than from the URL. Every tool call resolves the account from that credential and touches no other account's data. A request carrying no credential, an expired one, or one that does not match the data being asked for is refused with an authorization error rather than served or guessed at. Access is revocable server-side without the user redoing connector setup, and revoking is what happens when paid access ends, per [0017-tier-model-and-subscription-handling]. Where identity lives in the cloud store is fixed by [0018-cloud-sync-paid-tier], which this authenticates against. The server-instructions field serves `SYSTEM-PROMPT.md` as the connection-time system prompt to Claude. Serves SYSTEM-PROMPT.md. Full original spec: `archive/backlog-specs/0020-remote-mcp-server.md`.
Acceptance: connect Claude to the server as a custom connector by URL alone — the sign-in and consent page appears, and after approval each tool runs against that account's data and no other's; a call with a missing, expired or mismatched credential is refused; revoking access server-side stops the tools working without touching the connector; and the connection delivers `SYSTEM-PROMPT.md` as the system prompt.
Red flag: cleared
Refused: a per-user secret pasted into the connector URL or a header — the URL form leaks through logs, history and screenshots on Anthropic's own guidance, and there is no consumer-facing field for pasting a key at all.
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

#### Last session advises processing durable-local-data next [forward-advisory]

Replaces the spent advisory this close cleared, which pointed at [0006-side-scrolling-date-picker] —
built and removed in the run just closed.

The reason to take [durable-local-data] first is that it gates what the user actually wants from
this project. They asked directly whether Taskflow is safe to rely on yet, and the honest answer was
no: the app throws away every task on the device whenever the database shape changes, and that run
alone changed it three times. Until that is settled, each build can take their tasks with it.

It also overlaps the top of the cleared region. [0018-cloud-sync-paid-tier] is the same
data-durability question at a distance, so deciding what survives locally shapes what sync is for
rather than being decided by it.

Two things about the queue's state that the planning session will meet either way, and that make a
build run a poor first move. Everything above the cleared-to-run line is work the last run examined
and could not begin — a /next run would stall on [0017-tier-model-and-subscription-handling]
immediately; that is written up as [cleared-region-unbuildable]. And most of what the run shipped
has never been exercised on a device, collected as [verify-run-2026-08-31].

Advice rather than instruction: the user may plan as many times as they like before building, and
several of the captures now waiting are cheap decisions that would unblock the cleared region.

#### Help, Thanks and Report-a-bug content [help-thanks-report-content]

The words for the three bottom-of-drawer screens that [0022-help-thanks-report-a-bug-content] builds. Help should cover MCP setup, the production custom-instruction text, and the "tasks dated before today" behaviour described without naming the category — the SPEC §Tasks dated before today wording is ready. **This item also drafts the custom-instruction text itself**, folded in on 2026-08-25 from [custom-instruction-production-text]: that text is one of Help's three topics, so the words belong with the rest of Help's words rather than in an item of their own. What stayed behind there is only the live test, which reads the draft this item produces and reports back the wording changes that follow. Two of its inputs aren't ready: the live-tested wording of that custom-instruction text ([custom-instruction-production-text]) and the MCP setup design ([0019-ai-choice-flow-and-mcp-setup], [0020-remote-mcp-server]). Write the content when those have landed.

**Skipped in planning on 2026-08-25**, with the design progress made there recorded here. The item is mixed and mostly not writable yet: Help's MCP-setup instructions would have to be invented before [0019-ai-choice-flow-and-mcp-setup] and [0020-remote-mcp-server] define the path, and the custom-instruction text this item now drafts is in the same position. Thanks is writable today but is a single paragraph [0022-help-thanks-report-a-bug-content] will write when it builds, so splitting it out would produce a fragment rather than a piece of work.

**Where a bug report goes — settled 2026-08-31, the user's call: a dedicated email address.** A web form (buildable later on flintcraft.tech, replaceable without redesign) and a GitHub issue (requires an account most of Taskflow's non-technical users won't have) were the alternatives and lost on audience fit and cost to stand up. The address itself is not yet named; it gets named before [0022-help-thanks-report-a-bug-content] ships the screen, and creating it is a step only the user can do. The Report-a-bug words stay in this item rather than splitting off — they are one sentence from writable and a split would produce a fragment. Skipped again 2026-08-31: the other two inputs ([0019-ai-choice-flow-and-mcp-setup]/[0020-remote-mcp-server] setup design, and the custom-instruction text) still haven't landed.

#### Completed-history sub-system — the left half of the navigation spine [nav-completed-history]
Blocked by: [nav-left-spine-spec-edit]

The pages left of Today on the spine: a **Search / completed-history page** (leftmost), a **Yesterday page**, a **day-detail card layer**, and **share-a-day**. The right half (spine backbone, Projects/Strategy pages, side-menu mirror) was promoted to [nav-spine-spec-edit] during planning on 2026-06-17; this is the remainder. It was [nav-zoom-spine-and-completed-history] before that split, and grew out of [completed-task-post-tray-fate] during the same session (evidence citation, not a dependency).

**Completed-history (the Search page).** The leftmost page lists completed tasks in completion order, most recent at top, with date headers between days. Typing a search narrows what shows below; the relevant date headers still display above each day's results.

**Yesterday page.** A spine page, not a card. Entangled with completed-history: since past-due tasks stay on Today (principle 4), Yesterday's content is essentially what was completed yesterday — so its design belongs with this sub-system, not the schedule screens.

**Day-detail card layer (a separate axis).** Tapping a result, a group, or a date header opens that day on a **card in the foreground** — deliberately a *different* left-right axis from the spine, signalled by the card visual. Swipe right = previous (older) day slides in from the left; swipe left = next (newer) day slides in from the right. Swiping left past the newest card (day-before-yesterday) slides the whole card layer *and* the Search page off in one smooth motion, returning the user to the main spine, landing on **Yesterday**. Only from a day card can the user tap a single task to edit or uncomplete it.

**Share-a-day.** A share button on a day screen shares that single day's completed tasks. Format settled 2026-08-21: **PNG and Markdown, both offered**, per `workshop/resources/research/android-share-format-png-vs-pdf.md` — PNG because it renders inline in a chat thread rather than arriving as an attachment to open, and Markdown carried under the `text/plain` MIME type, since almost no Android app declares `text/markdown` and using it would produce a near-empty share sheet. PDF and plain-text-only were the alternatives and lost on that arrival-behaviour reading. Reconcile with the existing Strategy-doc share button, which uses the Android share sheet.

**SPEC consequences when developed.** Adds new sections for the completed-history page, the Yesterday page, the day-card layer, and share-a-day, and extends the spine described by [nav-spine-spec-edit] leftward (Search · Yesterday, left of Today). The right-half SPEC rewrite is handled by [nav-spine-spec-edit], which also absorbs the held spec-trim findings F3/F4/F18 — they live in the Schedule-view and side-menu sections it rewrites. This left-half item adds only the new completed-history sections; it does not touch those findings.

**Sub-questions, two settled in planning on 2026-08-21.** (1) *Settled — unified.* The leftmost page searches active and completed tasks together, the user's call: someone hunting a task usually doesn't know or care whether they already finished it. Accepted against it: the page does two jobs, and [search-feature]'s read-only rule sits oddly beside a completed list you tap into — the day-card layer resolves that, since editing happens only from a card. [search-feature] is reconciled by this rather than built separately, and on 2026-08-25 its two surviving decisions — read-only results, and scope covering all tasks plus Project names with the Strategy doc excluded — were folded into [nav-left-spine-spec-edit] and that item was deleted, so it is no longer in the queue to look up. (2) *Settled — PNG and Markdown, both offered*, per `workshop/resources/research/android-share-format-png-vs-pdf.md`. (3) *Still open — swipe and card-layer interaction detail*, deliberately left until there is a real screen to finalise it against.

**What this item still needs before it can be kept.** It bundles four sub-features and none of their screens exist, so what changes inside which files can't be stated — the bar for Processed. The SPEC half was split out on 2026-08-21 as [nav-left-spine-spec-edit], which carries the two settled decisions above and must land before any of this is buildable. This item is the build remainder and stays here until that spec edit ships and there are real screens to design the interaction against. Skipped for that reason in planning on 2026-08-25, with nothing else about it left open: once [nav-left-spine-spec-edit] has shipped, SPEC describes all four screens and this splits into buildable pieces, so it becomes keepable then rather than needing further design discussion first.

#### Personal strategy and memory dogfood — an early preview of Taskflow's Strategy-doc experience [personal-strategy-preview]
Not before: 2026-11-25

Alex's idea, raised in planning on 2026-06-24: use this workspace as an early, live preview of Taskflow's personal Strategy-doc experience, before Taskflow has the feature built. She'd keep a real personal Strategy doc and have the strategy conversations here with Claude directly, instead of through Taskflow plus a remote MCP server — neither of which exists yet. In her words: "We're just here, so we don't need the MCP."

Three threads bundled in it: (1) a real personal Strategy doc for Alex, maintained here in conversation with Claude — the experience a paid Taskflow user would eventually get via MCP, doubling as genuine design research for Taskflow's Strategy-doc feature; (2) Alex's real tasks, handled carefully so they don't go missing when Taskflow test builds wipe data — her live task data must not depend on the test app; (3) Claude-memory sync across her Claude surfaces. The insight she reached, and Claude strongly seconded: rather than pushing each strategy update out into many Claude memory stores, point all her Claudes at one canonical strategy doc and have them read from it. Pull-from-one beats push-to-many — one source of truth, nothing to hand-sync.

Why it was shelved, decided 2026-06-24: the method is one-spec-per-project. Two of the three threads (personal strategy, memory sync) aren't Taskflow app features, so they don't fit SPEC.md's contract that every entry describes something existing in the build. Governing three concerns in one workspace would need either multi-spec handling in the method or a deliberate re-framing of what this SPEC is about. Alex chose to wait for multi-spec support rather than bend SPEC now. That's a change to the method itself rather than a queue item here, which is why nothing in this queue holds it.

Privacy note to carry into any revival: if the personal strategy and real tasks get committed into this product repo and it's ever shared or made public, that's the user's private life data exposed. Decide the home with that in mind when this revives — it is the first question when this comes back, not an afterthought.

**Dated in planning on 2026-08-25, with the user's approval.** It waits on multi-spec support in the method, which no item in this queue can deliver and which belongs to the No code method project. It cannot be held below the readiness line either, because held work has to be specific enough to build and this is not. Left as a plain capture it returned to the top every session and was set aside again, which is what had been happening. Three months was chosen as long enough not to re-read it every session and short enough that it comes back while still fresh if multi-spec support lands sooner. It is not offered again before that date.

#### SPEC §Side menu still lists only the spine's right half [side-menu-spine-mismatch]

Found while building [nav-left-spine-spec-edit], which extended §Schedule view's spine sentence leftward to Search · Yesterday · Today · Tomorrow · Soon · Later · Strategy. §Side menu still says the menu "mirrors the spine from top to bottom: Today, Tomorrow, Soon, Later, then a single calm row for the Strategy doc" — which was the whole spine when it was written and is now five of its seven pages. The sentence claims to mirror the spine and no longer does.

Two ways it could go, and the choice is a product one rather than a wording fix: the menu gains Search and Yesterday rows so it really does mirror the spine, or the menu deliberately lists only the committed-execution pages and §Side menu says so instead of claiming to mirror. The second is arguable — a menu row for Search is odd when the page is one swipe away and has its own search field — so this is not a typo to correct.

Not written into SPEC by that build: the item's described work named §Schedule view and the four new sections, and the session that makes a choice is not the session that certifies it as product truth.

#### Taskflow has to stop wiping the device before it can hold real tasks [durable-local-data]

captured by you, 2026-09-01, at the moment an install prompt warned it would delete the app's data.
Your point: the goal is a state where your tasks live in Taskflow stably enough that you can start
actually using it, and that is a requirement rather than a nice-to-have.

Today the app destroys all local data on every schema change. `TaskflowDatabase` is built with
`fallbackToDestructiveMigration(dropAllTables = true)`, and the justification written beside it is
that this is "acceptable while there are no real users". You becoming a real user is exactly the
event that retires that justification, so the decision needs remaking rather than merely honouring.

It is not theoretical and it is not rare. The 2026-08-31 run alone took the schema from v2 to v5 —
recurring tasks, subtasks, a completion timestamp, life areas — and each of those bumps would have
emptied the device. Any run that touches the data model does it again.

What making this safe involves, as far as can be seen from here: pick the version from which real
data must survive, turn on Room's schema export so migrations can be written and tested against a
recorded schema, write real migrations from that version forward, and keep destructive fallback only
for versions below the line. There is also a smaller question underneath it — whether "stably keep my
tasks" means only surviving upgrades, or also surviving a reinstall and a lost phone, which reaches
into Android Auto Backup and the paid tier's cloud sync.

Two things that already help and are worth weighing before designing anything bigger: the JSON export
and import shipped in the 2026-08-31 run, so there is now a manual way to carry data across a wipe;
and Android Auto Backup is on by default per SPEC §JSON export and import.

Related but not the same: [personal-strategy-preview] mentions keeping your real tasks out of the
test app so they cannot be lost to a wipe. That is a workaround for the problem while it stands.
This item is about removing the problem so the workaround is not needed.

#### Dark mode is never followed, and window and content disagree [compose-dark-theme]

Found on 2026-09-02 while driving [verify-blank-new-task-form] on the device: the user reported the
onboarding screens as "dark against dark background". The immediate cause was fixed in that run —
the onboarding screen ran before the Scaffold that gives every other screen its background, so it
painted none and its text fell back to the default near-black. What the fix does not touch is the
condition underneath it.

`MainActivity` calls `MaterialTheme { AppRoot() }` with no colour scheme argument, so Compose always
uses its **light** palette whatever the phone is set to. The Android window theme underneath is
day/night — `res/values/themes.xml` is `Theme.Material.Light.NoActionBar` and `res/values-night/`
overrides it to the dark parent — so on a phone in dark mode the window is dark while everything
Compose paints on top of it is light. Anywhere a Surface paints a background the result is merely
odd; anywhere one does not, text lands on a background of the opposite polarity and becomes
unreadable, which is exactly what happened.

What deciding this involves, which is why it is not a fix to slip in: whether Taskflow has a dark
theme at all is a product question, not a defect. A no-shame, calm-surface app arguably wants one,
and a phone in dark mode at 1 AM is precisely the user this app is designed around (SPEC §Settings →
Day begins at exists for exactly that person). The options are to honour the system setting with a
real dark scheme, to commit to light only and make the window theme stop claiming otherwise by
dropping `values-night`, or to offer it as a setting. Only the second is cheap.

SPEC says nothing about colour, light or dark anywhere, so whichever way this goes it owes SPEC a
sentence.

#### Left-edge swipe has three claimants once the spine grows leftward [left-edge-swipe-collision]

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

#### Side menu's AI row is long enough to bloat the drawer [drawer-ai-row-copy]

captured by you, 2026-09-02, on seeing the drawer on the device: the menu is too fat, and the bottom
row should read something like "Turn on AI" instead.

The row currently reads **"Turn on AI for the full experience"**, which is long enough to wrap or to
force the drawer wider than the rest of its rows need. Every other entry in the menu is one or two
words — Today, Tomorrow, Soon, Later, Strategy, Settings, Help, Thanks, Report a bug — so this one
line sets the drawer's width on its own.

This is a SPEC edit rather than a copy tweak a build can make. SPEC names the string twice, in
§Side menu and in §Tier model — free and paid, both times as *"turn on AI for the full experience"*,
so the wording is currently product truth and changing it in code alone would put the app and SPEC
out of step.

Worth settling at the same time, since it is the same sentence: the phrase is doing two jobs, naming
the row and selling the tier. A short row plus the selling done on the screen it opens may be the
better split, given that screen is the AI choice flow and exists to make that case properly.

#### Edit dialogue shows a Notes field you never asked for, and hides subtasks entirely [notes-versus-subtasks]

captured by you, 2026-09-02, looking at the edit dialogue on the device: you don't understand why
there is a Notes field, which you never asked for, and no subtasks.

Both halves check out, and they are separate problems that happen to sit on the same screen.

**Notes.** It is there, and it is currently product truth: SPEC §Edit a task says the dialogue shows
"its title, notes, Project, and date", and two further SPEC sentences name notes among the things a
Project move must not destroy. Where it came from is thinner than that suggests — the earliest trace
in the record is the 0005 build entry, which lists "title, notes, editable Project incl. unassigned,
read-only date" as what it built, with no decision recorded anywhere about *wanting* notes. So this
looks like a field that arrived as part of a minimum-viable editor and then got written into SPEC as
though it had been chosen. Your not recognising it is evidence, not forgetfulness.

The question for planning is whether Taskflow wants free-text notes on a task at all. Against: this
is an app built to reduce the weight of a task list, and a notes box invites the user to put work
into describing work. For: a task sometimes genuinely carries a detail — an address, a phone number —
and the outliner's subtask lines are not the place for it.

**Subtasks.** They shipped in the 2026-08-31 run and they are, as you say, not visibly there. The
first line of the dialogue is labelled "Task" and pressing Enter at the end of it opens an indented
subtask line beneath — but nothing on screen says so. An earlier draft of that field carried the hint
"Press Enter to add a subtask" and it was dropped when the single text box became a line-per-field
outliner, which is how the only affordance disappeared. So the feature is built and undiscoverable,
which is close to not being built.

These interact, which is why they are one item: if notes goes, the dialogue is the outliner plus
Project, date and repeat, and the subtask lines are unmistakable because nothing else on the screen
looks like them. If notes stays, subtasks need an affordance that distinguishes them from it.

SPEC §Edit a task and §Edit dialogue: outliner-style typing for subtasks both describe this screen,
so whichever way it goes, they are owed the edit.

#### Date strip reads as a wall of numbers, and jumps by the wrong unit [date-strip-legibility]

captured by you, 2026-09-02, from the date strip on the device. Two changes you proposed, plus one
thing the screenshot shows on its own.

**Jump by week, not month.** The strip shows about seven tiles at once, so a jump button that moves
a month skips far past what is on screen and lands the user somewhere they have to re-read. A week
jump moves the strip by exactly what it displays, which makes the button's effect predictable — press
it and the next seven days arrive. Your reasoning, and it is the stronger argument: the control's
unit should match the view's unit. The month jump is not in SPEC; §Date picker — side-scrolling date
strip asks only for "a fast-forward affordance for crossing longer distances quickly", so months came
from the build item rather than from product truth, and changing it costs nothing in SPEC. Worth
weighing at the design pass: crossing to next April by week is many presses, so the answer may be a
week jump plus something coarser held further out, rather than a straight swap.

**Split the number from the month.** Tiles currently read "24/08 25/08 26/08 27/08" in a row, which
runs together into a continuous line of digits with nothing for the eye to catch on. Your proposal:
the date on its own, with the month name beneath it — so 24, then Aug. That gives each tile one large
glanceable number, and it stops the month being repeated seven times in a form that looks like part
of the number.

This one does reach SPEC, and in a way worth deciding deliberately. §Date picker — side-scrolling
date strip says each tile shows its date "in DD/MM format (or MM/DD per the user's setting)", and
§Settings → Date format says that setting applies "everywhere a date is shown". A tile showing 24
above Aug has no day/month order left to obey, so either the setting stops reaching the strip — which
contradicts the word "everywhere" — or the strip keeps a format the user picked and loses the
legibility. Naming which is what this item settles.

**Also seen, not raised: the seventh tile is clipped.** In the screenshot the last column shows "Su"
above "30/" with the rest cut off at the screen edge. The tiles are a fixed width chosen so five to
seven fit a typical phone, and on this device seven do not quite. A tile that renders half a date is
worse than one fewer tile.

#### Give the end-to-end test's notes a file, so the item has an observable [first-test-notes-observable]

captured by you, 2026-09-02, when [first-end-to-end-test] was presented and you reached for a
forward advisory to nag you about it at the next planning session — then said that is the opposite
of what `[user]` items are for and behaviour you worked hard to design out.

You are right on both counts, and the method already refuses the nag. Its `[user]` lifecycle names
exactly three ways an item is known complete — walked to its end in session, the user says they did
it, or the walkthrough named an observable and that observable checks out — and then says of the
remaining gap that it is left open "precisely so nobody later notices the hole and proposes an ask
to fill it".

What pushes toward the nag here is that this item uses none of the three. It cannot be walked (it
needs a day to pass), and its last step is "bring the notes to a planning session and say the test
has been done", which stores both the notes and the fact of completion in your memory alone.

The fix is the third route. Have the walkthrough name where the notes go — a file at a stated path —
and completion becomes checkable by looking, with nothing asked of you and nothing to remember.

It buys two more things beyond removing the nag. The notes survive, rather than depending on
recall days later about an app you were deliberately not concentrating on. And
[post-first-test-polish-review] is held against this item with those notes as its entire input;
today that audit waits on something that has no location, so it could not read them even once you
have written them.

Deciding this is a walkthrough edit on an existing item, which is planning work — hence a capture
rather than a change made while building.

#### [user] Verify the rest of the 2026-08-31 run on a device [verify-run-2026-08-31]

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

Best split into several sittings rather than driven as one walkthrough; whether it becomes several
items is a question for the planning session that processes it.

#### Google Drive is the likely cause of the recurring build lock [project-out-of-drive]

The project lives inside `My Drive`, so Google Drive syncs everything under it — including
`app/build`, which Gradle rewrites constantly and expects to be able to delete. On 2026-09-02 an
Android Studio build failed with "Unable to delete directory ...\app\build\...", reported once per
Gradle task so that a single file lock read on screen as a dozen errors. Deleting `app\build` cleared
it and the next build succeeded.

This is not the first time: CLAUDE.md's Project rules already carry a workaround for the same failure
from 2026-06-16, and TOOLS.md now records this instance. A problem with a standing workaround and a
second recorded occurrence is a condition rather than an incident.

Moving the project outside Drive would very likely end it, and would also stop ~59 MB of regenerated
build output being uploaded repeatedly. What it costs is the backup and the cross-machine access that
having it in Drive currently provides, which is why this is a decision rather than a fix — and there
are middle options worth weighing: excluding `app/build` from Drive's sync if Drive supports that on
this setup, or keeping the code outside Drive and letting git remain the backup, given the repository
is already on GitHub.

Stated as a hypothesis rather than a finding: nobody has moved the project and watched the failure
stop. What is established is that the lock happens, that it hits Android Studio and not only Claude's
shell, and that the folder Drive is syncing is the one Gradle cannot delete.

#### Day-boundary recompute already shipped inside the Settings item [boundary-tick-already-shipped]

[schedule-day-boundary-tick] sits below the cleared-to-run line, held against
[0012-settings-day-begins-at], and reads as outstanding work. It is not: its work shipped inside that
item during the 2026-08-31 run, exactly as that item's build block instructed it to
("Fold in [schedule-day-boundary-tick]: recompute slot placement at that boundary and on lifecycle
resume, so a task moves without waiting for an edit").

What was built: the Settings flow re-emits when the day-begins-at boundary passes, computing the
delay to the next boundary rather than polling, so Tomorrow's tasks become Today's with the app open
and nothing in the database changing. Resume is covered separately and for free, because collection
restarts then and re-reads the clock. The reasoning is in LOG/2026-09-02-0012-settings-day-begins-at.md.

So this is a disposition question rather than work: the item is done and should almost certainly be
deleted, but deleting a queue item is the user's call, and a build cannot make it. It is filed here so
the next planning session meets the fact rather than inferring it from a blocker that has vanished
from the queue — which is what the held item currently looks like from the outside.

Its own remaining acceptance — watching a Tomorrow task move across the boundary on a real device —
is part of [verify-run-2026-08-31].

#### Cleared region holds six items no build can start [cleared-region-unbuildable]

After the 2026-08-31 run, everything left above the cleared-to-run line is work that run examined and
could not begin. A /next run would meet [0017-tier-model-and-subscription-handling] first and stall
there, so the readiness line currently promises buildable work that is not buildable.

Why each stopped, established by reading the items rather than guessing:

- [0018-cloud-sync-paid-tier] — names "the cloud backend", but no backend was ever chosen and no
  server code exists in this repository. Its archived spec still reads "[To be filled in during the
  next planning session.]" where its goal should be.
- [0020-remote-mcp-server] — its authentication design is settled and detailed, but nothing decides
  where the server lives, what it is written in, or where it is hosted.
- [0017-tier-model-and-subscription-handling] — the local half (free disables sync and MCP, paid
  enables them) is buildable; the Google Play subscription wiring needs a Play Console product that
  does not exist, which is the user's to create.
- [0019-ai-choice-flow-and-mcp-setup] — the screens are buildable, but its acceptance is that a
  verification screen reports the connector reachable, and there is no server to reach.
- [0021-strategy-doc-reconciliation-paid-tier] — runs through Claude via the MCP server.
- [0022-help-thanks-report-a-bug-content] — its own text says the words come from
  [help-thanks-report-content], which is still unprocessed.

The shape underneath is one decision, not six: four of them wait on the paid tier having somewhere to
run. Settling where the backend and the MCP server live would release most of this region at once,
and [0017]'s local half could be split out and built without waiting for any of it.

What this item asks for is the disposition — move them below the line against named blockers, split
the buildable halves out, or re-scope them — which is planning work. A build cannot move the
readiness line.

#### Walk-away steps generated in a session are the integration's clearest case [walkaway-work-integration-case]

captured by you, 2026-09-02, on being handed [first-end-to-end-test] — a task to go away, use the app
for a day, and come back with notes.

Your observation: this is exactly the kind of work Taskflow's paid-tier integration exists to carry.
A step that has to happen away from the session, generated inside it, broken down so that stepping
away does not interrupt the flow it came from — sent from the planning side into Taskflow, where it
waits with its subtasks until it is done. The irony being that the item was handed over in a
conversation rather than sent to the app, which is the gap the integration closes.

Why it is worth keeping rather than letting it stay a nice remark. The onboarding video script
written in the same run argues the paid tier's value on page 3 with a generic case: a thing to do
comes up in conversation and Claude puts it in Taskflow. This is a sharper version of the same claim —
the task is not merely mentioned in the conversation, it is *produced by* the work, it has to leave
the session to be done, and the person doing it needs it in the place they will actually look. That is
a stronger argument than the script currently makes, and it came from using the thing.

Where it could land, for the planning session to decide: sharpening page 3 of ONBOARDING-VIDEO-SCRIPT.md
with this case, and a line in SPEC's account of what the integration is for. Neither is written here —
the script is a shipped deliverable and SPEC is product truth, so both are decisions rather than edits.

