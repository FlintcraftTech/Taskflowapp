# [HASH] — /next [strategy-share-silent-when-empty]: the Strategy empty state now says the doc can be shared once it holds something

Written 2026-09-12 at 13:05, read from the clock.

The 2026-09-06 audit tapped Share twice on a device with no Projects: no share sheet opened, the focused window stayed on Taskflow, and a cleared logcat caught nothing. It read that as a visible control silently doing nothing and left two dispositions open — open the sheet anyway, or disable the button and say why.

**The premise was corrected on 2026-09-12 by reading the source.** The button already carries `enabled = strategySections.isNotEmpty()`, so with no Projects it is genuinely disabled and the tap fired nothing, which is why the logcat was empty. The second disposition was therefore already the implemented behaviour, and a build following the capture as written would have added a guard that exists. What was actually missing is only the visible half: a disabled `TextButton` in a top bar is muted rather than obviously unavailable, and the empty state said nothing about sharing.

So the build is one sentence. The empty state now reads: "Your Projects appear here as headings, with room to write about each one. Make a Project and it shows up. You can share this doc once it has something in it."

**Refused: enabling the button so it shares an empty document.** Handing someone a blank share sheet is a worse answer than a control that is visibly not available yet.

The audit's other half — whether the share sheet opens at all — was unverifiable while this button was the only route to it. It was handed to [strategy-edit-persistence-blocked], which drives the same page on a device, and that audit ran in this session and saw the sheet open.

**Files touched:** `ui/strategy/StrategyScreen.kt` — the empty-state text. Reads but does not change `ui/schedule/ScheduleScreen.kt`, which owns the Share button and its guard; the guard is correct and stays as it is.

**Routed to Captures:** none.

**Verification:** done, UNCONFIRMED — it compiles and the sentence reads back in the file, but the empty state is only visible on a device with no Projects, and the phone has one named Family.
