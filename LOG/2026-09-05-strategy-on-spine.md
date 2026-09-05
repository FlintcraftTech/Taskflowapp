# [HASH] — Strategy moves from a drawer overlay to the spine's rightmost page

Date: 2026-09-05 11:12

Strategy is now reached by swiping right from Later, and its drawer row scrolls the pager to it the
way the slot rows already do. `Overlay.Strategy` is gone.

**No SPEC edit was needed, and establishing that is what settled the item.** During planning this
was first described as SPEC being ambiguous about whether Strategy is swipeable or menu-only. It is
not ambiguous — the user asked for the retrieve, and reading the record changed the answer. Three
LOG entries from June carry the spine including Strategy, and SPEC §Schedule view and UX principle 3
both say today that the spine "extends rightward into Strategy". That earlier reading is recorded in
the item so it is not reached for again.

Why the code differed, and why the reason had expired: `LOG/0003-side-menu-schedule-projects-app-actions.md`
records the overlay as a deliberate build choice — every deep destination was a placeholder in that
batch, so a navigation-library dependency bought nothing then. Strategy's real screen shipped later
and the routing was simply left alone. So the overlay was right when it was made and stopped being
right when Strategy stopped being a placeholder. Introducing Jetpack Navigation Compose to do this
was refused: 0003 weighed and rejected it, and putting Strategy on the pager *removes* a deep
destination rather than adding one, so that reasoning holds more strongly now.

The item named `StrategyScreen.kt` as read but not changed — it is rendered from a different place
rather than altered — so its own back control was wired to return to Today, which is what back means
everywhere else on the spine.

Files touched:
- `ui/navigation/Destination.kt` — `STRATEGY` added after `LATER`, `Overlay.Strategy` removed
- `ui/navigation/AppRoot.kt` — the overlay branch and the drawer's `onStrategy` callback removed
- `ui/navigation/AppDrawer.kt` — the hand-written Strategy row removed; it now comes from
  `SpinePage.entries` like every other page row
- `ui/schedule/ScheduleScreen.kt` — the STRATEGY branch

Routed to Captures: `[strategy-page-double-header]`, filed mid-run at 12:47. Leaving
`StrategyScreen.kt` unedited means it keeps the header row from its overlay days, so as a spine page
it now names itself twice, under the spine's own header. Cosmetic and nothing is broken; filed
rather than fixed because rehousing its share button is a design question rather than a deletion.

Tick: done, UNCONFIRMED — needs a device to confirm swiping right from Later opens Strategy, the
drawer row jumps to that page, back returns to Today, and the editor and share button still work. A
grep confirms no `Overlay.Strategy` or `onStrategy` reference remains anywhere in the source.
