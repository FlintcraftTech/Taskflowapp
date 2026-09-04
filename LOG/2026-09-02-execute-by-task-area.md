# 3defa43 — build [execute-by-task-area]: focus on one Project, visible while it lasts and gone when the app closes

Raised by Alex during the [project-create] device test on 2026-06-21 and carried unresolved for months: a way to focus temporarily on a single area and see all its tasks across the spine, for when motivation is only there for one part of life.

**The tension that kept it open.** The near-term slots are deliberately flat, horizon-sliced lists — UX principle 3 exists to refuse category-slicing — so serving area-focus there cuts against the one view built to refuse it. A transient filter mode was floated on 2026-06-22 and set aside over UI concerns; the user's own read that day was that there might be no good answer yet, and leaving it open was acceptable. It had no blocker and no trigger, so it returned to the top of the queue every session and was set aside again, which is why planning on 2026-08-25 took it up rather than dating it.

**What broke the tension, and it is one property rather than a feature.** Focus does not survive closing the app. A lens that cannot survive a relaunch cannot become how the user lives in the app, so the flat time-horizon list stays the app's real shape and focus is a temporary view over it. The rejected alternative remains a general, persistent filter over the slots: that restructures the slots into a category-sliced app, which is the thing principle 3 exists to refuse.

Two supporting properties follow from the same reasoning. Focus announces itself continuously — a recoloured top bar carrying the Project's name and an X — so it can never be on without the user knowing, and leaving is always one tap. And it is entered from a Later card's **header**, which is why [later-card-peek] had already separated the header from the expand chevron: the two are different acts on the same card.

Later itself is not filtered, since it is already organised by area — filtering it would leave one card on a page of cards.

Capture while focused files into the focused Project rather than Unassigned, because focus is the context the user is capturing in (UX principle 5). That is why the focused Project is held at the navigation root rather than inside the Schedule's own view-model: the add button needs it too. It is held in plain state rather than saved state, deliberately — saved state is exactly what would carry focus across a process death.

**Files touched:** app/src/main/java/com/example/taskflow/ui/schedule/LaterPage.kt (header tap enters focus, distinct from the chevron), ui/schedule/ScheduleViewModel.kt (focus filtering of the near-term slots and the tray, in memory only), ui/schedule/ScheduleScreen.kt (recoloured top bar with the Project's name and an X), ui/navigation/AppRoot.kt (focus held so it cannot survive the app closing; capture inherits it), ui/edit/EditTaskViewModel.kt (EditTarget.NewOnSlot carries the inherited Project).

**Routed to Captures:** none.

**Tick:** done, UNCONFIRMED: needs an Android Studio build and the device check (tap a Later card's header and Today/Tomorrow/Soon show only that Project, top bar recoloured and naming it; tap the X and every task returns; add a task from Today while focused and it belongs to the focused Project; kill and relaunch and it opens unfocused; the expand chevron still expands rather than entering focus).
