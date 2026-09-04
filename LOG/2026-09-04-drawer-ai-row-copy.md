# [HASH] — /plan [drawer-ai-row-copy]: the capture's mechanism was wrong, so shortening the row alone would have made the drawer look worse

The user's capture from 2026-09-02: the menu is too fat, and the bottom row should read something like
"Turn on AI". It read as though the long row set the drawer's width.

It does not. `AppDrawer.kt` calls `ModalDrawerSheet(modifier = modifier)` with no width, so the width
comes from Material3's own `DrawerDefaults.MaximumDrawerWidth`, which the app never passes and the text
cannot influence. Asked directly, the user confirmed the drawer was too **wide** and that the row sat on
**one** line filling that width — so the row was making an already-wide sheet visible rather than
causing it.

That is why both halves are one item. Shortening the copy alone would have left a wide sheet with
nothing in it reaching the edge, which is worse than what they complained about. So the sheet is sized
to wrap its longest row plus padding, bounded by the Material default. Choosing a fixed dp value was
refused: it would be an invented number with no derivation, and it would rot the next time a row is
reworded.

The copy change is product truth and the user's own point stands in SPEC: the phrase was doing two jobs,
naming the row and selling the tier, and the screen the row opens is the AI choice flow, which exists to
make that case properly.

One thing carried into the item for the build: `Overlay.TurnOnAi.label` is both the drawer row's text
and the title of the screen it opens, so the two move together — intended, not a side effect.

**Queue changes:** [drawer-ai-row-copy] rewritten from a capture into a build item under the same slug
and moved into Processed, cleared to run. SPEC §Side menu and §Tier model — free and paid both edited in
the same session, the old string appearing in each.

**Work processed:** kept, cleared to run — [drawer-ai-row-copy].
