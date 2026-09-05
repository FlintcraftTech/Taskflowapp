# 9b577c4 — Gradle's build output moved to a short path outside Drive, read from an optional local.properties entry

Date: 2026-09-05 11:05

The recurring "Unable to delete directory …\app\build" failure had hit twice, and the planning
session that scoped this had already demoted the original Google Drive hypothesis. What replaced it
was measured rather than guessed: sixteen files under `app/build` exceeded Windows' 260-character
path limit, the longest at 279, against a project root path already 99 characters long. "Unable to
delete directory" is what a deletion looks like when it walks a tree and meets files it cannot
address. Neither cause was proven and this change did not need to prove one, because it addresses
both at once — the longest path drops to roughly 190 characters and the folder Gradle rewrites
constantly leaves Drive's sync entirely.

The build directory is set from an optional `buildDir` entry read out of `local.properties`, via
`layout.buildDirectory`. Where the entry is absent the build behaves exactly as before, so a clone
on any other machine is unaffected — which is what let the machine-specific absolute path stay out
of a public repository. Hardcoding it into `app/build.gradle.kts` was refused at planning for that
reason, and moving the whole project out of Drive was refused by the user, whose reason was that
every one of their projects lives under the same parent folder and moving this one alone would cost
them every time they went looking for it.

**One correction during the build, and it broke the first sync.** The properties read was written
as `java.util.Properties()`, fully qualified. Inside a Gradle Kotlin build script `java` resolves to
Gradle's own Java extension rather than the package, so the script failed to compile with ten
cascading errors. Fixed by importing `java.util.Properties` at the top of the file and calling
`Properties()` bare, and by renaming a local `file` variable inside the apply block so it could not
shadow `Project.file`. A deprecation warning on `srcDirs(vararg)` was deliberately left alone: the
current form works, and the suggested replacement was not verified against the AGP version in use.

Files touched:
- `app/build.gradle.kts` — the optional `buildDir` read setting `layout.buildDirectory`, above the
  android block, plus the import correction
- `local.properties` — `buildDir=C:\builds\taskflow\app`, with a comment recording why

Routed to Captures: `[stale-app-build-folder]` — relocating where Gradle writes does not remove
what it wrote before, so the 2026-09-01 `app/build` folder is still in the project, around 59 MB
that Drive keeps syncing for nothing. Filed by the /rescan at the end of this session.

Tick: done, confirmed at 15:24 by the build the `[install-current-build-on-device]` walk-through
produced — `C:\builds\taskflow\app` holds the output and the APK. Two parts of the item's own
observation are NOT confirmed: the stale `app\build` is still present, and whether the delete
failure recurs needs more builds than one.
