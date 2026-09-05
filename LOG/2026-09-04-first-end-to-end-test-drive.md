# [HASH] — The first end-to-end test handed across and running through the user's working day

Walk-through drive record, opened live and appended to as it went.

Slug: first-end-to-end-test
Session: 91c12720-d901-4c60-846f-40c2ba990c64, 2026-09-04
Opened live as the drive started.

This is the first time the app is used as an app rather than checked feature by feature. What it
produces is a set of notes, and those notes are the input to [post-first-test-polish-review].

Capability check: Claude can install a build and drive taps over adb, and did both earlier in this
session. What it cannot do is notice that something feels wrong, which is the entire point of this
item — so it stays the user's.

## Actions

- 2026-09-04 19:06 — record opened. The item's stated precondition is met for the first time: its
  walkthrough opens with "install the current build on your device", and
  [install-current-build-on-device] was driven to its end earlier this session, with the install
  timestamp checked over adb rather than assumed. Its ordering note — run it after the cleared
  builds ship — is also satisfied: twelve shipped in this run and are on the phone.
- 2026-09-04 19:06 — the user took the item on, their own word, and left with it to use through
  their working day. Steps 2 to 5 are theirs and cannot be driven turn by turn: they describe a
  day's ordinary use rather than a sequence anyone can stand over. The whole walkthrough was
  therefore handed across in one piece, which is what a through-the-day item needs, and the usual
  one-step-at-a-time drive does not apply to it.
- 2026-09-04 19:06 — one thing said to the user that the item does not itself say: this build has
  never been exercised, and all twelve of the run's items are ticked UNCONFIRMED. So they were
  asked to separate a thing that is BROKEN from a thing that is merely rough, because the first is
  a defect this session can fix and the second is the polish note the item is actually collecting.

## Outcome

Handed across and in progress — the user took it on and is running it through their working day.
Not `done`: its completion is bringing the notes to a planning session, which has not happened.
Not `deferred`: they did not set it aside, they started it.

What closes it: the user says the test has been done and brings the notes. Those notes are what
[post-first-test-polish-review] exists to weigh — which of them earn a SPEC entry and which fold
into existing work.

Also true, and stated to the user so it is not read as a fresh fault: the reinstall earlier in this
session very likely emptied the app, so the test starts from an empty database. For this particular
item that is closer to help than harm — capturing real tasks from scratch is what step 2 asks for.
