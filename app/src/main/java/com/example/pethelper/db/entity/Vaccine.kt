package com.example.pethelper.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "vaccines", foreignKeys = [ForeignKey(
    Pet::class,
    ["id"],
    ["petId"],
    ForeignKey.CASCADE
)])
data class Vaccine (
    val name: String,
    val date: String,
    val time: String,
    val isNotif: Boolean,
    val notifDate: String?,
    val notifTime: String?,
    val isDone: Boolean,
    val petId: Int,
    @PrimaryKey(true)
    val id: Int = 0
)