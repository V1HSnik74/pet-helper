package com.example.pethelper.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "food_items", foreignKeys = [ForeignKey(
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
    val id: Int = 0
)

