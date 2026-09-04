# [HASH] — /plan [date-strip-legibility]: week jump, day-over-month tiles, no clipped tile — and the date-format setting's reach settled rather than quietly broken

The user's capture from 2026-09-02, from the date picker on the device: two proposals of theirs, plus a
clipped tile the screenshot showed on its own. All three live in one file, so it stayed one item.

**The jump control.** Reading the code changed the shape of the fix: it is not a bare button but a row
of `‹ month`, the month in view, and `month ›`, so the label is worth keeping. Single chevrons now move
a week and double chevrons a month. The user's reasoning carried it — the control's unit should match
the view's unit, since the strip shows about a week and a month jump lands them somewhere they must
re-read. Swapping month for week outright was refused on their own capture's point: crossing to next
April by week is many presses.

**The tiles.** Day number above the month name, so 24 over Aug rather than a row of digits running
together. This one reached SPEC in a way worth settling deliberately: §Date picker said each tile
showed DD/MM "or MM/DD per the user's setting", while §Settings → Date format said that setting applied
"everywhere a date is shown", and a tile reading 24 over Aug has no day/month order left to obey. The
resolution written into SPEC is that the setting governs dates rendered as numbers and does not reach
the tiles, because it exists to disambiguate digits and a month name leaves nothing ambiguous. Making
the strip a silent exception to "everywhere" was refused — the word was doing real work, and breaking it
quietly would leave two sections contradicting each other for the next reader.

**The clipped tile.** `TILE_WIDTH` is fixed, with a comment recording that it was sized so five to seven
fit and a 360dp screen fits six. On this device seven nearly fit and the seventh renders as "Su" over
"30/" cut off at the edge. Tiles are sized instead so a whole number fills the width available. No SPEC
change: SPEC never named a tile count.

**Queue changes:** [date-strip-legibility] rewritten from a capture into a build item under the same
slug and moved into Processed, cleared to run. SPEC §Date picker and §Settings → Date format both edited
in the same session.

**Work processed:** kept, cleared to run — [date-strip-legibility].
