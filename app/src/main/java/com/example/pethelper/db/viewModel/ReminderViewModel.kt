package com.example.pethelper.db.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethelper.R
import com.example.pethelper.db.AppDatabase
import com.example.pethelper.db.entity.Reminder
import com.example.pethelper.workers.scheduleNotif
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ReminderViewModel(app: Application) : AndroidViewModel(app) {
    val reminderDao = AppDatabase.getInstance(app).ReminderDao()

    fun getAllRemindersByPet(petId: Int): Flow<List<Reminder>> {
        return reminderDao.allRemindersByPet(petId)
    }

    fun getReminderById(id: Long): Flow<Reminder> {
        return reminderDao.getReminderById(id)
    }

    fun addReminder(
        name: String,
        note: String?,
        date: String,
        time: String?,
        icon: Int,
        isNotif: Boolean,
        notifDate: String,
        notifTime: String,
        petId: Int,
        petName: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = reminderDao.addReminder(
                Reminder(
                    name,
                    note,
                    date,
                    time,
                    icon,
                    isNotif,
                    notifDate,
                    notifTime,
                    petId
                )
            )
            if (isNotif) {
                scheduleNotif(
                    getApplication<Application>().applicationContext,
                    id,
                    name,
                    date,
                    notifDate,
                    notifTime,
                    "Event",
                    R.drawable.tip_cat,
                    petName
                )
            }
        }
    }

    fun deleteReminder(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            reminderDao.deleteReminder(id)
        }
    }

    fun getAllRemindersByPetSelectedDay(date: String, petId: Int): Flow<List<Reminder>> {
        return reminderDao.getAllReminderByPetSelectedDay(date, petId)
    }
}