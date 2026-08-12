package com.example.pethelper.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "notes",
    foreignKeys = [ForeignKey(
        entity = Pet::class,
        parentColumns = ["id"], childColumns = ["petId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val category: String,
    val description: String,
    @ColumnInfo(index = true)
    val petId: Int
)

