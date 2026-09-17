package com.example.pethelper.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    "medicines",
    foreignKeys = [ForeignKey(
        Pet::class,
        ["id"],
        ["petId"],
        ForeignKey.CASCADE
    )
    ],
    indices = [Index(value = ["petId"])]
)

data class Medicine(
    val name: String,
    val note: String?,
    val time: String,
    val intervalTimes: String,
    val intervalPeriod: String,
    val isNotif: Boolean,
    val timeNotif: String,
    val nextTaking: String,
    val petId: Int,
    @PrimaryKey(true)
    val id: Long = 0
)