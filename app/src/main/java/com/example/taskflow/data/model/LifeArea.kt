package com.example.taskflow.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One life area in the picture Claude carries (SPEC §Strategy doc, `SYSTEM-PROMPT.md`).
 *
 * Life areas are deliberately **not** in the Strategy doc's structure and nowhere in the UI: they
 * are an abstract framing layer Claude builds and uses on the paid tier, reached only through the
 * MCP tools. The table exists on the free tier too and simply stays empty — a schema is cheaper to
 * carry than a migration is later, and nothing on a free-tier device ever writes to it.
 *
 * [notes] is Claude's own working understanding of the area, in prose, rather than a fixed set of
 * fields: what makes an area of someone's life is not a shape a schema can anticipate, and the one
 * consumer is a language model.
 */
@Entity(tableName = "life_areas")
data class LifeArea(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val notes: String = "",

    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0,
)
