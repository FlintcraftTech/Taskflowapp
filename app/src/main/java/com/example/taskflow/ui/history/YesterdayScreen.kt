package com.example.taskflow.ui.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskflow.TaskflowApplication

/**
 * The Yesterday page — immediately left of Today on the spine (SPEC §Yesterday page). The day just
 * gone is the piece of history a person reaches for most often, so it is one swipe rather than a
 * search.
 *
 * Read-only, like the other history surfaces: a completed task is edited or un-completed from a day
 * card (SPEC §Day-detail card layer), not from here. The rows are greyed and struck through, the
 * same visual language the Completed tray on Today already uses, so "done" reads the same wherever
 * the user meets it.
 */
@Composable
fun YesterdayScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val app = context.applicationContext as TaskflowApplication
    val viewModel: YesterdayViewModel = viewModel(
        factory = YesterdayViewModel.factory(app.taskRepository, app.settingsRepository),
    )
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    if (tasks.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            // Stated as a fact about yesterday, not as a shortfall (UX principle 4).
            Text(
                text = "Nothing completed yesterday.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(32.dp),
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        tasks.forEach { task ->
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textDecoration = TextDecoration.LineThrough,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }
    }
}
