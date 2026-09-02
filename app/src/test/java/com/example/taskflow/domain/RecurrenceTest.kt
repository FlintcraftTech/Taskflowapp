package com.example.taskflow.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Unit tests for the repeat rule (SPEC §Recurring tasks). These cover the expansion arithmetic —
 * which dates a rule lands on — because that is the part with real edge cases: interval alignment,
 * month-end clamping, and the window never reaching back before the anchor.
 */
class RecurrenceTest {

    private val anchor: LocalDate = LocalDate.of(2026, 3, 2) // a Monday

    @Test
    fun `daily rule lands on every day from the anchor`() {
        val rule = Recurrence(RecurrenceUnit.DAY)
        val dates = rule.instancesBetween(anchor, anchor, anchor.plusDays(3))
        assertEquals(
            listOf(anchor, anchor.plusDays(1), anchor.plusDays(2), anchor.plusDays(3)),
            dates,
        )
    }

    @Test
    fun `every third day skips the two days between`() {
        val rule = Recurrence(RecurrenceUnit.DAY, interval = 3)
        val dates = rule.instancesBetween(anchor, anchor, anchor.plusDays(7))
        assertEquals(listOf(anchor, anchor.plusDays(3), anchor.plusDays(6)), dates)
    }

    @Test
    fun `weekly with no days named repeats on the anchor's own weekday`() {
        val rule = Recurrence(RecurrenceUnit.WEEK)
        val dates = rule.instancesBetween(anchor, anchor, anchor.plusDays(21))
        assertEquals(
            listOf(anchor, anchor.plusWeeks(1), anchor.plusWeeks(2), anchor.plusWeeks(3)),
            dates,
        )
    }

    @Test
    fun `weekly on named days lands on each of them`() {
        val rule = Recurrence(
            RecurrenceUnit.WEEK,
            daysOfWeek = setOf(DayOfWeek.MONDAY, DayOfWeek.THURSDAY),
        )
        val dates = rule.instancesBetween(anchor, anchor, anchor.plusDays(7))
        assertEquals(
            listOf(anchor, anchor.plusDays(3), anchor.plusDays(7)),
            dates,
        )
    }

    @Test
    fun `fortnightly counts from the anchor's own week, not the calendar's`() {
        val rule = Recurrence(RecurrenceUnit.WEEK, interval = 2)
        val dates = rule.instancesBetween(anchor, anchor, anchor.plusWeeks(4))
        assertEquals(listOf(anchor, anchor.plusWeeks(2), anchor.plusWeeks(4)), dates)
    }

    @Test
    fun `monthly clamps into a shorter month rather than skipping it`() {
        // Anchored on the 31st: January has one, February clamps to the 28th, March has one.
        val janEnd = LocalDate.of(2026, 1, 31)
        val rule = Recurrence(RecurrenceUnit.MONTH)
        val dates = rule.instancesBetween(janEnd, janEnd, LocalDate.of(2026, 3, 31))
        assertEquals(
            listOf(janEnd, LocalDate.of(2026, 2, 28), LocalDate.of(2026, 3, 31)),
            dates,
        )
    }

    @Test
    fun `nothing is generated before the anchor`() {
        val rule = Recurrence(RecurrenceUnit.DAY)
        val dates = rule.instancesBetween(anchor, anchor.minusDays(10), anchor.plusDays(1))
        assertEquals(listOf(anchor, anchor.plusDays(1)), dates)
    }

    @Test
    fun `an empty window generates nothing`() {
        val rule = Recurrence(RecurrenceUnit.DAY)
        assertTrue(rule.instancesBetween(anchor, anchor.plusDays(5), anchor.plusDays(1)).isEmpty())
    }

    @Test
    fun `serialize and parse round-trip`() {
        val rule = Recurrence(
            RecurrenceUnit.WEEK,
            interval = 2,
            daysOfWeek = setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
        )
        assertEquals(rule, Recurrence.parse(rule.serialize()))
    }

    @Test
    fun `a blank or malformed rule parses to no repeat`() {
        assertNull(Recurrence.parse(null))
        assertNull(Recurrence.parse(""))
        assertNull(Recurrence.parse("NONSENSE"))
        assertNull(Recurrence.parse("WEEK:0"))
    }

    @Test
    fun `describe reads as plain English`() {
        assertEquals("Every day", Recurrence(RecurrenceUnit.DAY).describe())
        assertEquals("Every 3 weeks", Recurrence(RecurrenceUnit.WEEK, interval = 3).describe())
        assertEquals(
            "Every week on Mon, Thu",
            Recurrence(
                RecurrenceUnit.WEEK,
                daysOfWeek = setOf(DayOfWeek.MONDAY, DayOfWeek.THURSDAY),
            ).describe(),
        )
    }
}
