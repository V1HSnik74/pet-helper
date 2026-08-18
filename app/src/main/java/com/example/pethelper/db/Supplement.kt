package com.example.pethelper.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "supplements", foreignKeys = [ForeignKey(
        entity = Pet::class,
        parentColumns = ["id"],
        childColumns = ["petId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Supplement(
    val name: String,
    val petId: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)