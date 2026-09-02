package com.example.taskflow.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskflow.TaskflowApplication
import com.example.taskflow.data.settings.DateFormat
import kotlinx.coroutines.launch

/**
 * The Settings screen (SPEC §Settings), reached from the side menu's bottom section. It holds the
 * app-level controls that belong to no task and no screen: when the user's day begins, how dates
 * are written, and the JSON export/import entries.
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val app = context.applicationContext as TaskflowApplication
    val viewModel: SettingsViewModel =
        viewModel(factory = SettingsViewModel.factory(app.settingsRepository))
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        SettingsHeader(onBack = onBack)
        HorizontalDivider()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            DayBeginsAtSetting(
                hour = settings.dayBeginsAtHour,
                onHourChange = viewModel::setDayBeginsAtHour,
            )
            DateFormatSetting(
                current = settings.dateFormat,
                onFormatChange = viewModel::setDateFormat,
            )
            TransferSection()
        }
    }
}

@Composable
private fun SettingsHeader(onBack: () -> Unit) {
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
            text = "Settings",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f),
        )
    }
}

/**
 * The day boundary (SPEC §Settings → Day begins at) — the only time picker in the whole app
 * (UX principle 7). An hour list rather than a clock dial: Taskflow does dates, not times of day,
 * and a minute-accurate boundary would be a precision nobody needs and a picker nobody enjoys.
 */
@Composable
private fun DayBeginsAtSetting(hour: Int, onHourChange: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(text = "Day begins at", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "When a new day starts for you. Tasks meant for today stay on Today until this hour.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(modifier = Modifier.padding(top = 8.dp)) {
            OutlinedButton(onClick = { expanded = true }) {
                Text(hourLabel(hour), modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                Text(if (expanded) "▾" else "▸")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                (0..23).forEach { candidate ->
                    DropdownMenuItem(
                        text = { Text(hourLabel(candidate)) },
                        onClick = {
                            onHourChange(candidate)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

/** 0 → "12:00 AM", 4 → "4:00 AM", 13 → "1:00 PM". */
private fun hourLabel(hour: Int): String {
    val suffix = if (hour < 12) "AM" else "PM"
    val display = when {
        hour == 0 -> 12
        hour <= 12 -> hour
        else -> hour - 12
    }
    return "$display:00 $suffix"
}

/**
 * Export and the two imports (SPEC §JSON export and import).
 *
 * The two imports are separately named actions rather than one action with a switch, because one
 * destroys the user's data and the other cannot. The destructive one asks for confirmation and
 * says what it will do; the additive one asks nothing, because nothing is lost by running it.
 *
 * Files are chosen through Android's own document picker, so Taskflow never needs storage
 * permission and the user picks where their data goes.
 */
@Composable
private fun TransferSection() {
    val context = LocalContext.current
    val app = context.applicationContext as TaskflowApplication
    val scope = rememberCoroutineScope()
    var pendingReplaceWarning by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            message = runCatching {
                val json = app.transferRepository.exportJson()
                context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
                "Exported."
            }.getOrElse { "Export failed: ${it.message}" }
        }
    }

    val replaceLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            message = runCatching {
                val json = context.contentResolver.openInputStream(uri)
                    ?.use { it.readBytes().decodeToString() }
                    ?: error("Could not read that file.")
                app.transferRepository.importReplacing(json)
                "Imported. Everything was replaced with the file's contents."
            }.getOrElse { "Import failed: ${it.message}" }
        }
    }

    val addLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            message = runCatching {
                val json = context.contentResolver.openInputStream(uri)
                    ?.use { it.readBytes().decodeToString() }
                    ?: error("Could not read that file.")
                app.transferRepository.importAdding(json)
                "Added. Nothing already here was changed."
            }.getOrElse { "Could not add those tasks: ${it.message}" }
        }
    }

    Column {
        Text(text = "Your data", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Everything stays on this device unless you move it yourself.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Column(
            modifier = Modifier.padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = { exportLauncher.launch("taskflow-export.json") },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Export to JSON")
            }
            OutlinedButton(
                onClick = { addLauncher.launch(JSON_MIME_TYPES) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Add tasks from a file")
            }
            OutlinedButton(
                onClick = { pendingReplaceWarning = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Import from JSON")
            }
        }
        message?.let { text ->
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }

    if (pendingReplaceWarning) {
        AlertDialog(
            onDismissRequest = { pendingReplaceWarning = false },
            title = { Text("Replace everything?") },
            text = {
                Text(
                    "Importing replaces every task, Project and Strategy note on this device with " +
                        "what is in the file. This cannot be undone. To add tasks without losing " +
                        "anything, use \"Add tasks from a file\" instead.",
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    pendingReplaceWarning = false
                    replaceLauncher.launch(JSON_MIME_TYPES)
                }) {
                    Text("Replace everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingReplaceWarning = false }) { Text("Cancel") }
            },
        )
    }
}

// Some file providers hand JSON back as plain text or with no type at all, so the picker accepts
// all three rather than showing the user a folder in which their own export is greyed out.
private val JSON_MIME_TYPES = arrayOf("application/json", "text/plain", "*/*")

/** DD/MM or MM/DD, applied everywhere a date is shown (SPEC §Settings → Date format). */
@Composable
private fun DateFormatSetting(current: DateFormat, onFormatChange: (DateFormat) -> Unit) {
    Column {
        Text(text = "Date format", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "How dates are written everywhere in the app.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        DateFormat.entries.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFormatChange(option) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = option == current, onClick = { onFormatChange(option) })
                Text(text = option.label, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

