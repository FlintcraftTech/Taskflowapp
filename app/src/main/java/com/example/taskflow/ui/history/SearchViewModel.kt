package com.example.taskflow.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.taskflow.data.model.ScheduleSlot
import com.example.taskflow.data.model.Task
import com.example.taskflow.data.repository.TaskRepository
import com.example.taskflow.data.settings.SettingsRepository
import com.example.taskflow.domain.SlotDeriver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

/** One day's completed tasks, as the history list groups them. */
data class CompletedDayUi(
    val date: LocalDate,
    val tasks: List<CompletedTaskUi>,
)

/** An active task matching the query — shown above the history, with the slot it lives on. */
data class ActiveResultUi(
    val id: Long,
    val title: String,
    val slot: ScheduleSlot,
)

/** Everything the Search page renders. */
data class SearchUiState(
    val query: String = "",
    val activeResults: List<ActiveResultUi> = emptyList(),
    val completedDays: List<CompletedDayUi> = emptyList(),
)

/**
 * The Search page (SPEC §Search and completed history) — one surface covering everything, active
 * tasks and completed ones together, plus Project names. Two boxes would make the user guess which
 * to open, and someone hunting for a task usually does not know or care whether they finished it.
 *
 * With an empty query the page is the completed history: every completion in completion order,
 * newest first, under a header per day. Typing narrows it, and the headers for the days that still
 * have results stay above them — so a filtered list still reads as a history rather than a pile.
 *
 * Days are cut on the day-begins-at boundary, not midnight (SPEC §Settings → Day begins at), so a
 * task finished at 1 AM sits under the day the user thinks it does.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel(
    taskRepository: TaskRepository,
    settingsRepository: SettingsRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")

    val uiState: StateFlow<SearchUiState> =
        combine(
            query.flatMapLatest { term ->
                // An empty term matches everything, which is exactly the unfiltered history the
                // page opens on — so there is no separate "no query" path to keep in step.
                taskRepository.search(term)
            },
            query,
            settingsRepository.settings,
        ) { matches, term, settings ->
            val hour = settings.dayBeginsAtHour
            val now = System.currentTimeMillis()

            val active = matches
                .filter { !it.isCompleted }
                // An active result is only useful if the page can say where to find it.
                .map { task ->
                    ActiveResultUi(
                        id = task.id,
                        title = task.title,
                        slot = task.slotFor(now, hour),
                    )
                }

            val completedDays = matches
                .filter { it.isCompleted && it.completedAt != null }
                .sortedByDescending { it.completedAt }
                .groupBy { SlotDeriver.logicalDate(it.completedAt!!, dayStartHour = hour) }
                .map { (day, tasks) ->
                    CompletedDayUi(
                        date = day,
                        tasks = tasks.map { CompletedTaskUi(id = it.id, title = it.title) },
                    )
                }

            SearchUiState(
                query = term,
                activeResults = active,
                completedDays = completedDays,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchUiState(),
        )

    fun setQuery(value: String) {
        query.value = value
    }

    /** Where a task currently lives: its date's slot, or the slot it is parked on if undated. */
    private fun Task.slotFor(nowMillis: Long, dayStartHour: Int): ScheduleSlot =
        date?.let { SlotDeriver.slotForDate(it, nowMillis, dayStartHour = dayStartHour) }
            ?: slot
            ?: ScheduleSlot.LATER

    companion object {
        fun factory(
            taskRepository: TaskRepository,
            settingsRepository: SettingsRepository,
        ) = viewModelFactory {
            initializer { SearchViewModel(taskRepository, settingsRepository) }
        }
    }
}
