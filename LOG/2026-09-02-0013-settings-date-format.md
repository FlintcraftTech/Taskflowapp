# 3defa43 — build [0013-settings-date-format]: DD/MM or MM/DD, applied wherever a date renders

SPEC §Settings → Date format is a two-option setting because date conventions vary by region and Taskflow ships with international users in mind — a single central setting beats hard-coding one convention or trying to detect locale automatically. SPEC is explicit that it applies "everywhere a date is shown": Schedule rows, the date-picker tiles, the Strategy doc, anywhere.

The work was almost entirely in honouring that word "everywhere". Both view-models that render dates had a formatter pinned to `dd/MM` as a private constant — written when there was no setting to read — and the date strip's tiles took theirs from a file-level constant in the edit screen. Each became a read of the setting instead, with the strip's formatter rebuilt only when the pattern changes so switching reformats every tile at once.

It rides on the flow [0012-settings-day-begins-at] established, so no new plumbing was needed: the same emission that re-buckets the Schedule when the day boundary moves also reformats every date when the format changes.

**Files touched:** app/src/main/java/com/example/taskflow/ui/settings/SettingsScreen.kt (the DD/MM ⁄ MM/DD choice), ui/schedule/ScheduleViewModel.kt (formatter read from the setting), ui/edit/EditTaskViewModel.kt (same, plus the pattern carried in the dialogue's state), ui/edit/EditTaskScreen.kt (the strip's formatter comes from the setting rather than a constant).

**Routed to Captures:** none.

**Tick:** done, UNCONFIRMED: needs an Android Studio build and the device check (switch to MM/DD and every date on screen flips; relaunch and the choice persists).
