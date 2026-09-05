# 9e24ba7 — /plan [project-out-of-drive]: the Google Drive hypothesis demoted after the user said other projects don't do this, and sixteen build files found over Windows' path limit

The capture proposed moving the project out of `My Drive` to end the recurring "Unable to delete
directory …\app\build\…" failure. The user rejected the premise rather than the fix: this never happens
in their other Android Studio projects, which Drive alone does not explain.

Measuring found something that does. Sixteen files under `app/build` exceed Windows' 260-character path
limit, the longest at 279, because the project's folder path is 99 characters before the project name
begins. "Unable to delete directory" is what a deletion looks like when it walks a tree and meets files
it cannot address. One complication is recorded rather than smoothed over: `LongPathsEnabled` is `1` on
this machine, which should lift that limit, and whether Gradle's Java processes honour it was not
established and is not guessed at. So path length leads and Drive is a co-suspect; neither is proven.

What made the item writable anyway is that the fix does not depend on which is guilty. Pointing
Gradle's build directory at `C:\builds\taskflow` takes the longest path to roughly 190 characters and
takes the folder Gradle rewrites out of Drive's sync in one change.

Two refusals carry forward. Moving the whole project out was the user's call to reject: every one of
their projects lives under the same parent folder, so moving this one alone costs them every time they
go looking for it. Excluding `app/build` from Drive's sync is not a real option at all — Drive for
desktop's folder selection decides what exists on the machine, not what is uploaded, so excluding the
folder Gradle must write is incoherent rather than unsupported. And hardcoding the path into the
committed build file was refused because this repository is public and a machine-specific absolute path
breaks every other checkout, which is why the value goes in the git-ignored `local.properties`.

**Queue changes:** [project-out-of-drive] rewritten whole from a capture into a build item under the
same slug — kept because [install-current-build-on-device] and [durable-local-data] both cite it — and
moved to the top of Processed, cleared to run.

**Work processed:** kept, cleared to run — [project-out-of-drive].
