package com.example.pethelper.db.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethelper.db.AppDatabase
import com.example.pethelper.db.entity.NoteHealth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NoteHealthViewModel(app: Application) : AndroidViewModel(app) {
    val noteHealthDao = AppDatabase.getInstance(app).NoteHealthDao()

    fun getAllCheckUpNotesByPet(petId: Int): Flow<List<NoteHealth>> {
        return noteHealthDao.getAllCheckUpNotesByPet(petId)
    }

    fun getAllOverallNotesByPet(petId: Int): Flow<List<NoteHealth>> {
        return noteHealthDao.getAllOverallNotesByPet(petId)
    }

    fun addNoteHealth(note: String, date: String, petId: Int, category: String) {
        viewModelScope.launch {
            noteHealthDao.addHealthNote(NoteHealth(note, date, petId, category))
        }
    }

    fun getHealthNoteById(id: Int): Flow<NoteHealth?> {
        return noteHealthDao.getHealthNoteById(id)
    }
}