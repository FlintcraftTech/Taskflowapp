# [HASH] — /next [strategy-edit-persistence-blocked]: a Strategy paragraph survives a relaunch and the share sheet opens, with one finding on text entry

Written 2026-09-12 at 13:05, read from the clock.

Nothing had ever checked that what a user writes under a Project heading in the Strategy doc is kept. The check was captured on 2026-09-06 but could not run: the database held no Projects, so the doc showed its empty state and there was no paragraph to type into. A Project named Family was created later that day, so a heading with an empty paragraph existed and the check was simply available. Steps 4 and 5 were added on 2026-09-12 to carry the other half of that audit — whether the share sheet opens at all — which [strategy-share-silent-when-empty] could not answer while a disabled button was the only route to it.

The substantive answers are both yes. Text typed under the Family heading was still there, unchanged, after a force-stop and relaunch. Tapping Share opened Android's own chooser — confirmed by `dumpsys window` showing `com.android.intentresolver.ChooserActivityLauncher` focused — and backing out left the page as it was with nothing sent. The box was then cleared and read back empty, leaving the doc as it was found.

**The one finding is about getting text in, not keeping it.** Sending a whole sentence in one `adb shell input text` left four characters in the box. Retyping character by character at a 0.4-second gap landed all six letters of a test word correctly. A third attempt at 0.35 seconds produced a *substituted* character — an `l` arriving as an `m`.

That last observation is why this is filed rather than shrugged off. TOOLS.md already records that `adb shell input text` can fail silently into a dialogue field, so dropping alone would prove nothing about the app. A substitution at a spacing that otherwise worked is not what a dropped keystroke looks like. And this field is architecturally unlike the others: the edit dialogue's outliner holds its text in a local form and writes once on Save, while this paragraph calls `setDescription` on every keystroke, which does a repository read, an upsert and a flow emission that recomposes the field. A value racing its own round trip would corrupt and drop characters in exactly this shape.

**Not established, and the capture says so:** whether a person typing at human speed hits it. Nobody has tried. The cheapest next step is reading the source for that round trip rather than more device work.

The 2026-09-06 lesson in TOOLS.md — that three failures of the same tool are evidence about the thing being driven just as readily as about the driver — is what stopped this being written off as an adb artifact, and it is also what stopped it being declared an app defect outright.

**Files touched:** none — an audit reads. `TOOLS.md` gained a line on typing into app text fields over adb, and on a tap not always placing the cursor where the text ends.

**Routed to Captures:** [strategy-paragraph-input-loses-characters].

**Findings routing:** one finding filed as a capture; none dropped.
