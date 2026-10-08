package com.example.pethelper.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.pethelper.data.entity.FoodItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodItemDao {
    @Query("SELECT  * FROM food_items WHERE petId=:petId")
    fun getAllFoodItemsByPet(petId: Int): Flow<List<FoodItem>>

    @Upsert
    suspend fun upsertFoodItem(foodItem: FoodItem)

    @Query("SELECT * FROM food_items WHERE id=:id")
    fun getFoodItemById(id: Long): Flow<FoodItem?>

    @Query("DELETE FROM food_items WHERE id=:id")
    suspend fun deleteFoodItem(id: Long)

    @Query("DELETE FROM food_items WHERE petId=:petId")
    suspend fun deleteAllFoodItemByPet(petId: Int)
}