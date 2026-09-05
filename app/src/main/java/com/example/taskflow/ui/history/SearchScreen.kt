package com.example.taskflow.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskflow.TaskflowApplication
import com.example.taskflow.data.model.ScheduleSlot
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * The Search page — the spine's leftmost (SPEC §Search and completed history). One search field over
 * one list covering everything: active tasks and completed ones, matched on task title or Project
 * name. It is the one surface that ignores the app's own structure, because a task someone is
 * searching for is a task they have lost, and horizon and Project are exactly what they cannot use
 * at that moment.
 *
 * **Results are read-only.** Nothing here completes or edits a task: tapping an active result
 * navigates to where that task actually lives. Editing and un-completing happen only from a day card
 * (SPEC §Day-detail card layer), which is what stops a tappable list of completed tasks becoming a
 * second place to change things — so a completed row is inert until that layer is built.
 */
@Composable
fun SearchScreen(
    onNavigateToSlot: (ScheduleSlot) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val app = context.applicationContext as TaskflowApplication
    val viewModel: SearchViewModel = viewModel(
        factory = SearchViewModel.factory(app.taskRepository, app.settingsRepository),
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = uiState.query,
            onValueChange = viewModel::setQuery,
            singleLine = true,
            label = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        )

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            uiState.activeResults.forEach { result ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToSlot(result.slot) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Text(text = result.title, style = MaterialTheme.typography.bodyLarge)
                    // Where it lives, so the tap's destination is stated before it happens.
                    Text(
                        text = result.slot.pageName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            uiState.completedDays.forEach { day ->
                Text(
                    text = day.date.headerLabel(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
                )
                day.tasks.forEach { task ->
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = TextDecoration.LineThrough,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                    )
                }
            }

            if (uiState.activeResults.isEmpty() && uiState.completedDays.isEmpty()) {
                Text(
                    text = if (uiState.query.isBlank()) {
                        "Nothing completed yet."
                    } else {
                        "Nothing matches that."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(32.dp),
                )
            }
        }
    }
}

/** The Schedule page a slot is shown as, for the "where this lives" line under a result. */
private val ScheduleSlot.pageName: String
    get() = when (this) {
        ScheduleSlot.TODAY -> "Today"
        ScheduleSlot.TOMORROW -> "Tomorrow"
        ScheduleSlot.SOON -> "Soon"
        ScheduleSlot.LATER -> "Later"
    }

/**
 * A day header. The month is named rather than numbered, so the date-format setting has nothing to
 * disambiguate here and does not reach it (SPEC §Settings → Date format).
 */
private val HEADER_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM")

private fun LocalDate.headerLabel(): String = format(HEADER_FORMAT)
