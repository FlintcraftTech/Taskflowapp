# 6d6267c — Subtasks made possible again by moving one rule: blank lines are dropped on save, not on every keystroke

Session date and time: 2026-09-06, 22:06.

Four SPEC behaviours were unreachable because of a round trip that ate its own output. `OutlinerField`
holds a task and its subtasks as one block of text. Pressing Enter appended an empty child line and
re-emitted the whole block; that text came straight back through `Outline.parse`, which filtered out empty
lines — so the child, empty by definition at the moment it is created, was discarded before it could be
typed into. There was no other way to make a subtask anywhere in the app, so subtasks living under their
parent, the parent's expand/collapse rollup, completion rolling up from children, and the promote target
were all blocked rather than merely untested.

**The blank-dropping rule was not a bug and was not repealed.** Its comment said exactly why it existed —
*"Blank lines are dropped, so pressing Enter and thinking better of it leaves nothing."* That is a real
courtesy, and it is preserved: the rule moved to the save path, which is the moment it was written for. An
abandoned half-typed line still leaves nothing behind; a line being typed into now survives.

**The reasoning that reached this build was contested, and correcting it is why the fix is two small
edits.** The [verify-run-2026-08-31] audit drove the app over adb, typed a title, pressed Enter, typed
again, and got one concatenated string with no second line and no indentation. It concluded the dialogue
has no outliner. That conclusion was wrong — `Outliner.kt` exists, `EditTaskScreen.kt` calls
`OutlinerField` as the dialogue's first field, and the whole thing is built and wired in. A build handed
the audit's version of this item would have set about writing a feature that was already there, leaving the
actual defect untouched behind it. The item was rewritten at the decision step after the defect was
reproduced on the phone through the keyboard's own Next key — not just an injected Enter, which rules out
an adb artifact — and traced in the code.

One consequence beyond the four blocked behaviours: half of cut-and-paste was unreachable too. Cutting a
childless task and pasting it back works, but the parent-with-children indented block SPEC says should
round-trip could not be produced, because no parent with children could be made.

A third change came out of the fix rather than being asked for. `promoteChildLine` indexed the database's
saved children by the dragged line's position in the outline; with empty lines now surviving, those two
indexes can diverge, so promote counts past the blanks and ignores a blank line outright. Without it the
fix would have introduced a defect in the feature it unblocked.

**Files touched:** `app/src/main/java/com/example/taskflow/ui/edit/Outliner.kt`,
`app/src/main/java/com/example/taskflow/ui/edit/EditTaskViewModel.kt`.

**Routed to Captures:** none from this item.

**Depth:** full — the reasoning that produced the item was contested and overturned, recorded above.

**Tick:** done, UNCONFIRMED — it compiles, proved by `BUILD SUCCESSFUL` from Android Studio's integrated
terminal at 21:47 against a working tree holding this change, but nobody has installed that build and run
the item's own observation: press the keyboard's Next key at the end of a title, see an indented empty line
with the cursor in it, type into it and save to get a subtask, and press Next and save without typing to
get none.

**Advisory:** filed — forward-advisory.

Two held items named this one as their blocker — [subtask-affordance-in-edit-dialogue] and
[first-end-to-end-test]. It shipped rather than being dropped, so both are candidates to lift; that is
planning work and was not done here.
