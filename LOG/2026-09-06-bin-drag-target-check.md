# 6d6267c — /next [bin-drag-target-check]: halted mid-drive, because the gesture it tests is broken for a human thumb too

**Outcome: halted mid-drive at step 2.** The check cannot be performed: dragging a lifted task navigates to
the next page instead of dragging it, so no target can be reached. Filed as [drag-eaten-by-page-swipe].
The item stays in the queue, and cannot pass until that ships.

**The premise this item was filed on is now known to be wrong**, which is the more valuable half of the
result. It was split out as `[user]` work on 2026-09-05 specifically because three adb attempts had failed
and were read as a limit of driving a device remotely. Alex's own thumb reproduced the failure exactly. So
this was never user work; it was a defect that three sessions in a row mistook for a tooling problem.

Original heading: walking Alex through the one check adb cannot drive

Recorded 2026-09-06, from 12:50, during a /next run. Written live as the walk-through proceeds.

The capability check was re-run before handing over and the `[user]` tag holds. Claude can drive taps,
swipes and text on this device, and has done so throughout this session — but not this. A long-press drag
that must land on one of two adjacent targets has been attempted three times across two sessions and every
attempt registered as a horizontal page swipe and navigated instead of dragging, including one built from
explicit `input motionevent DOWN / MOVE / UP` calls with a hold between them. That is recorded in TOOLS.md
as a capability limit rather than bad luck.

The screen is being held awake by `svc power stayon true`, set earlier in the run with Alex's approval
after the phone locked four times. It must be set back to false when the device work ends.

## Steps as they happen

- Step 1, the throwaway task: **already satisfied without action.** The item says to add a disposable task,
  or to use `AUDIT-completed-check` if it is already sitting on Today — it is, dated `05/09`, left behind by
  a session on 2026-09-06 that could not delete it over adb, which is the same failure this item exists for.
  Confirmed present on Today immediately before the hand-over. So the check disposes of the litter that
  proves its own point.
- Step 2 handed over 2026-09-06, 12:52: press and hold the task until it lifts, and keep holding.
  **Partly worked, then failed.** Alex reported the target row appearing with the bin on the left — so the
  long-press and the lift do work — but two things wrong with it. The icons render in the task area rather
  than at the top of the screen where SPEC and this walkthrough both put them. And moving her finger to
  drag navigated the page to Tomorrow rather than dragging the task.
- Steps 3, 4 and 5 not reached. They all depend on delivering the task onto a specific target, which is the
  thing that does not work.
- TOOLS.md's drag line was corrected in the same moment rather than left to a later session, since it was
  written earlier in this run and was actively misleading: it told future sessions the failure was theirs.

## What the next session should know

`AUDIT-completed-check` is still on Today at `05/09`. It has now survived three sessions' attempts to
delete it, and the reason is no longer a mystery — nothing can deliver a task to the bin on this build. It
is not litter left by carelessness; it is a standing reproduction of [drag-eaten-by-page-swipe], and it is
worth leaving there until that ships.

The screen-awake override `svc power stayon true` was still in force when this item halted; it is set back
to false at the end of the run's device work.

