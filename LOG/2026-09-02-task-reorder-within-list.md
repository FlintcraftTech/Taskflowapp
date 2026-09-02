# [HASH] — build [task-reorder-within-list]: one drag primitive written across the flat slots and the Later cards

SPEC §Reorder within a Schedule slot asks for within-list drag-reorder on two surfaces — the flat Schedule slots, and the task list inside each Later Project card. The data layer already persisted both orders; what was missing was the drag itself, with no pattern anywhere in the app to copy and no library. The item was explicit that the two surfaces share one primitive, built with the nested case in hand from the start, so flat-list assumptions do not get baked in and force a rewrite.

**The alternative that lost.** The obvious build is a `LazyColumn`-based drag, which is what most Compose reorderable lists are. It was rejected for a plain measured `Column`. A lazy list inside a Later card sits within the page's own vertical scroll with no bounded height to measure against, and its own scrolling fights both the page and the drag — precisely the nested case the item said to design against. The cost is named rather than hidden: the flat Schedule slots lose lazy row recycling, which a day's worth of tasks does not need.

Three properties came from taking the nested case first. Nothing assumes a fixed row height — each row reports its measured height as it lays out, and the drag walks those actual heights, so a wrapped two-line title behaves like any other row. Reordering is shown live against a local copy and committed once on release, because calling through to the database on every crossing would round-trip mid-gesture and fight the finger. And the gesture's horizontal component is handed to the caller rather than consumed, which is what let [0008-drag-task-between-schedule-screens] compose with this rather than needing a second, competing gesture.

Persisting rewrites the whole visible sequence to 0..n-1 rather than nudging the moved row. Sort positions drift as tasks arrive, leave and are pulled into Today from other slots, so writing the sequence outright is what makes the order the user just set the order they get.

**Files touched:** app/src/main/java/com/example/taskflow/ui/common/Reorderable.kt (new — long-press pick-up, live rearrangement against measured heights, commit-on-release, horizontal-drag hook), ui/schedule/SlotPage.kt (active list rebuilt on ReorderableColumn; rows lift while dragging; the completed tray left unreorderable), ui/schedule/LaterPage.kt (the same within a card, with the peek as the reorderable window), ui/schedule/ScheduleViewModel.kt (reorderSlot and reorderCard), ui/schedule/ScheduleScreen.kt (both callbacks passed through).

**Routed to Captures:** none.

**Tick:** done, UNCONFIRMED: needs an Android Studio build and the device check (drag a task within Today/Tomorrow/Soon to a new position, order persists across navigation and relaunch; same within a Later Project card).
