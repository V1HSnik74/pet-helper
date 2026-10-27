package com.vhsnik.pethelper.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.vhsnik.pethelper.data.entity.Pet
import kotlinx.coroutines.flow.Flow

@Dao
interface PetsDao {
    @Query("SELECT * FROM pets")
    fun getAllPets(): Flow<List<Pet>>

    @Upsert
    suspend fun upsertPet(pet: Pet)

    @Query("SELECT * FROM pets WHERE id=:petId")
    fun getPetById(petId: Int): Flow<Pet?>

    @Query("DELETE FROM pets WHERE id=:petId")
    suspend fun deletePet(petId: Int)
}
