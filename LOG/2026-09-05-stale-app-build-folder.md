# [HASH] — /plan [stale-app-build-folder]: kept standalone and placed first, with the deletion's claims checked before clearing it

Session date and time: 2026-09-05, afternoon planning run.

The capture proposed folding this into another piece of work because it is small. Refused: the
candidates are unrelated, and folding a folder deletion into the LOG index script or the Strategy header
fix makes both harder to read for no gain. It is one deletion a run disposes of in seconds, so it was
placed first in the cleared region instead — which is what "small" actually argues for.

An item that clears a deletion has claims worth testing rather than trusting, so they were tested. The
folder is still present at 53 MB, last written 2026-09-01 — the capture said 59 MB, which is the same
fact measured on a different day. `git check-ignore` confirms `app/.gitignore` ignores it and git tracks
nothing inside it, so the deletion loses nothing git could not already not restore, and the compiled
output and APK live at `C:\builds\taskflow\app` since [project-out-of-drive], so no build depends on it.
TOOLS.md separately records that deleting this folder from Claude's shell is what clears the Windows
file-lock failure, so the route is one already exercised.

Worth keeping about why it exists at all: [project-out-of-drive] pointed Gradle's output elsewhere, and
relocating where a tool writes says nothing about the folder it used to write. That item's own
observation expected `app\build` to be gone or empty, recorded that it was neither, and this is the
other half.

**Queue changes:** [stale-app-build-folder] rewritten with its file list and moved to Processed, cleared
to run, at the top.

**Work processed:** kept — [stale-app-build-folder].
