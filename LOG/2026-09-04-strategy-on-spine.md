# [HASH] — /plan [strategy-on-spine]: SPEC was not ambiguous after all — the record says Strategy is the spine's right end, twice, and the code's overlay was a build-time shortcut

Found while reading the navigation code during the completed-history split: `SpinePage` holds four
entries, Today through Later, while SPEC describes seven ending in Strategy, which is built as a drawer
overlay instead.

Claude first described this as SPEC being ambiguous, citing §Strategy doc's "reachable from the side
menu — a single calm row at the spine's right end" against §Schedule view's spine sentence. The user
asked whether there had been reflection on this and whether it was in the log. There had been, and
reading it changed the answer:

- `nav-spine-spec-edit.md` records the spine with "Strategy the page to its right", and says §Strategy
  doc's row sentence was rewritten "keeping the reachable-but-never-foregrounded intent" — which is
  about where the menu row sits and Today staying the default page, not about excluding Strategy from
  the spine.
- `later-by-project-spec-edit.md` restates it after Projects were folded into Later: the spine's right
  end becomes "Today, Tomorrow, Soon, Later, Strategy".
- `0003-side-menu-schedule-projects-app-actions.md` is where the code diverged, deliberately: deep
  destinations were shown as "a single placeholder overlay over the spine", chosen over Jetpack
  Navigation because "every deep destination is a placeholder in this batch", with the note that "the
  overlay can be swapped for NavHost later".

So SPEC has said this twice and consistently; the divergence is a build-time architecture choice whose
stated reason expired when [0015-strategy-doc-and-life-area-context] shipped the real screen and left
the routing alone. No SPEC edit was needed — the work is bringing the code to what SPEC already says.

The mis-reading is recorded in the item itself so it is not reached for again. Keeping the overlay and
editing SPEC to match was refused: UX principle 3 rests on the spine gliding from arranging time into
arranging areas of life, which needs Strategy to be somewhere you can swipe to.

**Queue changes:** [strategy-on-spine] filed and moved into Processed, cleared to run, placed after
[nav-search-completed-history] because it needs the same nullable-slot change.

**Work processed:** kept, cleared to run — [strategy-on-spine].
