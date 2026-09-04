package com.example.pethelper.db

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethelper.workers.scheduleVaccineNotif
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class VaccineViewModel(application: Application) : AndroidViewModel(application) {
    val vaccineDao = AppDatabase.getInstance(application).VaccineDao()

    fun allUpcomingVaccines(petId: Int): Flow<List<Vaccine>> {
        return vaccineDao.getAllUpcomingVaccinesByPet(petId)
    }

    fun vaccineHistory(petId: Int): Flow<List<Vaccine>> {
        return vaccineDao.getVaccineHistoryByPet(petId)
    }

    fun getVaccineById(id: Int): Flow<Vaccine?> {
        return vaccineDao.getVaccineById(id)
    }

    fun addUpcomingVaccine(
        name: String,
        date: String,
        time: String,
        isNotif: Boolean,
        notifDate: String,
        notifTime: String,
        petId: Int,
        context: Context
    ) {
        viewModelScope.launch {
            val id = vaccineDao.addVaccine(
                Vaccine(
                    name,
                    date,
                    time,
                    isNotif,
                    notifDate,
                    notifTime,
                    false,
                    petId
                )
            )
            if (isNotif) {
                scheduleVaccineNotif(
                    context.applicationContext,
                    id,
                    name,
                    date,
                    notifDate,
                    notifTime
                )
            }
        }
    }

    fun markVaccineAsDone(id: Int){
        viewModelScope.launch {
            vaccineDao.markVaccineAsDone(id)
        }
    }
}