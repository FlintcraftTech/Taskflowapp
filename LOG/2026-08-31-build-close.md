# baacb0b — orphaned-build close: a crashed 20-item run closed with 2 items shipped, partial date-picker code committed as-is, and the format-5 migration still owed

A build session started a 20-item run and crashed (or was never closed) partway. This fresh session ran /done to close it out, on the way to the /setup migration the installed plugin (format 5 against the project's format 4) is asking for — /setup refuses to run while a build working file exists.

What the record and the tree show, beyond the two shipped items (each with its own entry today): the crashed session had also started the third item, [0006-side-scrolling-date-picker] — a new DateStrip.kt plus edits to EditTaskScreen.kt and EditTaskViewModel.kt — which its build working file never recorded and never ticked. That partial, unreviewed code is committed here as-is; the item stays in Processed, and the forward advisory warns the next session to look at the code before building the item. None of the run's Kotlin ever compiled: Gradle's client-to-daemon loopback connection fails on this machine for Claude (retried at this close with JAVA_HOME set and --no-watch-fs --no-daemon, same failure), so the first real check of all of it is the next compile in Android Studio.

The wind-down look-back had nothing to scan: this closing session held none of the building session's conversation, so the record is authored from the build working file and the tree alone, and whatever reasoning lived only in that lost conversation is gone.

**Also in this chat:** the session opened into a format-out-of-date halt; /setup was attempted and correctly refused while the build file existed, which is why this close ran first. The stale BUILD-VIEW.md (nothing produces or reads it any more) was left in place pending the user's decision.

**Routed to Captures:** [verify-drawer-swipe-off-on-device] (the shipped drawer change's on-device acceptance checks); the forward advisory [forward-advisory] pointing at [0006-side-scrolling-date-picker]'s partial code.

Advisory: filed — [forward-advisory], advising that [0006-side-scrolling-date-picker]'s partial crashed-session code be inspected before the item is built.
