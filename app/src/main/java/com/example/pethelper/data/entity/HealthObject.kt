package com.example.pethelper.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class HealthCategory {
    CHECKUP, VACCINE, PREVENTION, MEDICINE
}

@Entity(
    tableName = "health_objects",
    [Index(value = ["petId"])],
    foreignKeys = [ForeignKey(
        Pet::class,
        ["id"],
        ["petId"],
        ForeignKey.CASCADE
    )]
)

data class HealthObject(
    val name: String,
    val category: HealthCategory,
    val petId: Int,
    val note: String? = null,
    val date: String? = null,
    val time: String? = null,
    val isDone: Boolean? = false,
    val isNotif: Boolean = false,
    val dateNotif: String? = null,
    val timeNotif: String? = null,
    val intervalTime: String? = null,
    val intervalPeriod: String? = null,
    val intervalValue: String? = null,
    val nextTaking: String? = null,
    @PrimaryKey(true)
    val id: Long = 0
)