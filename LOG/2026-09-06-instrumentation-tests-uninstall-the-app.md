# [HASH] — Red flag cleared by informed acceptance: no AGP setting stops a test run uninstalling the app, so the answer is export-first written into TOOLS.md

Recorded 2026-09-06, 00:29.

The capture said the instrumentation test run had probably removed Taskflow from the phone, taking the
Room database with it, and left the preventability question open — naming it as the thing that decides
whether an export-first ritual is the answer or a workaround for a setting nobody looked for.

The research settled it, and against the published advice. Every result says to set
`uninstallAfterTest = false`; that property was added in AGP 3 and does not occur anywhere in the AGP
9.2.1 jar this project resolves, which was read directly rather than trusted from documentation. What
9.2.1 has instead is an internal `uninstallApksAfterTest` parameter inside the Unified Test Platform
runner, with no `BooleanOption` and no DSL exposing it. A newer `android-test-engine` test-suite pathway
does carry an `android-test.uninstall-after-tests` option, but that is a different mechanism and adopting
it would be its own work. Filed as
`workshop/resources/research/instrumented-test-uninstall-after-run.md`, with the limit written in: the
classes were scanned for option names rather than decompiled.

**The red flag is cleared by informed acceptance rather than by a fix**, and the consent trail is in the
item. Alex was told plainly that a connected test run removes the app and its database, that no setting on
this AGP prevents it, that a JSON export beforehand is the protection, and that Android Auto Backup is a
second net which worked once unplanned and has never been deliberately tested. She chose to go ahead on
that basis.

**Where the warning goes changed mid-decision, and the correction is the useful part.** The capture
proposed adding it to [run-instrumentation-tests]'s walkthrough. That item had been walked to done hours
earlier and leaves the queue at this close, so a warning written there would have been deleted the same
day. TOOLS.md is read by every session before it touches the device, so the warning goes there instead —
which turned the item from a walkthrough edit into a build.

At the wind-down rescan the item was widened again, to carry three fact lines rather than one: the two
further device facts found while driving the phone this session belong in the same file, and filing them
separately would have meant two builds colliding on TOOLS.md.

**Queue changes:** capture rewritten as a build item, moved into Processed cleared to run, red-flag marker
set to cleared, heading changed from the uninstall to the wider TOOLS.md subject.
**Work processed:** kept — [instrumentation-tests-uninstall-the-app].
