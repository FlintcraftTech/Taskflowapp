# f82e03d — ScheduleViewModel.kt `dateLabelFor` — return null for the Tomorrow slot so Tomorrow rows show no date label (redundant with the page title); KDoc updated to match.

A Tomorrow task always carries exactly tomorrow's date — the slot is derived from the date, so nothing else can land there — which makes the DD/MM label fully redundant with the page title. Tomorrow has no past-date case the way Today does: a past-dated task falls onto Today, never Tomorrow, so dropping the label loses no stale-date signal. The date earns its place only on Soon (2–7 days) and Later (8+ days), where the page name doesn't tell you the actual day. Today's rule is unchanged — no label except when the date has slipped into the past.

Implemented by adding a `ScheduleSlot.TOMORROW -> false` branch to `dateLabelFor`'s `when (slot)`, with the KDoc rewritten to match; Soon/Later and Today's past-date branch are untouched. SPEC already described this behaviour — the spec-edit landed earlier (commit 988a9fa, SPEC §Schedule view, "Tomorrow does not show a date label") — so this build only brought the code into line, no new product truth. On-device the Tomorrow-shows-no-label result was confirmed; the Soon/Later DD/MM and past-Today regression checks couldn't run because date-editing (0006) doesn't exist yet, so they fold into the existing date-matrix deferred test.

**Files touched:**
- `app/src/main/java/com/example/taskflow/ui/schedule/ScheduleViewModel.kt` — `dateLabelFor`: added `ScheduleSlot.TOMORROW -> false`; KDoc updated.

**Routed to Captures:** none
