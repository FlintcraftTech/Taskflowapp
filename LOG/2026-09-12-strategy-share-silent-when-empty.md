# d6ac7e8 — /plan [strategy-share-silent-when-empty]: the guard the capture asked for already existed, so the item became "say why" rather than "add a check"

An audit tapped Share on the Strategy page twice with no Projects in the database.
No share sheet opened and a cleared logcat caught nothing at all. It read that as a
visible control silently doing nothing, and left two dispositions open: open the
sheet anyway, or disable the button and say why.

Reading `ScheduleScreen.kt` corrected the premise. The button already carries
`enabled = strategySections.isNotEmpty()`, so with no Projects it is genuinely
disabled and the tap fired nothing — which is exactly why the logcat was empty. The
audit's second disposition is already the implemented behaviour, and a build
following the capture as written would have added a guard that exists.

What is missing is only the visible half: a disabled `TextButton` in a top bar is
muted rather than obviously unavailable, and the empty state says nothing about
sharing. So the fix is one string — the empty state gains a closing sentence saying
the doc can be shared once it holds something. Refused: enabling the button so it
shares an empty document; handing someone a blank share sheet is a worse answer
than a control that is visibly not available yet.

SPEC §Strategy doc's share sentence was incomplete against that decision and gained
the condition, written with Alex present. The audit's other half — whether the
share sheet opens at all, unverifiable while this button was the only route to it —
was handed to [strategy-edit-persistence-blocked] and written into that item's own
steps as well as into this one.

**Queue changes:** kept into Processed, cleared to run, retitled to name what it
actually does.

**Work processed:** kept — [strategy-share-silent-when-empty].
