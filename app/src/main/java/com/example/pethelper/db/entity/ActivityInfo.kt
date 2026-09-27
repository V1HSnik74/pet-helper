package com.example.pethelper.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    "activity_info",
    [Index(value = ["petId"])],
    foreignKeys = [ForeignKey(Pet::class, ["id"], ["petId"])]
)
data class ActivityInfo(
    val activityLevel: String,
    val favActivities: String?,
    val limitations: String?,
    val goals: String,
    val petId: Int,
    @PrimaryKey(true)
    val id: Long = 0
)