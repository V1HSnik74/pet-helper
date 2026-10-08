package com.example.pethelper.data.repository.interfaces

import com.example.pethelper.data.entity.Note
import com.example.pethelper.data.entity.NoteSection
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getAllNotesByPet(petId: Int): Flow<List<Note>>
    fun getAllCommonNotesByCategory(petId: Int, category: String): Flow<List<Note>>
    fun getAllNotesBySection(petId: Int, section: NoteSection): Flow<List<Note>>
    suspend fun addNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(id: Long)
}