package com.example.pethelper.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    "reminders",
    [Index(value = ["petId"])],
    foreignKeys =
        [ForeignKey(
            Pet::class,
            ["id"],
            ["petId"],
            ForeignKey.CASCADE
        )
        ]
)
data class Reminder(
    val name: String,
    val note: String?,
    val date: String,
    val time: String?,
    val icon: Int,
    val isNotif: Boolean,
    val notifDate: String,
    val notifTime: String,
    val petId: Int,
    @PrimaryKey(true)
    val id: Long = 0
)