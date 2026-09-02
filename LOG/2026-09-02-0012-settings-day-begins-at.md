# [HASH] — build [0012-settings-day-begins-at]: the user's own day boundary, wired through bucketing, and a ticker so it fires with the app open

SPEC §Settings → Day begins at is the only time picker in the app (UX principle 7), and it exists for one person: someone up past midnight does not consider the day to have ended, and a task meant for "today" at 1 AM should still be on Today. It ships defaulting to 4 AM.

**The alternative that lost.** DataStore is the current recommendation for Android preferences and was passed over for SharedPreferences. This is two scalars written a handful of times in a device's life; SharedPreferences is already on the classpath, so a new dependency would buy nothing. It is still exposed as a `Flow`, because the boundary moving has to re-bucket every task on screen and nothing should have to remember to trigger that — a settings change therefore flows through exactly like a database change.

**[schedule-day-boundary-tick] folded in here**, as that item's build block instructed, and it is the more interesting half. Slot placement is derived from dates against "today", so when the boundary passes, Tomorrow's tasks become Today's with nothing in the database changing. Without something firing at that moment, the screen sits on yesterday's buckets until some other event redraws it — a task still sitting on Tomorrow at 4 AM with the app open. The settings flow therefore re-emits when the boundary passes, computing the delay to the next one rather than polling. Coming back to the app is covered separately and for free: collection restarts on resume, which re-reads the clock.

Because that item's work shipped inside this one, it is now built work still sitting in the queue as unbuilt, below the cleared-to-run line. Its fate is a planning decision, flagged at this close rather than settled here.

The hour picker is a list of hours rather than a clock dial. Taskflow does dates, not times of day, and a minute-accurate day boundary is a precision nobody needs and a picker nobody enjoys.

**Files touched:** app/src/main/java/com/example/taskflow/data/settings/SettingsRepository.kt (new — both settings, SharedPreferences-backed, exposed as a Flow that emits once immediately then on every change), ui/settings/SettingsViewModel.kt (new), ui/settings/SettingsScreen.kt (new — the hour picker), TaskflowApplication.kt (settingsRepository), ui/schedule/ScheduleViewModel.kt (settings threaded through bucketing and every date label; the boundary ticker), ui/edit/EditTaskViewModel.kt (the same boundary drives the strip's Today anchor), ui/navigation/AppRoot.kt (Settings opens the real screen instead of the placeholder).

**Routed to Captures:** none.

**Tick:** done, UNCONFIRMED: needs an Android Studio build and the device check (set day-begins-at a few minutes ahead, leave the app open across it, and a Tomorrow task moves to Today with no edit; the date strip's Today anchor follows; the setting survives relaunch).
