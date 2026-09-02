# b28ee0e — [nav-completed-history] gains `Blocked by: [nav-left-spine-spec-edit]` so it stops returning every session

Third session running, the item was presented and skipped for the same standing reason: it becomes keepable only once the SPEC edit [nav-left-spine-spec-edit] ships and there are real screens to design the interaction against. Nothing about the substance changed, so the fix was to how it waits: a `Blocked by:` line naming the spec edit, which stops the capture being offered while that item is open and brings it back by itself the moment the edit ships. The user agreed. No date was used — the wait is on something inside the queue, which is exactly the case the blocker field covers without approval machinery.

**Queue changes:** `Blocked by: [nav-left-spine-spec-edit]` added under the item's heading.
**Work processed:** [nav-completed-history] — held in Unprocessed against its blocker.
