package com.example.pethelper.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "vaccines", foreignKeys = [ForeignKey(
    entity = Pet::class,
    parentColumns = ["id"],
    childColumns = ["petId"],
    onDelete = ForeignKey.CASCADE
)])
data class Vaccine (
    val name: String,
    val date: String,
    val time: String,
    val isNotif: Boolean,
    val notifDate: String,
    val notifTime: String,
    val isDone: Boolean = false,
    val petId: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)