package com.example.pethelper.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NoteNutritionViewModel(application: Application) : AndroidViewModel(application) {
    val noteNutritionDao = AppDatabase.getInstance(application).NoteNutritionDao()

    fun getAllNoteNutritionByPet(petId: Int): Flow<List<NoteNutrition>> {
        return noteNutritionDao.getAllNotesNutritionByPet(petId)
    }

    fun getNoteById(id: Int): Flow<NoteNutrition?> {
        return noteNutritionDao.getNoteNutritionById(id)
    }

    fun addNoteNutrition(note: String, category: String, petId: Int) {
        viewModelScope.launch {
            noteNutritionDao.addNoteNutrition(NoteNutrition(note, category, petId))
        }
    }

    fun deleteNoteNutrition(id: Int) {
        viewModelScope.launch {
            noteNutritionDao.deleteNoteNutrition(id)
        }
    }
}