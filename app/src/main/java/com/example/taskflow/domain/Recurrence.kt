package com.example.taskflow.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * A task's repeat rule (SPEC §Recurring tasks), and the expansion of that rule into the individual
 * dates the Schedule pages show.
 *
 * Instances are **derived, not stored**. A recurring task is one row carrying a rule and an anchor
 * date; the dates it appears on are computed each time the Schedule is built. Materialising a row
 * per instance would mean writing an unbounded tail of future rows into the database and keeping
 * them in step with every later edit to the rule, and Taskflow only ever shows a month of them.
 * What *is* stored is the set of instance dates the user has completed — see
 * [com.example.taskflow.data.model.Task.completedInstances] — which is what lets completing one
 * Monday leave every other Monday alone.
 *
 * [unit] and [interval] together give "every N days / weeks / months"; the daily, weekly and monthly
 * presets are just interval 1, and "custom" in the editor is the same rule with N > 1. [daysOfWeek]
 * applies to [RecurrenceUnit.WEEK] only, and an empty set means "the same weekday as the anchor".
 */
data class Recurrence(
    val unit: RecurrenceUnit,
    val interval: Int = 1,
    val daysOfWeek: Set<DayOfWeek> = emptySet(),
) {

    /**
     * The stored form, e.g. `WEEK:1:MONDAY,THURSDAY`. A plain delimited string rather than a
     * relation of its own: a rule is only ever read back whole, alongside the task that owns it, so
     * a column costs one migration where a table would cost a join on every Schedule rebuild.
     */
    fun serialize(): String = buildString {
        append(unit.name)
        append(':')
        append(interval)
        if (unit == RecurrenceUnit.WEEK && daysOfWeek.isNotEmpty()) {
            append(':')
            append(daysOfWeek.sortedBy { it.value }.joinToString(",") { it.name })
        }
    }

    /** Plain-English rendering for the editor's summary line ("Every 2 weeks on Mon, Thu"). */
    fun describe(): String {
        val every = if (interval == 1) "Every ${unit.singular}" else "Every $interval ${unit.plural}"
        if (unit != RecurrenceUnit.WEEK || daysOfWeek.isEmpty()) return every
        val days = daysOfWeek.sortedBy { it.value }.joinToString(", ") {
            it.name.lowercase().replaceFirstChar(Char::uppercase).take(3)
        }
        return "$every on $days"
    }

    /**
     * Every date this rule falls on within [from]..[to] inclusive, given the task's own date as
     * [anchor]. Nothing before the anchor is generated — a repeat starts when the task is dated,
     * not retroactively.
     *
     * The window handed in here is at most [HORIZON_DAYS] wide (SPEC §Recurring tasks caps the tail
     * at 30 days), so walking it a day at a time is a few dozen comparisons. That is deliberately
     * chosen over closed-form date arithmetic per unit: the loop is the same three lines for daily,
     * weekly and monthly, and each unit's rule is a single readable predicate.
     */
    fun instancesBetween(anchor: LocalDate, from: LocalDate, to: LocalDate): List<LocalDate> {
        if (interval < 1 || to.isBefore(from)) return emptyList()
        val start = if (from.isBefore(anchor)) anchor else from
        if (to.isBefore(start)) return emptyList()

        val instances = mutableListOf<LocalDate>()
        var date = start
        while (!date.isAfter(to)) {
            if (fallsOn(anchor, date)) instances.add(date)
            date = date.plusDays(1)
        }
        return instances
    }

    /** Whether this rule, anchored at [anchor], puts an instance on [date]. */
    private fun fallsOn(anchor: LocalDate, date: LocalDate): Boolean = when (unit) {
        RecurrenceUnit.DAY ->
            ChronoUnit.DAYS.between(anchor, date) % interval == 0L

        RecurrenceUnit.WEEK -> {
            val wanted = daysOfWeek.ifEmpty { setOf(anchor.dayOfWeek) }
            // Week alignment is measured from the anchor's own week, so "every 2 weeks" repeats on
            // the anchor's fortnight rather than on whichever fortnight the calendar happens to be in.
            val anchorWeek = anchor.minusDays((anchor.dayOfWeek.value - 1).toLong())
            val dateWeek = date.minusDays((date.dayOfWeek.value - 1).toLong())
            val weeksApart = ChronoUnit.WEEKS.between(anchorWeek, dateWeek)
            date.dayOfWeek in wanted && weeksApart >= 0 && weeksApart % interval == 0L
        }

        RecurrenceUnit.MONTH -> {
            val monthsApart = ChronoUnit.MONTHS.between(anchor.withDayOfMonth(1), date.withDayOfMonth(1))
            // plusMonths clamps to the shorter month, so a task anchored on the 31st lands on the
            // 30th in April and the 28th in February rather than skipping those months entirely.
            monthsApart >= 0 && monthsApart % interval == 0L && anchor.plusMonths(monthsApart) == date
        }
    }

    companion object {
        /**
         * SPEC §Recurring tasks: instances more than a month out are not shown until the world
         * catches up to within a month of them. Manually dated one-off tasks are not capped.
         */
        const val HORIZON_DAYS: Long = 30

        /** Reads back [serialize]. A malformed or absent rule parses to null — a task with no repeat. */
        fun parse(raw: String?): Recurrence? {
            if (raw.isNullOrBlank()) return null
            val parts = raw.split(':')
            if (parts.size < 2) return null
            val unit = RecurrenceUnit.entries.firstOrNull { it.name == parts[0] } ?: return null
            val interval = parts[1].toIntOrNull()?.takeIf { it >= 1 } ?: return null
            val days = if (parts.size >= 3 && parts[2].isNotBlank()) {
                parts[2].split(',').mapNotNull { name ->
                    DayOfWeek.entries.firstOrNull { it.name == name }
                }.toSet()
            } else {
                emptySet()
            }
            return Recurrence(unit, interval, days)
        }
    }
}

/** The three repeat units the editor offers. "Custom" is any of them with an interval above 1. */
enum class RecurrenceUnit(val singular: String, val plural: String) {
    DAY("day", "days"),
    WEEK("week", "weeks"),
    MONTH("month", "months"),
}
