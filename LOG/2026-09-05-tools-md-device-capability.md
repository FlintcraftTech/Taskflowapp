# [HASH] — TOOLS.md's blanket "cannot compile, test or install" replaced by three accurate lines

Date: 2026-09-05 11:08

TOOLS.md closed with "Claude cannot compile, test or install this app." Only the first of those was
true, and the sentence had been read as all three. That is not a wrong fact — Gradle genuinely dies
from Claude's shell on a loopback-socket error — but a true fact stated far wider than it holds.

The cost was measurable and it is why this item existed. Nineteen work items shipped on 2026-09-02
with UNCONFIRMED ticks against them and the device sat untouched for two days, because the
environment file said device work was impossible. The capability check that `[verify-run-2026-08-31]`
required turned up what nobody had tried: `adb` was present in the Android SDK, a phone was
connected over wireless debugging, and Taskflow was installed on it.

So the correction narrows the claim rather than removing it. Deleting the old line outright was
refused at planning: the Gradle limit is real and load-bearing — it is why builds are the user's.

Files touched:
- `TOOLS.md` — the final bullet replaced by three: Claude cannot **compile**; Claude **can** drive a
  connected device, with the `platform-tools` path recorded because it is not on PATH and so reads
  as absent; and the on-device database's schema version cannot be read from its file header,
  because the database sits almost entirely in a write-ahead log.

Routed to Captures: none.

Tick: done, confirmed — a grep for "cannot compile, test or install" returns nothing and the file
names the platform-tools path.

**The correction proved itself within hours, and two further facts joined it the same day.** Later
in this session the user pressed Run in Android Studio and the app launched, but `dumpsys package`
reported the install timestamp unchanged — Android Studio had built without deploying. Claude then
installed the APK over adb, which is exactly the capability this item made visible, and the install
timestamp moved. Both facts were written into TOOLS.md in the moment: the APK under `intermediates/`
carries the test-only flag so `adb install` needs `-t`, and a launch on the phone is not evidence
that new code is on it.
