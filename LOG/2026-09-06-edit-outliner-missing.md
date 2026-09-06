# 5fa5c85 — The outliner is not missing: a new subtask line is destroyed by the parser that reads it back, and the item was rewritten around the real defect

Recorded 2026-09-06, 00:29.

The 2026-09-05 device audit concluded the edit dialogue has no outliner at all, which put four SPEC
behaviours out of reach and made this the largest gap it found. That conclusion is wrong, and finding
that out is most of this item's value.

`Outliner.kt` exists and `EditTaskScreen.kt` calls `OutlinerField` as the dialogue's first field. Claude's
first hypothesis was that the audit's evidence was an adb artifact — the line is single-line with the
keyboard action set to Next, and the subtask is created in `onNext`, which a raw injected Enter does not
fire. That hypothesis was wrong too. Driving the phone directly, tapping the keyboard's own Next key
produced nothing, and an injected Enter produced nothing either, so the failure is real.

**The cause is a round trip that eats its own output.** `onNext` appends an empty child line and re-emits
the rendered text; that text comes straight back through `Outline.parse`, which trims every line and
filters out the empty ones. The new child is empty by definition at the moment it is created, so it is
discarded before it can be typed into. The blank-dropping rule is deliberate and its comment says why —
pressing Enter and thinking better of it should leave nothing. It makes abandoning a half-typed line
harmless and starting one impossible.

So the fix moves the rule rather than repealing it: `parse` stops discarding blanks, and the save path in
`EditTaskViewModel.kt` drops empty children instead. Two files, and the item now refuses building an
outliner explicitly, because a build handed the audit's version would have written a second one and left
this defect behind it.

The older [subtask-affordance-in-edit-dialogue] had its blocker swapped to this item in the same move. It
had waited on the audit, which has now run — and which sharpened rather than resolved its concern, since
the hint it adds would advertise an Enter key that genuinely does nothing until this ships.

**Queue changes:** capture rewritten and moved into Processed cleared to run; [subtask-affordance-in-edit-dialogue]'s
`Blocked by:` changed from the completed audit to this item.
**Work processed:** kept — [edit-outliner-missing].
