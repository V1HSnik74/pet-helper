package com.example.pethelper.data.repository.impl

import com.example.pethelper.data.dao.ReminderDao
import com.example.pethelper.data.entity.Reminder
import com.example.pethelper.data.repository.interfaces.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao
) : ReminderRepository {
    override fun getAllReminders(petId: Int): Flow<List<Reminder>> =
        reminderDao.allRemindersByPet(petId)

    override fun getAllRemindersByDate(
        petId: Int,
        date: String
    ): Flow<List<Reminder>> =
        reminderDao.getAllReminderByPetSelectedDay(date, petId)

    override suspend fun addReminder(reminder: Reminder) =
        reminderDao.updateReminder(reminder)

    override suspend fun updateReminder(reminder: Reminder) =
        reminderDao.updateReminder(reminder)

    override suspend fun deleteReminder(id: Long) =
        reminderDao.deleteReminder(id)

}