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
class NutritionViewModel @Inject constructor(
    private val foodItemRepository: FoodItemRepository,
    private val noteRepository: NotesRepository,
    private val otherNutritionRepository: OtherNutritionRepository,
    private val generatedRecommendation: GeneratedRecommendation
) : ViewModel() {
    private val curPetId = MutableStateFlow<Int?>(null)
    fun setCurPetId(petId: Int) {
        curPetId.value = petId
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val allFoodItems: StateFlow<List<FoodItem>> =
        curPetId.filterNotNull().flatMapLatest {
            foodItemRepository.getAllFoodItems(it)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val allAllergies: StateFlow<List<OtherNutrition>> =
        curPetId.filterNotNull().flatMapLatest {
            otherNutritionRepository.getAllOtherNutritionCategory(it, "Allergy")
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    @OptIn(ExperimentalCoroutinesApi::class)
    val allSupplements: StateFlow<List<OtherNutrition>> =
        curPetId.filterNotNull().flatMapLatest {
            otherNutritionRepository.getAllOtherNutritionCategory(it, "Supplement")
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    @OptIn(ExperimentalCoroutinesApi::class)
    val allTreats: StateFlow<List<OtherNutrition>> =
        curPetId.filterNotNull().flatMapLatest {
            otherNutritionRepository.getAllOtherNutritionCategory(it, "Treat")
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val allNotesNutrition: StateFlow<List<Note>> =
        curPetId.filterNotNull().flatMapLatest {
            noteRepository.getAllNotesBySection(it, NoteSection.NUTRITION)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addFoodItem(
        name: String,
        description: String,
        portionSize: String,
        unit: String,
        icon: Int
    ) {
        val petId = curPetId.value ?: return
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

    fun addNoteNutrition(note: String, category: String) {
        val petId = curPetId.value ?: return
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

    fun addOtherNutrition(name: String, category: String) {
        val petId = curPetId.value ?: return
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

    fun deleteAllFoodItems() {
        val petId = curPetId.value ?: return
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
                _generatedRation.value =
                    generatedRecommendation.getNutritionRecommendation(pet, allergies, supplements)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isGenerating.value = false
            }
        }
    }
}