# [HASH] — /plan [remove-thanks-screen]: a screen nobody could explain turned out to be a leftover of the donation funding model, and was dropped from SPEC and queued out of the app

**Kind: processed.** Recorded 2026-09-17 at 16:01, read from the clock, during a planning session.

Taskflow's side menu carried a row labelled Thanks. Asked what that screen should say, nothing in the
project could answer: SPEC listed the row and no more, and the archived original spec for the
bottom-of-drawer screens named "Thanks" in its title and nowhere in its body. So the first question put was
not what it should say but whether it should exist.

Alex supplied what no document held: the screen dates from an earlier plan to fund Taskflow through "buy
me a coffee" style donations, replaced since by the paid subscription tier. Its premise went with that
change, and the only trace it left was a row nobody could explain. That is the reasoning this entry
carries; [0022-help-thanks-report-a-bug-content] and [payment-model-revisit] cite it rather than restating
it.

What this makes visible is worth keeping: a feature can outlive the decision it belonged to without
anything recording the connection, and the symptom is not a contradiction but an unanswerable question. The
question "what should this say?" had been travelling in [help-thanks-report-content] as though it were
merely undecided, and was set aside three times on that reading.

Refused: keeping the row and writing something for it. Taskflow's design instinct is to refuse anything
that does not earn its place, and a screen whose purpose nobody can state does not.

The removal itself is queued rather than done here: SPEC is planning's to write, the app is not.
`INBOX/sent.md` was grepped for the repealed behaviour and carries no mention of the Thanks screen, so
nothing announced publicly needs correcting.

**Queue changes:** new work item [remove-thanks-screen], cleared to run — deletes the drawer row, its
destination declaration and a stale comment, across three files in `ui/navigation/` and `ui/common/`.
SPEC §Side menu and §Settings both rewritten to drop Thanks from the app-actions list.

**Work processed:** kept — [remove-thanks-screen].
