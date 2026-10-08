package com.example.pethelper.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.pethelper.data.entity.Note
import com.example.pethelper.data.entity.NoteSection
import kotlinx.coroutines.flow.Flow

@Dao

interface NoteDao {
    @Query("SELECT * FROM notes WHERE petId=:petId")
    fun getAllNotesByPet(petId: Int): Flow<List<Note>>

    @Upsert
    suspend fun upsertNote(note: Note)

    @Query("SELECT * FROM notes WHERE id=:id")
    fun getNoteById(id: Long): Flow<Note?>

    @Query("SELECT * FROM notes WHERE category=:category AND petId=:petId AND section=:section")
    fun getAllCommonNotesByCategory(
        category: String,
        petId: Int,
        section: NoteSection = NoteSection.COMMON
    ): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE section=:section AND petId=:petId")
    fun getAllNotesBySection(section: NoteSection, petId: Int): Flow<List<Note>>

    @Query("DELETE FROM notes WHERE id=:id")
    suspend fun deleteNote(id: Long)
}