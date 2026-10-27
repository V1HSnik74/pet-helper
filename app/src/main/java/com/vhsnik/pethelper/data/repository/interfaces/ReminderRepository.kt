package com.vhsnik.pethelper.data.repository.interfaces

import com.vhsnik.pethelper.data.entity.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getAllReminders(petId: Int): Flow<List<Reminder>>
    fun getAllRemindersByDate(petId: Int, date: String): Flow<List<Reminder>>
    suspend fun addReminder(reminder: Reminder): Long
    suspend fun updateReminder(reminder: Reminder): Long
    suspend fun deleteReminder(id: Long)
}