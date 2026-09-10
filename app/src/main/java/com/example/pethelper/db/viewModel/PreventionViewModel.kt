package com.example.pethelper.db.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethelper.R
import com.example.pethelper.db.AppDatabase
import com.example.pethelper.db.entity.Prevention
import com.example.pethelper.workers.scheduleMarkAsDoneNotif
import com.example.pethelper.workers.scheduleNotif
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

    fun addPrevention(
        action: String,
        note: String?,
        date: String,
        isNotif: Boolean,
        dateNotif: String?,
        timeNotif: String?,
        petId: Int,
        petName: String,
        isDone: Boolean
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
                    isDone
                )
            )
            if (isNotif) {
                scheduleNotif(
                    getApplication<Application>().applicationContext,
                    id,
                    action,
                    date,
                    dateNotif!!,
                    timeNotif!!,
                    "Prevention",
                    R.drawable.parasites_dialog,
                    petName
                )
            }
            scheduleMarkAsDoneNotif(
                getApplication<Application>().applicationContext,
                id,
                date,
                "Prevention",
                R.drawable.parasites_dialog,
                contentText = "Have you done $action for $petName?",
                markAsFun = "preventionDone"
            )
        }
    }

    fun markPreventionAsDone(id: Int) {
        viewModelScope.launch {
            preventionDao.markPreventionAsDone(id)
        }
    }

    fun getPreventionById(id: Int): Flow<Prevention?> {
        return preventionDao.getPreventionById(id)
    }

}