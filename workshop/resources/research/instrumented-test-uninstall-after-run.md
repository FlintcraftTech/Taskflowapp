# Can the post-test uninstall be turned off? — AGP 9.2.1

Read 2026-09-05, prompted by [instrumentation-tests-uninstall-the-app].

## The question

A connected instrumentation test run installs the app and the test package, runs
the tests, and removes both afterwards — which takes Taskflow's Room database
with it. Can that removal be switched off, so the tests can be run without
losing the device's data?

## What the web says, and why it does not apply here

Every result found says the same thing: set `uninstallAfterTest = false` on the
instrumented test task. That advice dates from Android Gradle Plugin 3.0, where
the property was added to `DeviceProviderInstrumentTestTask`.

**It does not apply to this project.** Taskflow builds on AGP **9.2.1**
(`gradle/libs.versions.toml` line 2), and the string `uninstallAfterTest` does
not occur anywhere in the AGP 9.2.1 jar. The property is gone.

## What AGP 9.2.1 actually has, read out of the shipped jar

The jar inspected:
`~/.gradle/caches/modules-2/files-2.1/com.android.tools.build/gradle/9.2.1/…/gradle-9.2.1.jar`
— the exact artifact this project resolves. Every class was scanned for the
string `uninstall`. Twenty-one classes carry it. The three that matter:

- **`DeviceProviderInstrumentTestTask`** — the task behind `connectedDebugAndroidTest`,
  which is the path Android Studio's "run all tests in androidTest" takes. Its only
  uninstall-related member is `uninstallIncompatibleApks`, which is about replacing
  a mismatched install before the run, not cleaning up after it.
- **`UtpTestRunner` / `UtpTestUtilsKt`** — the Unified Test Platform runner these
  tasks delegate to. This is where the behaviour lives: it carries a parameter
  named `uninstallApksAfterTest`. **Nothing exposes it.** There is no matching
  entry in `BooleanOption` (which is where AGP's `android.*` Gradle properties are
  defined — `uninstallIncompatibleApks` has one, as
  `android.experimental.testOptions.uninstallIncompatibleApks`; this does not), and
  no DSL surface was found for it.
- **`TestSuiteTestTask$ConnectedTestSuiteCreationAction`** — a *different and newer*
  pathway, the `com.android.tools.androidtest:android-test-engine` test-suite
  mechanism. It does carry an option string `android-test.uninstall-after-tests`.
  This is not the task the project's tests ran through, and adopting that pathway
  would be its own piece of work rather than a config line.

## The finding

**On AGP 9.2.1, through the ordinary connected-test path, there is no supported
switch to keep the app installed after an instrumented test run.** The knob
exists inside the test runner; it is not wired to anything a build file can set.

So an export-first habit is the answer for now, not a workaround for a setting
nobody found.

## Limit of this read

The classes were scanned for strings, not decompiled. That establishes that no
public option name exists; it does not prove the internal parameter is hardcoded
to true rather than defaulted from something else. What would settle it beyond
doubt is running the tests once and watching whether the app survives — which is
the very experiment that costs the device's data, so it is not worth doing for
this.

Not attempted, and worth naming: Android Studio's own test *run configuration*
may deploy differently from a command-line Gradle invocation. This read covers
what AGP exposes, not what the IDE does on top of it.

## Frame assessment

- **TIME RANGE** — not applicable in the usual sense; the finding is pinned to one
  AGP version and says so.
- **PEOPLE** — applies to whoever runs Taskflow's instrumentation tests, which
  today is one person on one phone holding the only copy of their real data.
- **FRESHNESS** — amended on a cycle. AGP ships several times a year and this area
  is visibly mid-migration (an old property removed, a new test-suite pathway
  arriving). Re-read on any AGP upgrade.
- **RISK IF WRONG** — if a switch does exist and was missed, the cost is an
  unnecessary export step before each test run: mild friction, no data loss. The
  error in the other direction — assuming the AGP-3 advice works, setting it, and
  trusting it — is what loses data, and this read specifically rules that out.
- **ALTERNATIVES** — two were considered and are named above rather than ruled out:
  adopting the newer `android-test-engine` test-suite pathway, which does expose
  `android-test.uninstall-after-tests`; and checking whether Android Studio's run
  configuration behaves differently from the Gradle task. Neither was researched.
