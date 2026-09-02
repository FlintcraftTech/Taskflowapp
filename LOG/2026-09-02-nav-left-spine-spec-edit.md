# [HASH] — build [nav-left-spine-spec-edit]: four SPEC sections for the spine's left half, and the spine sentence extended

This item's whole job was a SPEC edit — the four sections the left-half navigation work needs before any of it can be built. It was split out of [nav-completed-history] in planning on 2026-08-21, once two of that item's three open questions were settled with the user, which is what made these sections writable at all.

The sections written, each carrying the decision behind it rather than only its shape:

**§Search and completed history** — one unified search over active and completed tasks, not two boxes. Settled 2026-08-21, the user's call: someone hunting a task usually does not know or care whether they already finished it, so two boxes means guessing which to open. [search-feature]'s two surviving decisions were folded in here on 2026-08-25 when that item was deleted — results are **read-only**, with a tap navigating to where the task lives, and scope covers all tasks across every slot and Project plus Project names, with the Strategy doc excluded because it is prose rather than items and its results cannot render as task rows. The current-screen and current-Project scopes were the rejected candidates: either would reintroduce the "am I looking in the right place?" guess that unified search exists to remove.

**§Yesterday page** — a spine page, not a card, immediately left of Today. Its content is essentially what was completed yesterday, because past-due tasks stay on Today rather than falling backwards (UX principle 4), so nothing else is left behind to show.

**§Day-detail card layer** — days get their own left-right axis, deliberately different from the spine's and signalled by the card visual, because a day is the unit history is remembered in and "which day am I looking at?" should not compete with "which horizon am I looking at?". Editing or un-completing a single task happens only from a day card, which is what keeps a tappable completed list from becoming a second place to change things.

**§Share a day** — PNG and Markdown, both offered, citing `workshop/resources/research/android-share-format-png-vs-pdf.md`: PNG renders inline in a chat thread rather than arriving as an attachment, and Markdown is carried as `text/plain` because almost no Android app declares `text/markdown` and doing so would produce a near-empty share sheet.

§Schedule view's spine sentence was extended leftward to read Search · Yesterday · Today · Tomorrow · Soon · Later · Strategy.

**What this build found and did not fix.** Extending that sentence left §Side menu claiming to "mirror the spine" while listing five of its seven pages. Whether the menu gains Search and Yesterday rows, or stops claiming to mirror, is a product choice rather than a wording fix — so it was filed rather than written, and SPEC lags that one sentence until the next planning run.

**Files touched:** SPEC.md — four sections added after §Completed task tray on Today; §Schedule view's spine sentence extended. No app code.

**Routed to Captures:** [side-menu-spine-mismatch].

**Tick:** done, confirmed: SPEC carries §Search and completed history, §Yesterday page, §Day-detail card layer and §Share a day, [search-feature]'s two surviving decisions appear inside the first of them, the spine sentence reads Search · Yesterday · Today · Tomorrow · Soon · Later · Strategy, and no app code changed. Read back after writing; the cited research file exists.
