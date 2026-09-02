package com.example.taskflow.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.taskflow.data.settings.SettingsRepository

/**
 * Drives the first-run flow (SPEC §Onboarding — first run).
 *
 * [showing] is true only until the flow is finished or escaped. It starts from the stored flag, so
 * a returning user never meets onboarding again, and it is held here rather than recomputed from
 * the flow of settings — the flow is a single pass through a sequence, not a reactive view of a
 * setting, and treating it as one would make it flicker away the instant the flag is written.
 */
class OnboardingViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    var showing by mutableStateOf(!settingsRepository.read().onboardingSeen)
        private set

    var page by mutableStateOf(OnboardingPage.SCHEDULE)
        private set

    fun next() {
        val entries = OnboardingPage.entries
        val index = entries.indexOf(page)
        if (index < entries.lastIndex) page = entries[index + 1] else finish()
    }

    /**
     * Ends the flow, whether the user reached the end or took the X. Both count as having seen it:
     * dismissing something is a decision, and re-showing it would be nagging.
     */
    fun finish() {
        settingsRepository.setOnboardingSeen()
        showing = false
    }

    /** Re-opens the AI choice from the side menu, without re-running the explainer cards. */
    fun reopenAiChoice() {
        page = OnboardingPage.CHOICE
        showing = true
    }

    companion object {
        fun factory(settingsRepository: SettingsRepository) = viewModelFactory {
            initializer { OnboardingViewModel(settingsRepository) }
        }
    }
}
