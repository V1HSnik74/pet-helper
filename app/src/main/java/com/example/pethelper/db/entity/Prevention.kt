package com.example.pethelper.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    "preventions",
    foreignKeys = [ForeignKey(
        Pet::class,
        ["id"],
        ["petId"],
        ForeignKey.CASCADE
    )
    ]
)
data class Prevention(
    val action: String,
    val note: String?,
    val date: String,
    val isNotif: Boolean,
    val dateNotif: String?,
    val timeNotif: String?,
    val petId: Int,
    val isDone: Boolean,
    @PrimaryKey(true)
    val id: Int = 0
)