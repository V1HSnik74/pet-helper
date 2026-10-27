package com.vhsnik.pethelper.data.repository.impl

import com.vhsnik.pethelper.data.dao.ActivityDao
import com.vhsnik.pethelper.data.entity.Activity
import com.vhsnik.pethelper.data.repository.interfaces.ActivityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val activityDao: ActivityDao
) : ActivityRepository {
    override fun getAllActivities(petId: Int): Flow<List<Activity>> =
        activityDao.getAllActivities(petId)

    override suspend fun addActivity(activity: Activity) = activityDao.addActivity(activity)
    override suspend fun deleteAllActivities(petId: Int) = activityDao.deleteActivitiesList(petId)
    override suspend fun deleteActivity(id: Long) = activityDao.deleteActivity(id)
    override suspend fun updateActivity(activity: Activity) = activityDao.addActivity(activity)
}