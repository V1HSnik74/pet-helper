package com.example.pethelper.ui.viewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethelper.ai.GeneratedRecommendation
import com.example.pethelper.data.entity.Activity
import com.example.pethelper.data.repository.interfaces.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val generatedRecommendation: GeneratedRecommendation
) : ViewModel() {
    fun getAllActivities(petId: Int): Flow<List<Activity>> {
        return activityRepository.getAllActivities(petId)
    }

    fun deleteActivities(petId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            activityRepository.deleteAllActivities(petId)
        }
    }

    fun deleteActivity(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            activityRepository.deleteActivity(id)
        }
    }

    fun addActivity(name: String, note: String, duration: String, petId: Int, icon: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            activityRepository.addActivity(Activity(name, duration, note, icon, petId))
        }
    }

    fun updateActivity(activity: Activity) {
        viewModelScope.launch(Dispatchers.IO) {
            activityRepository.updateActivity(activity)
        }
    }

    var isGenerating by mutableStateOf(false)
        private set
    var generatedActivities by mutableStateOf<List<Activity>?>(null)
        private set

    fun getActivitiesRecommendation(
        pet: PetFields
    ) {
        if (isGenerating) return
        isGenerating = true
        viewModelScope.launch {
            try {
                generatedRecommendation.getActivityRecommendation(
                    pet
                ).collect { generatedActivities = it }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isGenerating = false
            }
        }
    }

    fun saveActivityPlan(activities: List<Activity>) {
        viewModelScope.launch(Dispatchers.IO) {
            activities.forEach {
                activityRepository.addActivity(it)
            }
        }
    }

    fun clearGeneratedActivities() {
        generatedActivities = null
    }
}