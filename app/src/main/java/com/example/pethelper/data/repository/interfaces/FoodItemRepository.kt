package com.example.pethelper.data.repository.interfaces

import com.example.pethelper.data.entity.FoodItem
import kotlinx.coroutines.flow.Flow

interface FoodItemRepository {
    fun getAllFoodItems(petId: Int): Flow<List<FoodItem>>
    fun getFoodItemById(id: Long): Flow<FoodItem?>
    suspend fun addFoodItem(foodItem: FoodItem)
    suspend fun deleteFoodItem(id: Long)
    suspend fun updateFoodItem(foodItem: FoodItem)
    suspend fun  deleteAllFoodItems(petId: Int)
}