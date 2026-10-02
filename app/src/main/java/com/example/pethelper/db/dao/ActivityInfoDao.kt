package com.example.pethelper.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.pethelper.db.entity.ActivityInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityInfoDao {
    @Query("SELECT * FROM activity_info WHERE petId=:petId")
    fun getActivityInfo(petId: Int): Flow<ActivityInfo?>

    @Upsert
    suspend fun addActivityInfo(activityInfo: ActivityInfo)
}