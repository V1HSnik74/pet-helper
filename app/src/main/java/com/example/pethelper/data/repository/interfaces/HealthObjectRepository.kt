package com.example.pethelper.data.repository.interfaces

import com.example.pethelper.data.entity.HealthCategory
import com.example.pethelper.data.entity.HealthObject
import kotlinx.coroutines.flow.Flow

interface HealthObjectRepository {
    fun getAllHealthObject(petId: Int): Flow<List<HealthObject>>
    fun getCategoryHealthObject(petId: Int, category: HealthCategory): Flow<List<HealthObject>>
    fun getCategoryHealthObjectIsDone(
        petId: Int,
        category: HealthCategory,
        isDone: Boolean
    ): Flow<List<HealthObject>>

    fun getAllHealthObjectIsDone(petId: Int, isDone: Boolean): Flow<List<HealthObject>>
    fun getHealthObjectById(id: Long): Flow<HealthObject?>
    suspend fun addHealthObject(healthObject: HealthObject): Long
    suspend fun updateHealthObject(healthObject: HealthObject): Long
    suspend fun markAsDone(id: Long)
    suspend fun updateNextTaking(nextTaking: String, id: Long)
    suspend fun deleteHealthObject(id: Long)
}