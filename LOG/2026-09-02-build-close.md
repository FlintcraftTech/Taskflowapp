# 3defa43 — /next run of 19 items closed: what happened in the chat around the work

This session ran across 2026-08-31, 2026-09-01 and 2026-09-02. It built nineteen work items, each
with its own entry beside this one, and this entry carries what belongs to the chat rather than to
any of them.

**Rule gate:** not needed — no standing rule was authored or amended this session. The one item
carrying a rule-gate disposition, [record-visibility-line], carried its own and it is transcribed in
that item's entry.

## The environment, which cost most of the session's friction

Gradle cannot run from Claude's shell on this machine. Every invocation dies with
`java.io.IOException: Unable to establish loopback connection`, underneath it
`SocketException: Invalid argument: connect` inside `SocketConnection$SocketInputStream` — Gradle's
NIO selector cannot open its own internal socket pair. Not fixed by `--no-daemon`, `--no-watch-fs`,
matching `org.gradle.jvmargs`, forcing IPv4, or running unsandboxed; plain TCP loopback works fine
from PowerShell, so it is specific to the JVM Gradle forks.

So nineteen items were written without a single compile. That is the dominant fact about this run's
confidence, and it is why sixteen of the nineteen ticks read UNCONFIRMED.

`TOOLS.md` was created to stop a future session rediscovering this: the JDK's path (Android Studio's
bundled JBR — there is no `java` on PATH), the SDK path, the loopback failure and everything tried
against it, and the consequence that compiling is the user's job in Android Studio.

Then Android Studio's own build failed too, with "Unable to delete directory ...\app\build\...",
reported once per Gradle task so one file lock read on screen as a dozen errors. Deleting `app\build`
— 59 MB of git-ignored generated output — cleared it and the next build succeeded. The project sits
inside Google Drive, which syncs that folder as Gradle rewrites it; that is the likely cause and is
filed as [project-out-of-drive].

## The device session

Once installed, three things were checked and two `[user]` items walked to their end. What the device
turned up, beyond those items' own records:

The onboarding screens were unreadable — dark text on a dark window. Onboarding returns before
AppRoot's Scaffold, so it painted no background and its text fell back to the default near-black. It
was fixed in the run and the user confirmed it; the wider condition it exposed is [compose-dark-theme].

**A wrong diagnosis, made and withdrawn.** The drawer item's last step failed: a mid-screen swipe did
not page to Tomorrow. From the chevron working and a swipe over blank space working, it was concluded
that the per-row drag handler added by [task-reorder-within-list] was claiming the horizontal gesture
— a regression in this run's own work — and a fix was proposed and offered. The user asked to replay
the check gesture by gesture. On replay, against the same build with nothing changed, every variant
paged correctly, including the exact original sequence. The fix was withdrawn rather than applied.
The failure is most likely a swipe that did not register. It is recorded in that item's entry so a
recurrence has something to match against.

The word "regression" was also used without explaining it, and the user said they did not remember
reporting one — a fair correction, since it was a term for something they had not been told about.

## Corrections and decisions the user gave in conversation

- Six items were dropped from the run at the outset, on the user's approval, after self-scoping found
  they named no buildable files: the cloud backend and the MCP server have no home, the Play product
  does not exist, and the Help content waits on an unprocessed item. The queue was left untouched.
  The state that leaves is filed as [cleared-region-unbuildable].
- The user answered directly on whether Taskflow is safe to rely on yet. It is not, and the reason is
  [durable-local-data]: the app destroys all local data on every schema change, and this run changed
  the schema three times.
- On being offered a nagging advisory to chase [first-end-to-end-test], the user identified it as the
  behaviour they had designed out of the method — correctly, since the `[user]` lifecycle refuses it
  explicitly. [first-test-notes-observable] proposes the route the method actually provides.
- Design feedback given on the device, all of it reaching SPEC and none of it fixed in the run:
  [drawer-ai-row-copy], [notes-versus-subtasks], [date-strip-legibility], [left-edge-swipe-collision].

## The plugin moved mid-session

Throughliner went 1.21.1-test3 → test6 between 2026-09-01 and 2026-09-02. The document format epoch
did not move (5 both sides), so nothing needed migrating and the run resumed unchanged. The new
version introduced the build working file's `Run-level:` section, which was added and backfilled from
this session's own record. The project's version marker still reads test3 and `/setup` refreshes it
once no build is open.

## `[user]` item outcomes

- [verify-blank-new-task-form] — **done**, passed. Removed from Processed by this close.
- [verify-drawer-swipe-off-on-device] — **done**, passed. Removed from Processed by this close.
- [first-end-to-end-test] — **deferred**, on the user's own word. Left in Processed.

**Routed to Captures:** [side-menu-spine-mismatch], [durable-local-data], [compose-dark-theme],
[left-edge-swipe-collision], [drawer-ai-row-copy], [notes-versus-subtasks], [date-strip-legibility],
[first-test-notes-observable], [verify-run-2026-08-31], [project-out-of-drive],
[boundary-tick-already-shipped], [cleared-region-unbuildable], [walkaway-work-integration-case].

**Advisory:** filed — [forward-advisory], replacing the spent one that pointed at
[0006-side-scrolling-date-picker].
