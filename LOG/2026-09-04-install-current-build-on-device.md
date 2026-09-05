# 9e24ba7 — /plan [install-current-build-on-device]: one press of Run, split out so the verification audit measures the code that actually shipped

Created alongside the reshaping of [verify-run-2026-08-31], because that audit can only mean something
against a current build. The device was running a build installed 2026-09-01 at 16:41 while the newest
APK on disk was built 2026-09-02 at 16:26 — after the run's last commit. Installing that existing APK
over adb was considered and refused: nothing here can confirm it is the run's final state, and
verifying against the wrong binary produces confident wrong answers.

It is `[user]` work for exactly one reason, and the reason is recorded rather than assumed: Gradle
cannot run from Claude's shell on this machine, so producing a build is Android Studio's job. Claude
can install an APK; it cannot make one.

Unusually for a `[user]` item it carries an observable Claude can check without asking — the install
timestamp read over adb, compared against the last build. Its walkthrough also warns that the install
may wipe the app's data, since destructive fallback is exactly what [durable-local-data] is queued to
end, so the user meets that as a known bug rather than a fresh fault.

Placed second in the cleared region when written, then moved to the end at this close: eleven builds
now sit ahead of it, so installing early would leave the device stale again by the time anything was
checked. Running it last means one press of Run covers the whole run.

**Queue changes:** [install-current-build-on-device] filed and moved into Processed, cleared to run,
placed at the end of the cleared region with the other `[user]` items.

**Work processed:** kept, cleared to run — [install-current-build-on-device].
