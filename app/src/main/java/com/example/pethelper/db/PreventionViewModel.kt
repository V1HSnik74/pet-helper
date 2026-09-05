package com.example.pethelper.db

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethelper.workers.schedulePreventionNotif
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PreventionViewModel(application: Application) : AndroidViewModel(application) {
    val preventionDao = AppDatabase.getInstance(application).PreventionDao()

    fun getAllUpcomingPreventionsByPet(petId: Int): Flow<List<Prevention>> {
        return preventionDao.getAllUpcomingPreventionsByPet(petId)
    }

    fun getPreventionsHistoryByPet(petId: Int): Flow<List<Prevention>> {
        return preventionDao.getPreventionHistoryByPet(petId)
    }

    fun addUpcomingPrevention(
        action: String,
        note: String?,
        date: String,
        isNotif: Boolean,
        dateNotif: String,
        timeNotif: String,
        petId: Int,
        context: Context
    ) {
        viewModelScope.launch {
            val id = preventionDao.addPrevention(
                Prevention(
                    action,
                    note,
                    date,
                    isNotif,
                    dateNotif,
                    timeNotif,
                    petId,
                    false
                )
            )
            if (isNotif) schedulePreventionNotif(
                context.applicationContext,
                id,
                action,
                date,
                dateNotif,
                timeNotif
            )
        }
    }

    fun markPreventionAsDone(id: Int){
        viewModelScope.launch {
            preventionDao.markPreventionAsDone(id)
        }
    }

    fun getPreventionById(id: Int): Flow<Prevention?> {
        return preventionDao.getPreventionById(id)
    }

}