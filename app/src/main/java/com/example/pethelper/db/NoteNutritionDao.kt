package com.example.pethelper.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteNutritionDao {
    @Query("SELECT * FROM notes_nutrition WHERE petId=:petId")
    fun getAllNotesNutritionByPet(petId: Int): Flow<List<NoteNutrition>>

    @Query("SELECT * FROM notes_nutrition WHERE id=:id")
    fun getNoteNutritionById(id: Int): Flow<NoteNutrition?>

    @Upsert
    suspend fun addNoteNutrition(noteNutrition: NoteNutrition)

    @Query("DELETE FROM notes_nutrition WHERE id=:id")
    suspend fun deleteNoteNutrition(id: Int)
}