# [HASH] — /plan [day-begins-at-hour-granularity]: deleted after settling whole hours as deliberate, with SPEC rewritten so it is not re-filed

Settings → Day begins at offers twenty-four whole hours and no minutes, so someone
whose day turns at 4:30 cannot say so. SPEC justified the setting by the person who
stays up past midnight and said it lets the user define their own day boundary — a
claim hour granularity only partly delivered.

Reading the code showed the cost is not a picker swap. The whole hour is in the
data: the preference is stored as `dayBeginsAtHour: Int` and that integer is passed
into `SlotDeriver.logicalDate` and the boundary ticker at roughly ten call sites
across the Schedule, Search, Yesterday and edit view models. Minute precision means
changing the stored value, the deriver's signature and every caller.

Kept as hours, on three grounds: it is a set-once setting and the person SPEC
describes is served by any hour; the cost is out of proportion to a half-hour
preference nobody has asked for; and minute-exact boundaries pull against UX
principle 7, which refuses clock-time precision deliberately.

SPEC §Settings → Day begins at was rewritten to say whole hours and why, so the
next person to notice finds it decided rather than filing it again. The section had
called it "a single time picker", which read as free time selection.

**Queue changes:** deleted after the SPEC edit. [day-begins-at-rollover-still-unrun]
had cited it as a possible future change; that reference now names the decision.

**Work processed:** deleted — [day-begins-at-hour-granularity].
