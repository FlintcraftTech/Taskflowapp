# [HASH] — /plan [emulator-for-instrumented-tests]: filed and cleared — a virtual device so the app's tests stop being a threat to Alex's real tasks

Came out of the session's own look-back. A connected instrumentation test run
installs the app, runs the tests and then uninstalls both, taking the Room database
with it, and on AGP 9.2.1 no build setting prevents that. Alex accepted that risk
on 2026-09-06 — but she accepted it when the phone held test data, and her stated
intention to use Taskflow daily for about a month changes what is at stake rather
than what is true.

The collision is concrete rather than theoretical: [rotating-roster-recurrence],
cleared to run in this same session, names a 5 → 6 migration test as part of its
proof. Running it during her month would wipe her tasks. That was named to her at
the moment it was noticed, which is what gave her the chance to direct the fix
rather than have the item built as it stood.

Two halves, and they are not alternatives. The immediate one is a CLAUDE.md rule
requiring an export before any such run, folded into
[runs-in-android-studio-decision]; the item itself also gained a line saying so, so
an unattended run meets the condition rather than reading past it. This item is the
durable half — tests run on a virtual device instead of her phone.

It is `[user]` work because nothing here can do it, established by looking rather
than assuming: `emulator.exe` exists under the SDK, but `emulator -list-avds`
returns nothing, no `avd` folder exists under either user profile, there is no
`system-images` directory and `cmdline-tools` is not installed. So there is no
image to build a device from and no command-line route to fetch one — it is Android
Studio's Device Manager, and the download is hers to start.

**Queue changes:** filed into Unprocessed and moved into Processed, cleared to run,
placed at the end of the cleared region.

**Work processed:** kept — [emulator-for-instrumented-tests].
