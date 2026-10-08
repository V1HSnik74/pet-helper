package com.example.pethelper.data.repository.impl

import com.example.pethelper.data.dao.NoteDao
import com.example.pethelper.data.entity.Note
import com.example.pethelper.data.entity.NoteSection
import com.example.pethelper.data.repository.interfaces.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NotesRepository {
    override fun getAllNotesByPet(petId: Int): Flow<List<Note>> = noteDao.getAllNotesByPet(petId)
    override fun getAllCommonNotesByCategory(
        petId: Int,
        category: String
    ): Flow<List<Note>> = noteDao.getAllCommonNotesByCategory(category, petId)

    override fun getAllNotesBySection(
        petId: Int,
        section: NoteSection
    ): Flow<List<Note>> = noteDao.getAllNotesBySection(section, petId)

    override suspend fun addNote(note: Note) = noteDao.upsertNote(note)

    override suspend fun updateNote(note: Note) = noteDao.upsertNote(note)

    override suspend fun deleteNote(id: Long) = noteDao.deleteNote(id)
}