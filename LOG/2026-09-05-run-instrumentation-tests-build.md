# [user] Run the instrumentation tests, MigrationTest first — 2026-09-05 (walk-through)

The live drive of [run-instrumentation-tests] during a /next run. The item's planning record is
`2026-09-05-run-instrumentation-tests.md`; this is what happened when it was actually walked.

**Why this is the user's.** Gradle will not run from Claude's shell on this machine — re-tested 2026-09-05
and still failing with `Unable to establish loopback connection`, unchanged by moving the build output out
of Google Drive. Compiling is Android Studio's job and Android Studio is the user's. Claude drives the
device over adb and reads results back, but cannot start the run.

## Steps as driven

1. Open the project in Android Studio and connect the phone. **Done** — the user confirmed the device
   appears in the dropdown at the top of the window.
2. A Gradle sync was needed first — the IDE was showing "Gradle files have changed since last project
   sync", left over from [project-out-of-drive]'s `build.gradle.kts` change. Done through File → Sync
   Project with Gradle Files.
3. First run attempt: **the tests did not run, because they did not compile.** `TaskDaoTest.kt` called
   `taskDao.updateCompletion(taskId, isCompleted)` at three places, while the DAO method has taken a
   third argument, `completedAt`, since completion timestamps were added. Three errors, all the same.
   The app's own code compiled cleanly — only the test source set was broken. So these tests had been
   uncompilable for some time and nothing noticed, because nobody had ever run them: the very gap this
   item exists to close, found by closing it.
4. Fix made in this run with the user's agreement, adding `app/.../TaskDaoTest.kt` to the run's scope:
   the three call sites now pass `completedAt = System.currentTimeMillis()`.
5. Second run: **20 tests, 0 failures, 0 errors, 1.633s on the Pixel 6.** `MigrationTest ::
   version5DataSurvivesCloseAndReopen` among them — so the version-5 schema floor [durable-local-data]
   established is now demonstrated rather than asserted. The rest: `ExampleInstrumentedTest` (1),
   `ProjectDaoTest` (6), `TaskDaoTest` (12).

## What the walk-through settled beyond the item

**The results path was a prediction and is now a fact.** The item wrote its observable "as expected
rather than established", because these tests had never run and nothing proved Android Studio would put
results where Claude could read them. It does:
`C:\builds\taskflow\app\outputs\androidTest-results\connected\debug\TEST-<device>-_app-.xml`, a JUnit XML
Claude read directly, with per-test logcat files beside it. The step-4 fallback — the user reading the
panel aloud — was not needed and can be dropped if this item ever recurs.

**A detour the user raised mid-drive: the Claude Code plugin for Android Studio.** Researched and filed
at `workshop/resources/research/claude-code-jetbrains-plugin-capabilities.md`. It exposes no
code-execution tool, so it does not make build or test work Claude's; what it does add is that Claude can
read the IDE's inspection diagnostics, which is exactly the class of failure that stopped step 3. Getting
it working needed the CLI's full path in the plugin's Claude command setting, quoted with PowerShell's
call operator because the user's profile name contains a space:
`& "C:\Users\<user>\.local\bin\claude.exe"`. Two wrong paths were handed over before one was verified by
running it — the correction that the verify-before-handing-over rule exists for.

**Outcome: done.** Walked to its end this session.

