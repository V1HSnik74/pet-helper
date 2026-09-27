package com.example.pethelper.db.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethelper.db.AppDatabase
import com.example.pethelper.db.entity.ActivityInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ActivityInfoViewModel(app: Application) : AndroidViewModel(app) {
    val activityInfoDao = AppDatabase.getInstance(app).ActivityInfoDao()
    fun getActivityInfo(petId: Int): Flow<ActivityInfo> {
        return activityInfoDao.getActivityInfo(petId)
    }

    fun addActivityInfo(
        activityLevel: String, favActivities: String?,
        limitations: String?, goals: String, petId: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            activityInfoDao.addActivityInfo(
                ActivityInfo(
                    activityLevel,
                    favActivities,
                    limitations,
                    goals,
                    petId
                )
            )
        }
    }
}