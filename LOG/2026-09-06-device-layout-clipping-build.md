# PENDING — Two boxes too small for what was inside them: date tiles now grow with their content, and the spine header stopped stacking three things in one place

Session date and time: 2026-09-06, 22:07.

Two layout defects seen on the phone, filed together because both are text drawn on top of something else
rather than a judgement about how the app looks.

**The date strip's month row was cut through its letters, and the earlier fix could not have caught it.**
[date-strip-legibility] stopped clipping by computing tile *width* from the available space, which its
record states. `DateStrip.kt` still sized every tile with a fixed `TILE_HEIGHT`, and the month row is what
overflowed that — the letters cut along their lower half rather than truncated at their end. The capture's
instinct, a box too small for what is inside it, was right; the axis it implied was wrong, which is exactly
why a fix on the other axis left the defect standing.

The height now follows the content, with the old figure kept only as a floor so tiles still read as one
even row when the content is short — a `LazyRow` gives its children no shared height to fall back on.
**Raising `TILE_HEIGHT` to a larger fixed number was refused**, on the ground the item itself gave: a bare
dp value has no derivation, which is what an invented value was rejected for during [drawer-ai-row-copy].
A bigger number would only have moved the point at which the letters get cut, which is what a larger user
font scale reaches anyway.

**The header overlap was a stacking problem, not a sizing one.** `SpineHeader` placed three children in one
box — the menu key at the start, the title and its chevrons centred, and the focused Project's name at the
end — with the last two occupying the same horizontal space and drawing over each other. The audit recorded
the exact rendered string, "AU>DIT-PROJ", from a ten-character Project name; a longer name would have buried
the chevron entirely. The three are now slots in a row, so the name yields to the chevron by construction
rather than by fitting, with a width cap and an ellipsis past it, and a spacer balancing the menu key so the
title stays optically centred when the end slot is empty.

**The item named the wrong file for that half, and the scope-lock caught it.** It listed `AppRoot.kt`,
which only passes the focused Project down; the header is `SpineHeader` in `ui/schedule/ScheduleScreen.kt`.
The edit was refused, Alex approved adding that file to the run's list, and `AppRoot.kt` was left untouched
because it needed no change. The described work was unchanged throughout — only the file the item predicted
was wrong.

**The clipping was confirmed visually later the same session**, which no earlier check had managed: a
screenshot of the edit dialogue on the phone's build shows "Sept" losing its lower half on every tile. The
accessibility tree reports text regardless of clipping, so every prior check had been reading a source that
could not see this defect.

**Files touched:** `app/src/main/java/com/example/taskflow/ui/edit/DateStrip.kt`,
`app/src/main/java/com/example/taskflow/ui/schedule/ScheduleScreen.kt`. `AppRoot.kt` was listed by the item
and not edited.

**Routed to Captures:** none from this item.

**Depth:** full — an alternative was seriously weighed and lost, recorded above.

**Tick:** done, UNCONFIRMED — it compiles, proved by `BUILD SUCCESSFUL` at 21:47 including the `heightIn`
import swap in `DateStrip.kt` and the four imports added to `ScheduleScreen.kt`, but nobody has installed
that build and seen every date tile showing its month name whole, or a Project focused with a name long
enough to have caused the overlap showing both the name and the right chevron with neither drawn over the
other. A Project now exists for that second check — **Family**, created during this session — where none
did when the item was written.

**Advisory:** filed — forward-advisory.
