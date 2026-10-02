package com.example.pethelper.db.viewModel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethelper.BuildConfig
import com.example.pethelper.ai.RationRecommendation
import com.example.pethelper.db.AppDatabase
import com.example.pethelper.db.entity.Allergy
import com.example.pethelper.db.entity.FoodItem
import com.example.pethelper.db.entity.Supplement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FoodItemViewModel(application: Application) : AndroidViewModel(application) {
    val foodItemDao = AppDatabase.getInstance(application).FoodItemDao()

    fun allFoodItemsByPet(petId: Int): Flow<List<FoodItem>> {
        return foodItemDao.getAllFoodItemsByPet(petId)
    }

    fun getFoodItemById(id: Long): Flow<FoodItem?> {
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
        viewModelScope.launch(Dispatchers.IO) {
            foodItemDao.addFoodItem(FoodItem(name, description, portionSize, unit, icon, petId))
        }
    }

    private val rationRecommendation = RationRecommendation(
        BuildConfig.GEMINI_API_KEY
    )
    var isGenerating by mutableStateOf(false)
    private set
    var generatedRation by mutableStateOf<List<FoodItem>?>(null)
    private set

    fun generateRation(breed: String, weight: String, allAllergies: List<Allergy>, birthday: String,
                       allSupplements: List<Supplement>, neutered: String, petId: Int){
        if (isGenerating) return
        isGenerating = true
        viewModelScope.launch {
            try {
                rationRecommendation.getNutritionRecommendation(
                    breed,
                    weight, allAllergies, birthday, allSupplements, neutered,
                    petId
                ).collect {
                    generatedRation = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isGenerating = false
            }
        }
    }

    fun clearGeneratedRation(){
        generatedRation = null
    }
}