# 3defa43 — device check walked during the /next run of 2026-08-31

The device check for [add-flow-create-path-fixes], which gave the add dialogue a fresh view-model
store per open so the New-task title no longer carries over from the previous save.

## Precondition met on this item's turn

The item's walkthrough opens "on a device with the current build installed". At the moment this was
walked, the installed build predated the whole of this run's 19 shipped items, so checking the
behaviour on it would have tested code that has since been replaced. The walkthrough's real first
step is therefore an Android Studio build — which is also the first compile any of this run's code
has had, since Gradle cannot run from Claude's shell on this machine (see TOOLS.md).

## Actions

- Walk-through started. Step 1 (build and install from Android Studio) handed to the user.
- First build failed, and so did a second. The Build panel showed every Gradle task failing with one
  message: "Unable to delete directory …\app\build\…" — the file lock this project's CLAUDE.md
  already documents, not compile errors. Claude deleted `app\build` (git-ignored generated output,
  ~59 MB, nothing tracked) and recorded the cause in TOOLS.md.
- Second build, on the cleared folder: **compiled successfully**. This is the first compile of all
  19 items shipped in the 2026-08-31 run, and it passed. Install then stopped on Android Studio's
  "same package, different signature" prompt, which uninstalls the existing app and its data.
- The user confirmed the phone held only test data and accepted the uninstall. Install finished
  successfully. The build from the 2026-08-31 run is now on the device.
- Raised by the user at that prompt and filed as [durable-local-data]: the app destroys all local
  data on every schema change, and its recorded justification ("acceptable while there are no real
  users") expires the moment they start keeping real tasks in it, which is what they are working
  toward.
- Note for driving the remaining steps: the item's walkthrough predates this run. It says to look at
  a "Title field", which [0010-outliner-typing-drag-target-icons] replaced with the outliner's first
  line, and it does not know about the first-run onboarding [0016-onboarding-flow] added. The check
  itself is unchanged — a second Add must open blank — so the steps are being read onto the screens
  that now exist rather than the ones the item described.
- 2026-09-02: on the first run of the installed build the user reported the onboarding screens as
  "dark against dark background". Cause found without a screenshot — onboarding returns before
  AppRoot's Scaffold, so it painted no background and its text took the default near-black over a
  dark window. Fixed by wrapping the screen in a Surface; the user rebuilt and confirmed it reads
  fine. The wider condition behind it — Compose pinned to its light palette while the window theme
  follows the phone's dark mode — was filed as [compose-dark-theme] rather than fixed here.
- 2026-09-02: walked to its end. Onboarding completed to Today; a task added from the + button and
  saved appeared in the Today list; the + button pressed a second time opened with its top line
  empty, showing only the field's own "Task" label. The user read the label back rather than
  reporting an empty field, which is what confirms nothing carried over.

## Outcome

**done** — walked to its end on 2026-09-02 and passed. The fresh view-model store per dialogue open
holds: a second Add opens blank rather than showing the previous save's title.



