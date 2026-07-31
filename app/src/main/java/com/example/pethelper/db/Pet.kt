package com.example.pethelper.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class Pet(
    val name: String,
    val breed: String,
    val sex: String,
    val photo: String? = null,
    val age: Int? = null,
    val weight: Float? = null,
    val height: Float? = null,
    val about: String? = null,
    val birthday: String? = null,
    val color: String? = null,
    val neutered: String? = null,
    val microchipId: String? = null,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)