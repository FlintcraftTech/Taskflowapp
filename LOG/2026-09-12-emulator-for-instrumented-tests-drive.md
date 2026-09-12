# [HASH] — /next [emulator-for-instrumented-tests]: the AVD Pixel_6 created and booting, so instrumented tests stop running on Alex's phone

**Outcome: done.** Walked to its end in this session and confirmed by its own observable rather than by asking.

Opened live as the walkthrough started, and appended to as each step happened.

- 12:41, read from the clock — drive started. The observable was checked first
  rather than asking: `emulator -list-avds` from the SDK's emulator folder printed
  nothing, so no virtual device exists yet and the item is genuinely unrun.
- The Device Manager opened showing only the physical Pixel 6 (API 37, arm64) and
  nothing under Virtual, as the walkthrough expected.
- **The walkthrough's step 2 was wrong about this release and was corrected while
  driving.** The `+` button opens a short menu — Create Virtual Device, Select
  Remote Device — rather than going straight to the hardware picker. The item's
  rests-on line had warned that the Device Manager's wording is amended between
  releases, so this was read against the screen rather than insisted on.
- **Steps 3 and 4 are one page in this release, not two.** Choosing Pixel 6 opened a
  combined "Configure virtual device" page that had already selected API 37.1
  ("CinnamonBun"), Google APIs, x86_64, with a 2.0 GB download. That is far above the
  app's `minSdk = 26`, read from app/build.gradle.kts, so nothing needed changing and
  Finish started the download.
- The image installed and the device booted to the Android home screen.
- Observable met, checked rather than asked: `emulator -list-avds` prints `Pixel_6`,
  and `adb devices` lists `emulator-5554` alongside the physical phone.

**What this changes.** Instrumented tests now have somewhere to run that is not the
phone holding Alex's real tasks. The hazard it removes is the one in TOOLS.md: a
connected test run installs the app, runs the tests, then uninstalls both, taking the
Room database with it. The CLAUDE.md rule requiring a JSON export first still stands
and still applies whenever the phone is the target.

