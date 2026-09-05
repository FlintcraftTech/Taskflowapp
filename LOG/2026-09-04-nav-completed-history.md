# 9e24ba7 — /plan [nav-completed-history]: the spine's left half split into four buildable items, after a hold was written and withdrawn within the hour

The item bundled four sub-features — the Search page, the Yesterday page, the day-detail card layer and
share-a-day — and had been held against [nav-left-spine-spec-edit]. That shipped in the 2026-09-02 run,
so SPEC now carries all four sections as decided product truth and the item's own condition for
becoming keepable was met.

**A second hold was written and withdrawn in the same session, and the reasoning is kept so it is not
re-proposed.** Claude held it against [left-edge-swipe-collision], arguing that all four screens turn on
how the user reaches the pages left of Today when the left edge is also Android's back gesture. The
user knocked it down: swiping between pages is one mechanism, not two, so adding pages to the list makes
right-swipes work exactly as left-swipes already do, with no new gesture to build. And Android's back
gesture lives on both edges, so the outermost-strip contest has been present all along in the direction
that works fine. A collision the app already lives with does not gate the direction that does not exist
yet.

The four items were then designed in full at the user's direction rather than two now and two later.
That required settling the card-layer interaction detail the August design had deliberately left until
there was a real screen, which is recorded in [nav-day-card-layer] and in SPEC.

Foundations were confirmed in code before any of it was written: `Task.completedAt` exists,
`TaskDao.getCompletedTasks()` already returns a flow, and `StrategyScreen` already shares text through
the Android share sheet — the pattern share-a-day's Markdown half follows.

**Queue changes:** [nav-completed-history] deleted, its content living on in four new items —
[nav-yesterday-page] and [nav-search-completed-history] cleared to run, [nav-day-card-layer] and
[share-a-day] held below the line in dependency order.

**Work processed:** deleted after splitting — [nav-completed-history].
