# [HASH] — /plan [tools-md-device-capability]: TOOLS.md's "cannot compile, test or install" narrowed to the one limb that is true

Raised by Claude during the reshaping of [verify-run-2026-08-31] and processed in the same session on
the user's word. TOOLS.md's closing line read "Claude cannot compile, test or install this app". Only
the compile limb holds: Gradle dies from Claude's shell on a loopback-socket error, which is well
recorded above it. Testing is not blocked — a connected device answers adb. Installing is not blocked
either; what Claude cannot do is produce the APK to install.

The cost of the over-broad sentence is on the record rather than hypothetical: nineteen items shipped
on 2026-09-02 with UNCONFIRMED ticks against them and nobody tried the device for two days, because
the environment file said device work was impossible. Not a wrong fact — a true fact stated far wider
than it holds.

Deleting the line outright was refused. The Gradle limit is real and load-bearing, since it is why
builds are the user's, so the correction narrows the claim rather than removing it. A third bullet
records a dead end found the same day: the on-device database sits almost entirely in a write-ahead
log, so its schema version cannot be read from the file header without copying the database off the
phone, which carries real task content.

**Queue changes:** [tools-md-device-capability] filed and moved into Processed, cleared to run, placed
with the build work ahead of the `[user]` items.

**Work processed:** kept, cleared to run — [tools-md-device-capability].
