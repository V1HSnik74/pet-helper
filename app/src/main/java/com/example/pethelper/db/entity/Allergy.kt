package com.example.pethelper.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "allergies",
    indices = [Index(value = ["petId"])],
    foreignKeys = [ForeignKey(
    entity = Pet::class,
    parentColumns = ["id"],
    childColumns = ["petId"],
    onDelete = ForeignKey.CASCADE
)])
data class Allergy (
    val name: String,
    val petId: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)