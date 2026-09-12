# d6ac7e8 — /plan [runs-in-android-studio-decision]: sessions stay in the desktop app, and the item became a CLAUDE.md build carrying three rules

Alex raised the underlying question on 2026-09-05: compiling mid-run is only
possible in a Claude session started from Android Studio's terminal, so having that
capability would mean moving whole runs there. Against it: a terminal session has
none of the file viewer and side panel she reads the work through, and she is a
no-code developer for whom that surface is how the work is legible at all. For it:
a compile inside the run is what stops code items shipping ticked UNCONFIRMED.

Refused: moving runs into that terminal, on the legibility ground. Refused: a rule
naming which items require it — handing the command over works for every item, so
nothing has to be classified. What was taken instead is a fourth route nobody had
named: stay in the desktop app and hand the compile over as a paste-in step, which
is exactly what happened on 2026-09-06 and worked. The cost is stated rather than
hidden — Alex has to be at the machine when a run wants a compile, so an unattended
run still cannot confirm a build.

It also disposes of an assumption rather than betting on it: nobody had established
whether a session started in that terminal loads the plugin, the skills and the
hooks at all, and under this decision no session runs there, so the question never
has to be answered.

The item became a build because a planning session may not write CLAUDE.md. Two
further rules were folded in rather than filed separately, both writing the same
file: Alex's intention to use Taskflow daily for about a month before considering
publishing, recorded as the standing ordering instruction; and a rule that no
connected instrumentation run happens on her phone during that month without a JSON
export taken first, with any item whose observation needs such a run saying so in
its own text.

**Queue changes:** kept into Processed, cleared to run; retitled to name both
halves; its stale `Blocked by:` line dropped, its blocker having been answered.

**Work processed:** kept — [runs-in-android-studio-decision].
