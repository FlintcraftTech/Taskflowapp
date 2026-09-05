# 9e24ba7 — /plan [nav-day-card-layer]: the day-detail card, and the three interaction details the August design had deliberately left open

One of four items split out of [nav-completed-history] this session; the split's reasoning is in
`2026-09-04-nav-completed-history.md`. Held below the line because the card is opened from the Search
page, which does not exist until [nav-search-completed-history] ships.

The August design left the card layer's swipe and interaction detail open until there was a real screen
to finalise it against. The user chose to design all four items now rather than two now and two later,
which meant settling it here. Three answers, all theirs to approve and all now in SPEC §Day-detail card
layer as well as in the item:

- **Back closes the card** and returns to the page it was opened from — the same "up one level" meaning
  the edit dialogue and menu overlay already carry, which is why [left-edge-swipe-collision] leaves its
  handler able to take a third case.
- **Days with nothing completed are skipped.** Shown empty, a dated card with nothing on it reads as a
  reproach, which UX principle 4 refuses.
- **The far end bounces** — swiping right past the oldest day holding a completed task does nothing, the
  same as the end of the row of pages.

Editing completed tasks from the search results list was refused, per SPEC's read-only rule: a tappable
list of completed tasks must not become a second place to change things.

**Queue changes:** [nav-day-card-layer] filed and moved into Processed below the line, held against
[nav-search-completed-history]. SPEC §Day-detail card layer gained the three sentences at this close,
the spec-sync gate having caught that they were decided but never written.

**Work processed:** kept, held below the line — [nav-day-card-layer].
