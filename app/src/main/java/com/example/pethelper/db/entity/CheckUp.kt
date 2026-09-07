package com.example.pethelper.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    "checkUps",
    foreignKeys = [ForeignKey(
        Pet::class,
        ["id"],
        ["petId"],
        ForeignKey.CASCADE
    )]
)
data class CheckUp(
    val name: String,
    val note: String?,
    val date: String,
    val time: String,
    val isNotif: Boolean,
    val dateNotif: String?,
    val timeNotif: String?,
    val isDone: Boolean,
    val petId: Int,
    @PrimaryKey(true)
    val id: Int = 0
)