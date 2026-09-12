# d6ac7e8 — /plan [day-begins-at-rollover-still-unrun]: kept as a [user] check, cleared, with its cleanup step changed from a delete nothing can perform to ticking the task off

Nobody has watched a task on the Tomorrow page roll onto the Today page at the day
boundary. SPEC says such a task arrives with no label, no reordering and no shame,
and that Today's uncompleted tasks stay where the user put them. The picker was
confirmed present twice; the rollover itself never.

The `[user]` tag holds up under the capability check. A session can drive every
step over adb — change the setting, add the task, read Today's order back — but it
cannot sit for up to an hour, and it cannot judge the two things that matter: that
the task arrived unlabelled and that it did not jump the queue. What was added is
the third answer to that test: if a session is open when Alex starts, it can do
steps 2 to 5 for her and leave only the looking. Not split into its own item,
because the setup and the watch are one sitting — separating them would mean two
sessions coordinated minutes apart for the sake of five taps.

Its last step was wrong and is now fixed. It told the user to report the test task
as needing deletion, blaming [bin-drag-target-check] — reasoning that rested on the
retired belief that the failure was an adb limitation. Deleting is broken for
everyone until [drag-eaten-by-page-swipe] ships. The task is now completed instead:
the Completed tray empties itself at the next rollover, so the test disposes of its
own litter and this item does not have to wait on the drag fix.

**Queue changes:** kept into Processed, cleared to run, placed at the end of the
cleared region with the other work that stops for the user.

**Work processed:** kept — [day-begins-at-rollover-still-unrun].
