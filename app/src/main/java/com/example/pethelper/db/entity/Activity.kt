package com.example.pethelper.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    "activities", [Index(value = ["petId"])],
    foreignKeys = [ForeignKey(Pet::class, ["id"], ["petId"])]
)
data class Activity(
    val name: String,
    val duration: String,
    val note: String,
    val petId: Int,
    @PrimaryKey(true)
    val id: Long = 0
)