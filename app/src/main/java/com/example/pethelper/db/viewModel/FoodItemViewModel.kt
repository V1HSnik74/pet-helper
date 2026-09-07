package com.example.pethelper.db.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethelper.db.AppDatabase
import com.example.pethelper.db.entity.FoodItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FoodItemViewModel(application: Application) : AndroidViewModel(application) {
    val foodItemDao = AppDatabase.getInstance(application).FoodItemDao()

    fun allFoodItemsByPet(petId: Int): Flow<List<FoodItem>> {
        return foodItemDao.getAllFoodItemsByPet(petId)
    }

    fun getFoodItemById(id: Int): Flow<FoodItem?> {
        return foodItemDao.getFoodItemById(id)
    }

    fun addFoodItem(
        name: String,
        description: String,
        portionSize: String,
        unit: String,
        icon: Int,
        petId: Int
    ) {
        viewModelScope.launch {
            foodItemDao.addFoodItem(FoodItem(name, description, portionSize, unit, icon, petId))
        }
    }
}