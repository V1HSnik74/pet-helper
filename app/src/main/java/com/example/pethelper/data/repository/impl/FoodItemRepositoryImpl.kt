package com.example.pethelper.data.repository.impl

import com.example.pethelper.data.dao.FoodItemDao
import com.example.pethelper.data.entity.FoodItem
import com.example.pethelper.data.repository.interfaces.FoodItemRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FoodItemRepositoryImpl @Inject constructor(
    private val foodItemDao: FoodItemDao
) : FoodItemRepository {
    override fun getAllFoodItems(petId: Int): Flow<List<FoodItem>> =
        foodItemDao.getAllFoodItemsByPet(petId)

    override fun getFoodItemById(id: Long): Flow<FoodItem?> =
        foodItemDao.getFoodItemById(id)

    override suspend fun addFoodItem(foodItem: FoodItem) =
        foodItemDao.upsertFoodItem(foodItem)

    override suspend fun deleteFoodItem(id: Long) =
        foodItemDao.deleteFoodItem(id)

    override suspend fun updateFoodItem(foodItem: FoodItem) =
        foodItemDao.upsertFoodItem(foodItem)

    override suspend fun deleteAllFoodItems(petId: Int) =
        foodItemDao.deleteAllFoodItemByPet(petId)
}