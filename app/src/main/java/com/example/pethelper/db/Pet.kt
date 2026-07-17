package com.example.pethelper.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class Pet(
    val name: String,
    val breed: String,
    val sex: String,
    val photo: String? = null,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)