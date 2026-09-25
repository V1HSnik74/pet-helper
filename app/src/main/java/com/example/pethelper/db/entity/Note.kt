package com.example.pethelper.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val category: String,
    val description: String,
    val petId: Int
)