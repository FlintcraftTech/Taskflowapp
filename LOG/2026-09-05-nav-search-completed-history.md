# [HASH] — Search becomes the spine's leftmost page, matching task titles and Project names across active and completed work

Date: 2026-09-05 11:11

Search is one surface covering everything, because a task someone is searching for is a task they
have lost, and the app's own organising principles — horizon, Project — are exactly what they cannot
use at that moment. Scoping search to the current page or Project was refused at planning on that
reasoning, and SPEC settles it.

One DAO query does the whole job, joining `projects` so that typing a Project's name returns that
Project's tasks — the same gesture as typing a task's. Subtasks are excluded, as everywhere else
that lists tasks; a child is found through its parent. The Strategy doc is deliberately unreachable
from here, because it is prose rather than items and its matches could not render as task rows.

**SPEC pulls two ways here, and the build honoured both rather than choosing.** It describes the
page as covering active tasks and completed ones together, and it also describes the body as the
completed history that typing narrows. So with an empty query the page is the unfiltered history —
completions newest first under a header per day — and typing shows matching active tasks above that
dated list, with the headers for days that still have results staying above them. The empty term
matches everything, so there is no separate no-query path to keep in step. Day headers name the
month rather than numbering it, which keeps the date-format setting out of them.

**One deliberate gap, and it is SPEC-directed.** Tapping a completed result does nothing yet. SPEC
puts editing and un-completing only on a day card, which is `[nav-day-card-layer]` and is held
behind this item — so wiring a completed row now would create the second place to change things that
the read-only rule exists to prevent. An active result navigates to the slot it lives on, which is
what this item's own observation asks for, and the row states its destination before the tap.

**A scope addition, approved by the user mid-item.** The item named `TaskDao.kt` for the query, but
every screen in this app reaches the database through a repository, so `TaskRepository.kt` needed a
one-line pass-through. The alternative was making the Search page the only screen that talks to the
DAO directly.

Files touched:
- `ui/navigation/Destination.kt` — `SEARCH` added before `YESTERDAY`
- `data/local/TaskDao.kt` — the search query joining projects
- `data/repository/TaskRepository.kt` — the pass-through
- `ui/history/SearchViewModel.kt` — new; query state, and the active/completed split by logical day
- `ui/history/SearchScreen.kt` — new; the field, results, dated list and empty states
- `ui/schedule/ScheduleScreen.kt` — the SEARCH branch, wired to scroll the pager to a result's slot

Routed to Captures: none.

Tick: done, UNCONFIRMED — needs a device to confirm swiping right from Yesterday opens Search,
completions list newest first under date headers, typing narrows and leaves the headers above
matching days, a Project name matches, and a result cannot be completed or edited.
