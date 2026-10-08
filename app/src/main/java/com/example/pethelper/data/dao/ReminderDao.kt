package com.example.pethelper.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.pethelper.data.entity.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE petId=:petId")
    fun allRemindersByPet(petId: Int): Flow<List<Reminder>>

    @Upsert
    suspend fun updateReminder(reminder: Reminder): Long

    @Query("DELETE FROM reminders WHERE id=:id")
    suspend fun deleteReminder(id: Long)

    @Query("SELECT * FROM reminders WHERE date=:date AND petId=:petId")
    fun getAllReminderByPetSelectedDay(date: String, petId: Int): Flow<List<Reminder>>
}