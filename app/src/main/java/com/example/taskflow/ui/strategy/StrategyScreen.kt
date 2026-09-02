package com.example.taskflow.ui.strategy

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskflow.TaskflowApplication

/**
 * The Strategy doc (SPEC §Strategy doc): one heading per Project, generated from the Project's
 * name, with a paragraph beneath it the user writes.
 *
 * Headings are **not editable here** — they are Project names, and renaming an area of life is not
 * a thing to do by typing over a heading in a document. The doc reads roughly chronologically by
 * priority, which is a shape the user gives it through what they write rather than one the app
 * imposes.
 *
 * A share button hands the doc to Android's standard share sheet. Sharing the strategic picture
 * with the people in the user's life is part of what gives a Strategy doc its function.
 */
@Composable
fun StrategyScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val app = context.applicationContext as TaskflowApplication
    val viewModel: StrategyViewModel = viewModel(
        factory = StrategyViewModel.factory(app.projectRepository, app.strategyRepository),
    )
    val sections by viewModel.sections.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                // Text glyph rather than a Material icon — the icon pack isn't a project dependency.
                Text(
                    text = "←",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                text = "Strategy",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
            )
            TextButton(
                onClick = {
                    val share = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, viewModel.renderMarkdown(sections))
                    }
                    context.startActivity(Intent.createChooser(share, null))
                },
                enabled = sections.isNotEmpty(),
            ) {
                Text("Share")
            }
        }
        HorizontalDivider()

        if (sections.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Your Projects appear here as headings, with room to write about each " +
                        "one. Make a Project and it shows up.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp),
                )
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            sections.forEach { section ->
                key(section.projectId) {
                    Column {
                        Text(
                            text = section.heading,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        OutlinedTextField(
                            value = section.description,
                            onValueChange = { viewModel.setDescription(section.projectId, it) },
                            placeholder = { Text("What this Project is for, and when.") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                        )
                    }
                }
            }
        }
    }
}
