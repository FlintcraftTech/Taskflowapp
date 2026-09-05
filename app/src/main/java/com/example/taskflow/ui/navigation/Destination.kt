package com.example.taskflow.ui.navigation

import com.example.taskflow.data.model.ScheduleSlot

/**
 * The pages of the navigation spine, left to right (SPEC §Schedule view):
 * Search · Yesterday · Today · Tomorrow · Soon · Later · Strategy. Later is grouped by Project —
 * there is no separate Projects page, Projects live inside Later. Strategy is the right end: the
 * spine's story is that navigation glides from arranging time into arranging areas of life
 * (UX principle 3), which needs Strategy to be somewhere the user can swipe to.
 *
 * [title] is the header label. [slot] maps a page back to its domain slot, and is **null on a page
 * that is not a Schedule slot** — Yesterday is history rather than a place tasks can be put, so it
 * has none. A null slot is also what keeps such a page off the add surfaces: SPEC §Add a new task
 * names the four slots as the only ones, so the add button hides where there is no slot to add to.
 */
enum class SpinePage(val title: String, val slot: ScheduleSlot?) {
    SEARCH("Search", null),
    YESTERDAY("Yesterday", null),
    TODAY("Today", ScheduleSlot.TODAY),
    TOMORROW("Tomorrow", ScheduleSlot.TOMORROW),
    SOON("Soon", ScheduleSlot.SOON),
    LATER("Later", ScheduleSlot.LATER),
    STRATEGY("Strategy", null),
}

/**
 * A destination shown over the spine — reached by a drawer tap, not a swipe. Every Overlay is a
 * placeholder until its real screen is built (Settings 0012, Help/Thanks/Report-a-bug 0022, the AI
 * choice flow 0019). [label] is the title the placeholder shows.
 *
 * Strategy is deliberately not here. The overlay was the right shape while every deep destination
 * was a placeholder; Strategy's real screen shipped, so it became a spine page instead — see
 * [SpinePage.STRATEGY].
 */
sealed interface Overlay {
    val label: String

    data object Settings : Overlay { override val label: String = "Settings" }
    data object Help : Overlay { override val label: String = "Help" }
    data object Thanks : Overlay { override val label: String = "Thanks" }
    data object ReportBug : Overlay { override val label: String = "Report a bug" }
    // The row names its destination and no more. Making the case for the paid tier is the AI choice
    // flow's job, on the screen this row opens (SPEC §Side menu).
    data object TurnOnAi : Overlay { override val label: String = "Turn on AI" }
}
