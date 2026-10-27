package com.vhsnik.pethelper.data.repository.interfaces

import com.vhsnik.pethelper.data.entity.Activity
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun getAllActivities(petId: Int): Flow<List<Activity>>
    suspend fun addActivity(activity: Activity)
    suspend fun deleteAllActivities(petId: Int)
    suspend fun deleteActivity(id: Long)
    suspend fun updateActivity(activity: Activity)
}