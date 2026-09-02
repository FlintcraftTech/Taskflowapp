# [HASH] — build [empty-state-copy-and-visuals]: four empty states written together, including the card that is empty for the wrong reason

The item had waited deliberately: empty-state copy is best written in front of the real screens, and three of the four states only got real screens once Later became Project-grouped. That reason was recorded as expired in planning on 2026-08-25, which is what released it.

All four strings live together in the app's string resources rather than inline, so they read as one voice. The tone follows UX principle 4 — an empty list is a normal state, not a failure — so nothing chides the user or urges them to add anything, and the visual is a quiet mark rather than an illustration: something to rest the eye on that does not read as an error or as a prompt to act.

**The fourth state is the interesting one, and its design was settled with the user on 2026-08-25.** A Later card can read empty for two different reasons: the Project genuinely has nothing in it, or everything in it is dated for the next few days and therefore living on the Schedule instead. "Nothing in this Project yet" is simply false in the second case — and it was observed being false on a real device on 2026-06-24, where a Project created from a Tomorrow-dated task showed an empty card while the task sat correctly on Tomorrow.

The fix is **distinguishing copy, not a counter on every card**, which is the user's call on the recommendation. The rejected alternative was surfacing a near-term task count on every card, empty or not: it changes what every card shows, against SPEC §Schedule view's statement that the small peek is what keeps Later a calm overview rather than a wall of tasks. The copy fix costs nothing when a card is not in that state, because the sentence only appears when it is. The count it needs is computed while the Schedule buckets tasks anyway, and is read by nothing else.

No SPEC edit: SPEC already says every Project appears on Later even with nothing in it, and the wording of a message is not product truth.

**Files touched:** app/src/main/res/values/strings.xml (all four states, the on-schedule case as a plural), app/src/main/java/com/example/taskflow/ui/schedule/SlotPage.kt (per-slot empty state), ui/schedule/LaterPage.kt (the two empty-card cases and the no-Projects-yet line above the pinned Unassigned card), ui/schedule/ScheduleViewModel.kt (nearTermTaskCount per card, read only by this copy).

**Routed to Captures:** none.

**Tick:** done, UNCONFIRMED: needs an Android Studio build and the device check (an empty Today shows the slot empty state rather than a blank page; a Project with nothing in it shows the plain message; a Project whose only tasks are dated today or tomorrow shows the card message naming how many are on the schedule; a fresh install with no user Projects shows the Later message with only the Unassigned card).
