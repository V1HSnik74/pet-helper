package com.vhsnik.pethelper.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.vhsnik.pethelper.data.entity.HealthCategory
import com.vhsnik.pethelper.data.entity.HealthObject
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthObjectDao {
    @Query("SELECT * FROM health_objects WHERE petId=:petId")
    fun getAllHealthObjects(petId: Int): Flow<List<HealthObject>>

    @Query("SELECT * FROM health_objects WHERE petId=:petId AND category=:category")
    fun getCategoryHealthObjects(petId: Int, category: HealthCategory): Flow<List<HealthObject>>

    @Query("SELECT * FROM health_objects WHERE petId=:petId AND category=:category AND isDone=:isDone")
    fun getCategoryHealthObjectsIsDone(
        petId: Int,
        category: HealthCategory,
        isDone: Boolean
    ): Flow<List<HealthObject>>

    @Query("SELECT * FROM health_objects WHERE petId=:petId AND isDone=:isDone")
    fun getAllHealthObjectsIsDone(petId: Int, isDone: Boolean): Flow<List<HealthObject>>

    @Query("SELECT * FROM health_objects WHERE id=:id")
    fun getHealthObjectById(id: Long): Flow<HealthObject?>

    @Upsert
    suspend fun upsertHealthObject(healthObject: HealthObject): Long

    @Query("UPDATE health_objects SET isDone=1 WHERE id=:id")
    suspend fun markAsDone(id: Long)

    @Query("UPDATE health_objects SET nextTaking=:nextTaking WHERE id=:id")
    suspend fun updateNextTaking(nextTaking: String, id: Long)

    @Query("DELETE FROM health_objects WHERE id=:id")
    suspend fun deleteHealthObject(id: Long)
}