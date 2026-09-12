# [HASH] — /plan [two-cleared-items-underspecified]: deleted after designing [project-reorder-strategy] out — its sibling's design waits on the drag fix instead

Two items sat cleared to run that no build could follow, because neither named
files or said what changes inside them. A run on 2026-09-06 reached them, stopped,
and Alex dropped both — but nothing recorded why, so the next run would have
stopped in the same place.

Half of it was gone before this item came up: [project-delete-later] was re-held
below the line while processing
[project-delete-later-premise-may-be-gone], so no run reaches it. What was left was
[project-reorder-strategy], and designing it is planning work rather than a build,
since writing a queue entry's own instructions is not something a run may do.

The design turned out to be wiring rather than invention. `Project` already carries
a `sortOrder` column, `ProjectRepository.updateSortOrder` already writes it, and
`getAllOrdered` is what both the Strategy doc and the Later cards read — so making
the doc write that order gives the card reordering for free, exactly as SPEC says
it should. `ReorderableColumn` already exists and is used by the Schedule pages.
Nothing in the app calls `updateSortOrder` at all today, which is the whole gap.

The drag handle is the heading alone, settled with Alex. Refused: making the whole
section draggable — a bigger target, but a long-press meant to select a word in
the paragraph would start a drag instead.

**Queue changes:** deleted, content relocated into both items it named —
[project-reorder-strategy] gained a full design, a two-file list and its
observation; [project-delete-later] gained a note that its own design comes after
its blocker ships.

**Work processed:** deleted — [two-cleared-items-underspecified].
