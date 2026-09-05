# [HASH] — The side menu's AI row becomes "Turn on AI", and the drawer is sized to its own contents

Date: 2026-09-05 11:15

The user captured this on 2026-09-02 on seeing the drawer on the device: the menu was too fat, and
the bottom row should read something like "Turn on AI".

**The capture's mechanism was wrong, and the correction is why both halves shipped as one item.** It
read as though the long row set the drawer's width. It does not — `AppDrawer` passed
`ModalDrawerSheet` no width at all, so the width came from Material3's own default, which the app
never set and the text could not influence. The user confirmed on 2026-09-03 that the drawer was too
wide *and* the row sat on one line filling that width, so the row was making an already-wide sheet
visible rather than causing it. Shortening the copy alone would therefore have made it worse: a wide
sheet with nothing in it reaching the edge.

So the sheet is sized to wrap its own longest row, bounded by the Material default as a maximum.
Choosing a fixed dp value was refused: it would be an invented number with no derivation, and it
would rot the next time a row is reworded.

The copy change is product truth and SPEC was edited during the planning session that scoped this,
in §Side menu and §Tier model. The user's own point is recorded there: the phrase was doing two
jobs, naming the row and selling the tier, and the screen the row opens is the AI choice flow, which
exists to make that case properly. One thing to watch is intended rather than accidental —
`Overlay.TurnOnAi.label` is both the row's text and the title of the screen it opens, so the two
move together.

Files touched:
- `ui/navigation/Destination.kt` — the label shortened, with a comment recording why
- `ui/navigation/AppDrawer.kt` — the row's text, and a content-wrapping width bounded by the
  Material default
- `ui/navigation/AppRoot.kt` — the comment quoting the old string updated

Routed to Captures: none.

Tick: done, UNCONFIRMED — needs a device to confirm the bottom row reads "Turn on AI", the sheet's
right edge sits just past its longest row, every row still opens what it opened, and the screen that
row opens carries the new title. **One thing to watch at that check:** the width uses
`Modifier.width(IntrinsicSize.Max)` bounded by `DrawerDefaults.MaximumDrawerWidth`, and the sheet's
navigation list is vertically scrollable. A vertical scroll modifier governs height measurement
rather than width, so a width intrinsic should pass through it — but that reasoning was never
compiled at the time it was written.
