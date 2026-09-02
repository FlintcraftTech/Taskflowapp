package com.example.taskflow.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taskflow.data.model.LifeArea

/**
 * The life-area table's access layer. It has **no user-facing caller**: life areas are Claude's
 * framing layer and are reached only through the MCP tools on the paid tier (SPEC §Strategy doc).
 * Nothing in the app's own screens reads or writes them.
 */
@Dao
interface LifeAreaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(area: LifeArea): Long

    @Update
    suspend fun update(area: LifeArea)

    @Delete
    suspend fun delete(area: LifeArea)

    @Query("SELECT * FROM life_areas ORDER BY sort_order ASC")
    suspend fun getAllOrdered(): List<LifeArea>

    @Query("SELECT * FROM life_areas WHERE id = :id")
    suspend fun getById(id: Long): LifeArea?
}
