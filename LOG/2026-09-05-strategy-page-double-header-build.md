# 0bd8c64 — Strategy page's leftover header row removed and its Share action moved into the spine header

Moving the Strategy doc onto the spine ([strategy-on-spine]) left it with two headers: the spine's own,
naming the page, and `StrategyScreen`'s own row from its overlay days — a back arrow, the word
"Strategy" again, and the share button. Nothing was broken; it read wrong. The back arrow even behaved
correctly, returning to Today, which is exactly what the system back gesture already does under the
spine rule.

The user chose the fix in planning: the arrow and the title go, and the share button moves into a new
trailing action slot on the spine header. Two alternatives were refused. Leaving the share button in the
page's own row with only the arrow and title removed is the smallest change and still leaves two header
rows stacked, which is the thing being fixed. A share control at the foot of the document hides the app's
one sharing affordance below a scroll of the user's own writing.

The constraint that made this a design question rather than an obvious tidy-up: the spine header's
top-right was deliberately kept clear for the drag-to-delete target. That reservation is real but applies
only while a task is being dragged, and the Strategy page holds headings and paragraphs rather than
tasks — so on the one page that uses the slot, the corner is never claimed. The build wrote that
reasoning into the code as a comment, so the next person to look at the collision finds the answer there.

Ordinary state hoisting carries the action: `StrategyScreen` built its share intent from a view-model it
obtained itself, which a header outside the pager cannot reach, so the same factory call moved up into
`ScheduleScreen` and the sections pass down as a parameter.

Confirmation of the defect arrived from an unexpected direction during the same session: the device audit
swiped onto Strategy and photographed the doubled header, on the 2026-09-04 build the phone carries. The
fix is not on that phone.

**Files touched:** `app/src/main/java/com/example/taskflow/ui/schedule/ScheduleScreen.kt`,
`app/src/main/java/com/example/taskflow/ui/strategy/StrategyScreen.kt`.

**Routed to Captures:** none.

**Tick:** done, UNCONFIRMED — not compiled, since Gradle will not run from Claude's shell (TOOLS.md),
and not seen on a device. The item's observation is a device check: one "Strategy" in the spine header,
Share in its top-right, no back arrow on the page, and no trailing control on the other spine pages.
