# 5fa5c85 — Deleted, premise false on reading the mechanism: the backfill cannot touch a placeholder written in prose, and cannot false-alarm on one either

Recorded 2026-09-06, 00:29.

The capture found the literal placeholder token inside a sentence in `LOG/2026-08-21-setup.md`, in
backticks, in a paragraph describing an earlier session's backfill. Its worry was that the automatic
backfill matches the token mechanically, so a prose mention sits one find-and-replace away from rewriting
a committed record into nonsense. It said honestly that nobody had established whether this was a live
hazard or a dormant one, and that the answer decides everything.

Reading `session_start.py` settles it in the item's favour twice over. The backfill's pattern anchors on
the shape of a whole line — a heading, or an index line's opening dash, each followed by a separator — so
prose cannot match however the token appears in it. And the separate check that hunts for placeholders
written in the *wrong* place excludes any backticked occurrence outright, as prose by definition. Its own
comment names this exact case: prose discussing the token is correct writing and must never match, and
several entries do it, including the one about hash placeholders.

So the sentence is safe from both directions, and the rule the capture quoted — write the token in hash
position only — governs where a real placeholder goes rather than whether prose may name one. There is
nothing to build.

The finding is kept here rather than lost, because the sweep that produced this capture will be run again
by someone else, and the answer should be waiting for them.

**Queue changes:** capture deleted; no file changed.
**Work processed:** deleted — [prose-hash-token-in-setup-entry].
