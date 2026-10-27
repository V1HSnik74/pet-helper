package com.vhsnik.pethelper.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "food_items",
    indices = [Index(value = ["petId"])],
    foreignKeys = [ForeignKey(
    entity = Pet::class,
    parentColumns = ["id"],
    childColumns = ["petId"],
    onDelete = ForeignKey.CASCADE
)])
data class FoodItem(
    val name: String,
    val description: String,
    val portionSize: String,
    val unit: String,
    val icon: Int,
    val petId: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)