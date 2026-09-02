# b28ee0e — [verify-drawer-swipe-off-on-device] kept as a `[user]` item, cleared to run at the end of the cleared region

The device check for the [disable-drawer-swipe-open] build, which shipped uncompiled because Gradle's daemon fails for Claude on this machine. The capability check confirmed the tag: the next Android Studio build on the user's side is the first real compile of the change, and judging the gesture behaviour on a real screen needs their eyes, so `[user]` stands. The walkthrough was already fully scripted at filing — five steps, each naming the action and the thing to look for — and nothing in the queue blocks it, so it cleared to run, placed at the end of the cleared region with the other device checks per the end-preferred convention for user stops.

**Queue changes:** moved Unprocessed → Processed, cleared, before [verify-schedule-date-matrix]; readiness marker follows it.
**Work processed:** [verify-drawer-swipe-off-on-device] — kept, cleared to run.

---

## Walk-through, driven 2026-09-02 during the /next run of 2026-08-31

Step 1 (install the current build) was satisfied ahead of this item: the build from that run was
compiled and installed during [verify-blank-new-task-form]'s drive, which is recorded in its own
entry along with the Gradle `app\build` lock that had to be cleared first and the onboarding
contrast fix that followed. Driven ahead of [first-end-to-end-test] despite sitting after it in the
queue — this one is four gestures with the phone already in hand, where that one is a day of real
use and wants its own time.

## Actions

- Step 2, left-edge swipe right: no menu, and the page stayed on Today. The item's original look-for
  was only "the menu does not open", which the user pointed out is also satisfied by Today simply
  having nowhere to go leftward; the look-for was widened to both observations before driving it.
  That objection became the capture [left-edge-swipe-collision].
- Step 3, tap ☰: menu opened. Step 4, tap the dimmed area: menu closed.
- Step 5, swipe left from mid-screen: **failed on the first attempt** — the page stayed on Today.
- Diagnosis attempted from that failure and **it was wrong**. The chevron navigated fine, and a swipe
  over blank space paged, so it was concluded that the per-row long-press drag handler added by
  [task-reorder-within-list] was claiming the horizontal gesture. A fix was proposed and offered.
- The user asked to replay the check gesture by gesture. On that replay, against the same installed
  build and with no code changed in between: a swipe starting directly on the task row paged to
  Tomorrow, and the full original sequence — open the drawer, close it by tapping the scrim, then
  swipe over the row — also paged to Tomorrow. **Not reproducible.**
- The proposed fix was withdrawn rather than applied. Nothing was changed in response to this
  failure. The single failed swipe is most likely a gesture that did not register; it is recorded
  here so a recurrence has something to match against rather than being met fresh.

## Outcome

**done** — walked to its end on 2026-09-02 and passed. All five of the item's checks hold on the
installed build: the left-edge swipe opens no menu and moves no page, the ☰ opens the drawer, the
scrim tap closes it, and a mid-screen swipe pages to Tomorrow. [disable-drawer-swipe-open]'s
acceptance criteria, never run in the session that built them, are now met.


