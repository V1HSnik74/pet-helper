package com.example.pethelper.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao

interface NoteDao {
    @Query("SELECT * FROM notes WHERE petId=:petId")
    fun getAllNotesByPet(petId: Int): Flow<List<Note>>

    @Upsert
    suspend fun addNote(note: Note)

    @Query("SELECT * FROM notes WHERE id=:id")
    fun getNoteById(id: Int): Flow<Note?>

    @Query("UPDATE notes SET description=:description WHERE id=:id")
    suspend fun updateDescription(description: String, id: Int)

    @Query("UPDATE notes SET category=:category WHERE id=:id")
    suspend fun updateCategory(category: String, id: Int)

    @Query("SELECT * FROM notes WHERE category=:category AND petId=:petId")
    fun getAllNotesByCategory(category: String, petId: Int): Flow<List<Note>>
}