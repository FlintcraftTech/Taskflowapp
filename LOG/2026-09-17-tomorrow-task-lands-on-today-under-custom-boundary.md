# [HASH] — /plan [tomorrow-task-lands-on-today-under-custom-boundary]: diagnosed from the source as a general off-by-one — at any boundary after midday every dated task reads a day early

**Kind: processed.** Recorded 2026-09-17 at 16:01, read from the clock, during a planning session.

The capture reported one symptom: with Day begins at set to an afternoon hour, a task added from the
Tomorrow page appeared on the Today page instead. It named reading `SlotDeriver` as the next step, and
that read — plus the two `noonEpoch` functions and every `logicalDate` caller — settled it without a
device.

**The mechanism.** A task's date is stored as **noon** on the chosen day. `SlotDeriver.logicalDate` then
subtracts the day-begins-at hour from a stored moment before reading off its day. Noon minus four is the
same day; noon minus thirteen is the previous one. So the stored date reads one day early, and the shipped
default of 4:00 AM is why nothing looked wrong.

**The fault is general and the reported symptom is one instance.** At any boundary later than midday every
dated task shifts a day early across every Schedule surface, and recurring tasks shift too, because
`instancesOf` passes the task's stored date through the same adjustment as its anchor.

**The capture's diagnosis was the opposite of the truth**, and that is recorded because it changed what to
fix. It supposed the Schedule pages used the calendar date while the date strip used the logical one. Both
use the same function; the damage is in the round trip through a noon anchor too close to an afternoon
boundary to survive the subtraction. The capture's three unestablished questions are all settled by this:
the fault is in the interpretation of a stored date rather than in the derivation or the add path, and yes
it reaches Soon, Later and recurrence.

The fix stops applying the boundary adjustment to stored task dates while keeping it for real moments — the
clock and `completedAt`, where it is correct. Nothing stored changes, so no migration and nothing on the
phone is rewritten. Refused: anchoring stored dates at the boundary hour instead of noon, which also works
and would require rewriting every stored date whenever the setting changed. Refused: moving the anchor to
a later hour, which buys room rather than correctness.

SPEC needed no change — §Add a new task and §Data model were already right and the code was wrong.

**Queue changes:** [tomorrow-task-lands-on-today-under-custom-boundary] rewritten into a work item and
cleared to run — three files including `SlotDeriverTest.kt`. Placed ahead of
[day-begins-at-rollover-still-unrun], which now carries a line saying it must not be driven until this
ships, since its own setup is what exposed the fault.

**Work processed:** kept — [tomorrow-task-lands-on-today-under-custom-boundary].
