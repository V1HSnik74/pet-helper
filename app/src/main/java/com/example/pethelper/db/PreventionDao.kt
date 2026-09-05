package com.example.pethelper.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PreventionDao {

    @Query("SELECT * FROM preventions WHERE isDone=1 AND petId=:petId ORDER BY date DESC")
    fun getPreventionHistoryByPet(petId: Int): Flow<List<Prevention>>

    @Query("SELECT * FROM preventions WHERE isDone=0 AND petId=:petId ORDER BY date ASC")
    fun getAllUpcomingPreventionsByPet(petId: Int): Flow<List<Prevention>>

    @Upsert
    suspend fun addPrevention(prevention: Prevention): Int

    @Query("UPDATE vaccines SET isDone=1 WHERE id=:id")
    suspend fun markPreventionAsDone(id: Int)

    @Query("SELECT * FROM preventions WHERE id=:id")
    fun getPreventionById(id: Int): Flow<Prevention?>
}