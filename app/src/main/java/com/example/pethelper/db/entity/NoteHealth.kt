package com.example.pethelper.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    "notes_health",
    indices = [Index(value = ["petId"])],
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
    val id: Long = 0
)