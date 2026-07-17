package com.example.pethelper.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PetsDao{
    @Query("SELECT * FROM pets")
    fun getAllPets(): Flow<List<Pet>>

    @Upsert
    suspend fun upsertPet(pet: Pet)

    @Query("UPDATE pets SET photo = :photo WHERE id=:petId")
    suspend fun updatePhoto(photo: String, petId: Int)
}
