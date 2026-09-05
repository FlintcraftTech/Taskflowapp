package com.example.taskflow.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.taskflow.data.repository.TaskRepository
import com.example.taskflow.data.settings.SettingsRepository
import com.example.taskflow.domain.SlotDeriver
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** One completed task as the history surfaces render it. */
data class CompletedTaskUi(
    val id: Long,
    val title: String,
)

/**
 * The Yesterday page's state: what the user completed yesterday, newest first (SPEC §Yesterday page).
 *
 * Yesterday's bounds come from the day-begins-at setting rather than from midnight (SPEC §Settings →
 * Day begins at) — the same boundary the Schedule buckets tasks against, so a task completed at 1 AM
 * belongs to the same day on both surfaces.
 *
 * Completions are all the page shows, and that is not a simplification: tasks that slipped past their
 * date stay on Today rather than falling backwards (UX principle 4), so nothing else is left behind.
 */
class YesterdayViewModel(
    taskRepository: TaskRepository,
    settingsRepository: SettingsRepository,
) : ViewModel() {

    val tasks: StateFlow<List<CompletedTaskUi>> =
        combine(
            taskRepository.getCompletedTasks(),
            settingsRepository.settings,
        ) { completed, settings ->
            val hour = settings.dayBeginsAtHour
            val yesterday = SlotDeriver
                .logicalDate(System.currentTimeMillis(), dayStartHour = hour)
                .minusDays(1)
            completed
                .filter { task ->
                    val at = task.completedAt
                    at != null && SlotDeriver.logicalDate(at, dayStartHour = hour) == yesterday
                }
                // Newest first: the last thing finished is the one a person reaches back for.
                .sortedByDescending { it.completedAt }
                .map { CompletedTaskUi(id = it.id, title = it.title) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    companion object {
        fun factory(
            taskRepository: TaskRepository,
            settingsRepository: SettingsRepository,
        ) = viewModelFactory {
            initializer { YesterdayViewModel(taskRepository, settingsRepository) }
        }
    }
}
