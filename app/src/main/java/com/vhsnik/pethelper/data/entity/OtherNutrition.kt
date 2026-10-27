package com.vhsnik.pethelper.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "other_nutrition",
    [Index(value = ["petId"])],
    foreignKeys = [ForeignKey(
        Pet::class, ["id"], ["petId"],
        ForeignKey.CASCADE
    )]
)
data class OtherNutrition(
    val name: String,
    val petId: Int,
    val category: String,
    @PrimaryKey(true)
    val id: Long = 0
)