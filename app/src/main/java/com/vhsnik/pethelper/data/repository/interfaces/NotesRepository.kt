package com.vhsnik.pethelper.data.repository.interfaces

import com.vhsnik.pethelper.data.entity.Note
import com.vhsnik.pethelper.data.entity.NoteSection
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getAllCommonNotesByCategory(petId: Int, category: String): Flow<List<Note>>
    fun getAllNotesBySection(petId: Int, section: NoteSection): Flow<List<Note>>
    suspend fun addNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(id: Long)
}