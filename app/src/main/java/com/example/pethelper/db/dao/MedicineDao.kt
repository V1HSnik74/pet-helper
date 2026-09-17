package com.example.pethelper.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.pethelper.db.entity.Medicine
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {

    @Query("SELECT * FROM medicines WHERE petId=:petId")
    fun getAllMedicinesByPet(petId: Int): Flow<List<Medicine>>

    @Query("SELECT * FROM medicines WHERE petId=:petId ORDER BY nextTaking ASC")
    fun getAllUpcomingMedicinesByPet(petId: Int): Flow<List<Medicine>>

    @Query("SELECT * FROM medicines WHERE id=:id")
    fun getMedicineById(id: Long): Flow<Medicine?>

    @Upsert
    suspend fun addMedicine(medicine: Medicine): Long

    @Query("UPDATE medicines SET nextTaking=:nextTaking WHERE id=:id")
    suspend fun updateNextTaking(nextTaking: String, id: Long)
}