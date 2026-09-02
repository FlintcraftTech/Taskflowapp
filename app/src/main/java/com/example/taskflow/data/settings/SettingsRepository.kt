package com.example.taskflow.data.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/** How dates are written throughout the app (SPEC §Settings → Date format). */
enum class DateFormat(val pattern: String, val label: String) {
    DAY_FIRST("dd/MM", "DD/MM"),
    MONTH_FIRST("MM/dd", "MM/DD"),
}

/** Everything the Settings screen owns, read as one object so a screen observes a single flow. */
data class TaskflowSettings(
    /**
     * When "today" rolls over for this user, as an hour of the day (SPEC §Settings → Day begins at).
     * The only time picker in the app: people who stay up past midnight do not consider the day to
     * have ended, and a task meant for "today" at 1 AM should still be on Today.
     */
    val dayBeginsAtHour: Int = DEFAULT_DAY_BEGINS_AT_HOUR,
    val dateFormat: DateFormat = DateFormat.DAY_FIRST,
    /**
     * Whether first-run onboarding has been seen (SPEC §Onboarding — first run). Set by reaching
     * the end of the flow *or* by taking the X escape hatch: leaving early is a choice, and a flow
     * that reappeared after the user dismissed it would be nagging them into it.
     */
    val onboardingSeen: Boolean = false,
) {
    companion object {
        /** SPEC's shipped default: 4:00 AM. */
        const val DEFAULT_DAY_BEGINS_AT_HOUR: Int = 4
    }
}

/**
 * The app-level settings store (SPEC §Settings). Local to the device and deliberately small — two
 * values, both of which change how already-stored data is *read* rather than what is stored.
 *
 * Backed by SharedPreferences rather than DataStore: this is two scalars written by hand a handful
 * of times in a device's life, and SharedPreferences is already on the classpath, so a new
 * dependency would buy nothing here. It is exposed as a [Flow] all the same, so a settings change
 * flows through to the Schedule the same way a database change does — the day boundary moving has
 * to re-bucket every task on screen, and nothing should have to remember to trigger that.
 */
class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val settings: Flow<TaskflowSettings> = prefs.changes()
        .map { read() }
        .distinctUntilChanged()

    fun read(): TaskflowSettings = TaskflowSettings(
        dayBeginsAtHour = prefs.getInt(
            KEY_DAY_BEGINS_AT,
            TaskflowSettings.DEFAULT_DAY_BEGINS_AT_HOUR,
        ),
        dateFormat = prefs.getString(KEY_DATE_FORMAT, null)
            ?.let { name -> DateFormat.entries.firstOrNull { it.name == name } }
            ?: DateFormat.DAY_FIRST,
        onboardingSeen = prefs.getBoolean(KEY_ONBOARDING_SEEN, false),
    )

    fun setOnboardingSeen() {
        prefs.edit().putBoolean(KEY_ONBOARDING_SEEN, true).apply()
    }

    fun setDayBeginsAtHour(hour: Int) {
        prefs.edit().putInt(KEY_DAY_BEGINS_AT, hour.coerceIn(0, 23)).apply()
    }

    fun setDateFormat(format: DateFormat) {
        prefs.edit().putString(KEY_DATE_FORMAT, format.name).apply()
    }

    /**
     * Emits once immediately, then again on every change. The immediate emission matters: a
     * collector that only heard about *changes* would start with no settings at all and render the
     * first frame against nothing.
     */
    private fun SharedPreferences.changes(): Flow<Unit> = callbackFlow {
        trySend(Unit)
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> trySend(Unit) }
        registerOnSharedPreferenceChangeListener(listener)
        awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
    }

    private companion object {
        const val PREFS_NAME = "taskflow_settings"
        const val KEY_DAY_BEGINS_AT = "day_begins_at_hour"
        const val KEY_DATE_FORMAT = "date_format"
        const val KEY_ONBOARDING_SEEN = "onboarding_seen"
    }
}
