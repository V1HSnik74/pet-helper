package com.example.pethelper.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.pethelper.db.entity.NoteHealth
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteHealthDao {
    @Query("SELECT * FROM notes_health WHERE petId=:petId AND category=\"CheckUp\"")
    fun getAllCheckUpNotesByPet(petId: Int): Flow<List<NoteHealth>>

    @Query("SELECT * FROM notes_health WHERE petId=:petId AND category=\"Overall\"")
    fun getAllOverallNotesByPet(petId: Int): Flow<List<NoteHealth>>

    @Upsert
    suspend fun addHealthNote(noteHealth: NoteHealth)

    @Query("SELECT * FROM notes_health WHERE id=:id")
    fun getHealthNoteById(id: Int): Flow<NoteHealth?>
}