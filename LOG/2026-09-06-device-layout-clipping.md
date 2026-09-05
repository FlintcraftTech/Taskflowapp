# [HASH] — Kept, with the clipping's axis corrected: the tile's height is fixed, which is why the width fix could not have caught it

Recorded 2026-09-06, 00:29.

Two layout defects the 2026-09-05 audit photographed, filed together because both are text in a box too
small for it — the date-strip tiles cutting the month name, and the focused Project's name drawn over the
spine header's right chevron.

**The clipping was confirmed again at this decision step**, on the build now on the phone, so it survived
the 2026-09-04 date-strip work — and confirming it located the cause. [date-strip-legibility] stopped
clipping by computing tile **width** from available space, which its own record states. `DateStrip.kt`
still sizes each tile with a fixed `TILE_HEIGHT`, and the month row is what overflows that: the letters
are cut through along their lower half rather than truncated at the end. The capture's instinct — a box
too small for what is inside it — is right; the axis it implies is wrong, and that is why the earlier fix
could not have reached this.

So the item refuses raising `TILE_HEIGHT` to a larger fixed value, on the ground an invented dp value was
refused during [drawer-ai-row-copy]: a bare number has no derivation. The height follows the content
instead.

**The header overlap was not re-checked, and the item says so** rather than implying a fresh look. The
database held no Projects at all this session, so there was nothing to focus. It stands on the audit's
evidence, which recorded the exact rendered string on the older build — and the item's observation names
that a Project must exist for the check to run at all.

**Queue changes:** capture kept into Processed cleared to run, with Files, Observation, Refused and
Rests-on lines written.
**Work processed:** kept — [device-layout-clipping].
