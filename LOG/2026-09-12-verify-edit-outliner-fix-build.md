# [HASH] — /next [verify-edit-outliner-fix]: the subtask outliner verified on the phone, a clean pass with no findings

Written 2026-09-12 at 13:05, read from the clock.

On 2026-09-06 an audit found that no subtask could be created anywhere in the app: pressing Enter in the edit dialogue appended an empty child line and `Outline.parse` discarded it immediately. [edit-outliner-missing] moved the blank-dropping rule to the save path and compiled, and its own record said the on-device observation was still unrun. Nothing in the queue was tracking that verification while two items waited behind it, which is what this audit closed.

All six steps passed against the build compiled in this same session.

Step 1 established what was on the phone. The installed app predated the session's compile, so a fresh APK was installed first — and **that turned up an environment fact worth more than the step itself**. TOOLS.md recorded the debug APK's path as `<buildDir>/app/intermediates/apk/debug/`, and the copy there was a week old, while `assembleDebug` had written a fresh one to `<buildDir>/app/outputs/apk/debug/`. Installing from the recorded path would have put week-old code on the phone while every check read as a test of the new build. TOOLS.md now says to take the APK from `outputs/` and to check its timestamp against the compile either way.

Steps 2 and 3: pressing Enter at the end of a task's title opened an indented child line, and typing into it landed the text. Step 4: after saving and reopening, the child was still there, nested under its parent. Step 5: on the Today page the parent rendered with an expand control where its checkbox had been, with the child nested beneath it when expanded — SPEC §Parent tasks expand/collapse instead of having a checkbox, seen working. Step 6 left the database as it was found by ticking the subtask off, which completed the parent and sent it to the Completed tray, so completion rolling up from children was observed end to end as a side-effect of the cleanup.

**Files touched:** none — an audit reads. `TOOLS.md` was corrected as described above, which is an environment record rather than a target artifact.

**Routed to Captures:** none.

**Findings routing:** no findings filed, and none dropped. Every step passed, which is itself the finding: the fix [edit-outliner-missing] shipped works on a device, superseding the 2026-09-06 failure.
