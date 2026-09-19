package com.example.pethelper.db.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethelper.db.AppDatabase
import com.example.pethelper.db.entity.Medicine
import com.example.pethelper.workers.scheduleMedicineNotif
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class MedicineViewModel(app: Application) : AndroidViewModel(app) {
    val medicineDao = AppDatabase.getInstance(app).MedicineDao()
    fun getAllMedicinesByPet(petId: Int): Flow<List<Medicine>> {
        return medicineDao.getAllMedicinesByPet(petId)
    }

    fun getMedicineById(id: Long): Flow<Medicine?> {
        return medicineDao.getMedicineById(id)
    }

    fun addMedicine(
        name: String, note: String?, time: String,
        intervalTimes: String, intervalPeriod: String,
        isNotif: Boolean, timeNotif: String,
        petId: Int, petName: String, intervalValue: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = medicineDao.addMedicine(
                Medicine(
                    name,
                    note,
                    time,
                    intervalTimes,
                    intervalPeriod,
                    isNotif,
                    timeNotif,
                    "",
                    intervalValue,
                    petId
                )
            )
            val nextNotifDateTime = scheduleMedicineNotif(
                getApplication<Application>().applicationContext,
                id,
                name,
                time,
                intervalPeriod,
                timeNotif,
                intervalValue,
                petName,
                isNotif,
                intervalTimes
            ).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            medicineDao.updateNextTaking(nextNotifDateTime, id)
        }
    }
}