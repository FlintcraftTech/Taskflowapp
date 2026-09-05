package com.example.taskflow.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * The side drawer: one navigation list mirroring the spine top to bottom, then a single Strategy doc
 * row, with the app actions pinned apart at the bottom (SPEC §Side menu). Every page on the spine
 * gets a row, in spine order — the list is built from SpinePage itself, so a page added to the spine
 * arrives here with it rather than needing a row written by hand. Tapping a row moves the spine;
 * tapping the Strategy row or an app action opens a placeholder in this batch. Projects are not
 * listed here — they live inside Later, which is how the user reaches any Project.
 */
@Composable
fun AppDrawer(
    onSlot: (SpinePage) -> Unit,
    onAppAction: (Overlay) -> Unit,
    modifier: Modifier = Modifier,
) {
    // The sheet wraps its own content rather than taking Material3's default width, which the app
    // never passed and which left a drawer far wider than anything in it. Bounded by that default
    // as a maximum, so a long row can never push the sheet across the whole screen. Sized to the
    // content instead of to a chosen number: an invented dp figure would have no derivation and
    // would rot the next time a row is reworded.
    ModalDrawerSheet(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .widthIn(max = DrawerDefaults.MaximumDrawerWidth),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Navigation list.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                // Strategy is in here rather than below because it is a spine page like the rest —
                // its row scrolls the pager to it, exactly as the slot rows do.
                SpinePage.entries.forEach { page ->
                    DrawerRow(text = page.title) { onSlot(page) }
                }
            }
            // App actions, pinned apart at the bottom.
            HorizontalDivider()
            DrawerRow(text = "Settings") { onAppAction(Overlay.Settings) }
            DrawerRow(text = "Help") { onAppAction(Overlay.Help) }
            DrawerRow(text = "Thanks") { onAppAction(Overlay.Thanks) }
            DrawerRow(text = "Report a bug") { onAppAction(Overlay.ReportBug) }
            DrawerRow(text = "Turn on AI") { onAppAction(Overlay.TurnOnAi) }
        }
    }
}

@Composable
private fun DrawerRow(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    )
}
