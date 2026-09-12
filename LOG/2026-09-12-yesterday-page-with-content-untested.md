# [HASH] — /plan [yesterday-page-with-content-untested]: kept as an [audit] held against the first real day of use, with the crafted-import route refused rather than forgotten

The Yesterday page passed three of SPEC's four claims on the device and the fourth
went untested, because nothing had been completed the day before. A page that
correctly draws its empty state says nothing about how it draws a list, and this
page's entire content is that list.

The capture named two routes and chose neither: wait for a device pass that happens
to run the day after a completion, or drive it from a crafted import file. The
import route was checked rather than assumed — `TransferRepository.importAdding`
copies each incoming task through unchanged apart from its identifiers, so
completion state and completion date survive, which means it genuinely works.

Refused all the same: it would put a fabricated completed task permanently into
Alex's real history, and nothing can remove it while deleting a task is the drag
gesture [drag-eaten-by-page-swipe] fixes. It is recorded in the item as the
fallback if her first real day somehow produces no completion.

Held against [first-end-to-end-test] instead, which has her use the app for a
normal day and complete at least one task — so the day after it runs, the page has
genuine content and the check costs a swipe. Deliberately not folded into that
item's walkthrough: a walkthrough ends at its own observable rather than growing a
step that fires a day later.

**Queue changes:** kept into Processed, below the line, held against
[first-end-to-end-test]; reshaped from a capture into an `[audit]` with three
steps.

**Work processed:** kept — [yesterday-page-with-content-untested].
