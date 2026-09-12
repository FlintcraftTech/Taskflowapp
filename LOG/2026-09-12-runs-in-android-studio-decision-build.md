# [HASH] — /next [runs-in-android-studio-decision]: three standing rules written into CLAUDE.md — compile handovers, an export before instrumented tests, and daily use before publishing

Written 2026-09-12 at 13:04, read from the clock.

Claude's own shell cannot run `gradlew` on this machine, and the route that works is Android Studio's integrated terminal. What settled on 2026-09-07 was what a build does about that. Alex raised the question herself on 2026-09-05, seeing what the capability implied before anyone wrote it down: compiling inside a run is only possible from a session started in that terminal, so having the capability would mean moving whole runs there.

**Refused: moving `/next` runs into that terminal.** A terminal session has none of the file viewer and side panel Alex reads the work through, and she is a no-code developer for whom that surface is not a convenience but how the work is legible at all. **Refused too: a rule naming which items require a compile** — handing the command over works for every item, so nothing has to be classified. Taken instead is a fourth route: stay in the desktop app and hand the compile over as a paste-in step. That is what happened on 2026-09-06, and it is what happened again in this session.

The cost is stated rather than hidden: Alex has to be at the machine when a run wants a compile, so an unattended run still cannot confirm a build. The rule does not remove that limitation; it declines to pay for it with her working surface.

Two further rules were folded into the same file rather than filed separately, because a build touching one paragraph may as well write all three.

The first forbids a connected instrumentation test run on the phone without a JSON export taken first. Such a run installs the app, runs the tests and then uninstalls both, taking the Room database with it, and on AGP 9.2.1 no build setting prevents it. Alex accepted that on 2026-09-06 — but she accepted it when the phone held test data, and her month of daily use changes what is at stake rather than what is true. The rule also requires any queue item whose observation needs such a run to say so in its own text, so an unattended session meets the condition before driving the check rather than improvising at the moment it matters.

The second records the standing ordering instruction: until Alex has had her month of real daily use, the queue is worked for what makes daily use good rather than for what gets the app to the Play Store. The publishing chain is parked rather than abandoned, its blockers sitting in another project waiting on her financial and tax position. It belongs in CLAUDE.md rather than SPEC because it directs how sessions work on the project, not what the product is.

An unverified assumption was disposed of rather than bet on: nobody had established whether a Claude session started in that terminal loads the plugin, the skills and the hooks at all. Under this decision no session runs there, so the question never has to be answered. Recorded here in case the decision is revisited.

**Files touched:** `CLAUDE.md` — three new paragraphs under Project rules.

**Routed to Captures:** none.

**Verification:** done, confirmed. The rules read back in the file after writing, and the three-line compile route the first one hands over was exercised later in this same session, reaching `BUILD SUCCESSFUL`.
