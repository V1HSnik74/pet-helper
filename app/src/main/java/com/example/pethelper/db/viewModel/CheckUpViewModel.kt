package com.example.pethelper.db.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethelper.R
import com.example.pethelper.db.AppDatabase
import com.example.pethelper.db.entity.CheckUp
import com.example.pethelper.workers.scheduleMarkAsDoneNotif
import com.example.pethelper.workers.scheduleNotif
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CheckUpViewModel(application: Application) : AndroidViewModel(application) {
    val checkUpDao = AppDatabase.getInstance(application).CheckUpDao()
    fun getAllUpcomingCheckUpsByPet(petId: Int): Flow<List<CheckUp>> {
        return checkUpDao.getAllUpcomingCheckUpsByPet(petId)
    }

    fun getCheckUpsHistoryByPet(petId: Int): Flow<List<CheckUp>> {
        return checkUpDao.getCheckUpsHistoryByPet(petId)
    }

    fun addCheckUp(
        name: String,
        note: String?,
        date: String,
        time: String,
        isNotif: Boolean,
        dateNotif: String?,
        timeNotif: String?,
        petId: Int,
        petName: String,
        isDone: Boolean
    ) {
        viewModelScope.launch {
            val checkUpId = checkUpDao.addCheckUp(
                CheckUp(
                    name,
                    note,
                    date,
                    time,
                    isNotif,
                    dateNotif,
                    timeNotif,
                    isDone,
                    petId
                )
            )
            if (isNotif) {
                scheduleNotif(
                    getApplication<Application>().applicationContext,
                    checkUpId.toInt(),
                    name,
                    date,
                    dateNotif!!,
                    timeNotif!!,
                    "Check-up",
                    R.drawable.checkup_dialog,
                    petName
                )
            }
            scheduleMarkAsDoneNotif(
                getApplication<Application>().applicationContext,
                checkUpId.toInt(),
                date,
                "Prevention",
                R.drawable.parasites_dialog,
                time,
                "Have you done $name for $petName?",
                "checkUpDone"
            )
        }
    }

    fun getCheckUpById(id: Int): Flow<CheckUp?> {
        return checkUpDao.getCheckUpById(id)
    }

    fun markCheckUpAsDone(id: Int) {
        viewModelScope.launch {
            checkUpDao.markCheckUpAsDone(id)
        }
    }
}