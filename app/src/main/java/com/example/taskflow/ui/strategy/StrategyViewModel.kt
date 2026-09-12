package com.example.taskflow.ui.strategy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.taskflow.data.model.StrategyEntry
import com.example.taskflow.data.repository.ProjectRepository
import com.example.taskflow.data.repository.StrategyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * One Project's place in the Strategy doc: the heading Taskflow generates, and the paragraph the
 * user writes under it.
 */
data class StrategySection(
    val projectId: Long,
    val heading: String,
    val description: String,
)

/**
 * Backs the Strategy doc (SPEC §Strategy doc).
 *
 * The doc's **structure is mechanically generated**: one heading per Project, in Project order,
 * with the user writing only the paragraphs. That is what lets a free-tier user have a coherent
 * document without Claude in the loop — there is no document to lay out, only descriptions to
 * write. The system Unassigned Project is excluded: it is not an area of life, only the home for
 * tasks the user hasn't filed.
 */
class StrategyViewModel(
    private val projectRepository: ProjectRepository,
    private val strategyRepository: StrategyRepository,
) : ViewModel() {

    val sections: StateFlow<List<StrategySection>> =
        combine(
            projectRepository.getAllOrdered(),
            strategyRepository.getAllOrdered(),
        ) { projects, entries ->
            val byProject = entries.associateBy { it.projectId }
            // Driven by the Project list, not the entry list: a Project with no entry yet still
            // gets a heading and an empty paragraph, so a newly created Project appears in the doc
            // immediately rather than when someone happens to write about it.
            projects.map { project ->
                StrategySection(
                    projectId = project.id,
                    heading = project.name,
                    description = byProject[project.id]?.content.orEmpty(),
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Writes one Project's paragraph. Headings are not editable here — they are Project names. */
    fun setDescription(projectId: Long, description: String) {
        viewModelScope.launch {
            val existing = strategyRepository.getByProjectId(projectId)
            strategyRepository.upsert(
                existing?.copy(content = description)
                    ?: StrategyEntry(projectId = projectId, content = description),
            )
        }
    }

    /**
     * Sets Project order from the Strategy doc, which owns it app-wide — including the order of the
     * cards on Later (SPEC §Strategy doc).
     *
     * The whole sequence is rewritten to 0..n-1 rather than nudging the moved row, the same pattern
     * the Schedule reorders use: positions drift as Projects come and go, so writing the sequence
     * out is what keeps the order well-defined.
     */
    fun reorderProjects(orderedProjectIds: List<Long>) {
        viewModelScope.launch {
            orderedProjectIds.forEachIndexed { index, projectId ->
                projectRepository.updateSortOrder(projectId, index)
            }
        }
    }

    /** The whole doc as markdown — what the share button hands to Android's share sheet. */
    fun renderMarkdown(sections: List<StrategySection>): String = buildString {
        sections.forEachIndexed { index, section ->
            if (index > 0) append("\n\n")
            append("## ").append(section.heading).append("\n\n")
            append(section.description.ifBlank { "_Nothing written yet._" })
        }
    }

    companion object {
        fun factory(
            projectRepository: ProjectRepository,
            strategyRepository: StrategyRepository,
        ) = viewModelFactory {
            initializer { StrategyViewModel(projectRepository, strategyRepository) }
        }
    }
}
