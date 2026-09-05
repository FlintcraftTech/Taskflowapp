# 9b577c4 — Date strip: a week step beside the month step, day number over month name, and no clipped tile

Date: 2026-09-05 11:14

Three changes to the date picker's side-scrolling strip, all captured by the user on 2026-09-02 from
the strip on their own device. Two were their proposals; the clipped tile is what the screenshot
showed on its own.

**The jump control gains a week step.** It was already a labelled navigator — `‹ month`, the month
in view, `month ›` — so the label was worth keeping. It becomes `‹‹ ‹ Sep 2026 › ››`, single
chevrons moving a week and double a month. The user's reasoning is the stronger argument and is
recorded as theirs: the control's unit should match the view's unit, and the strip shows about a
week, so a month jump lands them somewhere they have to re-read. Swapping month for week outright
was refused — their own capture named the problem with it, that crossing to next April by week is
many presses, so the coarse step is kept rather than dropped.

**Tiles show the day number over the month name.** They read "24/08 25/08 26/08" in a row, which
runs together into a continuous line of digits with nothing for the eye to catch on. Each tile is
now 24 with Aug beneath it: one large glanceable number, and the month no longer repeated seven
times in a form that looks like part of the number. This one reached SPEC during planning and was
settled deliberately rather than patched — §Date picker said tiles showed DD/MM while §Settings →
Date format said that setting applied "everywhere a date is shown", and a tile reading 24 above Aug
has no day/month order left to obey. The resolution written into SPEC: the setting governs dates
rendered as numbers and does not reach the tiles. Making the strip a silent exception to
"everywhere" was refused, because the word was doing real work and quietly breaking it would leave
two sections contradicting each other for the next reader.

**No tile is clipped.** `TILE_WIDTH` was fixed, commented as fitting five to seven tiles on a
typical phone; on the user's device seven nearly fit and the seventh rendered as a cut-off sliver.
Tiles are now sized from the width actually available so a whole number of them fills the row. A
tile showing half a date is worse than one fewer tile.

**One consequence handled during the build.** The tiles stopping at the date-format setting left
`DateStrip`'s `dateFormatter` parameter dead. Rather than leave a parameter nothing reads, it was
removed along with its call site in `EditTaskScreen`'s `DateField` — a consequence of the described
change rather than new work. `EditUiState.datePattern` stays, since it still governs dates rendered
as numbers, and its comment now says so.

Files touched:
- `ui/edit/DateStrip.kt` — week and month steps, day-over-month tiles, computed tile width,
  `dateFormatter` removed, tile height raised for the third line
- `ui/edit/EditTaskScreen.kt` — `DateField`'s `datePattern` parameter and formatter removed
- `ui/edit/EditTaskViewModel.kt` — `datePattern`'s comment rewritten

Routed to Captures: none.

Tick: done, UNCONFIRMED — needs a device to confirm tiles read a day number over a month name with
none clipped at the right edge, single chevrons move seven days and double chevrons a month, the
month label still names the month in view, and switching DD/MM to MM/DD changes task rows while
leaving the tiles unchanged.
