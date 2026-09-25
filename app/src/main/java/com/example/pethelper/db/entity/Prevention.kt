package com.example.pethelper.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    "preventions",
    indices = [Index(value = ["petId"])],
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
    val id: Long = 0
)