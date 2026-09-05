# 9b577c4 — The run's twelve builds reach the phone, after Android Studio built without deploying

Walk-through drive record, opened live and appended to as it went.

Slug: install-current-build-on-device
Session: 91c12720-d901-4c60-846f-40c2ba990c64, 2026-09-04
Opened live as the drive started, and appended to as each step happened, so a crash mid-drive
leaves a record of what was actually done rather than nothing.

Driven at the end of a /next run that shipped twelve builds, all ticked UNCONFIRMED — the whole
point of this item is that one press of Run covers every one of them. The item's own ordering note
says exactly that, which is why it was moved to the end of the cleared region at the 2026-09-04
close.

Capability check before handing over, per step: compiling is the one thing Claude genuinely cannot
do on this machine — Gradle dies from Claude's shell on a loopback-socket error, recorded in
TOOLS.md and re-confirmed there this session. No tool available here produces an APK. The steps are
therefore the user's, and everything after the install is Claude's.

## Actions

- 2026-09-04 12:57 — record opened; step 1 given to the user (open the project in Android Studio,
  look for Gradle sync finishing without an error).
- 2026-09-04 15:07 — step 1 FAILED, and the fault was this run's own. Sync reported 10 errors in
  app/build.gradle.kts, all cascading from "Unresolved reference 'util'" at line 11: inside a Gradle
  Kotlin build script `java` resolves to Gradle's Java extension rather than the package, so the
  fully-qualified `java.util.Properties()` written by [project-out-of-drive] does not compile.
  Fixed by importing java.util.Properties at the top of the file and calling Properties() bare, and
  by renaming the local `file` variable inside the apply block so it cannot shadow Project.file.
  The one warning in the same report — srcDirs(vararg) deprecated in favour of a `directories`
  mutable set — was deliberately left alone: the current form works and the replacement was not
  verified against the AGP version in use. Step 1 re-issued.
- 2026-09-04 15:14 — step 1 PASSED. Gradle sync finished in 11 seconds with one warning, the
  srcDirs deprecation deliberately left in place. Step 2 given (connect the phone and pick it in
  the device dropdown).
- 2026-09-04 15:16 — step 2 PASSED, the user's word: their device is showing in the dropdown.
  Step 3 given (press Run).
- 2026-09-04 15:24 — step 3: the user pressed Run and the app launched on the phone. The build
  itself succeeded, and two of this run's items were confirmed by it as a side effect:
  C:\builds\taskflow\app was written at 15:24 with a fresh APK ([project-out-of-drive] works), and
  app/schemas/com.example.taskflow.data.local.TaskflowDatabase/5.json now exists
  ([durable-local-data]'s schema export works). The stale app\build folder from 2026-09-01 was left
  in place, untouched by the relocated build.
- 2026-09-04 15:58 — the item's observable FAILED on first check, with the user's consent to use
  the device. dumpsys reported lastUpdateTime=2026-09-01 16:41 and the running process was that
  old build, against a new APK dated 15:24 and a different size (11,073,366 bytes against the
  installed 11,024,250). So Android Studio compiled but did not deploy to this device. Why is not
  established and was not guessed at — only one device is attached, a Pixel 6 over wireless
  debugging, and the outcome is visible while Android Studio's reason is not.
- 2026-09-04 16:29 — installed by Claude over adb instead, with the user's explicit consent, which
  is the split TOOLS.md now records: Claude cannot produce an APK, but can install one. The first
  attempt failed with INSTALL_FAILED_TEST_ONLY, because the APK under intermediates/ carries the
  test-only flag; `adb install -r -t` succeeded. lastUpdateTime now reads 2026-09-04 16:29, so the
  item's observable is satisfied and the device carries this run's twelve builds.

## Outcome

done — walked to its end this session, and the observable checked rather than assumed.

