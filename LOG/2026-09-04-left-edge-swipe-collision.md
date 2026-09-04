# [HASH] — /plan [left-edge-swipe-collision]: a three-way edge collision shrank to one real defect — the back gesture closes the app from the spine, unhandled

Filed by the user on 2026-09-02 as a collision between Android's back gesture, the spine's own paging
and the drawer's swipe-to-open, and read as something that had to be settled before the pages left of
Today could be built.

It is not. The user's own argument settled it: swiping between pages is one mechanism, so right-swipes
will work exactly as left-swipes already do, and Android's back gesture lives on **both** edges, so the
contested outermost strip has been there all along in the direction they use daily without troubling
them.

Reading the code found what is actually wrong. Taskflow is a single activity, and `AppRoot.kt` enables
its `BackHandler` only while an edit dialogue or a menu overlay is open. On the bare spine back is
unhandled, so an edge back-swipe closes the app. Nobody chose that; it is what falls out of never
having decided.

The user settled the behaviour: back returns to Today, and closes the app only from Today — the standard
Android pattern for an app whose top level is a row of pages. Back stepping one page left was refused,
because after three right-swipes it walks the user through pages they never visited. SPEC §Schedule view
gained the sentence in the same session.

Claiming a gesture-exclusion zone was refused on a checked fact: Android caps exclusions at 200dp per
edge, so an app can only ever take a band, leaving paging to work in part of the edge and back in the
rest — worse than either clean answer.

Not established, and recorded as such: whether the phone in use is on gesture navigation or three-button
navigation. It dropped off wireless debugging mid-check.

**Queue changes:** [left-edge-swipe-collision] rescoped and rewritten under the same slug, kept because
[nav-completed-history] cited it, and moved into Processed cleared to run. Its original text is preserved
beneath the rescoping as the reasoning that produced it.

**Work processed:** kept, cleared to run — [left-edge-swipe-collision].
