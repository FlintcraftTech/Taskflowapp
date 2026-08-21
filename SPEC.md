# SPEC.md — Taskflow User Experience

This document describes what each feature is, how the user experiences it, and why the user needs it — the product's truth. It is not a catalogue of every UI element: exhaustive UI and implementation mechanics are build decisions, recorded in the session's LOG entry, not here. Every entry describes decided product truth rather than speculation. An entry may be written ahead of the build that implements it — a queue item whose whole job is a SPEC edit does exactly that, so the build has a settled description to work against — but only once the design is agreed. What does not belong here is anything still undecided, or a feature nobody has committed to: that is a plan, and it belongs in `QUEUE.md`.

`SPEC.md` only describes what has been decided. Open questions and undecided details do NOT live here as placeholders, and do NOT live here as sentences that gesture at the doc's own undecidedness (e.g. "currently undecided", "pending decision"). Open questions live in `QUEUE.md` as captures.

## Project context

Taskflow is a native Android task manager (Kotlin / Jetpack Compose, MVVM with Room) designed around executive dysfunction. It separates two thinking modes most task apps conflate: a Schedule view for committed, time-bounded execution, and Projects for divergent, no-time-pressure planning. A Strategy doc sits above Projects to organise the medium- and long-term picture. Taskflow's data lives locally on the device by default; cloud sync is the bundle that unlocks Claude integration on the paid tier, where the user talks to Claude in their normal Claude client (web, desktop, or mobile) and Claude reaches Taskflow through a remote MCP server. Taskflow does not import from, sync to, or export to any external task app.

## UX principles for Taskflow

These inform every design decision. Entries below should serve one or more of these principles. If a proposed change conflicts with a principle, flag the conflict before building.

1. **Two thinking modes, with a bright line between them.** The Schedule view (Today / Tomorrow / Soon / Later) is convergent — what's committed, what's next. Projects are divergent — what's possible, no time pressure. The transition between them is a deliberate per-task act in both directions; the app does not silently move a task from one to the other. Most task apps live entirely in convergent mode and quietly punish users who try to think divergently about future possibility, which is the failure mode Projects exist to prevent.

2. **Local-first source of truth.** Taskflow's own Room database is the source of truth for everything: tasks, Projects, the Strategy doc, ordering. No syncing to or importing from external task apps. Android Auto Backup (the OS-level feature) and an explicit JSON export/import screen handle portability for free-tier users. Cloud sync exists only as the paid tier and is the precondition for Claude integration.

3. **Time horizon, not list, structures execution.** The four Schedule slots — Today, Tomorrow, Soon, Later — map onto how attention naturally scales with time, and they are the left segment of a single left-to-right navigation spine that extends rightward into Strategy. Splitting the slots into discrete bounded pages (rather than one infinite scroll) makes each horizon a glanceable thing. The spine reflects zoom levels of time: a day is the finest grain, weeks are coarser, and past about a week time stops being the useful frame — so navigation glides from arranging time into arranging areas of life and Strategy rather than forcing everything onto a clock. Projects answer different questions ("what's possible?", "where does this belong?"); they are grouped on the **Later** slot — reached by swiping the spine to Later — rather than living on a page of their own. Projects are not slices of the day.

4. **No-shame zone.** Taskflow does not name, count, or visually pressure tasks that have slipped past their date. They simply remain on Today, in the order the user placed them, until the user does them or moves them. The app has no "overdue" label or category — the help docs describe the behaviour without naming it as a category at all.

5. **Capture inherits context.** Adding a task from a Schedule screen places it on that screen, in the system Unassigned Project until the user files it elsewhere. The default capture path never asks the user to set a date on Soon/Later capture or to pick a Project — those follow from where the user was when they tapped Add (and whatever defaults flow from that). Choosing or changing a task's Project, and assigning a date to an undated task, is a separate, deliberate act done in the edit modal.

6. **Spatial action equals temporal change.** Dragging a task between Schedule screens changes when it's committed for. Reordering within a screen changes its sequence. The user reschedules and reprioritises by moving things, not by opening editors and adjusting fields. The screen a task is on IS its time horizon, expressed visually.

7. **Taskflow does dates, not times-of-day.** Taskflow has no time-of-day picker anywhere except for a single setting that controls when "today" rolls over for the user (see Settings → Day begins at). Clock-time scheduling — "this meeting at 3pm" — is a different problem from time-horizon scheduling — "this task sometime soon." Taskflow's distinctive contribution is the four-screen time-horizon view above the level of clock time, where Soon and Later are real categories rather than empty space between dated events. Folding clock-time into Taskflow would dilute the thing that makes it itself. If the user wants clock-time scheduling, that's a calendar app's job.

## Functionalities

### Data model

A task has the following user-visible properties:

- A **Project** — every task belongs to one. There is no "unassigned / no Project" state: a task the user hasn't filed into one of their own Projects belongs to a system **"Unassigned"** Project instead (see *Schedule view* and *Strategy doc*). A task's Project is never empty.
- An optional **date**.
- An optional **Schedule slot** (Today / Tomorrow / Soon / Later), used only for undated tasks placed on Soon or Later. Dated tasks derive their slot from their date.

Where a task appears on the user's screens follows from these properties:

- **Dated tasks** appear in Schedule on the slot derived from their date (today → Today, tomorrow → Tomorrow, 2–7 days out → Soon, 8 or more days out → Later, before today → still Today). On Later, a far-future dated task appears inside its Project's card (see *Schedule view*).
- **Undated tasks parked on Soon** appear on Soon, which stays a single date-ordered list of tasks.
- **Undated tasks that aren't parked on Soon** — whether parked on Later or never given any slot — appear inside their Project's card on Later. This is where a task lives when it belongs to an area of life but isn't committed for any specific time.

Today and Tomorrow tasks always have a date — capturing on those screens always sets the date, and there is no path to land an undated task there. Undated parking is a Soon/Later-only behaviour.

The user needs this because Projects answer "where does this belong?" while Schedule answers "when am I doing it?" — two questions that should not collapse into one. Every task always belongs to a Project — its own area of life, or the system Unassigned Project if the user hasn't filed it — so every task has a home on Later and nothing falls through the cracks. Allowing tasks to live on Soon or Later without a date preserves the "I don't know exactly when, but soon" feeling that the user genuinely has, without forcing a date pick they can't yet make.

### Schedule view

The four Schedule slots — **Today**, **Tomorrow**, **Soon**, **Later** — are the four leftmost pages on a single horizontal navigation spine that continues rightward into the **Strategy** page (see *Side menu*). The user swipes between adjacent pages; Today is the default open page. Each page shows the tasks placed on that slot. On Today, Tomorrow, and Soon a task's Project is not shown — those pages are flat task lists, and during execution there the only place a task's Project surfaces is inside its edit dialogue. **Later is the exception: it is grouped by Project** (described below) — at the far horizon the area a task belongs to matters more than its exact date.

Each task on **Soon and Later** shows its date (when the task has one) in **DD/MM** format (or **MM/DD** if the user has selected that in Settings — see *Settings → Date format*). **Tomorrow does not show a date label** — a Tomorrow task always carries tomorrow's date, so the page name is the day signal and a label would only repeat it. Today's tasks do not show a date label except when the date is in the past, where the date communicates how stale the task is.

**Later — grouped by Project.** Unlike the other slots, Later does not list tasks by date. It is a vertical list of **expand/collapse cards, one per Project** — every Project the user has appears, even one with nothing in it (as an empty card). The cards are ordered to match the **Strategy doc** (see *Strategy doc*), with the system **Unassigned** Project pinned to the bottom. Each card opens showing its **first ~3 tasks**; the expand/collapse control reveals the rest and hides them again. The small peek keeps Later a calm overview of the user's areas of life rather than a wall of tasks.

Each card holds that Project's **far-future (8-or-more-days-out) dated tasks** — still showing their DD/MM date — together with the Project's **undated tasks**, the ones that belong to this area of life but aren't committed for any specific time. (This undated list is the old Project "dreaming surface," now folded into the card.) Tasks inside a card are **reorderable by drag**, and that order persists; it is the card's own ordering. Tasks can be completed from a card.

**Project-level actions** — reordering the Projects and deleting one — are not done by freely rearranging the cards. Reordering is owned by the Strategy doc (see *Strategy doc*); deleting is done by long-pressing a card and dragging it to a delete target (see *Create or delete a Project*). On the **paid tier**, long-pressing a card instead shows a toast — *"Discuss high-level strategy with Claude"* — and both go through Claude.

**At the day-begins-at boundary** (see *Settings → Day begins at*), Tomorrow's tasks roll into Today with no label, no reordering, and no shame. Today's tasks that did not get completed simply remain on Today in the order the user placed them — they do not move to the top, they do not gain a label, they do not change appearance. They are just still there.

The user needs this because execution lives in a single time continuum. While executing, the user wants to see what they have committed for now, next, this week, and beyond — sliced by attention horizon, not by category. The "still there from before" behaviour is deliberate (UX principle 4): a task that slips past its date should not become a daily reminder of failure.

### Recurring tasks

Recurring tasks created in Taskflow appear on whichever Schedule slot each instance's date falls into. Unlike one-off tasks, **every instance** in the visible window is shown — so a weekly Monday task appears as four separate rows in Later for the next month.

Recurring tasks are capped at **30 days into the future**: instances dated more than a month from today are not shown until the world catches up to within a month of them. A yearly birthday reminder, for example, is invisible until you're within a month of the next instance. Manually-dated one-off tasks are not capped — a task you've manually dated for next year appears in Later normally.

The user needs this because recurring tasks generate an indefinite tail of future instances. Showing all of them would flood Later; showing only the next would lose the rhythm the user actually wants to see (every week's "water plants," for example, sitting there as a checklist of the coming month). The 30-day cap is the compromise.

### Tasks dated before today

A task whose date is in the past stays on Today, in the position the user placed it, until the user completes it or moves it. The app does not call attention to it — no overdue label, no red colour, no visual pressure, no auto-reordering to the top of the list. The DD/MM date inline on the right is the only signal that the date has slipped, and only because that information is useful to the user when they choose to read it. Help docs describe the behaviour without naming the category — they say something like "Tasks dated before today stay on Today, in the order you placed them, until you do them or move them."

The user needs this because a no-shame zone (UX principle 4) means refusing to dramatize the past. Most "overdue" UI exists to nag the user; nagging is exactly the failure mode this app is built to avoid.

### Create or delete a Project

**Creating a Project.** A new Project is created from the **Project picker in a task's edit dialogue** (see *Edit a task*). The picker — used to file or refile a task into a Project — includes a **"New Project"** entry; choosing it opens a small foregrounded card where the user types the new Project's name. A name is all creation asks for — a Project's description is its Strategy-doc paragraph, written later (UX principle 5, lightest possible capture). On entry the Project is **appended to the end of the order**: it appears as a new card at the bottom of Later (above the pinned Unassigned card) and as a new heading-and-paragraph at the end of the Strategy doc, and the task being edited is filed into it.

**Deleting a Project.** A Project is deleted from **Later** by the same gesture used for tasks: **long-press its card and drag it to the delete target** in the upper-right corner. Deleting a Project does **not** delete its tasks — they are reassigned to the system **Unassigned** Project, so nothing is lost. On the **paid tier**, a Project is not deleted by direct drag: long-pressing the card shows the toast — *"Discuss high-level strategy with Claude"* — and deletion, like reordering, goes through discussion with Claude, because removing an area of life is a high-level act that earns a check-in.

The user needs this because creating and removing Projects are decisions about the shape of the user's life, not quick list edits. Folding creation into the one place a task's Project is already chosen keeps a single Project-assignment surface; reassigning a deleted Project's tasks to Unassigned means nothing is ever lost to a deletion; and gating deletion behind a deliberate gesture — and, on the paid tier, a conversation with Claude — protects the user from casually dropping a whole area of life.

### Move between Schedule and Project

A task moves between a near-term Schedule slot and its Project's place on Later as a side-effect of editing two of its properties:

- **Adding or changing a date** moves the task onto the Schedule slot its date falls in — Today, Tomorrow, Soon, or, for a date 8 or more days out, into its Project's card on Later.
- **Removing a date** (in the editor) drops the task out of the day and Soon lists and into its Project's card on Later, where it sits among that Project's undated tasks.
- **Changing the Project**, via the editor's **Project picker**, refiles the task under the new Project — it appears under that Project on Later, and if it is dated it continues on its Schedule slot too. This is the supported way to move a task between Projects, and it works identically on the free and paid tiers: it preserves the task's subtasks, notes, and recurrence, which deleting and re-creating the task would destroy.

Drag-to-reschedule between Schedule screens (covered separately) changes the date directly via gesture.

The user needs this because the bright line between Schedule and Project (UX principle 1) is preserved by making transitions explicit. Adjusting a task's date is the only way to push it into the near-term Schedule slots or pull it back to Later; the app never silently changes which side of the line a task lives on. Keeping the Project move on a property edit — rather than a destroy-and-rewrite — is deliberate: free-tier users have no Claude to help them refile, so the manual path must never cost a task its subtasks, notes, or recurrence.

### Drag a task between Schedule screens to reschedule

Tasks on Schedule screens can be picked up and dragged onto another Schedule slot. Dropping a task on a different slot reschedules it: the task's date is updated to fall in that slot. A task that was undated stays undated and parks on the new slot (only possible on Soon or Later).

When a parent task is dragged, its subtasks travel with it as a single unit — the parent's date (or parking) is rewritten and the children continue under it on the new screen.

The user needs this because rescheduling is the single most common edit a person makes to a task list, and spatial drag-to-reschedule is faster and more intuitive than opening an editor and tapping a date field (UX principle 6).

### Reorder within a Schedule slot

Within a single Schedule screen, the user reorders tasks by drag, without leaving the slot. The new order persists in the local database per-slot. On **Later**, where tasks are grouped into Project cards, reordering happens **within a card** — each Project card holds its own task order (see *Schedule view*). Reordering the Projects (the cards themselves) is a separate, higher-level act owned by the Strategy doc (see *Strategy doc*).

The user needs this because tasks in the same slot still have a sequence — what to do first, what to do last — and that sequence is the user's own ranking, not derivable from anything else.

### Add a new task

The user adds a task via a floating action button or equivalent affordance on a Schedule slot — **Today, Tomorrow, Soon, or Later**. These four slots are the only task-add surfaces; there is no separate Project surface to add from, and a task joins a Project only through the editor's Project picker (see *Edit a task*). Where the new task goes follows capture-inherits-context (UX principle 5):

- **From Today or Tomorrow** — date is auto-set to today or tomorrow; the task lands on that screen. Project defaults to the system Unassigned Project.
- **From Soon** — the task lands on Soon. The user is not forced to pick a date; if they leave it empty, the task stays an undated parked task on Soon. Project defaults to Unassigned.
- **From Later** — the task is created undated and appears under its Project on Later; with no Project picked it defaults to Unassigned and shows in the Unassigned card. The user is forced to pick neither a date nor a Project.

The user can change the Project, the date, or both inside the edit dialogue afterwards.

The user needs this because the most natural starting point for a new task is the place the user is currently looking at, with the lightest possible required input. Forcing a date pick on Soon/Later capture, or a Project pick when none was implied, would slow capture down — and slow capture is exactly the friction Taskflow is trying to remove.

### Edit a task

Tapping a task opens an edit dialogue showing its title, notes, Project, and date. The **Project field** is a picker, editable so the user can refile the task into another Project; the picker also offers a **"New Project"** entry that creates one on the spot (see *Create or delete a Project*). The date field is the side-scrolling date strip (see *Date picker — side-scrolling date strip*) — users can set a date, change a date, or clear a date. Setting or clearing the date moves the task into or out of the near-term Schedule slots accordingly (see *Move between Schedule and Project*). There is no time-of-day picker anywhere in this dialogue (UX principle 7).

The user needs this because they need a way to change the two things drag-to-reschedule cannot change in a single gesture: which Project the task lives in, and whether it's dated at all.

### Date picker — side-scrolling date strip

The date field inside the edit dialogue is a horizontal, side-scrollable strip of date tiles, embedded directly in the dialogue (not a popup). Each tile shows a single date in DD/MM format (or MM/DD per the user's setting). The user scrolls left and right to find a date and taps a tile to select it; the selected tile is highlighted. Today is the visual anchor, and tiles fade with distance from today to give the user a sense of how far they have scrolled. When the dialogue opens, the strip is centred on the task's own date if it has one, or on today if the task is undated, and it covers both past and future dates with a fast-forward affordance for crossing longer distances quickly.

A **"no date" tile** at the left edge of the strip lets the user clear the date. It is visually distinct from the date tiles, so it reads as a deliberate choice rather than just another faded tile.

The user needs this because the standard Android date picker (a calendar grid in a popup) is heavy for this app's purpose. Most date assignments in Taskflow are within a week or two of today, and a horizontal strip lets the user scrub through nearby dates as fast as their thumb. Embedding the picker in the dialogue keeps the editing flow continuous.

### Subtasks live under their parent

A subtask appears nested under its parent on whichever Schedule screen or in whichever Project the parent lives. Subtasks do not have their own date, do not have their own Project (they inherit the parent's), and do not have their own Schedule placement. When a parent is moved between screens or refiled to a different Project, its children move with it as a single unit.

The user needs this because subtasks are sub-units of a parent task, not independent items. Treating them as part of their parent — moving when the parent moves, listed under the parent on the parent's surface, sharing one date — keeps each surface focused on the unit of work the user actually thinks about (the parent task).

### Parent tasks expand/collapse instead of having a checkbox

A parent task (one with subtasks) has an expand/collapse control where its checkmark would otherwise be, rather than a checkmark of its own. Expanding it shows the parent's subtasks indented beneath it on the current screen. A parent's completion rolls up from its children: it is complete only when all its subtasks are complete, so completing the last subtask completes the parent and sends it to the Completed tray (see below), and un-completing any subtask brings the parent back out of the tray.

The user needs this because manually checking a parent while subtasks remain open creates an obvious state mismatch. Driving completion from the children up makes the parent's completion state derivable rather than user-entered.

### Edit dialogue: outliner-style typing for subtasks

Inside the edit dialogue, a task and its subtasks are rendered as a small outliner — the parent task's title on the top line, child task titles indented beneath — and the text area behaves like a normal paragraph editor, so adding and restructuring subtasks happens through ordinary typing rather than a separate mode. A child can be promoted to a top-level task (via the promote drag target — see *Drag-target icons*); if a parent ends up with no children after a promotion, it reverts to an ordinary task with a checkbox.

The user needs this because adding and breaking down subtasks are the highest-frequency edits a person makes when capturing work. Lifting subtask creation into the same gestures used for any text editing makes capturing a parent-with-children as fast as typing a paragraph. The promote target covers the natural follow-up — "actually this child should stand on its own" — without requiring a separate menu or mode.

### Drag-target icons

Whenever the user picks up a task by drag (whether from a Schedule screen, including a Project's card on Later, or inside an edit dialogue), a row of drag-target icons appears. The set depends on context:

- **From a Schedule screen (including a Project's card on Later):** a **bin** target (delete the task) and a **cut** target (remove the task from Taskflow and place its content on the device clipboard).
- **From inside an edit dialogue (subtask only):** **bin**, **cut**, and **promote** targets. Promote behaviour is described above.

The cut/paste flow uses the **device's OS clipboard**, not a Taskflow-internal one. Cutting a task removes it from Taskflow and writes its content to the clipboard as plain text; a parent and its children go as a single indented block. Pasting happens inside the edit dialogue through the device's normal paste, and the outliner restores the parent/child hierarchy from the indentation — so a Taskflow-to-Taskflow cut and paste round-trips its structure, and pasted non-Taskflow text comes in as new lines by the same rule.

**Risk accepted:** if the OS clipboard is overwritten between cut and paste (e.g., by another app copying a notification number), the cut task is lost permanently. The user has accepted this trade-off in exchange for the simplicity of using the device's native clipboard.

The user needs this because the operations Taskflow exposes on a dragged task (delete, cut, promote) are conceptually parallel — each is "send the task to a destination" — and giving them the same drag-to-icon shape keeps the gesture vocabulary consistent. Routing paste through the device's normal paste mechanism means the user's existing muscle memory works.

### Completed task tray on Today

At the bottom of the Today screen is a greyed-out list of completed tasks. When the user checks off any task — on any Schedule screen, including inside a Project's card on Later — it joins this tray. Tapping a completed task in the tray opens its edit dialogue, where the user can uncheck individual subtasks (which un-completes the parent and removes it from the tray) or otherwise modify the task.

Completed tasks stay in the tray until the **day-begins-at rollover** (see *Settings → Day begins at*), at which point the tray clears — each new day starts with a fresh, empty tray. Clearing the tray does not delete the tasks: completed tasks are retained and persist in the database; they only leave the tray view.

The user needs this because seeing what they have already done provides a sense of progress, and routing all completions through one tray (rather than letting them disappear from each screen individually) gives the user one place to find and undo a mis-tapped completion.

### Onboarding — first run

The first time the user opens Taskflow, a short onboarding flow runs:

- **Two cards making the Schedule / Project distinction explicit.** One frames Schedule as where the user commits to what's next; the other frames Projects as where the user keeps what's possible, with no time pressure.
- **A multi-page video demonstrating the AI value.** What the paid tier with Claude unlocks in practice.
- **Two choices at the end.** *Skip AI for now (free)* or *How do I set up Claude?*. If the user chooses Claude setup, a follow-up screen explains they need a Claude account and deep-links into Anthropic's add-custom-connector modal where possible.
- **An X-in-corner escape hatch at any point** drops the user into no-AI mode (free tier).

The user needs this because the Schedule / Project split is the structural thing the user must understand before the app makes sense, and the AI choice is real and visible — the free tier is genuinely complete, not a hobbled trial — so the user should be able to see what they're choosing between rather than have it hidden.

### Tier model — free and paid

Two tiers, presented openly during onboarding and re-accessible from the side menu:

- **Free tier.** Local-only. No cloud sync. No Claude integration. The user gets the full Schedule, full Projects, and the full Strategy doc editor (with its mechanically-generated structure — see *Strategy doc*), plus manual JSON export/import. Free is "hard mode" by design — the user writes everything themselves. Free is not hidden, not punished, and not a time-limited trial. It is a complete product.
- **Paid tier.** Cloud sync plus Claude integration via remote MCP, bundled. Cloud sync is the infrastructure precondition for the MCP server to be reachable from Anthropic's servers; the two cannot meaningfully be separated.
- **Trial.** 30 days of paid tier, handled through Google Play.
- **Paused subscription.** While a paid subscription is paused, Taskflow reverts to **local-only** operation — no cloud sync, the same as the free tier (a non-paying user is never the case where data lives only in the cloud). The device's Room database stays the source of truth throughout, and re-syncs to the cloud when the subscription resumes.

The side menu always shows a "turn on AI for the full experience" entry that re-triggers the AI choice flow.

The user needs this because the value Taskflow offers on the paid tier — Claude integration into the user's normal Claude environment — is real, but it requires the user to already use Claude. Forcing every user into that path would mis-position the app. Offering both tiers honestly lets users self-select.

### Claude integration via remote MCP

On the paid tier, the user talks to Claude in their **normal Claude client** (claude.ai web, desktop app, or mobile app — there is no chat UI inside Taskflow). Taskflow exposes its data via a remote MCP server reachable on the public internet. The user adds the MCP connector once via claude.ai on the web; from there, it syncs to every Claude client on the user's account.

Claude's behaviour through MCP is governed by `SYSTEM-PROMPT.md` (the system prompt the MCP server hands Claude on connection) — covering life-area exploration, project-suggestion etiquette, Strategy doc reconciliation, proactive Taskflow checks, and tone.

The user needs this because the value proposition is the deep integration into the user's full Claude environment — Taskflow showing up where they already chat with Claude about everyday issues. An in-app chat would be a smaller, weaker product even if it reached a wider audience. Routing through MCP commits Taskflow to a narrower audience of Claude users and accepts that as the correct positioning.

### Strategy doc

The Strategy doc is a single document that lives above Projects in the structure Claude sees. Its **structure is mechanically generated**: each of the user's Projects gets a heading (the Project's name) and a paragraph (the user's description) underneath. The system **Unassigned** Project is excluded — it gets no heading or paragraph, because it is not a real area of life, only the home for tasks the user hasn't filed. There are no life-area headers. There are no time-zoom buckets.

The Strategy doc is also the **source of Project order** for the whole app: the order of its headings is the order Projects appear everywhere else, including the cards on **Later**. Reordering Projects is therefore a Strategy-doc act, and a deliberately high-level one:

- **On the free tier**, the user reorders Projects directly in the Strategy-doc editor, by dragging the Project headings.
- **On the paid tier**, Project order is changed through discussion with Claude rather than by direct drag — reordering Projects is a high-level strategic decision that earns a check-in, not a quick gesture. Long-pressing a Project card on Later surfaces a toast pointing the user to this: *"Discuss high-level strategy with Claude."*

The doc reads roughly as a chronological-by-priority piece — for example, *"this Project is prioritised above all else,"* *"this Project is for in about six months' time after Project B is completed,"* *"this Project is indefinitely postponed"* — with the user composing the paragraphs under each Project's auto-generated heading.

The doc is reachable from the side menu — a single calm row at the spine's right end — but is not foregrounded in everyday navigation; Taskflow still presents primarily as a task app.

**On the free tier**, the user edits the descriptions directly in an in-app markdown editor; the headings are auto-managed from the user's Projects, and the user sets Project order here by dragging headings (see above). There is no Claude reconciliation.

**On the paid tier**, the same editor is used. On the user's first opening of the Strategy doc area after switching to the paid tier (e.g., starting a trial), Claude reads the existing content and looks for tasks that contradict the Strategy or seem missing from it; it presents what it finds in groups and asks the user what to do — never silently editing. After the initial pass, every time the user submits an edit, Claude reconciles downstream tasks the same way.

A **share button** in the Strategy doc area lets the user share the doc (or a portion of it) via Android's standard share sheet — to a partner, parent, friend, or another app. Sharing the strategic picture with the people in the user's life is part of what gives a Strategy doc its function.

Life areas (e.g. Family, Work, Health) are not in the doc structure or anywhere in the UI. They are an abstract framing layer Claude carries (paid tier only) — see `SYSTEM-PROMPT.md` for how Claude builds and uses that picture.

The user needs this because long-range planning is real work the user does, and a structure that holds both moment-by-moment execution (Schedule) and decade-spanning intent (Strategy) without conflating them is the point of Taskflow. Mechanical structure means free-tier users still get a coherent doc shape without Claude in the loop, and paid-tier users get reconciliation rather than re-authoring. Sharing is what turns strategic intent from a private artifact into something the people who matter to the user can engage with.

### JSON export and import

A screen in Settings exports the user's full database — tasks, Projects, Strategy doc, ordering — to a JSON file. The same screen imports a JSON file produced by an export. Export and import are explicit, user-driven actions; nothing happens automatically. Android's Auto Backup is also active by default for all users (this is the OS-level feature that backs up app data to the user's own Google Drive and restores on reinstall) — Taskflow does not disable it.

The user needs this because portability matters even with no external integrations. JSON export/import gives every user explicit control over their data, including free-tier users who never get cloud sync, and provides a recovery path beyond Auto Backup for people who want it.

### Settings

The Settings screen is reachable from the side menu's bottom section (alongside Help, Thanks, and Report a bug — see *Side menu*). Settings holds the user-configurable controls that don't live on a task or a screen.

The user needs this because there is a small set of app-level controls (Day begins at, Date format, JSON export/import, AI tier) that need a home, and Settings is where Android users expect to find them.

### Settings → Day begins at

The Settings screen contains a single time picker called **Day begins at**. This is the only time picker in the entire app (UX principle 7). It controls when "today" rolls over for the user — used for the Tomorrow → Today rollover, for the calendar's "today" anchor in the side-scrolling date picker, and for any other place the app needs to know whether the user considers themselves to be in a new day yet. It ships with a default value of **4:00 AM**.

The user needs this because people who stay up past midnight do not consider the day to have ended — a task they meant to do "today" at 1 AM should still be on Today, not Tomorrow. A single, configurable cutoff lets the user define their own day boundary without polluting the rest of the app with time pickers.

### Settings → Date format

A two-option setting for how dates are displayed throughout the app: **DD/MM** (default) or **MM/DD**. The setting applies everywhere a date is shown — on Schedule task rows, on date picker tiles, on the Strategy doc, anywhere.

The user needs this because date conventions vary by region and Taskflow ships with international users in mind. A single, central setting beats hard-coding one convention or trying to detect locale automatically.

### Side menu

A side menu opens by **tapping the ☰ button in the top bar**, sliding in from the left as a drawer. Swipe-to-open is intentionally disabled — the ☰ is the one opener — so the gesture does not collide with the spine's horizontal-swipe navigation. The menu is a single navigation list that mirrors the spine from top to bottom: **Today**, **Tomorrow**, **Soon**, **Later**, then a single calm row for the **Strategy doc**. Tapping any entry opens that page. Projects are not listed in the menu — they live inside **Later** (see *Schedule view*), which is how the user reaches any Project.

Pinned to the bottom of the drawer, separated from the navigation list, are the **app actions**: **Settings**, **Help**, **Thanks**, and **Report a bug**, plus a "turn on AI for the full experience" entry that re-triggers the AI choice flow on the free tier.

The user needs this because the menu gives one-tap reach to every page on the spine, in spine order, while the things that are not spine pages — app-level actions — sit apart at the bottom where Android users expect them, off the task surfaces.

### No notifications in v1

Taskflow sends no notifications, reminders, or push alerts of any kind in v1. It is a **foreground-only** app: it does its work when the user opens it and stays silent otherwise. This is a deliberate absence — in the same spirit as having no "overdue" label — not a missing feature. Notifications would pull against UX principle 4 (no nagging) and UX principle 7 (no clock-time), the two things Taskflow is built to refuse. The decision is revisitable post-v1.

The user needs this because a task app that pings is a task app that nags, and nagging is the failure mode Taskflow exists to avoid. The silence is part of the no-shame design, not a gap in it.

---
*No-code method — Version 55.*
