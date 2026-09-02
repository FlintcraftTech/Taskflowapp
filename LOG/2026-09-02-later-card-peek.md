# [HASH] — build [later-card-peek]: Later cards open showing their first three tasks instead of collapsed

SPEC §Schedule view says each Later Project card opens showing its first ~3 tasks, with the rest behind the expand/collapse control — the small peek is what keeps Later a calm overview of the user's areas of life rather than a wall of tasks. The cards had been built collapsed to their headers, showing nothing until tapped, so the page opened as a list of names with no sense of what was in them.

The card body now always renders, showing the first three tasks by default and all of them when expanded. Two smaller decisions fell out of that. The chevron gained its own click target rather than sharing the header's, because SPEC keeps expand/collapse distinct from the header — which mattered more than it seemed at the time, since [execute-by-task-area] later gave the header its own job. And a card holding no more than the peek shows no chevron at all: there is nothing hidden to reveal, so the control would be a promise of more that does not exist.

The nested `LazyColumn` inside the card was replaced by a plain `Column`. A lazy list sits inside the page's own vertical scroll with no bounded height to measure itself against, and a peek is a few rows — laziness bought nothing and cost the nesting behaviour.

**Files touched:** app/src/main/java/com/example/taskflow/ui/schedule/LaterPage.kt — peek rendering, chevron as its own control hidden when nothing is hidden, nested LazyColumn replaced by a Column.

**Routed to Captures:** none.

**Tick:** done, UNCONFIRMED: needs an Android Studio build and the device check (a card with more than 3 opens showing 3, the rest on expand; a card with 3 or fewer shows all; collapse and expand still work).
