package com.example.taskflow.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.taskflow.data.settings.DateFormat
import com.example.taskflow.data.settings.SettingsRepository
import com.example.taskflow.data.settings.TaskflowSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/** Backs the Settings screen. Thin by design — the repository holds all the behaviour there is. */
class SettingsViewModel(
    private val repository: SettingsRepository,
) : ViewModel() {

    val settings: StateFlow<TaskflowSettings> = repository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), repository.read())

    fun setDayBeginsAtHour(hour: Int) = repository.setDayBeginsAtHour(hour)

    fun setDateFormat(format: DateFormat) = repository.setDateFormat(format)

    companion object {
        fun factory(repository: SettingsRepository) = viewModelFactory {
            initializer { SettingsViewModel(repository) }
        }
    }
}
