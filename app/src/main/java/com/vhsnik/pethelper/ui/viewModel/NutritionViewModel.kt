package com.vhsnik.pethelper.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vhsnik.pethelper.ai.GeneratedRecommendation
import com.vhsnik.pethelper.data.entity.FoodItem
import com.vhsnik.pethelper.data.entity.Note
import com.vhsnik.pethelper.data.entity.NoteSection
import com.vhsnik.pethelper.data.entity.OtherNutrition
import com.vhsnik.pethelper.data.repository.interfaces.FoodItemRepository
import com.vhsnik.pethelper.data.repository.interfaces.NotesRepository
import com.vhsnik.pethelper.data.repository.interfaces.OtherNutritionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val foodItemRepository: FoodItemRepository,
    private val noteRepository: NotesRepository,
    private val otherNutritionRepository: OtherNutritionRepository,
    private val generatedRecommendation: GeneratedRecommendation
) : ViewModel() {
    fun getAllFoodItems(petId: Int) = foodItemRepository.getAllFoodItems(petId)
    fun getAllAllergies(petId: Int) =
        otherNutritionRepository.getAllOtherNutritionCategory(petId, "Allergy")

    fun getAllSupplements(petId: Int) =
        otherNutritionRepository.getAllOtherNutritionCategory(petId, "Supplement")

    fun getAllTreats(petId: Int) =
        otherNutritionRepository.getAllOtherNutritionCategory(petId, "Treat")

    fun getAllNotesNutrition(petId: Int) =
        noteRepository.getAllNotesBySection(petId, NoteSection.NUTRITION)

    fun addFoodItem(
        name: String,
        description: String,
        portionSize: String,
        unit: String,
        icon: Int,
        petId: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            foodItemRepository.addFoodItem(
                FoodItem(
                    name,
                    description,
                    portionSize,
                    unit,
                    icon,
                    petId
                )
            )
        }
    }

    fun addNoteNutrition(note: String, category: String, petId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.addNote(
                Note(
                    note,
                    petId,
                    NoteSection.NUTRITION,
                    category = category
                )
            )
        }
    }

    fun addOtherNutrition(name: String, category: String, petId: Int) {
        viewModelScope.launch {
            otherNutritionRepository.addOtherNutrition(OtherNutrition(name, petId, category))
        }
    }

    fun updateFoodItem(foodItem: FoodItem) {
        viewModelScope.launch {
            foodItemRepository.updateFoodItem(foodItem)
        }
    }


    fun updateNoteNutrition(noteNutrition: Note) {
        viewModelScope.launch {
            noteRepository.updateNote(noteNutrition)
        }
    }

    fun deleteFoodItem(id: Long) {
        viewModelScope.launch {
            foodItemRepository.deleteFoodItem(id)
        }
    }

    fun deleteAllFoodItems(petId: Int) {
        viewModelScope.launch {
            foodItemRepository.deleteAllFoodItems(petId)
        }
    }

    fun deleteNoteNutrition(id: Long) {
        viewModelScope.launch {
            noteRepository.deleteNote(id)
        }
    }

    fun deleteOtherNutrition(id: Long) {
        viewModelScope.launch {
            otherNutritionRepository.deleteOtherNutrition(id)
        }
    }
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generatedRation = MutableStateFlow<List<FoodItem>?>(null)
    val generatedRation: StateFlow<List<FoodItem>?> = _generatedRation.asStateFlow()

    fun clearGeneratedRation() {
        _generatedRation.value = null
    }

    fun generateRation(
        pet: PetFields, allergies: List<OtherNutrition>,
        supplements: List<OtherNutrition>
    ) {
        if (_isGenerating.value) return
        _isGenerating.value = true
        viewModelScope.launch {
            try {
                generatedRecommendation.getNutritionRecommendation(
                    pet, allergies, supplements
                ).collect {
                    _generatedRation.value = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isGenerating.value = false
            }
        }
    }
}