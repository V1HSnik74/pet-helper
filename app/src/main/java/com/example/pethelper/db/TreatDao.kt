package com.example.pethelper.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TreatDao {
    @Query("SELECT * FROM treats WHERE petId=:petId")
    fun getAllTreatsByPet(petId: Int): Flow<List<Treat>>

    @Query("SELECT * FROM treats WHERE id=:id")
    fun getTreatById(id: Int): Flow<Treat?>

    @Upsert
    suspend fun addTreat(treat: Treat)

    @Query("DELETE FROM treats WHERE id=:id")
    suspend fun deleteTreat(id: Int)
}