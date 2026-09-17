# bbbef3c — /next [nav-day-card-layer]: the day-detail card layer, opened from a search result and moving on its own axis

Written 2026-09-12 at 13:05, read from the clock. "Yesterday" below names a page in Taskflow, not a date.

Tapping a search result, a group or a date header now opens that single day as a card in the foreground, moving on a deliberately different left-right axis from the spine. A day is the unit history is actually remembered in, and giving days their own axis keeps "which day am I looking at?" from competing with "which horizon am I looking at?". This is also the only place a completed task can be edited or un-completed — search results stay read-only, which is what stops a tappable list of completed tasks becoming a second place to change things.

Three interaction details the August design deliberately left until there was a real screen were settled on 2026-09-03 and are built as settled. Back closes the card and returns to the page it was opened from, the same "up one level" meaning the edit dialogue and the menu overlay already have. Days with nothing completed are skipped, because a dated card with nothing on it reads as a reproach, which UX principle 4 refuses. The far end bounces: swiping right past the oldest day holding a completion does nothing.

**One of those three had no natural expression, and the choice is worth recording.** "Swiping left past the newest card carries the card layer and the Search page off together, landing on the Yesterday page" is not something a pager does. It is built as one blank trailing page: the pager holds one more page than there are days, and settling on that page triggers the exit. The alternative was intercepting an overscroll gesture, which is fiddlier and fails differently on different devices. The blank page costs a page nobody should ever see for longer than the moment they are leaving on.

Days holding completions are ordered oldest first in the view model, so the pager's own direction is the card layer's direction: the next page brings the newer day in from the right, the previous brings the older in from the left. Days are cut on the day-begins-at boundary rather than midnight, the same boundary every other history surface uses.

The layer is hosted in `AppRoot` above the pager and below the edit dialogue, so tapping a completed task inside a card opens the editor over it and closing the editor returns to the card. Its back case slots into the existing `BackHandler`'s list of levels — which that handler's own comment anticipated, having been written as a list of cases rather than an if/else pair precisely so this layer could be added without restructuring it.

**Files touched:** new `ui/history/DayCardLayer.kt` (121 lines) and `ui/history/DayCardViewModel.kt` (77 lines); `ui/history/SearchScreen.kt` (completed rows and date headers became tappable); `ui/navigation/AppRoot.kt` (hosts the layer, adds its back case, drives the exit); `ui/schedule/ScheduleScreen.kt` (threads the callback through to the Search page).

**Routed to Captures:** none.

**Verification:** done, UNCONFIRMED — it compiles, but nobody has opened a card from a search result on a device, swiped through days, or left past the newest one. [share-a-day] names this item as its blocker and cannot be judged ready on a compile alone.
