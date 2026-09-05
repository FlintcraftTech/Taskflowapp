# [HASH] — Yesterday joins the spine, and SpinePage.slot becomes nullable so history pages can exist

Date: 2026-09-05 11:10

Yesterday sits immediately left of Today and shows what the user completed yesterday. It went first
of the left-half items because it is the simplest real page and because the enum change it carries
is a precondition for the others.

The shared plumbing is the substance. `SpinePage` paired every entry with a non-null `ScheduleSlot`,
which encoded an assumption that every page is a place tasks can be put. Yesterday is history, so
`slot` became `ScheduleSlot?`. That nullability then does real work beyond compiling: it is what
keeps a history page off the add surfaces, since SPEC §Add a new task names the four slots as the
only ones. The add button is now absent on a page with no slot rather than present and inert, and
dropping a dragged task on such a page is guarded for the same reason — dropping a task on Yesterday
would have to mean something, and it doesn't. Giving Yesterday its own `ScheduleSlot` was refused at
planning on exactly that ground.

Yesterday's bounds are computed from the day-begins-at setting rather than from midnight, so a task
finished at 1 AM sits under the day the user thinks it does, on this surface as on the Schedule.
The page shows completions only, and that is not a simplification: tasks that slipped past their
date stay on Today rather than falling backwards, so nothing else is left behind. Its empty state
states a fact about yesterday rather than a shortfall.

**One thing came free and is worth recording, because it changed what the other two navigation items
had to do.** The drawer's Yesterday row was not written by hand — `AppDrawer` already builds its
navigation list from `SpinePage.entries`, so adding the enum entry produced the row in spine order
at no cost. The same held for Search and Strategy, which is why no drawer edit appears under either.
The item had expected a hand-written row, folded in from a deleted `[side-menu-spine-mismatch]`.

Files touched:
- `ui/navigation/Destination.kt` — `slot` made nullable, `YESTERDAY` added before `TODAY`
- `ui/history/YesterdayViewModel.kt` — new; yesterday's completions against the day boundary
- `ui/history/YesterdayScreen.kt` — new; the read-only greyed list and its empty state
- `ui/schedule/ScheduleScreen.kt` — the null-slot branch, and the drop-to-reschedule guard
- `ui/navigation/AppRoot.kt` — add button hidden where there is no slot
- `ui/navigation/AppDrawer.kt` — comment rewritten to record that the list mirrors the whole spine

Routed to Captures: none.

Tick: done, UNCONFIRMED — needs a device to confirm swiping right from Today opens Yesterday, a task
completed today does not appear on it, swiping left returns to Today, and the app still opens on
Today after a relaunch.
