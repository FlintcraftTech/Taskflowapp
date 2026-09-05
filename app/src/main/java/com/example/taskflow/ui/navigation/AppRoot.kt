package com.example.taskflow.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskflow.TaskflowApplication
import com.example.taskflow.ui.common.PlaceholderScreen
import com.example.taskflow.ui.onboarding.OnboardingScreen
import com.example.taskflow.ui.onboarding.OnboardingViewModel
import com.example.taskflow.ui.edit.EditTarget
import com.example.taskflow.ui.edit.EditTaskScreen
import com.example.taskflow.ui.schedule.ScheduleScreen
import com.example.taskflow.ui.settings.SettingsScreen
import kotlinx.coroutines.launch

/**
 * Top-level navigation host. Wraps the app in a left-edge drawer (SPEC §Side menu) over the
 * navigation spine. The spine — the four Schedule slots (Today · Tomorrow · Soon · Later) — is the
 * home surface; Projects live inside Later (grouped cards), so there is no separate Project surface.
 * The remaining "deep" destinations — the app actions — are shown as a placeholder overlay. The
 * drawer scrolls the spine for every page's row and opens an overlay only for the rest.
 */
@Composable
fun AppRoot(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val app = context.applicationContext as TaskflowApplication
    val onboarding: OnboardingViewModel =
        viewModel(factory = OnboardingViewModel.factory(app.settingsRepository))

    // First run sits over everything, before the drawer and the spine exist for the user
    // (SPEC §Onboarding — first run). The X drops straight into free-tier use.
    if (onboarding.showing) {
        OnboardingScreen(
            page = onboarding.page,
            onNext = onboarding::next,
            onSkipAi = onboarding::finish,
            // The Claude setup path itself is its own piece of work; until it exists, choosing it
            // finishes onboarding and the side menu's "Turn on AI" entry brings the choice back.
            onSetUpClaude = onboarding::finish,
            onEscape = onboarding::finish,
            modifier = modifier,
        )
        return
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = SpinePage.TODAY.ordinal) { SpinePage.entries.size }
    var overlay by remember { mutableStateOf<Overlay?>(null) }
    // The edit dialogue sits above the spine and any overlay; it's its own state so closing it
    // returns to whatever was underneath rather than the bare spine.
    var editTarget by remember { mutableStateOf<EditTarget?>(null) }

    // Which Project the user is temporarily focused on (SPEC §Focus on one Project temporarily).
    // Held in a plain remember, not rememberSaveable, deliberately: focus must not survive the app
    // closing, and saved state is exactly what would carry it across a process death. It lives here
    // rather than in the Schedule's view-model because capture needs it too — a task added while
    // focused belongs to the focused Project.
    var focusedProjectId by remember { mutableStateOf<Long?>(null) }

    // System back means "up one level", and the levels are checked in the order they sit on screen:
    // the edit dialogue first, then an open overlay, then the spine itself — where back returns to
    // Today and only closes the app from Today (SPEC §Schedule view). Left unhandled on the spine,
    // an edge back-swipe on any page closed the app outright, which nobody chose.
    //
    // Written as a list of cases rather than an if/else pair so a further layer above the spine —
    // the day-detail card — can be added as another case rather than by restructuring this.
    val offTodayOnSpine = editTarget == null && overlay == null &&
        pagerState.currentPage != SpinePage.TODAY.ordinal
    BackHandler(enabled = editTarget != null || overlay != null || offTodayOnSpine) {
        when {
            editTarget != null -> editTarget = null
            overlay != null -> overlay = null
            // Animated rather than jumped, so back reads as travel along the spine and the user can
            // see which direction they came from.
            else -> scope.launch { pagerState.animateScrollToPage(SpinePage.TODAY.ordinal) }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        // Swipe-to-open is off deliberately (SPEC §Side menu): the ☰ is the one opener, so the
        // left-edge drag cannot collide with the spine's horizontal-swipe navigation.
        //
        // Gated on isOpen rather than set flat false, because Material3 hangs BOTH the drag anchor
        // and the scrim's tap-to-close off this one flag — a flat false would silently take the
        // scrim tap with it and trap the user in an open drawer. Closed: gestures off, so no drag
        // can open it. Open: gestures on, so the scrim tap and drag-to-close behave normally.
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            AppDrawer(
                onSlot = { page ->
                    overlay = null
                    scope.launch {
                        drawerState.close()
                        pagerState.scrollToPage(page.ordinal)
                    }
                },
                onAppAction = { action ->
                    // "Turn on AI" re-triggers the AI choice rather than
                    // opening a screen of its own (SPEC §Tier model — free and paid).
                    if (action is Overlay.TurnOnAi) {
                        onboarding.reopenAiChoice()
                    } else {
                        overlay = action
                    }
                    scope.launch { drawerState.close() }
                },
            )
        },
        modifier = modifier,
    ) {
        Scaffold(
            floatingActionButton = {
                // The add FAB (SPEC §Add a new task). It appears on the four Schedule slots — the
                // surfaces whose context a new task inherits — and is hidden over the placeholder
                // overlays and the edit dialogue itself. On Later the new task is created undated and
                // defaults to the Unassigned card.
                // A page with no Schedule slot — Yesterday — is not one of the four add surfaces
                // SPEC §Add a new task names, so the button is absent there rather than disabled.
                val slot = SpinePage.entries[pagerState.currentPage].slot
                val showFab = editTarget == null && overlay == null && slot != null
                if (showFab && slot != null) {
                    // Capture inherits context (UX principle 5), and while focused the focused
                    // Project is the context the user is capturing in.
                    FloatingActionButton(
                        onClick = { editTarget = EditTarget.NewOnSlot(slot, focusedProjectId) },
                    ) {
                        // Text glyph rather than a Material icon — the icon pack isn't a dependency.
                        Text(text = "+", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            val currentEdit = editTarget
            val currentOverlay = overlay
            val contentModifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            val openEditor: (Long) -> Unit = { taskId -> editTarget = EditTarget.Existing(taskId) }

            if (currentEdit != null) {
                EditTaskScreen(
                    target = currentEdit,
                    onClose = { editTarget = null },
                    modifier = contentModifier,
                )
            } else if (currentOverlay == null) {
                ScheduleScreen(
                    pagerState = pagerState,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onTaskClick = openEditor,
                    modifier = contentModifier,
                    focusedProjectId = focusedProjectId,
                    onFocusProject = { focusedProjectId = it },
                )
            } else if (currentOverlay is Overlay.Settings) {
                SettingsScreen(
                    onBack = { overlay = null },
                    modifier = contentModifier,
                )
            } else {
                PlaceholderScreen(
                    title = currentOverlay.label,
                    onBack = { overlay = null },
                    modifier = contentModifier,
                )
            }
        }
    }
}
