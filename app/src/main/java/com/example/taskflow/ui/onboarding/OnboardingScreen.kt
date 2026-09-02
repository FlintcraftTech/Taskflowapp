package com.example.taskflow.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** The pages of the first-run flow, in order (SPEC §Onboarding — first run). */
enum class OnboardingPage {
    /** Schedule: where you commit to what's next. */
    SCHEDULE,

    /** Projects: where you keep what's possible, with no time pressure. */
    PROJECTS,

    /** The multi-page video showing what Claude adds on the paid tier. */
    VIDEO,

    /** The real choice at the end: free, or set Claude up. */
    CHOICE,
}

/**
 * First-run onboarding (SPEC §Onboarding — first run).
 *
 * Two cards make the Schedule / Projects distinction explicit, because that split is the structural
 * thing a user has to understand before the app makes sense. Then the video showing what the paid
 * tier is actually for, then a choice presented as a real one — the free tier is a complete
 * product, not a hobbled trial, so the user is shown what they are choosing between rather than
 * pushed past it.
 *
 * The X in the corner leaves at any point and drops the user straight into free-tier use. It is on
 * every page, including the choice: an escape that vanishes when the decision arrives is not an
 * escape.
 */
@Composable
fun OnboardingScreen(
    page: OnboardingPage,
    onNext: () -> Unit,
    onSkipAi: () -> Unit,
    onSetUpClaude: () -> Unit,
    onEscape: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Wrapped in a Surface, which is what gives this screen a background *and* the matching text
    // colour for everything inside it. Onboarding is the one screen that runs before AppRoot's
    // Scaffold exists, so without this it inherits no theme surface: it painted nothing and its
    // text fell back to the default near-black, which on a dark-themed device is unreadable.
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize(),
    ) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            when (page) {
                OnboardingPage.SCHEDULE -> ExplainerCard(
                    title = "Today, Tomorrow, Soon, Later",
                    body = "Your schedule is what you have committed to, sliced by how far away it " +
                        "is. Swipe between the four to see what is next.",
                    onNext = onNext,
                )

                OnboardingPage.PROJECTS -> ExplainerCard(
                    title = "Projects hold what's possible",
                    body = "Projects are the areas of your life. Things can sit there with no date " +
                        "and no pressure until you are ready to commit to them.",
                    onNext = onNext,
                )

                OnboardingPage.VIDEO -> VideoPlaceholder(onNext = onNext)

                OnboardingPage.CHOICE -> ChoicePage(
                    onSkipAi = onSkipAi,
                    onSetUpClaude = onSetUpClaude,
                )
            }
        }

        // The escape hatch, present on every page including the last.
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(48.dp)
                .clip(CircleShape)
                .clickable(onClick = onEscape),
            contentAlignment = Alignment.Center,
        ) {
            // Text glyph rather than a Material icon — the icon pack isn't a project dependency.
            Text(text = "✕", style = MaterialTheme.typography.titleLarge)
        }
    }
    }
}

@Composable
private fun ExplainerCard(title: String, body: String, onNext: () -> Unit) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
    )
    Text(
        text = body,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 12.dp),
    )
    Button(onClick = onNext, modifier = Modifier.padding(top = 24.dp)) {
        Text("Next")
    }
}

/**
 * Stands in for the onboarding video until it is filmed. What the video says is being designed
 * separately (the onboarding video script item) and filming waits on the Claude integration
 * existing, so this ships as a placeholder deliberately rather than as a gap.
 */
@Composable
private fun VideoPlaceholder(onNext: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp),
            ),
    ) {
        Text(
            text = "A short video showing what Claude adds goes here.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(24.dp),
        )
    }
    Button(onClick = onNext, modifier = Modifier.padding(top = 24.dp)) {
        Text("Next")
    }
}

/**
 * The two choices, given equal weight on purpose. Free is not a lesser path — it is the whole app
 * without Claude — so it is not styled as the thing to talk the user out of.
 */
@Composable
private fun ChoicePage(onSkipAi: () -> Unit, onSetUpClaude: () -> Unit) {
    Text(
        text = "Want Claude in the loop?",
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
    )
    Text(
        text = "Taskflow works completely on its own. With Claude connected, you can talk about " +
            "your projects wherever you already talk to Claude.",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 12.dp),
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(top = 24.dp),
    ) {
        OutlinedButton(onClick = onSkipAi) { Text("Skip AI for now") }
        Button(onClick = onSetUpClaude) { Text("How do I set up Claude?") }
    }
    TextButton(onClick = onSkipAi, modifier = Modifier.padding(top = 8.dp)) {
        Text("You can turn this on later from the menu.")
    }
}
