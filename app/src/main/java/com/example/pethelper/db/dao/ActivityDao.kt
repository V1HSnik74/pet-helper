package com.example.pethelper.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.pethelper.db.entity.Activity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities WHERE petId=:petId")
    fun getAllActivities(petId: Int): Flow<List<Activity>>

    @Query("DELETE FROM activities WHERE petId=:petId")
    suspend fun deleteActivitiesList(petId: Int)

    @Upsert
    suspend fun addActivity(activity: Activity)
}