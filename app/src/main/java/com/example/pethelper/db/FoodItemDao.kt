package com.example.pethelper.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodItemDao {
    @Query("SELECT  * FROM food_items WHERE petId=:petId")
    fun getAllFoodItemsByPet(petId: Int): Flow<List<FoodItem>>

    @Upsert
    suspend fun addFoodItem(foodItem: FoodItem)

    @Query("SELECT * FROM food_items WHERE id=:id")
    fun getFoodItemById(id: Int): Flow<FoodItem?>
}