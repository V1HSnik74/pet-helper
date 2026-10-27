package com.vhsnik.pethelper.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class NoteSection{
    COMMON, NUTRITION, OVERALL_HEALTH, CHECK_UPS
}

@Entity(
    tableName = "notes",
    indices = [Index(value = ["petId"])],
    foreignKeys = [ForeignKey(
        entity = Pet::class,
        parentColumns = ["id"], childColumns = ["petId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Note(
    val description: String,
    val petId: Int,
    val section: NoteSection,
    val date: String? = null,
    val category: String? = null,
    @PrimaryKey(true)
    val id: Long = 0
)