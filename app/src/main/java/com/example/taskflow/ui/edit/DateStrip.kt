package com.example.taskflow.ui.edit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs

/**
 * The side-scrolling date strip (SPEC §Date picker — side-scrolling date strip): a horizontal,
 * side-scrollable row of date tiles embedded in the edit dialogue rather than a popup calendar.
 *
 * Layout, left to right: a visually distinct "No date" tile, then one tile per day from
 * [DAYS_BEFORE] days before today to [DAYS_AFTER] days after it. Today is the visual anchor —
 * labelled and weighted — and tiles fade linearly with distance from today, floored at
 * [MIN_TILE_ALPHA] so even a far-off tile stays readable rather than disappearing. Tapping a tile
 * selects that date; tapping "No date" clears it.
 *
 * The month-jump row above the strip crosses the twelve-month span quickly (a month per tap) and
 * names the month currently in view, so a long scrub is not the only way to reach next spring.
 */
@Composable
fun DateStrip(
    selectedDate: LocalDate?,
    today: LocalDate,
    dateFormatter: DateTimeFormatter,
    onSelectDate: (LocalDate) -> Unit,
    onClearDate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Index 0 is the "no date" tile; index 1 is the first real day. dateAt/indexOf convert between
    // the two spaces, and every scroll target below is expressed as a list index.
    val firstDate = remember(today) { today.minusDays(DAYS_BEFORE) }
    val dayCount = (DAYS_BEFORE + DAYS_AFTER + 1).toInt()
    val itemCount = dayCount + 1

    fun dateAt(index: Int): LocalDate = firstDate.plusDays((index - 1).toLong())
    fun indexOf(date: LocalDate): Int =
        (java.time.temporal.ChronoUnit.DAYS.between(firstDate, date) + 1).toInt()

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Open centred on the task's own date, or on today when it is undated. Keyed on the initial
    // selection only: re-centring on every tap would fight the user's own scrolling.
    LaunchedEffect(Unit) {
        val target = indexOf(selectedDate ?: today).coerceIn(0, itemCount - 1)
        listState.scrollToItem((target - CENTRING_OFFSET).coerceAtLeast(0))
    }

    // The month named in the jump row: the month of the leftmost real tile currently in view.
    val visibleMonth by remember {
        derivedStateOf {
            val index = listState.firstVisibleItemIndex.coerceIn(1, itemCount - 1)
            dateAt(index)
        }
    }

    fun jumpMonths(delta: Long) {
        val target = indexOf(visibleMonth.plusMonths(delta)).coerceIn(1, itemCount - 1)
        scope.launch { listState.animateScrollToItem((target - CENTRING_OFFSET).coerceAtLeast(0)) }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            TextButton(onClick = { jumpMonths(-1) }) { Text("‹ month") }
            Text(
                text = visibleMonth.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()) +
                    " " + visibleMonth.year,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = { jumpMonths(1) }) { Text("month ›") }
        }

        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(TILE_GAP),
            contentPadding = PaddingValues(vertical = 4.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            item(key = "no-date") {
                NoDateTile(selected = selectedDate == null, onClick = onClearDate)
            }
            items(count = dayCount, key = { it }) { dayIndex ->
                val date = firstDate.plusDays(dayIndex.toLong())
                DateTile(
                    date = date,
                    label = date.format(dateFormatter),
                    isToday = date == today,
                    selected = date == selectedDate,
                    distanceFromToday = abs(
                        java.time.temporal.ChronoUnit.DAYS.between(today, date),
                    ),
                    onClick = { onSelectDate(date) },
                )
            }
        }
    }
}

/**
 * The clear-the-date tile at the strip's left edge. Deliberately not a faded date tile: it carries
 * a word rather than numbers and a dashed outline, so it reads as a choice the user makes rather
 * than as a date they have scrolled too far from (SPEC §Date picker — side-scrolling date strip).
 */
@Composable
private fun NoDateTile(selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(TILE_WIDTH)
            .height(TILE_HEIGHT)
            .clip(RoundedCornerShape(TILE_CORNER))
            .background(if (selected) colors.primaryContainer else colors.surfaceVariant)
            .border(
                BorderStroke(if (selected) 2.dp else 1.dp, colors.outline),
                RoundedCornerShape(TILE_CORNER),
            )
            .clickable(onClick = onClick),
    ) {
        Text(
            text = "No\ndate",
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            color = if (selected) colors.onPrimaryContainer else colors.onSurfaceVariant,
        )
    }
}

/**
 * One day. [distanceFromToday] drives a linear fade with a floor, so distance is legible as a
 * gradient without any tile becoming unreadable, and the fade is continuous rather than stepped at
 * the Schedule-slot boundaries — the strip is about days, not slots.
 */
@Composable
private fun DateTile(
    date: LocalDate,
    label: String,
    isToday: Boolean,
    selected: Boolean,
    distanceFromToday: Long,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val fade = (1f - (distanceFromToday.toFloat() / FADE_SPAN)).coerceIn(MIN_TILE_ALPHA, 1f)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(TILE_WIDTH)
            .height(TILE_HEIGHT)
            .clip(RoundedCornerShape(TILE_CORNER))
            .background(if (selected) colors.primaryContainer else colors.surface)
            .then(
                if (selected || isToday) {
                    Modifier.border(
                        BorderStroke(
                            if (selected) 2.dp else 1.dp,
                            if (selected) colors.primary else colors.outlineVariant,
                        ),
                        RoundedCornerShape(TILE_CORNER),
                    )
                } else {
                    Modifier
                },
            )
            .clickable(onClick = onClick)
            // Selection and the today anchor stay at full strength; only unselected ordinary tiles
            // carry the distance fade, so the two things the eye looks for are never dimmed.
            .alpha(if (selected || isToday) 1f else fade),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isToday) {
                    "Today"
                } else {
                    date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                },
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) colors.onPrimaryContainer else colors.onSurface,
            )
        }
    }
}

// Roughly one month back and twelve forward, per the item's stated span.
private const val DAYS_BEFORE: Long = 30
private const val DAYS_AFTER: Long = 365

// Tiles are sized so five to seven sit on a typical phone width (a 360dp screen fits six).
private val TILE_WIDTH = 52.dp
private val TILE_HEIGHT = 56.dp
private val TILE_GAP = 6.dp
private val TILE_CORNER = 8.dp

// Scrolling puts the target tile roughly mid-strip rather than hard against the left edge.
private const val CENTRING_OFFSET: Int = 3

// The fade reaches its floor well before the strip's far end, so the near weeks — where nearly
// every date assignment lands — carry the visible part of the gradient.
private const val FADE_SPAN: Float = 60f
private const val MIN_TILE_ALPHA: Float = 0.45f
