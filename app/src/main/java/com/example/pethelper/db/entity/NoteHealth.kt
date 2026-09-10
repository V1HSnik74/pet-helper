package com.example.pethelper.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    "notes_health",
    foreignKeys = [ForeignKey(
        Pet::class,
        ["id"],
        ["petId"],
        ForeignKey.CASCADE
    )]
)
data class NoteHealth(
    val note: String,
    val date: String,
    val petId: Int,
    val category: String,
    @PrimaryKey(true)
    val id: Int = 0
)