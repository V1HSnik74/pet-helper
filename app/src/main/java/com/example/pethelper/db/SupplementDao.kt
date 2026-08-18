package com.example.pethelper.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SupplementDao {
    @Query("SELECT * FROM supplements WHERE petId=:petId")
    fun getAllSupplementsByPet(petId: Int): Flow<List<Supplement>>

    @Query("SELECT * FROM supplements WHERE id=:id")
    fun getSupplementById(id: Int): Flow<Supplement>

    @Upsert
    suspend fun addSupplement(supplement: Supplement)

    @Query("DELETE FROM supplements WHERE id=:id")
    suspend fun deleteSupplement(id: Int)
}