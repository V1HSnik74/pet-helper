package com.example.pethelper.db.viewModel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethelper.BuildConfig
import com.example.pethelper.ai.ActivityRecommendation
import com.example.pethelper.db.AppDatabase
import com.example.pethelper.db.entity.Activity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ActivityViewModel(app: Application) : AndroidViewModel(app) {
    val activityDao = AppDatabase.getInstance(app).ActivityDao()
    fun getAllActivities(petId: Int): Flow<List<Activity>> {
        return activityDao.getAllActivities(petId)
    }

    fun deleteActivities(petId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            activityDao.deleteActivitiesList(petId)
        }
    }

    fun addActivity(name: String, note: String, duration: String, petId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            activityDao.addActivity(Activity(name, duration, note, petId))
        }
    }

    private val activityRecommendation = ActivityRecommendation(BuildConfig.GEMINI_API_KEY)
    var isGenerating by mutableStateOf(false)
        private set
    var generatedActivities by mutableStateOf<List<Activity>?>(null)
        private set

    fun getActivitiesRecommendation(
        breed: String,
        weight: String,
        birthday: String,
        neutered: String,
        activityLevel: String,
        favoriteActivities: String,
        limitations: String,
        goals: String,
        petId: Int,
        animal: String
    ) {
        if (isGenerating) return
        isGenerating = true
        viewModelScope.launch {
            try {
                activityRecommendation.getActivityRecommendation(
                    breed,
                    weight,
                    birthday,
                    neutered,
                    activityLevel,
                    favoriteActivities,
                    limitations,
                    goals,
                    petId,
                    animal
                ).collect { generatedActivities = it }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isGenerating = false
            }
        }
    }
    fun clearGeneratedActivities(){
        generatedActivities = null
    }
}