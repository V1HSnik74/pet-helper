package com.example.pethelper.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    val noteDao = AppDatabase.getInstance(application).notesDao()

    fun allNotes(petId: Int): Flow<List<Note>> {
        return noteDao.getAllNotesByPet(petId)
    }

    fun getNoteById(id: Int): Flow<Note?> {
        return noteDao.getNoteById(id)
    }

    fun addNote(description: String, category: String, date: String, petId: Int) {
        viewModelScope.launch {
            noteDao.addNote(
                Note(
                    date = date,
                    description = description,
                    category = category,
                    petId = petId
                )
            )
        }
    }

    fun updateCategory(category: String, id: Int) {
        viewModelScope.launch {
            noteDao.updateCategory(category, id)
        }
    }

    fun updateDescription(description: String, id: Int) {
        viewModelScope.launch {
            noteDao.updateDescription(description, id)
        }
    }

    fun getNoteByCategory(category: String, petId: Int) : Flow<List<Note>>{
        return noteDao.getAllNotesByCategory(category, petId)
    }
}