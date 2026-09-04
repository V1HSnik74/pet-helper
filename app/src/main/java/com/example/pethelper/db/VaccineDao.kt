package com.example.pethelper.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface VaccineDao {

    @Upsert
    suspend fun addVaccine(vaccine: Vaccine): Int

    @Query("SELECT * FROM vaccines WHERE isDone=0 AND petId=:petId ORDER BY date ASC")
    fun getAllUpcomingVaccinesByPet(petId: Int): Flow<List<Vaccine>>

    @Query("SELECT * FROM vaccines WHERE isDone=1 AND petId=:petId ORDER BY date DESC")
    fun getVaccineHistoryByPet(petId: Int): Flow<List<Vaccine>>

    @Query("UPDATE vaccines SET isDone=1 WHERE id=:id")
    suspend fun markVaccineAsDone(id: Int)

    @Query("SELECT * FROM vaccines WHERE id=:id")
    fun getVaccineById(id: Int): Flow<Vaccine?>

}