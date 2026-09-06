# QUEUE

## Processed

> Vetted work, ready to build — worked top to bottom. Each piece of work is one
> item: a `#### ` heading naming it, a `[slug]` at the end of that heading line, and
> a short rationale beneath. A leading flavor tag names how it runs — none for a
> build (Claude edits files), `[audit]` for a review pass, `[user]` for a step only
> you can do. A security or privacy risk Claude surfaces lives here too, as a work
> item carrying a `Red flag · State: cleared/uncleared` marker. The line below marks
> how far down is cleared to build; anything below it is decided but not ready yet.

#### Free-tier delete a Project on Later [project-delete-later]

**Lifted on 2026-09-05.** The drag gesture it waited on exists and has been driven on a real phone:
[task-reorder-within-list] shipped on 2026-09-02, and the 2026-09-05 device audit dragged a task above
another within Today and saw the order hold across a relaunch (TEST-LOG row 041), and dragged a task to
the target row and back (row 043). The bin target itself was not separately hit — the audit says
steering an adb drag between two adjacent targets is guesswork — so this build adds the Project-card
case to a gesture that is proven, rather than inventing it.

Free tier — long-press a Later Project card and drag it to a delete target in the upper-right, the same gesture used to delete a task. Deleting a Project does not delete its tasks: they reassign to the system Unassigned Project (reassign logic shipped in [unassigned-project-model]). SPEC §Create or delete a Project describes this. Carved from the retired [project-lifecycle-later] during planning on 2026-06-22 — its create half was promoted to [project-create-picker-ui], its three remaining pieces split out by dependency.

What actually holds it is the general drag + bin/delete drag-target gesture (SPEC §Drag-target icons), which doesn't exist anywhere in the app yet (confirmed 2026-06-22). Any of several items could deliver it — [task-reorder-within-list] builds the first drag primitive, and [0008-drag-task-between-schedule-screens], [0010-outliner-typing-drag-target-icons] or [0011-cut-and-paste-os-clipboard] would each bring the bin target. It is held against [task-reorder-within-list] because that is the one sitting nearest the top; if the bin gesture arrives by another route first, this lifts then instead. Build order doesn't matter — only that the bin gesture exists.

#### Free-tier reorder Projects in the Strategy-doc editor [project-reorder-strategy]

**Lifted on 2026-09-05.** The editor it waited on exists and was seen working on a phone:
[0015-strategy-doc-and-life-area-context] shipped on 2026-09-02, and the 2026-09-05 device audit
confirmed the doc renders one heading per Project with Unassigned excluded (TEST-LOG row 047). What that
row did not exercise is edit persistence across a relaunch or the share sheet — neither of which this
item touches, since it adds heading drag rather than paragraph editing.

Free tier — drag Project headings in the Strategy-doc editor to set Project order; the Strategy doc owns Project order app-wide, including the Later card order. SPEC §Strategy doc describes this. Carved from the retired [project-lifecycle-later] during planning on 2026-06-22. Held because the Strategy-doc editor must exist before headings can be dragged in it.

#### [user] Check the bin drag target deletes a task [bin-drag-target-check]

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

#### Subtasks in the edit dialogue have no affordance, so nobody finds them [subtask-affordance-in-edit-dialogue]
Blocked by: [edit-outliner-missing]

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

Bottom-of-drawer screen content including help and custom instructions. Full original spec: `archive/backlog-specs/0022-help-thanks-report-a-bug-content.md`. The words that go in these screens are still being worked out — see [help-thanks-report-content].

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
Blocked by: [edit-outliner-missing]

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

**Where a bug report goes — settled 2026-08-31, the user's call: a dedicated email address.** A web form (buildable later on flintcraft.tech, replaceable without redesign) and a GitHub issue (requires an account most of Taskflow's non-technical users won't have) were the alternatives and lost on audience fit and cost to stand up.

**The address is `bugs@flintcraft.tech`, recorded here on 2026-09-05.** Created and tested on 2026-09-05 during a /next run and working: a message sent from an outside account arrived in the Workspace inbox, labelled External. It is a Google Workspace **alias** on the flintcraft.tech domain rather than a separate mailbox — free, no extra licence, and mail to it lands in the inbox the user already reads. Both choices [bug-report-email-address] left open are therefore settled: the domain, and the alias-not-mailbox shape. One limit to carry: an alias **receives** but does not **send**, so replying to a bug report from that address rather than a personal one would need a send-as configured in Gmail, which nobody has done. Not needed for the screen to print an address, so it is noted rather than filed — if replying-as-bugs is ever wanted, it becomes its own item.

**One half of the 2026-08-31 rejection is now spent, checked on 2026-09-05.** A bug-report form does exist at flintcraft.tech/report, and its project dropdown already lists TaskFlow alongside Throughliner. So "it would cost something to stand up" is no longer true and must not be reached for again. What still decides it is the other half, and the page strengthens it: the form carries a note telling non-automated visitors not to complete it, because it is built for reports Claude has captured and handed over. Taskflow's users are mostly non-technical people with no Claude in the loop, so an in-app Report-a-bug screen pointing at that form would send them to a page that says it is not for them. The email address stands. The Report-a-bug words stay in this item rather than splitting off — they are one sentence from writable and a split would produce a fragment. Skipped again 2026-08-31: the other two inputs ([0019-ai-choice-flow-and-mcp-setup]/[0020-remote-mcp-server] setup design, and the custom-instruction text) still haven't landed.

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

#### Decide whether some runs move into Android Studio's terminal, and which [runs-in-android-studio-decision]
Blocked by: [gradle-from-ide-terminal]

Whether `/next` runs that need a compile should happen in a Claude session started from Android Studio's
integrated terminal instead of the desktop app — and, if so, what rule in CLAUDE.md says which work items
require it.

Raised by Alex on 2026-09-05, during the decision step on [gradle-from-ide-terminal]. She saw what that
item's payoff actually implies before it was written down: compiling mid-run would only be possible in a
session started in that terminal, so realising it means moving whole runs there, and that is a departure from
how she works rather than a free capability.

**The trade, as far as it is understood today.** Against: a terminal session has none of the file viewer and
side panel she reads the work through, and she is a no-code developer for whom that surface is not a
convenience but how the work is legible at all. For: a compile inside the run is what would stop code items
shipping ticked UNCONFIRMED, and would take the `[user]` flavor off the test runs. The middle option is that
only some items — the ones where an unverified build genuinely holds something up — carry the requirement,
which is what a CLAUDE.md rule would have to name.

Held against [gradle-from-ide-terminal] because there is nothing to decide unless `gradlew` actually works in
that terminal. If it fails, this entry is deleted unread.

A second thing to establish before deciding, and it is not established now: whether a Claude session started
in that terminal loads the plugin, the skills and the hooks at all. Same CLI, same configuration, so it is
expected — but nobody has run one, and the whole point of this entry is not to presuppose things nobody has
checked.

Filed 2026-09-05 during planning, at the moment the implication was raised.

#### The Strategy doc's Share button does nothing when the doc is empty [strategy-share-silent-when-empty]

From the [verify-run-2026-08-31-remainder] audit on 2026-09-06, not yet reviewed.

Tapped Share in the Strategy page's trailing slot on a device, twice, with no Projects in the database. No
share sheet opened, the focused window stayed on Taskflow's own MainActivity, and a cleared logcat caught
nothing at all — no chooser intent, no ActivityNotFound, no error. The button is drawn in the normal enabled
style, so from the user's side a visible control simply does not respond.

Why it matters. SPEC §Strategy doc says a share button lets the user share the doc via Android's standard
share sheet, and names no exception for an empty doc. A new user's Strategy doc is empty by definition —
it fills as they make Projects — so the first person to press this button is exactly the person for whom it
does nothing, and they have no way to tell a deliberate no-op from a broken app.

Two dispositions are open and the audit does not choose between them: either the sheet should open with
whatever the doc holds, or the button should render as disabled with the empty state saying why. What is
not open is leaving an enabled-looking button silent.

It also leaves half of the audit's Strategy check unrun: whether the share sheet opens at all is still
unverified, because the only route to it was this button.

Filed 2026-09-06, 11:35, mid-run, read from the device clock.

#### Day begins at offers whole hours only, so the day boundary cannot be set to a half hour [day-begins-at-hour-granularity]

From the [verify-run-2026-08-31-remainder] audit on 2026-09-06, not yet reviewed.

Opening Settings → Day begins at on a device shows a dropdown list of whole hours — 12:00 AM, 1:00 AM,
2:00 AM and so on through the day. There is no minute field and no minute steps. The control is a picker
of twenty-four values rather than a time picker.

Why it matters, in two ways.

For the user: a person whose day genuinely turns at 4:30 AM cannot say so. SPEC §Settings → Day begins at
justifies the setting by the person who stays up past midnight and does not consider the day ended, and
says it lets the user define their own day boundary — a claim hour granularity only partly delivers.
Whether that matters enough to change is a decision, not a defect, which is why this is filed rather than
fixed: hours may well be the right simplicity for a setting most people set once.

For testing: it is what stopped this audit running the day-begins-at rollover check. That check needs the
boundary set a few minutes ahead so the clock crosses it while somebody is watching. With whole hours the
shortest possible wait is up to sixty minutes, on the user's own phone, in the middle of a run — see
[day-begins-at-rollover-still-unrun].

Observed on 2026-09-06 at 11:32 on the phone, on the 2026-09-05 build. The setting was left at its 4:00 AM
default; the dropdown was dismissed without selecting anything.

Filed 2026-09-06, 11:36, mid-run, read from the device clock.

#### [user] Watch a Tomorrow task roll into Today at the day boundary [day-begins-at-rollover-still-unrun]

From the [verify-run-2026-08-31-remainder] audit on 2026-09-06, not yet reviewed.

The one check that audit could not run. SPEC §Schedule view says that at the day-begins-at boundary
Tomorrow's tasks roll into Today with no label, no reordering and no shame, and that Today's uncompleted
tasks stay where the user put them. The picker was confirmed present at its 4:00 AM default on 2026-09-05
and again on 2026-09-06; what nobody has watched is the rollover itself.

Why it did not run this time. The check needs the boundary set a short way ahead so the clock crosses it
while somebody watches. The setting takes whole hours only ([day-begins-at-hour-granularity]), so the
shortest wait available is up to sixty minutes — a wait in the middle of a run, on the user's phone,
holding the only session there is. It is `[user]` work for that reason rather than because Claude cannot
perform the steps: a session can drive every one of them, but not the waiting.

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
8. Set Day begins at back to 4:00 AM. Look for: the row reading 4:00 AM again.
9. Tell a planning session what you saw at step 7, and that "rollover-test" is still on Today needing
   deleting — a session cannot delete it over adb, which is what [bin-drag-target-check] is about.

Observable: "rollover-test" present on Today after the boundary, unlabelled and not reordered. A session
can read the task's presence off the device; the no-label and no-reorder halves are the user's eyes.

Rests on, read 2026-09-06 on the phone: that Day begins at is a whole-hour dropdown defaulting to 4:00 AM,
and that a task added from Tomorrow is dated tomorrow (SPEC §Add a new task).

Filed 2026-09-06, 11:37, mid-run, read from the device clock.

#### Strategy doc edit persistence is unverifiable while no Project exists and none can be deleted [strategy-edit-persistence-blocked]

From the [verify-run-2026-08-31-remainder] audit on 2026-09-06, not yet reviewed.

The audit could not check that a paragraph typed into the Strategy doc survives a relaunch, and the reason
is worth recording because it will recur.

The Strategy doc's structure is mechanically generated — one heading and paragraph per Project, Unassigned
excluded (SPEC §Strategy doc). The database holds no Projects, so the page shows its empty state and there
is no paragraph to type into. Confirmed on the device on 2026-09-06: a single header, the Share action in
the trailing slot, and the empty-state sentence.

Making a Project would create the paragraph — and would leave a Project behind that no session can remove.
Deleting one is a long-press drag onto a target (SPEC §Create or delete a Project), and steering a drag
between adjacent targets over adb has failed three times across two sessions, which is now a recorded
capability limit in TOOLS.md. So the cost of running this check is a permanent Project in the user's real
database, which is a worse outcome than the check is worth.

Two ways out, neither chosen here. Either this becomes a `[user]` item run alongside a Project the user
wants anyway, or it waits until deleting a Project is reachable without the drag. The second is the reason
this is filed rather than tagged now: which it should be depends on work that has not been planned.

The same blockage sits under [verify-far-future-project-card], which needs a Project of the user's own for
the same reason — the audit did confirm the far-future case inside the Unassigned card (a task dated
14/02/2027 showed in Later with its DD/MM label), so what is missing there is specifically the user-Project
half.

Filed 2026-09-06, 11:38, mid-run, read from the device clock.

#### The Yesterday page was seen empty, so what it holds when there is something to hold is untested [yesterday-page-with-content-untested]

From the [verify-run-2026-08-31-remainder] audit on 2026-09-06, not yet reviewed.

The audit reached Yesterday and read it against SPEC §Yesterday page, which says it sits immediately left
of Today on the spine, is a page rather than a card, and holds essentially what the user completed
yesterday. Three of those four hold on the device: it is one swipe left of Today, it is a full page with
its own header, and its empty state reads "Nothing completed yesterday."

The fourth is untested, because nothing had been completed yesterday. A page that correctly renders its
empty state tells you nothing about how it renders a list — and this page's whole content is that list.

Getting a completion dated yesterday is not something a session can arrange: the completion timestamp
comes from the clock, so it can only be made by completing something yesterday, or by an import carrying
a completed-date. The JSON export format does carry completion state and the date it was completed
(SPEC §JSON export and import), so an import is the one route that does not require waiting a day, and
"Add tasks from a file" exists in Settings alongside the replacing import.

Worth deciding at planning which is wanted: fold this into the next device pass that happens to run the
day after a completion, or build it as a check driven from a crafted import file. The second is more work
and repeatable; the first is free and depends on luck.

Filed 2026-09-06, 11:39, mid-run, read from the device clock.

#### Fix the RLS test steps in [supabase-apply-cloud-migrations], which as written cannot detect a broken policy [rls-test-steps-bypass-rls]

Found on 2026-09-06 while driving [supabase-apply-cloud-migrations] in a /next run, not yet reviewed.

The item's steps 6 and 7 are the whole point of it — signed in as one test user, query `tasks` and see only
that user's row; query with no session and see nothing. Both are written as queries run in the Supabase SQL
editor. **Run there, both return every row whatever the policies say**, because the editor connects as the
`postgres` role, which carries the `bypassrls` attribute. Confirmed against Supabase's own Row Level
Security documentation on 2026-09-06.

Why this matters more than a wording slip. The check would not fail loudly — it would return both rows,
which reads exactly like a policy that is not working. So the likely outcome is someone concluding the RLS
work is broken when it is fine, or, in the other direction, a later reader taking "we ran the test" as
evidence the policies hold when nothing was tested at all. A verification step that cannot distinguish a
pass from a fail is worse than no step, because it produces a record.

The documented fix is to impersonate the role inside the transaction before selecting:

```
set local role authenticated;
set local request.jwt.claim.sub = '<the test user uuid>';
select * from public.tasks;
```

and for the null-identity case, `set local role anon;` with no claim set. `auth.uid()` reads the claim, so
the policies then evaluate as they would for a real request.

Two further corrections the same steps need, both hit while driving them:

- **Step 5 cannot insert a task row on its own.** `tasks.project_id` is `not null` and references
  `public.projects`, so a project row has to exist for each test user before any task row can. The step as
  written says only to insert one row into `tasks` under each user.
- **The grant is a separate failure from the policy.** Supabase's documentation notes a missing grant
  raises error `42501` before any policy is evaluated, so a `42501` during this test means something other
  than what the item is checking, and the step should say so rather than leaving it to be misread as a
  policy denial.

The drive on 2026-09-06 applied all of this live rather than following the item, and its record carries what
actually ran. This item exists so the queue entry itself stops carrying steps that cannot work — the next
person to run it should not have to rediscover this.

Filed 2026-09-06, 12:04, mid-run, read from the device clock.

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
will actually use until this drive — which is the argument for the impersonation steps in
[rls-test-steps-bypass-rls] being fixed rather than dropped.

What the fix has to decide, and why this is a queue item rather than a line typed in during the drive:
whether the grants live in `0001` beside the table definitions, in `0002` beside the policies, or in a new
`0003`; and whether to grant per-table or to set default privileges on the schema. A project already
holding a partly-applied schema also needs the answer to "what does a re-run do" — all three files are
currently write-once, with no `if not exists` guards, so re-running any of them errors.

Filed 2026-09-06, 12:22, mid-run, read from the device clock.

#### Rotating-roster repeats — a recurring task that cycles through a list rather than repeating identically [rotating-roster-recurrence]

Raised by you on 2026-09-06, describing how you actually want to use a Project for keeping up with your
immediate family.

The want, in the abstract: a single recurring task whose *subject* advances through an ordered roster each
time it comes round, rather than a task that repeats unchanged. The rotation you described has six
positions covering four people, where one position recurs and one is a free choice between two of them at
the moment it lands, and then the whole cycle restarts.

Taskflow cannot express this. `Recurrence` holds a fixed interval, and every generated instance is
identical to the last — SPEC §Recurring tasks describes instances of one task appearing on the slots their
dates fall into, with nothing that varies between them. There is no per-instance content, no ordered list,
and no notion of an instance the user resolves a choice for.

Why it is worth having rather than a curiosity. The failure it fixes is the one this app exists for: a
person who wants to stay in touch with four people evenly has to hold the rotation in their head, and
holding a rotation in your head is exactly the executive-function load Taskflow is meant to absorb. The
workaround available today is four separate recurring tasks at staggered offsets, which drifts as soon as
one is completed late and cannot express "either of these two, my choice".

Three things a design would have to settle, none decided here:

- **Where the roster lives.** A list on the task, or something the Project holds? The second is a bigger
  idea — it starts to make a Project a thing with members — and may be out of scope for what is a task
  feature.
- **What a "choose one" position means.** It is not a rotation step; it is a prompt. That may be a
  different feature wearing the same coat, and worth separating before either is built.
- **Whether the free tier gets it.** A rotation is exactly the sort of thing the paid tier's Claude
  integration could arrange conversationally, so this may be a case where the two tiers want different
  answers rather than the same one.

Names are deliberately not recorded. The people in the roster are third parties who have published
nothing, so they are described by relationship rather than named, per this project's scrub checklist. The
concrete roster is Alex's to hold; what the queue needs is the shape.

Filed 2026-09-06, 12:40, mid-run, read from the device clock.

#### Dragging a lifted task navigates to the next page instead of dragging, so no drag target can be reached [drag-eaten-by-page-swipe]

Found on 2026-09-06 by Alex, on a real phone with her own thumb, while being walked through
[bin-drag-target-check].

Long-pressing a task on Today lifts it and raises the row of drag targets. Moving the finger from there
does not drag the task: the page navigates to Tomorrow. The task is never delivered to any target, so the
bin, the cut and the promote targets are all unreachable by the gesture SPEC gives for reaching them
(SPEC §Drag-target icons).

**This overturns what the record believed until today, and the correction matters more than the defect.**
Three attempts to drive this over adb — one on 2026-09-05, two on 2026-09-06 — each registered as a
horizontal page swipe rather than a drag. All three were read as a limit of driving a device over adb, and
that reading was written into TOOLS.md earlier in this same session and used to justify tagging this check
as work only a person could do. Alex's thumb produced the identical failure. So the adb attempts were not
failing to reproduce a working gesture; they were reproducing a broken one, faithfully, three times. The
TOOLS.md line has been corrected.

The likely mechanism, stated as the obvious suspect rather than as established: the Schedule pages are a
horizontal pager, and a horizontal drag inside the list area is being consumed by the pager before the
lifted task's own drag handler sees it. SPEC §Side menu records that swipe-to-open was deliberately
disabled on the drawer for exactly this reason — "so the gesture does not collide with the spine's
horizontal-swipe navigation" — so the collision was anticipated in one place and appears not to have been
handled in this one. Whoever builds this should confirm that before designing around it.

**A second symptom, possibly the same defect and possibly not.** The target row does not render where SPEC
puts it. SPEC §Create or delete a Project describes dragging to "the delete target in the upper-right
corner", and this item's own walkthrough says to look for the targets "at the top of the screen". Alex
reports the icons appearing in the task area instead, with the bin on the left. If the targets are drawn
inside the scrolling list rather than pinned to the top bar, then the drag that must reach them is a drag
within the pager's own territory, which would explain the first symptom — but they may equally be two
faults. Worth checking together.

Consequence for the queue: [bin-drag-target-check] cannot pass while this stands, and neither can the
promote target, the cut target from a Schedule screen, or free-tier Project deletion
([project-delete-later]), all of which are reached by this one gesture. That last one was cleared to run
on the strength of the drag primitive being proven — it was proven for reordering within a list, which is
a vertical drag, and not for delivery onto a target.

Filed 2026-09-06, 12:58, mid-run, read from the device clock.

#### Give [project-delete-later] and [project-reorder-strategy] file lists, or they block every future run [two-cleared-items-underspecified]

Both sit at the top of the cleared region and neither can be built. A /next run on 2026-09-06 dropped them
at its self-scoping step: neither carries a `Files:` line, and neither says what changes inside any file.
They describe the behaviour wanted — drag a Project card onto a delete target, drag Project headings to
set order — which is a design, not an instruction a build can follow.

Alex agreed to drop them from that run and the queue was left untouched, which is the point of this
capture: nothing in the queue records why they were skipped, so the next run reaches them, performs the
same check, and stops in the same place. The cost is not the stop — the stop is correct — it is that the
stop happens after a user has been asked to approve a run containing them.

What each needs before it is buildable is the ordinary decision-step work: which files change, and what
changes inside them. For the delete item that means deciding where the drag-target row is built and what
the reassign-to-Unassigned path touches; for the reorder item, which file owns Project order and how a
heading drag writes it.

**Read [project-delete-later-premise-may-be-gone] before designing the first of them.** Its gesture may not
work at all, which would make designing its file list premature.

Filed 2026-09-06, 21:55, by the look-back over the /next run's own conversation.
Filed 2026-09-06 21:53, stamped by the queue tool.

#### Re-examine [project-delete-later]: the gesture it is built on may not work at all [project-delete-later-premise-may-be-gone]

[project-delete-later] was lifted into the cleared region on 2026-09-05 on an explicit argument, written
into the item: that the drag gesture it needs "exists and has been driven on a real phone", citing
[task-reorder-within-list] shipping and two TEST-LOG rows from the 2026-09-05 device audit.

On 2026-09-06 Alex long-pressed a task on her own phone and tried to drag it. The page navigated to
Tomorrow; the task never moved. Filed as [drag-eaten-by-page-swipe].

**The lifting argument does not survive that, and the reason is a distinction nobody had drawn.** What
[task-reorder-within-list] proved is a *vertical* drag that reorders within a list. What this item needs is
delivery of a dragged task *onto a target* — a different gesture, in the pager's own horizontal territory,
and the one that fails. The two were treated as one primitive, and the row cited as evidence (dragging a
task to a target row and back) is now in doubt for the same reason.

So this item is not merely blocked; the sentence that cleared it was wrong. That is why this is filed as a
re-examination rather than as a `Blocked by:` line: adding a blocker would leave the item's own prose still
asserting the gesture is proven, and a later reader would take that at face value.

Deciding what happens to it belongs to a planning session. The options visible from here: hold it against
[drag-eaten-by-page-swipe] and correct its prose, or return it to Unprocessed until the gesture question
settles. Both need the sentence about the drag being proven rewritten either way.

The same doubt reaches anything else reached by that gesture — the promote target, the cut target from a
Schedule screen, and [bin-drag-target-check], which halted mid-drive on 2026-09-06 for exactly this.

Filed 2026-09-06, 21:56, by the look-back over the /next run's own conversation.
Filed 2026-09-06 21:53, stamped by the queue tool.

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

**Why this was missed, which is worth more than the rows.** The rule says a build "runs tests"; this run's
testing happened inside an `[audit]` item and inside `[user]` walk-throughs, neither of which reads as a
build running a test suite. The rule's trigger and the run's shape did not meet. Whoever picks this up
should decide whether TEST-LOG.md is meant to cover audit and walk-through outcomes at all, or only tests a
build runs — because writing these rows without settling that just moves the ambiguity into the file.

Everything needed is already recorded in the session's LOG entries, so nothing has to be re-run.

Filed 2026-09-06, 21:57, by the look-back over the /next run's own conversation.
Filed 2026-09-06 21:53, stamped by the queue tool.

#### Repair the unfillable commit-hash placeholder in LOG/2026-08-25-setup.md [setup-log-entry-malformed-placeholder]

Every session opening reports it: that entry carries a commit-hash placeholder somewhere other than hash
position — not at the start of a heading, and not on an index line — so the automatic backfill that fills
placeholders at every session start can never reach it. The backfill is working correctly; the entry is
malformed.

It has been reported at the opening of session after session and never fixed, which is the actual cost. A
warning that appears every time and is never actioned trains everyone to read past the whole class of
opening warnings, including ones that matter. That is the argument for spending five minutes on a cosmetic
defect in a year-old record.

The fix is to move the placeholder into the entry's heading, per the entry template, so the next session
start fills it with the real hash. The entry's prose is not otherwise touched — this is a record of what
happened on 2026-08-25 and its content is not in question.

Filed 2026-09-06, 21:58, by the look-back over the /next run's own conversation.
Filed 2026-09-06 21:53, stamped by the queue tool.

#### Session-start clock line read 00:37 while the real clock read 11:31 — a method defect to report [session-start-clock-eleven-hours-out]

On 2026-09-06 the session opening stated "Date at session start: 2026-09-06 00:37 — read from the system
clock." Minutes later, in the same session, `date` on this machine returned 11:31 and the connected phone
returned 11:31 in the same turn. Two independent clocks agreed with each other and disagreed with the
opening line by about eleven hours.

**Why it matters more than a cosmetic slip.** That line is presented to every session as the anchor for
dates and times, and the method's own rules direct sessions to prefer a computed field like it over their
own assumption — precisely so that recorded times are trustworthy. A line that is confidently wrong is
worse than an absent one, because it is designed to be trusted. Every capture and record filed against it
would carry a wrong hour, and nothing downstream could detect it: a wrong timestamp reads exactly like a
right one.

In this session nothing was written from it. The mismatch was noticed when the phone's clock was read for
an unrelated reason, and after that every time written into a capture or record came from a live `date`
reading in the turn that wrote it, which is what the current rules ask for anyway. So this project's
records are sound; the defect is in the tool, for everyone using it.

Eleven hours is close to a timezone offset, and this machine is on AEST — which is UTC+10, and eleven with
daylight saving. That is a suggestion of where to look, not a diagnosis, and it should be checked rather
than assumed.

**Route: this is the method misbehaving rather than this project.** It goes to the Throughliner project as
mail, not into this queue as work — this capture exists so the observation is not lost while it waits for
someone to send it, and it should be deleted once the report has gone. Nothing has been sent: outbound mail
needs Alex to see the exact text and say yes.

Filed 2026-09-06, 21:59, by the look-back over the /next run's own conversation.
Filed 2026-09-06 21:53, stamped by the queue tool.

#### Last session advises processing [project-delete-later-premise-may-be-gone] next [forward-advisory]

Replaces the previous advisory, which pointed at [edit-outliner-missing]; that item shipped on 2026-09-06
and its note was spent.

**Open on [project-delete-later-premise-may-be-gone], then [two-cleared-items-underspecified].** Together
they cover the two items now sitting at the top of the cleared region, and both need answering before any
build run can move.

The condition, stated as a condition: a /next run started as the queue now stands reaches
[project-delete-later] first, finds it names no files and does not say what changes inside any, and halts
on it — building nothing past it. That happened on 2026-09-06 and the user dropped both items from the run
to let the rest proceed. Nothing in the queue records that, so it happens again.

**The overlap scan found a real overlap, and it is the sharper of the two problems.** Unprocessed now holds
a finding that invalidates the sentence which cleared the top processed item. [project-delete-later] was
lifted on the argument that its drag gesture "exists and has been driven on a real phone". On 2026-09-06
Alex tried that gesture with her own thumb and the page navigated instead of the task dragging
([drag-eaten-by-page-swipe]). What had been proven was vertical reordering within a list; what the item
needs is delivery of a task onto a target, which is a different gesture and the one that fails. So the item
is not merely underspecified — the reasoning that put it where it is has been overturned, and designing its
file list before settling that would be designing for a feature that may not be reachable.

Two other captures are worth knowing about while planning, though neither blocks the top of the queue.
[cloud-schema-missing-grants] records that the cloud schema grants the signed-in role nothing, so the app's
first real query would be refused; the grants were applied by hand to the live database during the session
and exist in no file, which is the one gap between the repository and the working Supabase project.
[runs-in-android-studio-decision] has had its blocker resolved — Gradle does run from Android Studio's
terminal — and is now a live question about where sessions happen.
Filed 2026-09-06 22:06, stamped by the queue tool.

