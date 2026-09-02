package com.example.taskflow

import android.app.Application
import com.example.taskflow.data.local.TaskflowDatabase
import com.example.taskflow.data.repository.ProjectRepository
import com.example.taskflow.data.repository.StrategyRepository
import com.example.taskflow.data.repository.TaskRepository
import com.example.taskflow.data.settings.SettingsRepository
import com.example.taskflow.data.transfer.TransferRepository

class TaskflowApplication : Application() {

    val database: TaskflowDatabase by lazy {
        TaskflowDatabase.getInstance(this)
    }

    val taskRepository: TaskRepository by lazy {
        TaskRepository(database.taskDao())
    }

    val projectRepository: ProjectRepository by lazy {
        // taskDao is passed so deleting a Project can reassign its tasks to Unassigned (FK is RESTRICT).
        ProjectRepository(database.projectDao(), database.taskDao())
    }

    val strategyRepository: StrategyRepository by lazy {
        StrategyRepository(database.strategyEntryDao())
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(this)
    }

    val transferRepository: TransferRepository by lazy {
        TransferRepository(database.taskDao(), database.projectDao(), database.strategyEntryDao())
    }
}
