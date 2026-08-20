package com.example.pethelper.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AllergyDao {
    @Query("SELECT * FROM allergies WHERE petId=:petId")
    fun getAllAllergiesByPet(petId: Int): Flow<List<Allergy>>

    @Query("SELECT * FROM allergies WHERE id=:id")
    fun getAllergyById(id: Int): Flow<Allergy?>

    @Upsert
    suspend fun addAllergy(allergy: Allergy)

    @Query("DELETE FROM allergies WHERE id=:id")
    suspend fun deleteAllergy(id: Int)
}