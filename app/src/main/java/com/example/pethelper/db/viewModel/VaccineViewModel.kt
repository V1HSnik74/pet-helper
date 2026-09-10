package com.example.pethelper.db.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethelper.R
import com.example.pethelper.db.AppDatabase
import com.example.pethelper.db.entity.Vaccine
import com.example.pethelper.workers.scheduleMarkAsDoneNotif
import com.example.pethelper.workers.scheduleNotif
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

    fun addVaccine(
        name: String,
        date: String,
        time: String,
        isNotif: Boolean,
        notifDate: String?,
        notifTime: String?,
        petId: Int,
        petName: String,
        isDone: Boolean
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
                    isDone,
                    petId
                )
            )
            if (isNotif) {
                scheduleNotif(
                    getApplication<Application>().applicationContext,
                    id,
                    name,
                    date,
                    notifDate!!,
                    notifTime!!,
                    "Vaccine",
                    R.drawable.vaccine_dialog,
                    petName
                )
            }
            scheduleMarkAsDoneNotif(
                getApplication<Application>().applicationContext,
                id,
                date,
                "Vaccine",
                R.drawable.vaccine_dialog,
                time,
                "Have you done $name for $petName?",
                "vaccineDone"
            )
        }
    }

    fun markVaccineAsDone(id: Int) {
        viewModelScope.launch {
            vaccineDao.markVaccineAsDone(id)
        }
    }
}