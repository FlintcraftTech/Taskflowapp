package com.example.taskflow.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taskflow.data.model.Project
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project: Project): Long

    @Update
    suspend fun update(project: Project)

    @Delete
    suspend fun delete(project: Project)

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getById(id: Long): Project?

    // The system Unassigned Project, kept separable from the ordered user-Project list so the
    // Later screen can pin it to the bottom (pinning lands in the screen batch). There is exactly
    // one is_system row; LIMIT 1 guards regardless.
    @Query("SELECT * FROM projects WHERE is_system = 1 LIMIT 1")
    suspend fun getSystemUnassigned(): Project?

    // User-facing ordered Project lists exclude the system Unassigned Project — it is not a real
    // area of life and must not appear among the user's own Projects.
    @Query("SELECT * FROM projects WHERE is_system = 0 ORDER BY sort_order ASC")
    fun getAllOrdered(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE is_system = 0 ORDER BY sort_order ASC")
    suspend fun getAllOrderedList(): List<Project>

    @Query("UPDATE projects SET sort_order = :newOrder WHERE id = :projectId")
    suspend fun updateSortOrder(projectId: Long, newOrder: Int)

    @Query("SELECT COALESCE(MAX(sort_order), -1) FROM projects")
    suspend fun getMaxSortOrder(): Int

    // Everything, system Project included. For export and import only: those two need the whole
    // table rather than the user-facing view of it.
    @Query("SELECT * FROM projects ORDER BY sort_order ASC")
    suspend fun getAllIncludingSystem(): List<Project>

    // Clears the user's Projects for a replacing import. The system Unassigned Project is left
    // alone — it is seeded, not owned by the user, and every task needs it to exist.
    @Query("DELETE FROM projects WHERE is_system = 0")
    suspend fun deleteAllUserProjects()
}
