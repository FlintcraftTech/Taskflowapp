package com.example.taskflow.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskflow.TaskflowApplication
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * The day-detail card layer (SPEC §Day-detail card layer): one day, in full, as a card in the
 * foreground over whichever page opened it.
 *
 * It moves on a deliberately different left-right axis from the spine, and the card visual is what
 * signals the difference — the user is no longer moving along the spine, they are moving through
 * days. A day is the unit history is actually remembered in, so giving days their own axis keeps
 * "which day am I looking at?" from competing with "which horizon am I looking at?".
 *
 * Three behaviours the design settled:
 *
 * - **Days with nothing completed are skipped.** The pager's pages are only days that hold a
 *   completion, so a swipe lands on the next day that has something.
 * - **The far end bounces.** Swiping right past the oldest such day does nothing — the pager's own
 *   start, no special case and no message.
 * - **Swiping left past the newest card leaves.** One page beyond the newest day is a blank leaving
 *   page: settling on it carries the card layer and the Search page off together, landing the user
 *   on Yesterday. That is why the pager has one more page than there are days.
 *
 * This is the only place a completed task can be edited or un-completed; tapping one here opens its
 * edit dialogue. The search results themselves stay read-only, so a tappable list of completed
 * tasks does not become a second place to change things.
 */
@Composable
fun DayCardLayer(
    date: LocalDate,
    onTaskClick: (Long) -> Unit,
    onExitToYesterday: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val app = context.applicationContext as TaskflowApplication
    val viewModel: DayCardViewModel = viewModel(
        factory = DayCardViewModel.factory(app.taskRepository, app.settingsRepository),
    )
    val days by viewModel.days.collectAsStateWithLifecycle()

    if (days.isEmpty()) return

    val startIndex = viewModel.indexFor(days, date)
    // The extra trailing page is the leaving page described above.
    val pagerState = rememberPagerState(initialPage = startIndex) { days.size + 1 }

    LaunchedEffect(pagerState.settledPage, days.size) {
        if (pagerState.settledPage == days.size) onExitToYesterday()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f)),
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            // The leaving page renders nothing: it exists to be settled on, and the moment it is
            // the layer is already on its way out.
            if (page >= days.size) return@HorizontalPager
            val day = days[page]

            Card(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                ) {
                    Text(
                        text = day.date.format(CARD_HEADER_FORMAT),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    day.tasks.forEach { task ->
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTaskClick(task.id) }
                                .padding(vertical = 10.dp),
                        )
                    }
                }
            }
        }
    }
}

/**
 * The card's own date line. The month is named rather than numbered, so the date-format setting has
 * nothing to disambiguate here and does not reach it (SPEC §Settings → Date format).
 */
private val CARD_HEADER_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM")
