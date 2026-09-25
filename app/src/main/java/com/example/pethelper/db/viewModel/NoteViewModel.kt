package com.example.pethelper.db.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethelper.db.AppDatabase
import com.example.pethelper.db.entity.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    val noteDao = AppDatabase.getInstance(application).notesDao()

    fun allNotes(petId: Int): Flow<List<Note>> {
        return noteDao.getAllNotesByPet(petId)
    }

    fun getNoteById(id: Long): Flow<Note?> {
        return noteDao.getNoteById(id)
    }

    fun addNote(description: String, category: String, date: String, petId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
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

    fun updateCategory(category: String, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.updateCategory(category, id)
        }
    }

    fun updateDescription(description: String, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.updateDescription(description, id)
        }
    }

    fun getNoteByCategory(category: String, petId: Int) : Flow<List<Note>>{
        return noteDao.getAllNotesByCategory(category, petId)
    }
}