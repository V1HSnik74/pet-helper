package com.example.pethelper.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes_nutrition", foreignKeys = [ForeignKey(
        entity = Pet::class,
        parentColumns = ["id"],
        childColumns = ["petId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class NoteNutrition(
    val note: String,
    val category: String,
    val petId: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)