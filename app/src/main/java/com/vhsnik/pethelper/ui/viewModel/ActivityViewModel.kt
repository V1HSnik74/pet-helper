package com.vhsnik.pethelper.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vhsnik.pethelper.ai.GeneratedRecommendation
import com.vhsnik.pethelper.data.entity.Activity
import com.vhsnik.pethelper.data.repository.interfaces.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val generatedRecommendation: GeneratedRecommendation
) : ViewModel() {
    private val curPetId = MutableStateFlow<Int?>(null)
    fun setPetId(id: Int) {
        curPetId.value = id
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val allActivities: StateFlow<List<Activity>> =
        curPetId.filterNotNull().flatMapLatest {
            activityRepository.getAllActivities(it)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun deleteActivities() {
        val petId = curPetId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            activityRepository.deleteAllActivities(petId)
        }
    }

    fun deleteActivity(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            activityRepository.deleteActivity(id)
        }
    }

    fun addActivity(name: String, note: String, duration: String, icon: Int) {
        val petId = curPetId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            activityRepository.addActivity(Activity(name, duration, note, icon, petId))
        }
    }

    fun updateActivity(activity: Activity) {
        viewModelScope.launch(Dispatchers.IO) {
            activityRepository.updateActivity(activity)
        }
    }

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()
    private val _generatedActivities = MutableStateFlow<List<Activity>?>(null)
    val generatedActivities = _generatedActivities.asStateFlow()

    fun getActivitiesRecommendation(
        pet: PetFields
    ) {
        if (_isGenerating.value) return
        _isGenerating.value = true
        viewModelScope.launch {
            try {
                _generatedActivities.value = generatedRecommendation.getActivityRecommendation(pet)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isGenerating.value = false
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
        _generatedActivities.value = null
    }
}