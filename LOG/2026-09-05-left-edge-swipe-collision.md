# 9b577c4 — Back returns to Today from any spine page, and closes the app only from Today

Date: 2026-09-05 11:09

This was filed as a three-way collision over the left edge and rescoped during planning on the
user's reasoning, which removed most of it. Swiping between pages is one mechanism, not two: adding
Yesterday and Search to the page list makes right-swipes work exactly as left-swipes already do.
And Android's back gesture lives on **both** edges, so the outermost-strip contest had been present
all along in the direction that works fine. A collision the app already lives with, in the direction
used daily, is not a gate on the direction that does not exist yet.

What was left was real but small. `AppRoot.kt` enabled its `BackHandler` only while an edit dialogue
or a menu overlay was open, so on the bare spine back was unhandled and an edge back-swipe on any
page closed the app. Nobody chose that; it is what falls out of never having decided.

Settled by the user: back returns to Today, and closes the app only from Today — the standard
Android pattern for an app whose top level is a row of pages. Back stepping one page left was
refused at planning, because it reads well until someone swipes right three times and back then
walks them through pages they never visited. Claiming a gesture-exclusion zone was refused too:
Android caps exclusions at 200dp per edge, so the app could only ever take a band, leaving paging to
work in part of the edge and back in the rest.

The handler was rewritten as a `when` block over cases rather than an if/else pair, because
`[nav-day-card-layer]` adds a third layer above the spine and its back behaviour was already settled
on 2026-09-03. That item does its own wiring; this one only had to leave the structure able to take
the case. The pager move is animated rather than jumped, so back reads as travel along the spine.

Files touched:
- `app/src/main/java/com/example/taskflow/ui/navigation/AppRoot.kt` — the `BackHandler` rewritten
  with a third case returning the pager to Today

Routed to Captures: none.

Tick: done, UNCONFIRMED — needs a device to confirm back from Later, Soon or Tomorrow lands on
Today, back again closes the app, and the dialogue and overlay branches still behave. One thing
remains unestablished from planning and still is: whether the phone is on gesture or three-button
navigation. On three-button there is no edge-back at all and none of this is reachable.
