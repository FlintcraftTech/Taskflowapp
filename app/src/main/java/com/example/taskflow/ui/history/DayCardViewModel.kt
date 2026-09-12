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
import java.time.LocalDate

/**
 * The days the card layer can move through: every day that holds at least one completed task,
 * oldest first, each with that day's completions newest first.
 *
 * Days with nothing completed are absent rather than empty, which is what makes swiping skip them.
 * A dated card with nothing on it reads as a reproach, and UX principle 4 refuses that.
 *
 * Oldest first because the pager's own direction is the card layer's direction: moving to the next
 * page brings the newer day in from the right, and moving back brings the older day in from the
 * left (SPEC §Day-detail card layer).
 *
 * Days are cut on the day-begins-at boundary rather than midnight (SPEC §Settings → Day begins at),
 * the same boundary every other history surface uses.
 */
class DayCardViewModel(
    taskRepository: TaskRepository,
    settingsRepository: SettingsRepository,
) : ViewModel() {

    val days: StateFlow<List<CompletedDayUi>> =
        combine(
            taskRepository.getCompletedTasks(),
            settingsRepository.settings,
        ) { completed, settings ->
            val hour = settings.dayBeginsAtHour
            completed
                .filter { it.completedAt != null }
                .groupBy { SlotDeriver.logicalDate(it.completedAt!!, dayStartHour = hour) }
                .toSortedMap()
                .map { (day, tasks) ->
                    CompletedDayUi(
                        date = day,
                        tasks = tasks
                            .sortedByDescending { it.completedAt }
                            .map { CompletedTaskUi(id = it.id, title = it.title) },
                    )
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    /**
     * Where the layer should open for a date the user tapped. The date itself may hold nothing —
     * a header can be tapped on a filtered list — so the nearest day at or before it is used, and
     * failing that the nearest after it.
     */
    fun indexFor(days: List<CompletedDayUi>, date: LocalDate): Int {
        val atOrBefore = days.indexOfLast { !it.date.isAfter(date) }
        if (atOrBefore >= 0) return atOrBefore
        return 0
    }

    companion object {
        fun factory(
            taskRepository: TaskRepository,
            settingsRepository: SettingsRepository,
        ) = viewModelFactory {
            initializer { DayCardViewModel(taskRepository, settingsRepository) }
        }
    }
}
