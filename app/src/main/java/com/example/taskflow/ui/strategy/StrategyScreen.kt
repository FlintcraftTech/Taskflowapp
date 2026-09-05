package com.example.taskflow.ui.strategy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * The Strategy doc (SPEC §Strategy doc): one heading per Project, generated from the Project's
 * name, with a paragraph beneath it the user writes.
 *
 * Headings are **not editable here** — they are Project names, and renaming an area of life is not
 * a thing to do by typing over a heading in a document. The doc reads roughly chronologically by
 * priority, which is a shape the user gives it through what they write rather than one the app
 * imposes.
 *
 * The page carries no header of its own: as the spine's rightmost page it is named by the spine
 * header, which also holds the Share action that hands the doc to Android's standard share sheet.
 * Sharing the strategic picture with the people in the user's life is part of what gives a Strategy
 * doc its function.
 */
@Composable
fun StrategyScreen(
    sections: List<StrategySection>,
    onDescriptionChange: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
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
                            onValueChange = { onDescriptionChange(section.projectId, it) },
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
