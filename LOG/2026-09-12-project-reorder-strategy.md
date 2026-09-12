# [HASH] — /next [project-reorder-strategy]: Project headings drag to reorder in the Strategy doc, and the shared drag primitive gained a handle slot to allow it

Written 2026-09-12 at 13:05, read from the clock.

The Strategy doc is the source of Project order for the whole app, including the cards on Later (SPEC §Strategy doc), and on the free tier the user sets that order by dragging the headings. Everything needed already existed and nothing called it: `Project` carries a `sortOrder`, `ProjectRepository.updateSortOrder` writes it, and `ReorderableColumn` is the app's one drag primitive. The work was wiring rather than invention.

**A design problem the item could not have anticipated came up mid-build, and it cost a scope addition.** The item says the heading alone is the drag handle, settled with Alex on 2026-09-06 — refused there was making the whole section draggable, paragraph included, because a long-press meant to select a word in the paragraph would start a drag instead. But `ReorderableColumn` attaches the long-press-and-drag to the entire row and offered no way to narrow it. Two ways forward: give the primitive a handle slot, or rely on the paragraph's text box swallowing the long-press for its own text selection. **The second was rejected as unverifiable and accidental** — it would probably work, but it could not be checked without a device, and correct behaviour arrived at by side-effect is not a design. Alex approved adding `Reorderable.kt` to the run's scope.

The handle slot is written so every existing caller is untouched: `itemContent` gained a `ReorderableItemScope` receiver exposing `Modifier.dragHandle()`, and a `dragFromHandleOnly` flag decides whether the row itself still picks up. Callers that ignore the receiver — the Schedule pages and the Later cards — compile and behave exactly as before. The gesture itself became a modifier attachable to either the row or the handle, carrying its origin reporting with it so pointer positions stay in the right frame whichever element the finger is on.

`reorderProjects` rewrites the whole sequence to 0..n-1 rather than nudging the moved row — the pattern `ScheduleViewModel.reorderSlot` and `reorderCard` already use, because positions drift as Projects come and go.

`LaterPage.kt` needed no edit: the cards already read Project order from `getAllOrdered`, so setting the order here moves them for free, which is what SPEC means by the Strategy doc owning Project order app-wide.

**Files touched:** `ui/strategy/StrategyScreen.kt` (sections became a `ReorderableColumn` with the heading as the handle), `ui/strategy/StrategyViewModel.kt` (`reorderProjects`), `ui/common/Reorderable.kt` (the handle slot), and `ui/schedule/ScheduleScreen.kt` (the call site passes `onReorder`).

**Routed to Captures:** none.

**Verification:** done, UNCONFIRMED — it compiles (`BUILD SUCCESSFUL`, 2026-09-12), but nobody has dragged a heading on a device, checked the order survives a relaunch, or checked the Later cards follow it.
