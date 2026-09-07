package com.example.pethelper.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.pethelper.db.entity.CheckUp
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckUpDao {
    @Query("SELECT * FROM checkUps WHERE isDone=0 AND petId=:petId")
    fun getAllUpcomingCheckUpsByPet(petId: Int): Flow<List<CheckUp>>

    @Query("SELECT * FROM checkUps WHERE isDone=1 AND petId=:petId")
    fun getCheckUpsHistoryByPet(petId: Int): Flow<List<CheckUp>>

    @Upsert
    suspend fun addCheckUp(checkUp: CheckUp): Int

    @Query("SELECT * FROM checkUps WHERE id=:id")
    fun getCheckUpById(id: Int): Flow<CheckUp?>

    @Query("UPDATE checkUps SET isDone=1 WHERE id=:id")
    suspend fun markCheckUpAsDone(id: Int)
}