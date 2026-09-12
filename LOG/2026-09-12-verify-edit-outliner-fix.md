# d6ac7e8 — /plan [verify-edit-outliner-fix]: filed and cleared — the unverified fix two held items were waiting on had no check of its own

Alex asked, mid-session, whether the app is yet safe to use for real without losing
her tasks. Answering it from the record surfaced a gap nobody had filed: the fix
for subtasks being uncreatable shipped on 2026-09-06, compiles, and has never been
opened on a phone — while two items waited behind it and nothing in the queue named
the verification.

Both of those holds were also pointing at nothing. They named [edit-outliner-missing],
which left the queue when it shipped, so neither could ever have lifted. They were
repointed at this check. For [first-end-to-end-test] that is the more faithful
reading of Alex's own condition, which was that subtasks *work* — shipping alone
does not meet it.

Claude drives it rather than Alex: the capability check found adb sufficient, the
same way a session drove the date-behaviour checks on 2026-09-06 after finding a
`[user]` tag wrong on similar work. What is needed from her is the phone unlocked,
probably more than once, since it re-locks.

Its first step establishes something this session could not: whether the phone is
even carrying the fix. Compiling is not installing, and a session on 2026-09-04
found Android Studio's Run building without deploying. If the install predates the
fix the check stops and hands the build command back, because Claude cannot compile
from its own shell. It leaves no litter — the test task is ticked off rather than
deleted, deleting being the broken gesture.

**Queue changes:** filed into Unprocessed and moved into Processed, cleared to run,
then placed at the end of the cleared region with the other work that stops for the
user.

**Work processed:** kept — [verify-edit-outliner-fix].
